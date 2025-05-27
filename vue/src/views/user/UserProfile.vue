<template>
  <div class="user-profile">
    <h2 class="page-title">个人资料</h2>

    <el-card class="profile-card">
      <div class="user-basic-info">
        <div class="avatar-container">
          <el-avatar
            :size="100"
            :src="userInfo.avatar || defaultAvatar"
          ></el-avatar>
          <el-button
            class="change-avatar-btn"
            type="primary"
            size="small"
            @click="handleChangeAvatar"
          >
            更换头像
          </el-button>
          <input
            ref="avatarUploadInput"
            type="file"
            accept="image/*"
            style="display: none"
            @change="handleAvatarUpload"
          />
        </div>

        <div class="user-info">
          <h3>{{ userInfo.name || userInfo.username }}</h3>
          <p>{{ getRoleName(userInfo.role) }}</p>
          <p v-if="userInfo.lastLoginTime">
            上次登录：{{ formatDate(userInfo.lastLoginTime) }}
          </p>
        </div>
      </div>
    </el-card>

    <el-card class="form-card">
      <template #header>
        <div class="card-header">
          <span>基本信息</span>
          <el-button
            type="primary"
            size="small"
            :icon="isEditing ? 'Check' : 'Edit'"
            @click="toggleEdit"
          >
            {{ isEditing ? "保存" : "编辑" }}
          </el-button>
        </div>
      </template>

      <el-form
        ref="profileForm"
        :model="profileData"
        :rules="profileRules"
        label-width="100px"
        :disabled="!isEditing"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="profileData.username" disabled></el-input>
        </el-form-item>

        <el-form-item label="真实姓名" prop="name">
          <el-input
            v-model="profileData.name"
            placeholder="请输入您的真实姓名"
          ></el-input>
        </el-form-item>

        <el-form-item label="电子邮箱" prop="email">
          <el-input
            v-model="profileData.email"
            placeholder="请输入您的电子邮箱"
          ></el-input>
        </el-form-item>

        <el-form-item label="联系电话" prop="phone">
          <el-input
            v-model="profileData.phone"
            placeholder="请输入您的联系电话"
          ></el-input>
        </el-form-item>

        <el-form-item label="角色">
          <el-input v-model="roleName" disabled></el-input>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="form-card">
      <template #header>
        <div class="card-header">
          <span>修改密码</span>
        </div>
      </template>

      <el-form
        ref="passwordForm"
        :model="passwordData"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="当前密码" prop="currentPassword">
          <el-input
            v-model="passwordData.currentPassword"
            type="password"
            placeholder="请输入当前密码"
            show-password
          ></el-input>
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordData.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          ></el-input>
        </el-form-item>

        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
            v-model="passwordData.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          ></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleChangePassword"
            >修改密码</el-button
          >
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from "vue";
import { useStore } from "vuex";
import { ElMessage, ElMessageBox } from "element-plus";
import { Edit, Check } from "@element-plus/icons-vue";

