<template>
  <div class="announcement-management">
    <div class="header">
      <h2>公告管理</h2>
      <el-button type="primary" @click="openDialog()">发布公告</el-button>
    </div>

    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索公告标题"
        clearable
        @keyup.enter="handleSearch"
        style="width: 300px; margin-right: 10px"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-select
        v-model="searchType"
        placeholder="公告类型"
        clearable
        style="width: 200px; margin-right: 10px"
      >
        <el-option label="普通公告" :value="0" />
        <el-option label="重要公告" :value="1" />
        <el-option label="紧急公告" :value="2" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <el-table
      :data="filteredAnnouncements"
      stripe
      style="width: 100%"
      v-loading="loading"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" show-overflow-tooltip />
      <el-table-column prop="type" label="类型" width="100">
        <template #default="scope">
          <el-tag :type="getTypeTag(scope.row.type)">
            {{ getTypeLabel(scope.row.type) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="优先级" width="100">
        <template #default="scope">
          <el-tag type="info">{{ scope.row.priority }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'warning'">
            {{ scope.row.status === 1 ? "已发布" : "未发布" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="publisher" label="发布者" width="120" />
      <el-table-column prop="publishTime" label="发布时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.publishTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="openDialog(scope.row)"
            >编辑</el-button
          >
          <el-button
            size="small"
            :type="scope.row.status === 0 ? 'success' : 'warning'"
            @click="toggleStatus(scope.row)"
          >
            {{ scope.row.status === 0 ? "发布" : "撤回" }}
          </el-button>
          <el-popconfirm
            title="确定删除该公告吗？"
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

    <!-- 添加/编辑公告弹窗 -->
    <el-dialog
      :title="form.id ? '编辑公告' : '发布公告'"
      v-model="dialogVisible"
      width="700px"
    >
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="公告类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择公告类型">
            <el-option label="普通公告" :value="0" />
            <el-option label="重要公告" :value="1" />
            <el-option label="紧急公告" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-input-number v-model="form.priority" :min="0" :max="100" />
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="6"
            placeholder="请输入公告内容"
          />
        </el-form-item>
        <el-form-item label="发布状态" prop="status">
          <el-switch
            v-model="form.status"
            :active-value="1"
            :inactive-value="0"
            active-text="立即发布"
            inactive-text="暂存草稿"
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
const announcements = ref([]);
const loading = ref(false);
const dialogVisible = ref(false);
const saveLoading = ref(false);
const formRef = ref(null);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const searchKeyword = ref("");
const searchType = ref("");

// 表单数据
const form = reactive({
  id: null,
  title: "",
  content: "",
  type: 0,
  priority: 0,
  status: 0,
});

// 表单验证规则
const rules = {
  title: [
    { required: true, message: "请输入公告标题", trigger: "blur" },
    { min: 2, max: 100, message: "长度在 2 到 100 个字符", trigger: "blur" },
  ],
  content: [
    { required: true, message: "请输入公告内容", trigger: "blur" },
    {
      min: 10,
      max: 5000,
      message: "长度在 10 到 5000 个字符",
      trigger: "blur",
    },
  ],
  type: [{ required: true, message: "请选择公告类型", trigger: "change" }],
  priority: [{ required: true, message: "请设置优先级", trigger: "change" }],
};

// 过滤后的公告列表
const filteredAnnouncements = computed(() => {
  let result = [...announcements.value];

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter((item) =>
      item.title.toLowerCase().includes(keyword)
    );
  }

  if (searchType.value !== "") {
    result = result.filter((item) => item.type === searchType.value);
  }

  return result;
});

// 生命周期
onMounted(() => {
  fetchAnnouncements();
});

// 获取所有公告
const fetchAnnouncements = async () => {
  loading.value = true;
  try {
    const res = await apiGet("/announcements");
    if (res.code === 200) {
      announcements.value = res.data;
      total.value = res.data.length;
    } else {
      ElMessage.error(res.msg || "获取公告列表失败");
    }
  } catch (error) {
    console.error("获取公告列表出错:", error);
    ElMessage.error("获取公告列表失败");
  } finally {
    loading.value = false;
  }
};

// 打开弹窗
const openDialog = (row) => {
  formRef.value?.resetFields();

  if (row) {
    // 编辑模式，填充表单
    Object.keys(form).forEach((key) => {
      form[key] = row[key];
    });
  } else {
    // 添加模式，重置表单
    Object.keys(form).forEach((key) => {
      form[key] =
        key === "id"
          ? null
          : key === "type" || key === "priority" || key === "status"
          ? 0
          : "";
    });
  }

  dialogVisible.value = true;
};

// 保存公告
const handleSave = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (!valid) return;

    saveLoading.value = true;
    try {
      const res = await (form.id
        ? apiPut(`/announcements/${form.id}`, form)
        : apiPost("/announcements", form));
      if (res.code === 200) {
        ElMessage.success(form.id ? "更新成功" : "添加成功");
        dialogVisible.value = false;
        fetchAnnouncements();
      } else {
        ElMessage.error(res.msg || (form.id ? "更新失败" : "添加失败"));
      }
    } catch (error) {
      console.error("保存公告出错:", error);
      ElMessage.error(form.id ? "更新失败" : "添加失败");
    } finally {
      saveLoading.value = false;
    }
  });
};

// 删除公告
const handleDelete = async (id) => {
  try {
    const res = await apiDelete(`/announcements/${id}`);
    if (res.code === 200) {
      ElMessage.success("删除成功");
      fetchAnnouncements();
    } else {
      ElMessage.error(res.msg || "删除失败");
    }
  } catch (error) {
    console.error("删除公告出错:", error);
    ElMessage.error("删除失败");
  }
};

// 切换公告状态
const toggleStatus = async (row) => {
  try {
    const newStatus = row.status === 0 ? 1 : 0;
    const res = await apiPut(`/announcements/${row.id}`, {
      ...row,
      status: newStatus,
      publishTime: newStatus === 1 ? new Date() : null,
    });

    if (res.code === 200) {
      ElMessage.success(newStatus === 1 ? "发布成功" : "撤回成功");
      fetchAnnouncements();
    } else {
      ElMessage.error(res.msg || (newStatus === 1 ? "发布失败" : "撤回失败"));
    }
  } catch (error) {
    console.error("更新公告状态出错:", error);
    ElMessage.error("操作失败");
  }
};

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
};

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = "";
  searchType.value = "";
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

// 获取公告类型标签样式
const getTypeTag = (type) => {
  const tags = {
    0: "",
    1: "warning",
    2: "danger",
  };
  return tags[type] || "";
};

// 获取公告类型标签文本
const getTypeLabel = (type) => {
  const labels = {
    0: "普通公告",
    1: "重要公告",
    2: "紧急公告",
  };
  return labels[type] || "未知类型";
};
</script>

<style scoped>
.announcement-management {
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
