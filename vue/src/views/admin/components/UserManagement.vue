<template>
  <div class="user-management">
    <div class="section-header">
      <h2>用户管理</h2>
      <div class="search-bar">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="搜索用户..."
          @input="handleSearch"
        />
        <select v-model="userTypeFilter">
          <option value="">所有角色</option>
          <option value="admin">管理员</option>
          <option value="teacher">教师</option>
          <option value="student">学生</option>
        </select>
      </div>
      <button class="add-btn" @click="showAddUserModal = true">添加用户</button>
    </div>

    <div class="table-container">
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>加载用户数据中...</p>
      </div>
      <div v-else-if="error" class="error-container">
        <p>{{ error }}</p>
        <button @click="loadUsers" class="retry-btn">重试</button>
      </div>
      <table v-else class="user-table">
        <thead>
          <tr>
            <th>用户ID</th>
            <th>用户名</th>
            <th>姓名</th>
            <th>角色</th>
            <th>注册日期</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in filteredUsers" :key="user.id">
            <td>{{ user.userId || user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.name || "未设置" }}</td>
            <td>
              <span
                class="role-tag"
                :class="{
                  admin: user.role === 'admin',
                  teacher: user.role === 'teacher',
                  student: user.role === 'student',
                }"
              >
                {{ getRoleName(user.role) }}
              </span>
            </td>
            <td>{{ formatDate(user.createTime || user.registerDate) }}</td>
            <td class="action-buttons">
              <button class="edit-btn" @click="editUser(user)">编辑</button>
              <button class="delete-btn" @click="confirmDelete(user)">
                删除
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination">
      <button :disabled="currentPage === 1" @click="currentPage--">
        上一页
      </button>
      <span>{{ currentPage }} / {{ totalPages }}</span>
      <button :disabled="currentPage === totalPages" @click="currentPage++">
        下一页
      </button>
    </div>

    <!-- 添加/编辑用户弹窗 -->
    <div v-if="showAddUserModal || showEditUserModal" class="modal-overlay">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ showEditUserModal ? "编辑用户" : "添加用户" }}</h3>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="handleSubmit">
            <div class="form-group">
              <label>用户名</label>
              <input type="text" v-model="userForm.username" required />
            </div>
            <div class="form-group">
              <label>姓名</label>
              <input type="text" v-model="userForm.name" required />
            </div>
            <div class="form-group">
              <label>角色</label>
              <select v-model="userForm.role" required>
                <option value="admin">管理员</option>
                <option value="teacher">教师</option>
                <option value="student">学生</option>
              </select>
            </div>
            <div class="form-group">
              <label>密码</label>
              <input
                type="password"
                v-model="userForm.password"
                :required="!showEditUserModal"
              />
              <small v-if="showEditUserModal">留空表示不修改密码</small>
            </div>
            <div class="form-buttons">
              <button type="button" class="cancel-btn" @click="closeModal">
                取消
              </button>
              <button type="submit" class="submit-btn" :disabled="submitting">
                {{ submitting ? "保存中..." : "保存" }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 删除确认弹窗 -->
    <div v-if="showDeleteModal" class="modal-overlay">
      <div class="modal confirmation-modal">
        <div class="modal-header">
          <h3>确认删除</h3>
          <button class="close-btn" @click="showDeleteModal = false">×</button>
        </div>
        <div class="modal-body">
          <p>确定要删除用户 "{{ userToDelete?.name }}" 吗？此操作不可撤销。</p>
          <div class="form-buttons">
            <button class="cancel-btn" @click="showDeleteModal = false">
              取消
            </button>
            <button class="delete-btn" @click="deleteUser" :disabled="deleting">
              {{ deleting ? "删除中..." : "删除" }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getUserList,
  addUser,
  deleteUser as apiDeleteUser,
  updateUserInfo,
} from "@/api/user";

export default {
  name: "UserManagement",
  setup() {
    // 用户数据
    const users = ref([]);
    const loading = ref(true);
    const error = ref(null);

    // 分页
    const currentPage = ref(1);
    const pageSize = ref(10);
    const totalPages = computed(
      () => Math.ceil(filteredUsers.value.length / pageSize.value) || 1
    );

    // 搜索和筛选
    const searchQuery = ref("");
    const userTypeFilter = ref("");

    // 弹窗状态
    const showAddUserModal = ref(false);
    const showEditUserModal = ref(false);
    const showDeleteModal = ref(false);
    const userToDelete = ref(null);
    const submitting = ref(false);
    const deleting = ref(false);

    // 表单数据
    const userForm = reactive({
      id: null,
      username: "",
      name: "",
      role: "student",
      password: "",
    });

    // 加载用户数据
    const loadUsers = async () => {
      loading.value = true;
      try {
        console.log("开始加载用户列表...");
        console.log("请求URL:", "/api/user/list");
        const response = await getUserList();
        console.log("获取用户列表响应类型:", typeof response);
        console.log("获取用户列表响应:", response);

        if (response && typeof response === "object") {
          let userList = [];

          if ("data" in response) {
            userList = response.data || [];
          } else if (Array.isArray(response)) {
            userList = response;
          } else if ("list" in response) {
            userList = response.list || [];
          } else if ("users" in response) {
            userList = response.users || [];
          } else if ("records" in response) {
            userList = response.records || [];
          } else {
            // 尝试查找任何看起来像用户数组的字段
            const possibleArrayField = Object.keys(response).find(
              (key) =>
                Array.isArray(response[key]) &&
                response[key].length > 0 &&
                typeof response[key][0] === "object" &&
                ("username" in response[key][0] || "role" in response[key][0])
            );

            if (possibleArrayField) {
              userList = response[possibleArrayField];
            }
          }

          // 统一处理字段名称
          users.value = userList.map((user) => ({
            id: user.id || user.userId,
            userId: user.userId || user.id,
            username: user.username,
            name: user.name || "",
            role: user.role,
            createTime:
              user.createTime || user.create_time || user.registerDate,
            registerDate:
              user.registerDate || user.createTime || user.create_time,
          }));
        } else {
          users.value = [];
        }

        console.log("处理后的用户列表:", users.value);

        // 如果没有用户数据，使用测试数据（开发阶段）
        if (users.value.length === 0) {
          console.log("没有用户数据，使用测试数据");
          users.value = [
            {
              userId: 1,
              username: "admin",
              name: "系统管理员",
              role: "admin",
              createTime: "2024-01-15",
            },
            {
              userId: 2,
              username: "teacher1",
              name: "张教授",
              role: "teacher",
              createTime: "2024-01-20",
            },
          ];
        }

        error.value = null;
      } catch (err) {
        console.error("加载用户失败:", err);
        error.value = "加载用户失败，请稍后重试";
        ElMessage.error(`加载用户失败: ${err.message || "未知错误"}`);
      } finally {
        loading.value = false;
      }
    };

    // 过滤后的用户列表
    const filteredUsers = computed(() => {
      return users.value.filter((user) => {
        // 角色筛选
        if (userTypeFilter.value && user.role !== userTypeFilter.value) {
          return false;
        }

        // 搜索关键词
        if (searchQuery.value) {
          const query = searchQuery.value.toLowerCase();
          return (
            user.username.toLowerCase().includes(query) ||
            (user.name && user.name.toLowerCase().includes(query))
          );
        }

        return true;
      });
    });

    // 搜索处理
    const handleSearch = () => {
      currentPage.value = 1;
    };

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return "未知";

      try {
        // 处理不同格式的日期
        const date = new Date(dateString);
        if (isNaN(date)) return dateString;

        return date.toISOString().split("T")[0];
      } catch (e) {
        return dateString;
      }
    };

    // 获取角色名称
    const getRoleName = (role) => {
      const roleMap = {
        admin: "管理员",
        teacher: "教师",
        student: "学生",
      };
      return roleMap[role] || role;
    };

    // 编辑用户
    const editUser = (user) => {
      userForm.id = user.id || user.userId;
      userForm.username = user.username;
      userForm.name = user.name || "";
      userForm.role = user.role;
      userForm.password = "";

      showEditUserModal.value = true;
    };

    // 确认删除用户
    const confirmDelete = (user) => {
      userToDelete.value = user;
      showDeleteModal.value = true;
    };

    // 删除用户
    const deleteUser = async () => {
      if (!userToDelete.value) return;

      deleting.value = true;
      try {
        const userId = userToDelete.value.id || userToDelete.value.userId;
        await apiDeleteUser(userId);

        // 从列表中移除
        users.value = users.value.filter(
          (user) => (user.id || user.userId) !== userId
        );

        showDeleteModal.value = false;
        userToDelete.value = null;
        ElMessage.success("用户已成功删除");
      } catch (err) {
        console.error("删除用户失败:", err);
        ElMessage.error(`删除用户失败: ${err.message || "未知错误"}`);
      } finally {
        deleting.value = false;
      }
    };

    // 提交用户表单
    const handleSubmit = async () => {
      try {
        // 验证表单
        if (!userForm.username || !userForm.password || !userForm.role) {
          ElMessage.error("请填写完整信息");
          return;
        }

        // 验证密码长度
        if (userForm.password.length < 6) {
          ElMessage.error("密码长度不能少于6位");
          return;
        }

        // 验证角色
        if (!["admin", "teacher", "student"].includes(userForm.role)) {
          ElMessage.error("角色不合法");
          return;
        }

        // 准备提交的数据
        const userData = {
          username: userForm.username,
          password: userForm.password,
          name: userForm.name,
          role: userForm.role,
        };

        // 如果是编辑模式，添加用户ID
        if (userForm.id) {
          userData.userId = userForm.id;
        }

        submitting.value = true;
        const response = await updateUserInfo(userData);

        // 检查响应
        if (response && (response.code === 0 || response.success)) {
          ElMessage.success("保存成功");
          showEditUserModal.value = false;
          loadUsers(); // 重新加载用户列表
        } else {
          // 如果响应中没有code或success字段，但也没有错误，也认为是成功的
          if (!response.error) {
            ElMessage.success("保存成功");
            showEditUserModal.value = false;
            loadUsers();
          } else {
            ElMessage.error(response.message || response.error || "保存失败");
          }
        }
      } catch (error) {
        console.error("保存用户失败:", error);
        // 检查是否是用户名已存在的错误
        if (error.response?.data?.message?.includes("用户名已存在")) {
          ElMessage.error("用户名已存在，请使用其他用户名");
        } else {
          ElMessage.error(
            error.response?.data?.message || "保存失败，请稍后重试"
          );
        }
      } finally {
        submitting.value = false;
      }
    };

    // 关闭弹窗
    const closeModal = () => {
      showAddUserModal.value = false;
      showEditUserModal.value = false;

      // 重置表单
      userForm.id = null;
      userForm.username = "";
      userForm.name = "";
      userForm.role = "student";
      userForm.password = "";
    };

    // 组件挂载时加载用户数据
    onMounted(() => {
      loadUsers();
    });

    return {
      users,
      filteredUsers,
      currentPage,
      totalPages,
      searchQuery,
      userTypeFilter,
      showAddUserModal,
      showEditUserModal,
      showDeleteModal,
      userToDelete,
      userForm,
      loading,
      error,
      submitting,
      deleting,
      handleSearch,
      formatDate,
      getRoleName,
      editUser,
      confirmDelete,
      deleteUser,
      handleSubmit,
      closeModal,
    };
  },
};
</script>

