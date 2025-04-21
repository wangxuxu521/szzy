<template>
  <div class="type-management">
    <div class="header">
      <h2>类型管理</h2>
      <el-button type="primary" @click="openDialog()">添加类型</el-button>
    </div>

    <el-table :data="types" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="typeId" label="ID" width="80" />
      <el-table-column prop="typeName" label="类型名称" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="openDialog(scope.row)"
            >编辑</el-button
          >
          <el-popconfirm
            title="确定删除该类型吗？"
            @confirm="handleDelete(scope.row.typeId)"
          >
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑类型弹窗 -->
    <el-dialog
      :title="form.typeId ? '编辑类型' : '添加类型'"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
        <el-form-item label="类型名称" prop="typeName">
          <el-input v-model="form.typeName" placeholder="请输入类型名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入类型描述"
            :rows="4"
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
import { ref, onMounted, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { apiGet, apiPost, apiDelete } from "@/api/request";
import { formatDate } from "@/utils/format";

// 数据
const types = ref([]);
const loading = ref(false);
const dialogVisible = ref(false);
const saveLoading = ref(false);
const formRef = ref(null);

// 表单数据
const form = reactive({
  typeId: null,
  typeName: "",
  description: "",
});

// 表单验证规则
const rules = {
  typeName: [
    { required: true, message: "请输入类型名称", trigger: "blur" },
    { min: 2, max: 50, message: "长度在 2 到 50 个字符", trigger: "blur" },
  ],
  description: [{ max: 500, message: "最多 500 个字符", trigger: "blur" }],
};

// 生命周期
onMounted(() => {
  fetchTypes();
});

// 获取所有类型
const fetchTypes = async () => {
  loading.value = true;
  try {
    const res = await apiGet("/types");
    if (res.code === 200) {
      types.value = res.data;
    } else {
      ElMessage.error(res.msg || "获取类型列表失败");
    }
  } catch (error) {
    console.error("获取类型列表出错:", error);
    ElMessage.error("获取类型列表失败");
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
      form[key] = key === "typeId" ? null : "";
    });
  }

  dialogVisible.value = true;
};

// 保存类型
const handleSave = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (!valid) return;

    saveLoading.value = true;
    try {
      const res = await apiPost("/types", form);
      if (res.code === 200) {
        ElMessage.success(form.typeId ? "更新成功" : "添加成功");
        dialogVisible.value = false;
        fetchTypes();
      } else {
        ElMessage.error(res.msg || (form.typeId ? "更新失败" : "添加失败"));
      }
    } catch (error) {
      console.error("保存类型出错:", error);
      ElMessage.error(form.typeId ? "更新失败" : "添加失败");
    } finally {
      saveLoading.value = false;
    }
  });
};

// 删除类型
const handleDelete = async (id) => {
  try {
    const res = await apiDelete(`/types/${id}`);
    if (res.code === 200) {
      ElMessage.success("删除成功");
      fetchTypes();
    } else {
      ElMessage.error(res.msg || "删除失败");
    }
  } catch (error) {
    console.error("删除类型出错:", error);
    ElMessage.error("删除失败");
  }
};
</script>

<style scoped>
.type-management {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