export default {
  name: "UserProfile",
  setup() {
    const store = useStore();
    const profileForm = ref(null);
    const passwordForm = ref(null);
    const avatarUploadInput = ref(null);
    const isEditing = ref(false);

    // 默认头像
    const defaultAvatar =
      "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png";

    // 获取用户信息
    const userInfo = computed(() => store.getters["user/userInfo"]);

    // 表单数据
    const profileData = reactive({
      username: "",
      name: "",
      email: "",
      phone: "",
      role: "",
    });

    const passwordData = reactive({
      currentPassword: "",
      newPassword: "",
      confirmPassword: "",
    });

    // 角色名称
    const roleName = computed(() => getRoleName(profileData.role));

    // 表单验证规则
    const profileRules = {
      name: [
        { required: true, message: "请输入您的真实姓名", trigger: "blur" },
        { min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur" },
      ],
      email: [
        { required: true, message: "请输入您的电子邮箱", trigger: "blur" },
        { type: "email", message: "请输入正确的邮箱地址", trigger: "blur" },
      ],
      phone: [
        {
          pattern: /^1[3-9]\d{9}$/,
          message: "请输入有效的手机号码",
          trigger: "blur",
        },
      ],
    };

    const passwordRules = {
      currentPassword: [
        { required: true, message: "请输入当前密码", trigger: "blur" },
        {
          min: 6,
          max: 20,
          message: "密码长度在 6 到 20 个字符",
          trigger: "blur",
        },
      ],
      newPassword: [
        { required: true, message: "请输入新密码", trigger: "blur" },
        {
          min: 6,
          max: 20,
          message: "密码长度在 6 到 20 个字符",
          trigger: "blur",
        },
      ],
      confirmPassword: [
        { required: true, message: "请再次输入新密码", trigger: "blur" },
        {
          validator: (rule, value, callback) => {
            if (value !== passwordData.newPassword) {
              callback(new Error("两次输入的密码不一致"));
            } else {
              callback();
            }
          },
          trigger: "blur",
        },
      ],
    };

    // 初始化用户数据
    const initUserData = () => {
      profileData.username = userInfo.value.username || "";
      profileData.name = userInfo.value.name || "";
      profileData.email = userInfo.value.email || "";
      profileData.phone = userInfo.value.phone || "";
      profileData.role = userInfo.value.role || "";
    };

    // 获取角色名称
    function getRoleName(role) {
      const roleMap = {
        admin: "管理员",
        teacher: "教师",
        student: "学生",
      };
      return roleMap[role] || "未知角色";
    }

    // 格式化日期
    function formatDate(dateString) {
      if (!dateString) return "";
      const date = new Date(dateString);
      return date.toLocaleString("zh-CN", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
      });
    }

    // 切换编辑状态
    const toggleEdit = async () => {
      if (isEditing.value) {
        // 保存信息
        await saveProfile();
      } else {
        // 进入编辑状态
        isEditing.value = true;
      }
    };

    // 保存个人资料
    const saveProfile = async () => {
      if (!profileForm.value) return;

      await profileForm.value.validate(async (valid) => {
        if (valid) {
          try {
            // 更新用户信息
            await store.dispatch("user/updateUserProfile", {
              name: profileData.name,
              email: profileData.email,
              phone: profileData.phone,
            });

            ElMessage.success("个人资料更新成功");
            isEditing.value = false;
          } catch (error) {
            console.error("更新个人资料失败:", error);
            ElMessage.error(
              "更新个人资料失败: " + (error.message || "未知错误")
            );
          }
        } else {
          return false;
        }
      });
    };

    // 修改密码
    const handleChangePassword = async () => {
      if (!passwordForm.value) return;

      await passwordForm.value.validate(async (valid) => {
        if (valid) {
          try {
            // 修改密码
            await store.dispatch("user/changePassword", {
              oldPassword: passwordData.currentPassword,
              newPassword: passwordData.newPassword,
            });

            ElMessage.success("密码修改成功");

            // 清空表单
            passwordData.currentPassword = "";
            passwordData.newPassword = "";
            passwordData.confirmPassword = "";
            passwordForm.value.resetFields();
          } catch (error) {
            console.error("修改密码失败:", error);
            ElMessage.error("修改密码失败: " + (error.message || "未知错误"));
          }
        } else {
          return false;
        }
      });
    };

    // 更换头像
    const handleChangeAvatar = () => {
      avatarUploadInput.value.click();
    };

    // 处理头像上传
    const handleAvatarUpload = async (event) => {
      const file = event.target.files[0];
      if (!file) return;

      // 验证文件类型
      const allowedTypes = ["image/jpeg", "image/png", "image/gif"];
      if (!allowedTypes.includes(file.type)) {
        ElMessage.error("只能上传 JPG/PNG/GIF 格式的图片");
        return;
      }

      // 验证文件大小 (2MB)
      const maxSize = 2 * 1024 * 1024;
      if (file.size > maxSize) {
        ElMessage.error("图片大小不能超过 2MB");
        return;
      }

      try {
        const formData = new FormData();
        formData.append("avatar", file);

        // 上传头像
        await store.dispatch("user/uploadAvatar", formData);

        ElMessage.success("头像更新成功");
      } catch (error) {
        console.error("上传头像失败:", error);
        ElMessage.error("上传头像失败: " + (error.message || "未知错误"));
      }
    };

    // 组件挂载时获取用户数据
    onMounted(() => {
      initUserData();
    });

    return {
      profileForm,
      passwordForm,
      avatarUploadInput,
      userInfo,
      profileData,
      passwordData,
      isEditing,
      roleName,
      defaultAvatar,
      profileRules,
      passwordRules,
      getRoleName,
      formatDate,
      toggleEdit,
      saveProfile,
      handleChangePassword,
      handleChangeAvatar,
      handleAvatarUpload,
    };
  },
};
</script>

<style scoped>
.user-profile {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  color: #333;
}

.profile-card {
  margin-bottom: 20px;
}

.form-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-basic-info {
  display: flex;
  align-items: center;
  padding: 20px;
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 30px;
}

.change-avatar-btn {
  margin-top: 10px;
}

.user-info {
  flex: 1;
}

.user-info h3 {
  margin: 0 0 10px 0;
  font-size: 20px;
  color: #333;
}

.user-info p {
  margin: 5px 0;
  color: #666;
}

@media (max-width: 768px) {
  .user-basic-info {
    flex-direction: column;
    text-align: center;
  }

  .avatar-container {
    margin-right: 0;
    margin-bottom: 20px;
  }
}
</style>