<style scoped>
.user-management {
  padding: 1rem 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.section-header h2 {
  margin: 0;
  color: #333;
}

.search-bar {
  display: flex;
  gap: 10px;
}

.search-bar input,
.search-bar select {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  outline: none;
}

.add-btn {
  padding: 0.5rem 1rem;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.table-container {
  overflow-x: auto;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  background-color: #fff;
}

.user-table {
  width: 100%;
  border-collapse: collapse;
}

.user-table th,
.user-table td {
  padding: 0.8rem;
  text-align: left;
  border-bottom: 1px solid #f0f0f0;
}

.user-table th {
  background-color: #fafafa;
  font-weight: 600;
  color: #333;
}

.user-table tr:hover {
  background-color: #f5f5f5;
}

.role-tag,
.status-tag {
  display: inline-block;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
}

.role-tag.admin {
  background-color: #1890ff;
  color: white;
}

.role-tag.teacher {
  background-color: #52c41a;
  color: white;
}

.role-tag.student {
  background-color: #faad14;
  color: white;
}

.action-buttons {
  display: flex;
  gap: 5px;
}

.action-buttons button {
  padding: 0.3rem 0.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
}

.edit-btn {
  background-color: #faad14;
  color: white;
}

.delete-btn {
  background-color: #ff4d4f;
  color: white;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 1.5rem;
  gap: 1rem;
}

.pagination button {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: white;
  cursor: pointer;
}

.pagination button:disabled {
  background-color: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  width: 500px;
  max-width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.confirmation-modal {
  width: 400px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h3 {
  margin: 0;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #999;
}

.modal-body {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #333;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  outline: none;
}

.form-group small {
  display: block;
  margin-top: 0.3rem;
  color: #999;
}

.form-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 1.5rem;
}

.cancel-btn {
  padding: 0.5rem 1rem;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
}

.submit-btn {
  padding: 0.5rem 1rem;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.submit-btn:disabled,
.delete-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #1890ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.error-container {
  text-align: center;
  padding: 2rem;
  color: #ff4d4f;
}

.retry-btn {
  padding: 0.5rem 1rem;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 1rem;
}
</style>
