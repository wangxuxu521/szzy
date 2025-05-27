<template>
  <div class="type-management">
    <h2>资源类型管理</h2>

    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="left-actions">
        <el-button type="primary" @click="handleAddType">添加类型</el-button>
        <el-button
          type="danger"
          :disabled="selectedTypes.length === 0"
          @click="handleBatchDelete"
          >批量删除</el-button
        >
      </div>

      <div class="right-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索类型名称"
          style="width: 200px; margin-right: 10px"
          clearable
          @clear="handleSearchClear"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>

        <el-tooltip content="刷新列表" placement="top">
          <el-button
            :icon="RefreshRight"
            circle
            @click="fetchTypes"
            :loading="loading"
          />
        </el-tooltip>

        <el-tooltip content="切换视图" placement="top">
          <el-button
            :icon="viewMode === 'tree' ? List : Grid"
            circle
            @click="toggleViewMode"
          />
        </el-tooltip>
      </div>
    </div>

    <!-- 树形视图 -->
    <div v-if="viewMode === 'tree' && !loading" class="tree-view">
      <el-tree
        ref="treeRef"
        :data="typeTree"
        node-key="typeId"
        :props="{
          label: 'typeName',
          children: 'children',
        }"
        default-expand-all
        :expand-on-click-node="false"
        show-checkbox
        @check="handleCheckChange"
      >
        <template #default="{ node, data }">
          <div class="tree-node">
            <div class="node-label">
              <span>{{ node.label }}</span>
              <el-tag
                v-if="data.parentName"
                size="small"
                type="info"
                class="parent-tag"
              >
                父类型: {{ data.parentName }}
              </el-tag>
            </div>
            <div class="node-actions">
              <el-tag
                size="small"
                :type="data.status === 1 ? 'success' : 'danger'"
              >
                {{ data.status === 1 ? "启用" : "禁用" }}
              </el-tag>
              <el-button-group class="ml-2">
                <el-button size="small" @click.stop="handleEditType(data)"
                  >编辑</el-button
                >
                <el-button
                  size="small"
                  type="danger"
                  @click.stop="handleDeleteType(data)"
                  >删除</el-button
                >
              </el-button-group>
            </div>
          </div>
        </template>
      </el-tree>
    </div>

    <!-- 表格视图 -->
    <el-table
      v-else
      v-loading="loading"
      :data="types"
      border
      style="width: 100%; margin-top: 20px"
      @selection-change="handleSelectionChange"
      :row-class-name="tableRowClassName"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column label="ID" prop="typeId" width="80" />
      <el-table-column label="类型名称" prop="typeName">
        <template #default="scope">
          <div style="display: flex; align-items: center">
            <span>{{ scope.row.typeName }}</span>
            <el-tag
              v-if="scope.row.parentId && isChildType(scope.row)"
              size="small"
              type="info"
              style="margin-left: 8px"
            >
              子类型
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="描述" prop="description" />
      <el-table-column label="父类型" width="150">
        <template #default="scope">
          <template v-if="scope.row.parentId">
            <el-tooltip
              :content="`父类型ID: ${scope.row.parentId}`"
              placement="top"
            >
              <el-tag type="success">{{
                getParentTypeName(scope.row.parentId) || "-"
              }}</el-tag>
            </el-tooltip>
          </template>
          <el-tag v-else type="info">顶级类型</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="排序" width="80" prop="sortOrder" />
      <el-table-column label="创建时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(scope.row)"
          />
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
          <el-cascader
            v-model="typeForm.parentId"
            :options="typeOptions"
            :props="{
              checkStrictly: true,
              value: 'typeId',
              label: 'typeName',
              emitPath: false,
            }"
            placeholder="选择父类型（可选）"
            clearable
            :disabled="isEdit && typeForm.typeId === typeForm.parentId"
          />
          <div v-if="typeForm.parentId" class="selected-parent-info">
            已选父类型: {{ getParentTypeName(typeForm.parentId) }}
          </div>
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
import { ref, reactive, onMounted, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { RefreshRight, Search, Grid, List } from "@element-plus/icons-vue";
import {
  getAllTypes,
  getTypeById,
  getTypeTree,
  searchTypes,
  addType,
  updateType,
  updateTypeStatus,
  deleteType,
  batchDeleteTypes,
} from "@/api/type";

export default {
  name: "TypeManagement",
  setup() {
    // 数据列表
    const types = ref([]);
    const typeTree = ref([]);
    const loading = ref(false);
    const submitting = ref(false);
    const searchKeyword = ref("");
    const selectedTypes = ref([]);
    const viewMode = ref("table"); // 'table' or 'tree'
    const treeRef = ref(null);

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

    // 类型选项，用于级联选择器
    const typeOptions = computed(() => {
      // 排除正在编辑的类型及其子类型
      const filteredTypes = isEdit.value
        ? types.value.filter((t) => t.typeId !== typeForm.typeId)
        : types.value;

      // 构建级联选择器的选项结构
      const buildTypeOptions = (items, parentId = null) => {
        const result = [];
        const children = items.filter(
          (item) =>
            (parentId === null && item.parentId === null) ||
            item.parentId === parentId
        );

        for (const child of children) {
          const option = {
            typeId: child.typeId,
            typeName: child.typeName,
          };

          const grandchildren = buildTypeOptions(items, child.typeId);
          if (grandchildren.length > 0) {
            option.children = grandchildren;
          }

          result.push(option);
        }

        return result;
      };

      return buildTypeOptions(filteredTypes);
    });

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

        // 为每个类型添加父类型名称
        types.value.forEach((type) => {
          if (type.parentId) {
            const parent = types.value.find((t) => t.typeId === type.parentId);
            if (parent) {
              type.parentName = parent.typeName;
            }
          }
        });

        // 如果是树形视图，获取树形结构
        if (viewMode.value === "tree") {
          await fetchTypeTree();
        }
      } catch (error) {
        console.error("获取类型列表失败:", error);
        ElMessage.error("获取类型列表失败");
        types.value = [];
      } finally {
        loading.value = false;
      }
    };

    // 获取类型树结构
    const fetchTypeTree = async () => {
      try {
        const res = await getTypeTree();
        if (res && res.data) {
          typeTree.value = res.data;
          // 处理树形数据中的父类型名称
          processTreeData(typeTree.value);
        } else if (Array.isArray(res)) {
          typeTree.value = res;
          processTreeData(typeTree.value);
        } else {
          typeTree.value = [];
        }
      } catch (error) {
        console.error("获取类型树结构失败:", error);
        ElMessage.error("获取类型树结构失败");
        typeTree.value = [];
      }
    };

    // 处理树形数据，确保每个节点都有父类型信息
    const processTreeData = (nodes) => {
      if (!nodes || !Array.isArray(nodes)) return;

      nodes.forEach((node) => {
        // 如果有parentId但没有parentName，尝试从types列表查找
        if (node.parentId && !node.parentName) {
          const parent = types.value.find((t) => t.typeId === node.parentId);
          if (parent) {
            node.parentName = parent.typeName;
          }
        }

        // 递归处理子节点
        if (node.children && node.children.length > 0) {
          processTreeData(node.children);
        }
      });
    };

    // 检查是否是子类型
    const isChildType = (type) => {
      return type.parentId != null;
    };

    // 表格行样式
    const tableRowClassName = ({ row }) => {
      if (row.parentId) {
        return "child-row";
      }
      return "";
    };

    // 搜索类型
    const handleSearch = async () => {
      if (!searchKeyword.value.trim()) {
        fetchTypes();
        return;
      }

      loading.value = true;
      try {
        const res = await searchTypes(searchKeyword.value.trim());
        if (res && res.data) {
          types.value = res.data;
          // 为搜索结果添加父类型名称
          types.value.forEach((type) => {
            if (type.parentId) {
              // 这里可能需要再次请求获取父类型信息，因为搜索结果可能不包含父类型
              getTypeById(type.parentId)
                .then((parentRes) => {
                  if (parentRes && parentRes.data) {
                    type.parentName = parentRes.data.typeName;
                  }
                })
                .catch(() => {});
            }
          });
        } else if (Array.isArray(res)) {
          types.value = res;
        } else {
          types.value = [];
        }
      } catch (error) {
        console.error("搜索类型失败:", error);
        ElMessage.error("搜索类型失败");
      } finally {
        loading.value = false;
      }
    };

    // 清除搜索
    const handleSearchClear = () => {
      searchKeyword.value = "";
      fetchTypes();
    };

    // 切换视图模式
    const toggleViewMode = () => {
      viewMode.value = viewMode.value === "table" ? "tree" : "table";

      if (viewMode.value === "tree") {
        fetchTypeTree();
      } else {
        fetchTypes();
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

    // 表格选择变化
    const handleSelectionChange = (selection) => {
      selectedTypes.value = selection;
    };

    // 树形选择变化
    const handleCheckChange = (data, checked) => {
      selectedTypes.value = checked.checkedNodes;
    };

    // 更新状态
    const handleStatusChange = async (type) => {
      try {
        await updateTypeStatus(type.typeId, type.status);
        ElMessage.success(`${type.typeName} 状态已更新`);
      } catch (error) {
        console.error("更新状态失败:", error);
        ElMessage.error("更新状态失败");
        // 恢复原状态
        type.status = type.status === 1 ? 0 : 1;
      }
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

    // 批量删除
    const handleBatchDelete = () => {
      if (selectedTypes.value.length === 0) {
        ElMessage.warning("请选择要删除的类型");
        return;
      }

      const typeNames = selectedTypes.value.map((t) => t.typeName).join("、");

      ElMessageBox.confirm(
        `确定要删除选中的 ${selectedTypes.value.length} 个类型吗？包括：${typeNames}`,
        "批量删除确认",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(async () => {
          loading.value = true;
          try {
            const typeIds = selectedTypes.value.map((t) => t.typeId);
            await batchDeleteTypes(typeIds);
            ElMessage.success("批量删除成功");
            fetchTypes();
            selectedTypes.value = [];

            // 清除选中状态
            if (viewMode.value === "tree" && treeRef.value) {
              treeRef.value.setCheckedKeys([]);
            }
          } catch (error) {
            console.error("批量删除失败:", error);
            ElMessage.error("批量删除失败: " + (error.message || "未知错误"));
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
      typeTree,
      loading,
      submitting,
      dialogVisible,
      isEdit,
      typeForm,
      typeFormRef,
      typeRules,
      typeOptions,
      searchKeyword,
      selectedTypes,
      viewMode,
      treeRef,
      RefreshRight,
      Search,
      Grid,
      List,
      fetchTypes,
      getParentTypeName,
      formatDate,
      handleSearch,
      handleSearchClear,
      toggleViewMode,
      handleSelectionChange,
      handleCheckChange,
      handleStatusChange,
      handleAddType,
      handleEditType,
      handleDeleteType,
      handleBatchDelete,
      submitTypeForm,
      resetTypeForm,
      isChildType,
      tableRowClassName,
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

.left-actions,
.right-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.tree-view {
  margin-top: 20px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 15px;
}

.tree-node {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding-right: 8px;
}

.node-label {
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.parent-tag {
  margin-left: 8px;
  font-size: 12px;
}

.node-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.ml-2 {
  margin-left: 8px;
}

.el-form-item {
  margin-bottom: 22px;
}

.selected-parent-info {
  margin-top: 5px;
  font-size: 13px;
  color: #606266;
}

:deep(.child-row) {
  background-color: #f9fafc;
}

:deep(.child-row td) {
  border-top: 1px dashed #ebeef5;
  border-bottom: 1px dashed #ebeef5;
}
</style>
