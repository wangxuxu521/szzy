<template>
  <div class="type-management">
    <h2>资源类型管理</h2>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAddType">添加类型</el-button>
      <el-tooltip content="刷新列表" placement="top">
        <el-button
          :icon="RefreshRight"
          circle
          @click="fetchTypes"
          :loading="loading"
        />
      </el-tooltip>
    </div>

    <!-- 类型列表 -->
    <el-table
      v-loading="loading"
      :data="types"
      border
      style="width: 100%; margin-top: 20px"
    >
      <el-table-column label="ID" prop="typeId" width="80" />
      <el-table-column label="类型名称" prop="typeName" />
      <el-table-column label="描述" prop="description" />
      <el-table-column label="父类型" width="120">
        <template #default="scope">
          {{ getParentTypeName(scope.row.parentId) || "-" }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? "启用" : "禁用" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="handleEditType(scope.row)"
            >编辑</el-button
          >
          <el-button
            size="small"
            type="danger"
            @click="handleDeleteType(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 类型表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑类型' : '添加类型'"
      width="500px"
    >
      <el-form
        ref="typeFormRef"
        :model="typeForm"
        :rules="typeRules"
        label-width="100px"
      >
        <el-form-item label="类型名称" prop="typeName">
          <el-input v-model="typeForm.typeName" />
        </el-form-item>

        <el-form-item label="父类型">
          <el-select
            v-model="typeForm.parentId"
            placeholder="选择父类型（可选）"
            clearable
          >
            <el-option
              v-for="type in types.filter((t) => t.typeId !== typeForm.typeId)"
              :key="type.typeId"
              :label="type.typeName"
              :value="type.typeId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input v-model="typeForm.description" type="textarea" :rows="3" />
        </el-form-item>

        <el-form-item label="排序顺序" prop="sortOrder">
          <el-input-number v-model="typeForm.sortOrder" :min="0" />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="typeForm.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="submitTypeForm"
            :loading="submitting"
            >确定</el-button
          >
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { RefreshRight } from "@element-plus/icons-vue";
import {
  getAllTypes,
  getTypeById,
  addType,
  updateType,
  deleteType,
} from "@/api/type";

export default {
  name: "TypeManagement",
  setup() {
    // 数据列表
    const types = ref([]);
    const loading = ref(false);
    const submitting = ref(false);

    // 对话框状态
    const dialogVisible = ref(false);
    const isEdit = ref(false);
    const typeFormRef = ref(null);

    // 表单数据
    const typeForm = reactive({
      typeId: null,
      typeName: "",
      parentId: null,
      description: "",
      sortOrder: 0,
      status: 1,
    });

    // 表单验证规则
    const typeRules = {
      typeName: [
        { required: true, message: "请输入类型名称", trigger: "blur" },
        { min: 2, max: 50, message: "长度在2到50个字符", trigger: "blur" },
      ],
    };

    // 获取类型列表
    const fetchTypes = async () => {
      loading.value = true;
      try {
        const res = await getAllTypes();
        if (res && res.data) {
          types.value = res.data;
        } else if (Array.isArray(res)) {
          types.value = res;
        } else {
          types.value = [];
        }
      } catch (error) {
        console.error("获取类型列表失败:", error);
        ElMessage.error("获取类型列表失败");
        types.value = [];
      } finally {
        loading.value = false;
      }
    };

    // 获取父类型名称
    const getParentTypeName = (parentId) => {
      if (!parentId) return "";
      const parent = types.value.find((t) => t.typeId === parentId);
      return parent ? parent.typeName : "";
    };

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return "";
      const date = new Date(dateString);
      return date.toLocaleString();
    };

    // 添加类型
    const handleAddType = () => {
      isEdit.value = false;
      resetTypeForm();
      dialogVisible.value = true;
    };

    // 编辑类型
    const handleEditType = (type) => {
      isEdit.value = true;
      resetTypeForm();

      Object.keys(typeForm).forEach((key) => {
        if (type[key] !== undefined) {
          typeForm[key] = type[key];
        }
      });

      dialogVisible.value = true;
    };

    // 删除类型
    const handleDeleteType = (type) => {
      ElMessageBox.confirm(
        `确定要删除类型"${type.typeName}"吗？这可能会影响关联的资源。`,
        "删除确认",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(async () => {
          loading.value = true;
          try {
            await deleteType(type.typeId);
            ElMessage.success("类型删除成功");
            fetchTypes();
          } catch (error) {
            console.error("删除类型失败:", error);
            ElMessage.error("删除类型失败: " + (error.message || "未知错误"));
          } finally {
            loading.value = false;
          }
        })
        .catch(() => {});
    };

    // 重置表单
    const resetTypeForm = () => {
      if (typeFormRef.value) {
        typeFormRef.value.resetFields();
      }

      typeForm.typeId = null;
      typeForm.typeName = "";
      typeForm.parentId = null;
      typeForm.description = "";
      typeForm.sortOrder = 0;
      typeForm.status = 1;
    };

    // 提交表单
    const submitTypeForm = async () => {
      if (!typeFormRef.value) return;

      await typeFormRef.value.validate(async (valid) => {
        if (!valid) return;

        submitting.value = true;
        try {
          if (isEdit.value) {
            await updateType(typeForm);
            ElMessage.success("类型更新成功");
          } else {
            await addType(typeForm);
            ElMessage.success("类型添加成功");
          }

          dialogVisible.value = false;
          fetchTypes();
        } catch (error) {
          console.error(
            isEdit.value ? "更新类型失败:" : "添加类型失败:",
            error
          );
          ElMessage.error(
            (isEdit.value ? "更新" : "添加") +
              "类型失败: " +
              (error.message || "未知错误")
          );
        } finally {
          submitting.value = false;
        }
      });
    };

    // 生命周期钩子
    onMounted(() => {
      fetchTypes();
    });

    return {
      types,
      loading,
      submitting,
      dialogVisible,
      isEdit,
      typeForm,
      typeFormRef,
      typeRules,
      RefreshRight,
      fetchTypes,
      getParentTypeName,
      formatDate,
      handleAddType,
      handleEditType,
      handleDeleteType,
      submitTypeForm,
      resetTypeForm,
    };
  },
};
</script>

<style scoped>
.type-management {
  padding: 1rem;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.el-form-item {
  margin-bottom: 22px;
}
</style>
