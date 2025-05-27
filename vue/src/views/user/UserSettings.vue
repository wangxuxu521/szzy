<template>
  <div class="user-settings">
    <h2 class="page-title">账号设置</h2>

    <el-tabs v-model="activeTab" class="settings-tabs">
      <el-tab-pane label="系统设置" name="system">
        <el-card class="settings-card">
          <h3>界面设置</h3>

          <el-form label-width="120px">
            <el-form-item label="界面语言">
              <el-select v-model="settings.language" placeholder="选择语言">
                <el-option label="简体中文" value="zh-CN" />
                <el-option label="English" value="en-US" disabled />
              </el-select>
            </el-form-item>

            <el-form-item label="主题模式">
              <el-radio-group v-model="settings.theme">
                <el-radio label="light">浅色模式</el-radio>
                <el-radio label="dark">深色模式</el-radio>
                <el-radio label="auto">跟随系统</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="列表显示数量">
              <el-slider
                v-model="settings.pageSize"
                :min="5"
                :max="50"
                :step="5"
                show-stops
                show-input
              />
            </el-form-item>
          </el-form>

          <el-divider />

          <h3>个性化设置</h3>

          <el-form label-width="120px">
            <el-form-item label="起始页面">
              <el-select
                v-model="settings.startPage"
                placeholder="选择起始页面"
              >
                <el-option label="首页" value="home" />
                <el-option label="资源库" value="resources" />
                <el-option label="个人中心" value="profile" />
              </el-select>
            </el-form-item>
          </el-form>

          <div class="form-actions">
            <el-button type="primary" @click="saveSystemSettings"
              >保存设置</el-button
            >
            <el-button @click="resetSystemSettings">恢复默认</el-button>
          </div>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="通知设置" name="notification">
        <el-card class="settings-card">
          <h3>通知接收设置</h3>

          <el-form label-width="0">
            <el-form-item>
              <el-checkbox v-model="notifications.emailNotification"
                >接收邮件通知</el-checkbox
              >
            </el-form-item>

            <el-form-item v-if="notifications.emailNotification">
              <el-checkbox v-model="notifications.resourceUpdates"
                >资源更新通知</el-checkbox
              >
            </el-form-item>

            <el-form-item v-if="notifications.emailNotification">
              <el-checkbox v-model="notifications.resourceComments"
                >资源评论通知</el-checkbox
              >
            </el-form-item>

            <el-form-item v-if="notifications.emailNotification">
              <el-checkbox v-model="notifications.resourceReviews"
                >资源审核结果通知</el-checkbox
              >
            </el-form-item>

            <el-form-item v-if="notifications.emailNotification">
              <el-checkbox v-model="notifications.systemAnnouncements"
                >系统公告通知</el-checkbox
              >
            </el-form-item>
          </el-form>

          <el-divider />

          <h3>站内消息设置</h3>

          <el-form label-width="0">
            <el-form-item>
              <el-checkbox v-model="notifications.inboxMessages"
                >接收站内消息</el-checkbox
              >
            </el-form-item>

            <el-form-item v-if="notifications.inboxMessages">
              <el-checkbox v-model="notifications.autoRead"
                >自动标记已读</el-checkbox
              >
            </el-form-item>
          </el-form>

          <div class="form-actions">
            <el-button type="primary" @click="saveNotificationSettings"
              >保存设置</el-button
            >
            <el-button @click="resetNotificationSettings">恢复默认</el-button>
          </div>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="账号安全" name="security">
        <el-card class="settings-card">
          <h3>密码管理</h3>

          <el-form
            ref="passwordForm"
            :model="passwordData"
            :rules="passwordRules"
            label-width="120px"
          >
            <el-form-item label="当前密码" prop="currentPassword">
              <el-input
                v-model="passwordData.currentPassword"
                type="password"
                placeholder="请输入当前密码"
                show-password
              />
            </el-form-item>

            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordData.newPassword"
                type="password"
                placeholder="请输入新密码"
                show-password
              />
            </el-form-item>

            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input
                v-model="passwordData.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                show-password
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="changePassword"
                >修改密码</el-button
              >
            </el-form-item>
          </el-form>

          <el-divider />

          <h3>登录安全</h3>

          <el-form label-width="150px">
            <el-form-item label="登录设备管理">
              <el-button @click="showDevicesDialog">查看登录设备</el-button>
            </el-form-item>

            <el-form-item label="登录记录">
              <el-button @click="showLoginHistoryDialog"
                >查看登录记录</el-button
              >
            </el-form-item>

            <el-form-item label="注销账号">
              <el-button type="danger" @click="showDeleteAccountDialog"
                >注销账号</el-button
              >
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 登录设备对话框 -->
    <el-dialog v-model="deviceDialogVisible" title="登录设备管理" width="500px">
      <div class="devices-list">
        <el-empty v-if="devices.length === 0" description="暂无登录设备" />
        <div v-else>
          <div
            v-for="(device, index) in devices"
            :key="index"
            class="device-item"
          >
            <div class="device-info">
              <div class="device-icon">
                <el-icon>
                  <el-icon-monitor v-if="device.type === 'desktop'" />
                  <el-icon-mobile v-if="device.type === 'mobile'" />
                  <el-icon-tablet v-if="device.type === 'tablet'" />
                  <el-icon-more
                    v-if="
                      device.type !== 'desktop' &&
                      device.type !== 'mobile' &&
                      device.type !== 'tablet'
                    "
                  />
                </el-icon>
              </div>
              <div class="device-details">
                <h4>{{ device.name }}</h4>
                <p>{{ device.ip }} · {{ device.location }}</p>
                <p>上次登录: {{ device.lastLogin }}</p>
              </div>
            </div>
            <el-button
              v-if="!device.current"
              type="danger"
              size="small"
              @click="logoutDevice(device.id)"
            >
              退出登录
            </el-button>
            <el-tag v-else type="success">当前设备</el-tag>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 登录记录对话框 -->
    <el-dialog
      v-model="loginHistoryDialogVisible"
      title="登录记录"
      width="600px"
    >
      <el-table :data="loginHistory" style="width: 100%">
        <el-table-column prop="time" label="登录时间" width="180" />
        <el-table-column prop="ip" label="IP地址" width="120" />
        <el-table-column prop="location" label="登录地点" />
        <el-table-column prop="device" label="设备信息" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === '成功' ? 'success' : 'danger'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 注销账号对话框 -->
    <el-dialog
      v-model="deleteAccountDialogVisible"
      title="注销账号"
      width="450px"
    >
      <div class="delete-account-content">
        <el-alert
          title="警告：此操作不可撤销"
          type="error"
          description="注销账号后，您的所有数据将被删除，且无法恢复。请慎重操作。"
          :closable="false"
          show-icon
        />

        <p class="warning-text">请输入您的密码确认此操作：</p>

        <el-input
          v-model="deleteAccountPassword"
          type="password"
          placeholder="请输入您的密码"
          show-password
        />
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteAccountDialogVisible = false"
            >取消</el-button
          >
          <el-button type="danger" @click="confirmDeleteAccount"
            >确认注销</el-button
          >
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Monitor as ElIconMonitor,
  Mobile as ElIconMobile,
  Tablet as ElIconTablet,
  More as ElIconMore,
} from "@element-plus/icons-vue";

