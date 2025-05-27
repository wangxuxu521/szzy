<script>
import { login } from "@/api/user";
import { mapActions } from "vuex";

export default {
  name: "Login",
  data() {
    return {
      loginForm: {
        username: "",
        password: "",
      },
      loading: false,
      errorMessage: "",
      registrationSuccess: false,
    };
  },
  created() {
    // 检查是否是注册成功后跳转而来
    if (this.$route.query.registered === "true") {
      this.registrationSuccess = true;
    }
  },
  methods: {
    ...mapActions("user", ["login"]),

    async handleLogin() {
      if (!this.loginForm.username || !this.loginForm.password) {
        this.errorMessage = "请输入用户名和密码";
        return;
      }

      // 去除用户名和密码中的空白字符
      const loginData = {
        username: this.loginForm.username.trim(),
        password: this.loginForm.password.trim(),
      };

      this.loading = true;
      this.errorMessage = "";
      this.registrationSuccess = false;

      try {
        // 调用Vuex的login action
        await this.login(loginData);

        // 登录成功后跳转到首页
        this.$router.push("/");
      } catch (error) {
        console.error("登录错误:", error);

        // 添加更详细的错误信息
        if (error.response) {
          console.log("错误状态:", error.response.status);
          console.log("错误数据:", error.response.data);

          if (error.response.data && typeof error.response.data === "string") {
            this.errorMessage = error.response.data;
          } else if (error.response.data && error.response.data.message) {
            this.errorMessage = error.response.data.message;
          } else {
            this.errorMessage = `登录失败 (${error.response.status})`;
          }
        } else if (error.request) {
          console.log("无响应错误:", error.request);
          this.errorMessage = "服务器未响应，请检查网络连接";
        } else if (error.message) {
          this.errorMessage = error.message;
        } else {
          this.errorMessage = "登录失败，请稍后重试";
        }
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      <h2>思政教学资源管理系统</h2>
      <div v-if="registrationSuccess" class="success-message">
        注册成功！请使用您的账号登录系统。
      </div>
      <div class="form-group">
        <label for="username">用户名</label>
        <input
          id="username"
          type="text"
          v-model="loginForm.username"
          placeholder="请输入用户名"
          class="form-input"
          @keyup.enter="handleLogin"
        />
      </div>
      <div class="form-group">
        <label for="password">密码</label>
        <input
          id="password"
          type="password"
          v-model="loginForm.password"
          placeholder="请输入密码"
          class="form-input"
          @keyup.enter="handleLogin"
        />
      </div>
      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>
      <button @click="handleLogin" class="login-button" :disabled="loading">
        {{ loading ? "登录中..." : "登录" }}
      </button>
      <div class="register-link">
        没有账号？<router-link to="/register">去注册</router-link>
      </div>
      <div class="login-tips">
        <p>测试账号：</p>
        <p>管理员: admin_user / 123456</p>
        <p>教师: teacher_user / 123456</p>
        <p>学生: student_user / 123456</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.login-box {
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
  margin-bottom: 2rem;
}

.form-group {
  margin-bottom: 1.5rem;
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

.login-button {
  width: 100%;
  padding: 0.8rem;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s;
}

.login-button:hover:not(:disabled) {
  background-color: #40a9ff;
}

.login-button:disabled {
  background-color: #bae7ff;
  cursor: not-allowed;
}

.error-message {
  color: #ff4d4f;
  margin-bottom: 1rem;
  text-align: center;
}

.success-message {
  color: #52c41a;
  margin-bottom: 1rem;
  text-align: center;
  padding: 8px;
  background-color: #f6ffed;
  border: 1px solid #b7eb8f;
  border-radius: 4px;
}

.register-link {
  margin-top: 1.5rem;
  text-align: center;
  font-size: 0.9rem;
}

.register-link a {
  color: #1890ff;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}

.login-tips {
  margin-top: 1.5rem;
  font-size: 0.8rem;
  color: #999;
}

.login-tips p {
  margin: 0.2rem 0;
}
</style>
