<template>
  <div>
    <AppHeader />
    <div class="profile-settings">
      <div class="page-header">
        <h1>个人资料设置</h1>
      </div>

      <el-card class="settings-card">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本信息" name="basic">
            <el-form
              ref="basicFormRef"
              :model="basicForm"
              :rules="basicRules"
              label-width="100px"
              class="basic-form"
            >
              <el-form-item label="用户名">
                <el-input v-model="basicForm.username" disabled />
                <div class="form-tip">用户名不可修改</div>
              </el-form-item>
              <el-form-item label="姓名" prop="name">
                <el-input
                  v-model="basicForm.name"
                  placeholder="请输入您的姓名"
                />
              </el-form-item>
              <el-form-item label="所属院系" prop="department">
                <el-input
                  v-model="basicForm.department"
                  placeholder="请输入您的所属院系"
                />
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input
                  v-model="basicForm.email"
                  placeholder="请输入您的邮箱"
                />
              </el-form-item>
              <el-form-item label="头像">
                <div class="avatar-wrapper">
                  <div class="avatar-preview">
                    <div class="avatar">{{ getAvatarInitials() }}</div>
                  </div>
                  <div class="avatar-tip">
                    <p>目前使用的是基于姓名的自动生成头像</p>
                  </div>
                </div>
              </el-form-item>
              <el-form-item>
                <el-button
                  type="primary"
                  @click="saveBasicInfo"
                  :loading="savingBasic"
                  >保存信息</el-button
                >
              </el-form-item>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="修改密码" name="password">
            <el-form
              ref="passwordFormRef"
              :model="passwordForm"
              :rules="passwordRules"
              label-width="120px"
              class="password-form"
            >
              <el-form-item label="当前密码" prop="currentPassword">
                <el-input
                  v-model="passwordForm.currentPassword"
                  type="password"
                  placeholder="请输入当前密码"
                  show-password
                />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input
                  v-model="passwordForm.newPassword"
                  type="password"
                  placeholder="请输入新密码"
                  show-password
                />
                <div class="form-tip">
                  密码长度至少为6位，建议包含字母、数字和特殊字符
                </div>
              </el-form-item>
              <el-form-item label="确认新密码" prop="confirmPassword">
                <el-input
                  v-model="passwordForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入新密码"
                  show-password
                />
              </el-form-item>
              <el-form-item>
                <el-button
                  type="primary"
                  @click="savePassword"
                  :loading="savingPassword"
                  >修改密码</el-button
                >
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import { getTeacherInfo, updateTeacherInfo } from "@/api/teacher";
import { changePassword } from "@/api/user";
import AppHeader from "@/components/common/AppHeader.vue";

