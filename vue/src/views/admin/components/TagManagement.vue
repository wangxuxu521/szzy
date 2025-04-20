<template>
  <div class="tag-management">
    <div class="management-header">
      <h2>标签管理</h2>
      <el-button type="primary" @click="openAddModal">添加标签</el-button>
    </div>

    <el-table :data="tags" style="width: 100%" v-loading="loading">
      <el-table-column prop="tagId" label="ID" width="80"></el-table-column>
      <el-table-column prop="tagName" label="标签名称"></el-table-column>
      <el-table-column prop="tagType" label="标签类型">
        <template #default="scope">
          <el-tag :type="getTagTypeClass(scope.row.tagType)">
            {{ getTagTypeLabel(scope.row.tagType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="openEditModal(scope.row)"
            >编辑</el-button
          >
          <el-button
            size="small"
            type="danger"
            @click="confirmDelete(scope.row.tagId)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑标签弹窗 -->
    <el-dialog
      :title="tagForm.tagId ? '编辑标签' : '添加标签'"
      v-model="tagModalVisible"
      width="500px"
    >
      <el-form
        :model="tagForm"
        label-width="100px"
        :rules="rules"
        ref="tagFormRef"
      >
        <el-form-item label="标签名称" prop="tagName">
          <el-input
            v-model="tagForm.tagName"
            placeholder="请输入标签名称"
          ></el-input>
        </el-form-item>
        <el-form-item label="标签类型" prop="tagType">
          <el-select
            v-model="tagForm.tagType"
            placeholder="请选择标签类型"
            style="width: 100%"
          >
            <el-option label="主题" value="theme"></el-option>
            <el-option label="学科" value="subject"></el-option>
            <el-option label="格式" value="format"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="tagModalVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTagForm" :loading="submitting">
          确认
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, reactive } from "vue";
import { getTagList, saveTag, deleteTag } from "@/api/tag";
import { ElMessage, ElMessageBox } from "element-plus";

export default {
  name: "TagManagement",
  setup() {
    const tags = ref([]);
    const loading = ref(false);
    const tagModalVisible = ref(false);
    const submitting = ref(false);
    const tagFormRef = ref(null);

    const tagForm = reactive({
      tagId: null,
      tagName: "",
      tagType: "",
    });

    const rules = {
      tagName: [
        { required: true, message: "请输入标签名称", trigger: "blur" },
        { min: 1, max: 50, message: "长度在1到50个字符之间", trigger: "blur" },
      ],
      tagType: [
        { required: true, message: "请选择标签类型", trigger: "change" },
      ],
    };

    const fetchTags = async () => {
      loading.value = true;
      try {
        console.log("开始请求标签列表...");
        const response = await getTagList();
        console.log("获取标签列表成功:", response);
        tags.value = response;
      } catch (error) {
        console.error("获取标签列表失败:", error);
        ElMessage.error(`获取标签列表失败: ${error.message || "未知错误"}`);
      } finally {
        loading.value = false;
      }
    };

    const openAddModal = () => {
      resetTagForm();
      tagModalVisible.value = true;
    };

    const openEditModal = (tag) => {
      Object.assign(tagForm, tag);
      tagModalVisible.value = true;
    };

    const resetTagForm = () => {
      tagForm.tagId = null;
      tagForm.tagName = "";
      tagForm.tagType = "";
    };

    const submitTagForm = async () => {
      if (!tagFormRef.value) return;

      await tagFormRef.value.validate(async (valid) => {
        if (valid) {
          submitting.value = true;
          try {
            await saveTag(tagForm);
            ElMessage.success(tagForm.tagId ? "标签更新成功" : "标签添加成功");
            tagModalVisible.value = false;
            fetchTags();
          } catch (error) {
            ElMessage.error(tagForm.tagId ? "标签更新失败" : "标签添加失败");
            console.error(error);
          } finally {
            submitting.value = false;
          }
        }
      });
    };

    const confirmDelete = (tagId) => {
      ElMessageBox.confirm("确定要删除这个标签吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          handleDelete(tagId);
        })
        .catch(() => {
          // 用户取消删除
        });
    };

    const handleDelete = async (tagId) => {
      try {
        await deleteTag(tagId);
        ElMessage.success("标签删除成功");
        fetchTags();
      } catch (error) {
        ElMessage.error("标签删除失败");
        console.error(error);
      }
    };

    const getTagTypeClass = (type) => {
      const typeMap = {
        theme: "success",
        subject: "primary",
        format: "warning",
      };
      return typeMap[type] || "info";
    };

    const getTagTypeLabel = (type) => {
      const typeMap = {
        theme: "主题",
        subject: "学科",
        format: "格式",
      };
      return typeMap[type] || type;
    };

    onMounted(() => {
      fetchTags();
    });

    return {
      tags,
      loading,
      tagModalVisible,
      submitting,
      tagForm,
      tagFormRef,
      rules,
      openAddModal,
      openEditModal,
      submitTagForm,
      confirmDelete,
      getTagTypeClass,
      getTagTypeLabel,
    };
  },
};
</script>

<style scoped>
.tag-management {
  padding: 20px;
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

h2 {
  margin: 0;
}
</style>
