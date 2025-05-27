<template>
  <div class="register-container">
    <div class="register-box">
      <h2>用户注册</h2>
      <div class="form-group">
        <label for="username">用户名</label>
        <input
          id="username"
          type="text"
          v-model="registerForm.username"
          placeholder="请输入用户名 (3-20个字符)"
          class="form-input"
        />
      </div>
      <div class="form-group">
        <label for="name">姓名</label>
        <input
          id="name"
          type="text"
          v-model="registerForm.name"
          placeholder="请输入真实姓名 (选填)"
          class="form-input"
        />
      </div>
      <div class="form-group">
        <label for="password">密码</label>
        <input
          id="password"
          type="password"
          v-model="registerForm.password"
          placeholder="请输入密码 (6-20个字符)"
          class="form-input"
        />
      </div>
      <div class="form-group">
        <label for="confirmPassword">确认密码</label>
        <input
          id="confirmPassword"
          type="password"
          v-model="registerForm.confirmPassword"
          placeholder="请再次输入密码"
          class="form-input"
        />
      </div>
      <div class="form-group">
        <label for="role">角色</label>
        <select id="role" v-model="registerForm.role" class="form-input">
          <option value="student">学生</option>
          <option value="teacher">教师</option>
        </select>
      </div>
      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>
      <button
        @click="handleRegister"
        class="register-button"
        :disabled="loading"
      >
        {{ loading ? "注册中..." : "注册" }}
      </button>
      <div class="login-link">
        已有账号？<router-link to="/login">去登录</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { register } from "@/api/user";

export default {
  name: "Register",
  data() {
    return {
      registerForm: {
        username: "",
        name: "",
        password: "",
        confirmPassword: "",
        role: "student",
      },
      loading: false,
      errorMessage: "",
    };
  },
  methods: {
    validateForm() {
      // 验证用户名
      if (!this.registerForm.username) {
        this.errorMessage = "请输入用户名";
        return false;
      }
      if (
        this.registerForm.username.length < 3 ||
        this.registerForm.username.length > 20
      ) {
        this.errorMessage = "用户名长度应为3-20个字符";
        return false;
      }

      // 验证密码
      if (!this.registerForm.password) {
        this.errorMessage = "请输入密码";
        return false;
      }
      if (
        this.registerForm.password.length < 6 ||
        this.registerForm.password.length > 20
      ) {
        this.errorMessage = "密码长度应为6-20个字符";
        return false;
      }

      // 验证确认密码
      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        this.errorMessage = "两次输入的密码不一致";
        return false;
      }

      return true;
    },

    async handleRegister() {
      if (!this.validateForm()) {
        return;
      }

      this.loading = true;
      this.errorMessage = "";

      try {
        // 调用注册API
        const response = await register({
          username: this.registerForm.username.trim(),
          name:
            this.registerForm.name.trim() || this.registerForm.username.trim(),
          password: this.registerForm.password,
          role: this.registerForm.role,
        });

        // 检查响应状态
        if (response && (response.code === 200 || response === "注册成功")) {
          // 注册成功，跳转到登录页
          this.$router.push({
            path: "/login",
            query: { registered: "true" },
          });
        } else {
          // 注册失败
          this.errorMessage = response?.message || "注册失败，请稍后再试";
        }
      } catch (error) {
        console.error("注册错误:", error);
        if (error.response && error.response.data) {
          this.errorMessage =
            error.response.data.message || "注册失败，请稍后再试";
        } else {
          this.errorMessage = error.message || "注册失败，请检查网络连接";
        }
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.register-box {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

h2 {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1.2rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #333;
}

.form-input {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.form-input:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.register-button {
  width: 100%;
  padding: 0.8rem;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-top: 0.5rem;
}

.register-button:hover:not(:disabled) {
  background-color: #40a9ff;
}

.register-button:disabled {
  background-color: #bae7ff;
  cursor: not-allowed;
}

.error-message {
  color: #ff4d4f;
  margin-bottom: 1rem;
  text-align: center;
}

.login-link {
  margin-top: 1.5rem;
  text-align: center;
  font-size: 0.9rem;
}

.login-link a {
  color: #1890ff;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