export default {
  name: "ProfileSettings",
  components: {
    AppHeader,
  },
  setup() {
    // 数据
    const activeTab = ref("basic");

    // 基本信息表单
    const basicForm = ref({
      userId: null,
      username: "",
      name: "",
      department: "",
      email: "",
    });

    const basicRules = {
      name: [{ required: true, message: "请输入姓名", trigger: "blur" }],
      email: [
        { required: true, message: "请输入邮箱地址", trigger: "blur" },
        { type: "email", message: "请输入正确的邮箱地址", trigger: "blur" },
      ],
    };

    const basicFormRef = ref(null);
    const savingBasic = ref(false);

    // 密码表单
    const passwordForm = ref({
      currentPassword: "",
      newPassword: "",
      confirmPassword: "",
    });

    const passwordRules = {
      currentPassword: [
        { required: true, message: "请输入当前密码", trigger: "blur" },
      ],
      newPassword: [
        { required: true, message: "请输入新密码", trigger: "blur" },
        { min: 6, message: "密码长度至少为6位", trigger: "blur" },
      ],
      confirmPassword: [
        { required: true, message: "请再次输入新密码", trigger: "blur" },
        {
          validator: (rule, value, callback) => {
            if (value !== passwordForm.value.newPassword) {
              callback(new Error("两次输入的密码不一致"));
            } else {
              callback();
            }
          },
          trigger: "blur",
        },
      ],
    };

    const passwordFormRef = ref(null);
    const savingPassword = ref(false);

    // 获取教师信息
    const fetchTeacherInfo = async () => {
      try {
        const res = await getTeacherInfo();
        if (res.code === 200 && res.data) {
          basicForm.value = {
            userId: res.data.userId,
            username: res.data.username,
            name: res.data.name || "",
            department: res.data.department || "",
            email: res.data.email || "",
          };
        } else {
          ElMessage.error(res.message || "获取个人信息失败");
        }
      } catch (error) {
        console.error("获取教师信息失败:", error);
        ElMessage.error("获取个人信息失败，请稍后重试");
      }
    };

    // 保存基本信息
    const saveBasicInfo = async () => {
      if (!basicFormRef.value) return;

      await basicFormRef.value.validate(async (valid) => {
        if (!valid) return;

        savingBasic.value = true;

        try {
          const res = await updateTeacherInfo({
            userId: basicForm.value.userId,
            name: basicForm.value.name,
            department: basicForm.value.department,
            email: basicForm.value.email,
          });

          if (res.code === 200) {
            ElMessage.success("个人信息更新成功");
          } else {
            ElMessage.error(res.message || "更新失败");
          }
        } catch (error) {
          console.error("更新个人信息失败:", error);
          ElMessage.error("更新失败，请稍后重试");
        } finally {
          savingBasic.value = false;
        }
      });
    };

    // 修改密码
    const savePassword = async () => {
      if (!passwordFormRef.value) return;

      await passwordFormRef.value.validate(async (valid) => {
        if (!valid) return;

        savingPassword.value = true;

        try {
          const res = await changePassword({
            oldPassword: passwordForm.value.currentPassword,
            newPassword: passwordForm.value.newPassword,
          });

          if (res.code === 200) {
            ElMessage.success("密码修改成功");
            // 清空表单
            passwordForm.value = {
              currentPassword: "",
              newPassword: "",
              confirmPassword: "",
            };
            // 重置表单校验状态
            passwordFormRef.value.resetFields();
          } else {
            ElMessage.error(res.message || "密码修改失败");
          }
        } catch (error) {
          console.error("修改密码失败:", error);
          ElMessage.error("修改密码失败，请稍后重试");
        } finally {
          savingPassword.value = false;
        }
      });
    };

    // 获取头像首字母
    const getAvatarInitials = () => {
      if (!basicForm.value.name) return "?";
      return basicForm.value.name.charAt(0).toUpperCase();
    };

    onMounted(() => {
      fetchTeacherInfo();
    });

    return {
      activeTab,
      basicForm,
      basicRules,
      basicFormRef,
      savingBasic,
      passwordForm,
      passwordRules,
      passwordFormRef,
      savingPassword,
      saveBasicInfo,
      savePassword,
      getAvatarInitials,
    };
  },
};
</script>

<style scoped>
.profile-settings {
  padding: 1rem 0;
}

.page-header {
  margin-bottom: 2rem;
}

.page-header h1 {
  margin: 0;
  font-size: 1.8rem;
  color: #333;
}

.settings-card {
  margin-bottom: 2rem;
}

.basic-form,
.password-form {
  max-width: 600px;
  margin: 0 auto;
  padding: 1rem 0;
}

.form-tip {
  font-size: 0.8rem;
  color: #999;
  margin-top: 0.3rem;
}

.avatar-wrapper {
  display: flex;
  align-items: center;
}

.avatar-preview {
  margin-right: 1.5rem;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background-color: #1890ff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  font-weight: bold;
}

.avatar-tip {
  color: #666;
}

.avatar-tip p {
  margin: 0.3rem 0;
}
</style>
