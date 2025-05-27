<template>
  <div class="user-management">
    <div class="header">
      <h2>用户管理</h2>
      <el-button type="primary" @click="openDialog()">添加用户</el-button>
    </div>

    <div class="search-bar">
      <el-input
        v-model="searchQuery"
        placeholder="搜索用户..."
        clearable
        @keyup.enter="handleSearch"
        style="width: 300px; margin-right: 10px"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-select
        v-model="userTypeFilter"
        placeholder="所有角色"
        clearable
        style="width: 200px; margin-right: 10px"
        @change="handleSearch"
      >
        <el-option label="管理员" value="admin" />
        <el-option label="教师" value="teacher" />
        <el-option label="学生" value="student" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <el-table :data="pagedUsers" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="用户ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="name" label="姓名">
        <template #default="scope">
          {{ scope.row.name || "未设置" }}
        </template>
      </el-table-column>
      <el-table-column prop="role" label="角色" width="120">
        <template #default="scope">
          <el-tag :type="getRoleTagType(scope.row.role)">
            {{ getRoleName(scope.row.role) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册日期" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="openDialog(scope.row)"
            >编辑</el-button
          >
          <el-popconfirm
            title="确定删除该用户吗？"
            @confirm="handleDelete(scope.row.id)"
          >
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 添加/编辑用户弹窗 -->
    <el-dialog
      :title="form.id ? '编辑用户' : '添加用户'"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            :disabled="!!form.id"
          />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select
            v-model="form.role"
            placeholder="请选择角色"
            style="width: 100%"
          >
            <el-option label="管理员" value="admin" />
            <el-option label="教师" value="teacher" />
            <el-option label="学生" value="student" />
          </el-select>
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!form.id">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="password" v-else>
          <el-input
            v-model="form.password"
                type="password"
            placeholder="留空表示不修改密码"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave" :loading="saveLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from "vue";
import { ElMessage } from "element-plus";
import { Search } from "@element-plus/icons-vue";
import { apiGet, apiPost, apiPut, apiDelete } from "@/api/request";
import { formatDate } from "@/utils/format";

// 数据
    const users = ref([]);
const loading = ref(false);
const dialogVisible = ref(false);
const saveLoading = ref(false);
const formRef = ref(null);
    const currentPage = ref(1);
    const pageSize = ref(10);
const total = ref(0);
    const searchQuery = ref("");
    const userTypeFilter = ref("");

    // 表单数据
const form = reactive({
      id: null,
      username: "",
      name: "",
      role: "student",
      password: "",
    });

// 表单验证规则
const rules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 3, max: 50, message: "长度在 3 到 50 个字符", trigger: "blur" },
  ],
  name: [
    { required: true, message: "请输入姓名", trigger: "blur" },
    { min: 2, max: 50, message: "长度在 2 到 50 个字符", trigger: "blur" },
  ],
  role: [{ required: true, message: "请选择角色", trigger: "change" }],
  password: [
    {
      required: true,
      message: "请输入密码",
      trigger: "blur",
      validator: (rule, value, callback) => {
        if (!form.id && !value) {
          callback(new Error("请输入密码"));
        } else if (value && value.length < 6) {
          callback(new Error("密码长度不能少于6位"));
          } else {
          callback();
        }
      },
    },
  ],
};

// 过滤后的用户列表
const filteredUsers = computed(() => {
  let result = [...users.value];

  if (searchQuery.value) {
    const keyword = searchQuery.value.toLowerCase();
    result = result.filter(
      (item) =>
        item.username.toLowerCase().includes(keyword) ||
        (item.name && item.name.toLowerCase().includes(keyword))
    );
  }

  if (userTypeFilter.value) {
    result = result.filter((item) => item.role === userTypeFilter.value);
  }

  total.value = result.length;
  return result;
});

// 分页后的用户列表
const pagedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredUsers.value.slice(start, end);
});

// 生命周期
onMounted(() => {
  fetchUsers();
});

// 获取所有用户
const fetchUsers = async () => {
  loading.value = true;
  try {
    const res = await apiGet("/user/list");
    if (res.code === 200) {
      users.value = res.data.map((user) => ({
        id: user.userId || user.id,
            username: user.username,
            name: user.name || "",
            role: user.role,
        createTime: user.createTime || user.registerDate,
          }));
        } else {
      ElMessage.error(res.msg || "获取用户列表失败");
    }
  } catch (error) {
    console.error("获取用户列表出错:", error);
    ElMessage.error("获取用户列表失败");
      } finally {
        loading.value = false;
      }
    };

// 打开弹窗
const openDialog = (row) => {
  formRef.value?.resetFields();

  if (row) {
    // 编辑模式，填充表单
    form.id = row.id;
    form.username = row.username;
    form.name = row.name;
    form.role = row.role;
    form.password = "";
  } else {
    // 添加模式，重置表单
    form.id = null;
    form.username = "";
    form.name = "";
    form.role = "student";
    form.password = "";
  }

  dialogVisible.value = true;
};

// 保存用户
const handleSave = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (!valid) return;

    saveLoading.value = true;
    try {
      const userData = {
        username: form.username,
        name: form.name,
        role: form.role,
      };

      if (form.password) {
        userData.password = form.password;
      }

      const res = await (form.id
        ? apiPut(`/user/${form.id}`, userData)
        : apiPost("/user/register", userData));

      if (res.code === 200) {
        ElMessage.success(form.id ? "更新成功" : "添加成功");
        dialogVisible.value = false;
        fetchUsers();
      } else {
        ElMessage.error(res.msg || (form.id ? "更新失败" : "添加失败"));
      }
    } catch (error) {
      console.error("保存用户出错:", error);
      ElMessage.error(form.id ? "更新失败" : "添加失败");
    } finally {
      saveLoading.value = false;
    }
  });
};

// 删除用户
const handleDelete = async (id) => {
  try {
    const res = await apiDelete(`/user/${id}`);
    if (res.code === 200) {
      ElMessage.success("删除成功");
      fetchUsers();
    } else {
      ElMessage.error(res.msg || "删除失败");
    }
  } catch (error) {
    console.error("删除用户出错:", error);
    ElMessage.error("删除失败");
  }
};

// 搜索
    const handleSearch = () => {
      currentPage.value = 1;
    };

// 重置搜索
const resetSearch = () => {
  searchQuery.value = "";
  userTypeFilter.value = "";
  currentPage.value = 1;
};

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val;
  currentPage.value = 1;
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
};

// 获取角色标签类型
const getRoleTagType = (role) => {
  const types = {
    admin: "danger",
    teacher: "success",
    student: "",
  };
  return types[role] || "";
    };

    // 获取角色名称
    const getRoleName = (role) => {
  const names = {
        admin: "管理员",
        teacher: "教师",
        student: "学生",
      };
  return names[role] || role;
};
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.search-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

:deep(.el-tag) {
  margin-right: 8px;
}

.dialog-footer {
  padding-top: 20px;
  text-align: right;
}
</style>