export default {
  name: "UserSettings",
  components: {
    ElIconMonitor,
    ElIconMobile,
    ElIconTablet,
    ElIconMore,
  },
  setup() {
    const store = useStore();
    const router = useRouter();
    const passwordForm = ref(null);
    const activeTab = ref("system");

    // 系统设置
    const settings = reactive({
      language: "zh-CN",
      theme: "light",
      pageSize: 10,
      startPage: "home",
    });

    // 通知设置
    const notifications = reactive({
      emailNotification: true,
      resourceUpdates: true,
      resourceComments: true,
      resourceReviews: true,
      systemAnnouncements: true,
      inboxMessages: true,
      autoRead: false,
    });

    // 密码表单
    const passwordData = reactive({
      currentPassword: "",
      newPassword: "",
      confirmPassword: "",
    });

    // 密码验证规则
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

    // 对话框状态
    const deviceDialogVisible = ref(false);
    const loginHistoryDialogVisible = ref(false);
    const deleteAccountDialogVisible = ref(false);
    const deleteAccountPassword = ref("");

    // 登录设备列表
    const devices = ref([
      {
        id: 1,
        name: "Chrome Windows 10",
        type: "desktop",
        ip: "192.168.1.1",
        location: "中国 北京",
        lastLogin: "2024-05-27 10:30:45",
        current: true,
      },
      {
        id: 2,
        name: "Safari iPhone",
        type: "mobile",
        ip: "192.168.1.2",
        location: "中国 上海",
        lastLogin: "2024-05-25 16:22:10",
        current: false,
      },
    ]);

    // 登录记录
    const loginHistory = ref([
      {
        time: "2024-05-27 10:30:45",
        ip: "192.168.1.1",
        location: "中国 北京",
        device: "Chrome Windows 10",
        status: "成功",
      },
      {
        time: "2024-05-25 16:22:10",
        ip: "192.168.1.2",
        location: "中国 上海",
        device: "Safari iPhone",
        status: "成功",
      },
      {
        time: "2024-05-24 08:15:30",
        ip: "192.168.1.3",
        location: "中国 广州",
        device: "Firefox Windows 10",
        status: "失败",
      },
    ]);

    // 加载用户设置
    const loadUserSettings = () => {
      try {
        // 尝试从本地存储加载设置
        const savedSettings = localStorage.getItem("user_settings");
        if (savedSettings) {
          const parsedSettings = JSON.parse(savedSettings);
          Object.assign(settings, parsedSettings);
        }

        const savedNotifications = localStorage.getItem("user_notifications");
        if (savedNotifications) {
          const parsedNotifications = JSON.parse(savedNotifications);
          Object.assign(notifications, parsedNotifications);
        }

        // 应用主题设置
        applyTheme(settings.theme);
      } catch (error) {
        console.error("加载用户设置失败:", error);
      }
    };

    // 保存系统设置
    const saveSystemSettings = () => {
      try {
        localStorage.setItem("user_settings", JSON.stringify(settings));
        applyTheme(settings.theme);
        ElMessage.success("系统设置已保存");
      } catch (error) {
        console.error("保存系统设置失败:", error);
        ElMessage.error("保存设置失败");
      }
    };

    // 重置系统设置
    const resetSystemSettings = () => {
      settings.language = "zh-CN";
      settings.theme = "light";
      settings.pageSize = 10;
      settings.startPage = "home";
      saveSystemSettings();
    };

    // 保存通知设置
    const saveNotificationSettings = () => {
      try {
        localStorage.setItem(
          "user_notifications",
          JSON.stringify(notifications)
        );
        ElMessage.success("通知设置已保存");
      } catch (error) {
        console.error("保存通知设置失败:", error);
        ElMessage.error("保存设置失败");
      }
    };

    // 重置通知设置
    const resetNotificationSettings = () => {
      notifications.emailNotification = true;
      notifications.resourceUpdates = true;
      notifications.resourceComments = true;
      notifications.resourceReviews = true;
      notifications.systemAnnouncements = true;
      notifications.inboxMessages = true;
      notifications.autoRead = false;
      saveNotificationSettings();
    };

    // 应用主题
    const applyTheme = (theme) => {
      const htmlEl = document.documentElement;

      if (theme === "dark") {
        htmlEl.classList.add("dark-theme");
      } else if (theme === "light") {
        htmlEl.classList.remove("dark-theme");
      } else if (theme === "auto") {
        // 跟随系统
        const prefersDark = window.matchMedia(
          "(prefers-color-scheme: dark)"
        ).matches;
        if (prefersDark) {
          htmlEl.classList.add("dark-theme");
        } else {
          htmlEl.classList.remove("dark-theme");
        }
      }
    };

    // 修改密码
    const changePassword = async () => {
      if (!passwordForm.value) return;

      await passwordForm.value.validate(async (valid) => {
        if (valid) {
          try {
            // 调用修改密码API
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
        }
      });
    };

    // 显示登录设备对话框
    const showDevicesDialog = () => {
      deviceDialogVisible.value = true;
    };

    // 显示登录记录对话框
    const showLoginHistoryDialog = () => {
      loginHistoryDialogVisible.value = true;
    };

    // 显示注销账号对话框
    const showDeleteAccountDialog = () => {
      deleteAccountDialogVisible.value = true;
      deleteAccountPassword.value = "";
    };

    // 退出设备登录
    const logoutDevice = async (deviceId) => {
      try {
        // 调用退出设备API
        ElMessage.success("已退出该设备的登录");

        // 更新设备列表
        devices.value = devices.value.filter(
          (device) => device.id !== deviceId
        );
      } catch (error) {
        console.error("退出设备登录失败:", error);
        ElMessage.error("操作失败，请稍后重试");
      }
    };

    // 确认注销账号
    const confirmDeleteAccount = async () => {
      if (!deleteAccountPassword.value) {
        ElMessage.warning("请输入密码确认操作");
        return;
      }

      try {
        // 调用注销账号API
        ElMessageBox.confirm(
          "注销账号是不可逆操作，您的所有数据将被删除。确定要继续吗？",
          "最终确认",
          {
            confirmButtonText: "确认注销",
            cancelButtonText: "取消",
            type: "warning",
          }
        )
          .then(async () => {
            try {
              // 假设这里是实际的API调用
              // await store.dispatch('user/deleteAccount', { password: deleteAccountPassword.value });

              ElMessage.success("账号已注销");
              deleteAccountDialogVisible.value = false;

              // 清除登录信息并返回登录页
              await store.dispatch("user/logout");
              router.push("/login");
            } catch (error) {
              console.error("注销账号失败:", error);
              ElMessage.error("注销账号失败: " + (error.message || "未知错误"));
            }
          })
          .catch(() => {
            // 用户取消操作
          });
      } catch (error) {
        console.error("注销账号失败:", error);
        ElMessage.error("操作失败，请稍后重试");
      }
    };

    // 组件挂载时加载用户设置
    onMounted(() => {
      loadUserSettings();
    });

    return {
      activeTab,
      settings,
      notifications,
      passwordData,
      passwordRules,
      passwordForm,
      deviceDialogVisible,
      loginHistoryDialogVisible,
      deleteAccountDialogVisible,
      deleteAccountPassword,
      devices,
      loginHistory,
      saveSystemSettings,
      resetSystemSettings,
      saveNotificationSettings,
      resetNotificationSettings,
      changePassword,
      showDevicesDialog,
      showLoginHistoryDialog,
      showDeleteAccountDialog,
      logoutDevice,
      confirmDeleteAccount,
    };
  },
};
</script>

<style scoped>
.user-settings {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  color: #333;
}

.settings-tabs {
  margin-top: 20px;
}

.settings-card {
  margin-bottom: 20px;
}

.settings-card h3 {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 18px;
  color: #333;
}

.form-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.devices-list {
  max-height: 400px;
  overflow-y: auto;
}

.device-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.device-item:last-child {
  border-bottom: none;
}

.device-info {
  display: flex;
  align-items: center;
}

.device-icon {
  font-size: 24px;
  color: #409eff;
  margin-right: 15px;
}

.device-details h4 {
  margin: 0 0 5px 0;
  font-size: 16px;
}

.device-details p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.warning-text {
  margin: 20px 0 10px;
  color: #f56c6c;
  font-weight: bold;
}

.delete-account-content {
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .user-settings {
    padding: 10px;
  }
}
</style>
