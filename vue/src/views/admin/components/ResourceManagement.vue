<template>
  <div class="resource-management">
    <div class="section-header">
      <h2>资源管理</h2>
      <div class="search-bar">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="搜索资源..."
          @input="handleSearch"
        />
        <select v-model="resourceTypeFilter">
          <option value="">所有类型</option>
          <option value="教学资源">教学资源</option>
          <option value="教学案例">教学案例</option>
          <option value="研究成果">研究成果</option>
        </select>
      </div>
      <button class="add-btn" @click="showAddResourceModal = true">
        上传资源
      </button>
    </div>

    <div class="table-container">
      <table class="resource-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>标题</th>
            <th>类型</th>
            <th>上传者</th>
            <th>上传日期</th>
            <th>浏览量</th>
            <th>下载量</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="resource in filteredResources" :key="resource.id">
            <td>{{ resource.id }}</td>
            <td class="resource-title">{{ resource.title }}</td>
            <td>
              <span class="type-tag" :class="getTypeClass(resource.type)">
                {{ resource.type }}
              </span>
            </td>
            <td>{{ resource.author }}</td>
            <td>{{ resource.uploadDate }}</td>
            <td>{{ resource.views }}</td>
            <td>{{ resource.downloads }}</td>
            <td>
              <span
                class="status-tag"
                :class="{
                  active: resource.status === 'approved',
                  pending: resource.status === 'pending',
                  rejected: resource.status === 'rejected',
                }"
              >
                {{ getStatusName(resource.status) }}
              </span>
            </td>
            <td class="action-buttons">
              <button class="view-btn" @click="viewResource(resource)">
                查看
              </button>
              <button class="edit-btn" @click="editResource(resource)">
                编辑
              </button>
              <button
                v-if="resource.status === 'pending'"
                class="approve-btn"
                @click="approveResource(resource)"
              >
                审核通过
              </button>
              <button
                v-if="resource.status === 'pending'"
                class="reject-btn"
                @click="rejectResource(resource)"
              >
                拒绝
              </button>
              <button class="delete-btn" @click="confirmDelete(resource)">
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

    <!-- 添加/编辑资源弹窗 -->
    <div
      v-if="showAddResourceModal || showEditResourceModal"
      class="modal-overlay"
    >
      <div class="modal">
        <div class="modal-header">
          <h3>{{ showEditResourceModal ? "编辑资源" : "上传资源" }}</h3>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitResourceForm">
            <div class="form-group">
              <label>标题</label>
              <input type="text" v-model="resourceForm.title" required />
            </div>
            <div class="form-group">
              <label>类型</label>
              <select v-model="resourceForm.type" required>
                <option value="教学资源">教学资源</option>
                <option value="教学案例">教学案例</option>
                <option value="研究成果">研究成果</option>
              </select>
            </div>
            <div class="form-group">
              <label>描述</label>
              <textarea v-model="resourceForm.description" rows="4"></textarea>
            </div>
            <div class="form-group">
              <label>标签</label>
              <div class="tag-select-container">
                <el-select
                  v-model="selectedTags"
                  multiple
                  filterable
                  allow-create
                  default-first-option
                  placeholder="请选择或输入标签"
                  style="width: 100%"
                >
                  <el-option
                    v-for="tag in availableTags"
                    :key="tag.tagId"
                    :label="tag.tagName"
                    :value="tag.tagName"
                  >
                    <span>{{ tag.tagName }}</span>
                    <el-tag
                      size="small"
                      class="tag-type-indicator"
                      :type="getTagTypeClass(tag.tagType)"
                    >
                      {{ getTagTypeLabel(tag.tagType) }}
                    </el-tag>
                  </el-option>
                </el-select>
              </div>
            </div>
            <div class="form-group" v-if="!showEditResourceModal">
              <label>上传文件</label>
              <input type="file" @change="handleFileUpload" />
            </div>
            <div class="form-buttons">
              <button type="button" class="cancel-btn" @click="closeModal">
                取消
              </button>
              <button type="submit" class="submit-btn">保存</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 资源详情弹窗 -->
    <div v-if="showResourceDetail" class="modal-overlay">
      <div class="modal detail-modal">
        <div class="modal-header">
          <h3>资源详情</h3>
          <button class="close-btn" @click="showResourceDetail = false">
            ×
          </button>
        </div>
        <div class="modal-body">
          <div class="detail-item">
            <div class="detail-label">标题</div>
            <div class="detail-value">{{ selectedResource.title }}</div>
          </div>
          <div class="detail-item">
            <div class="detail-label">类型</div>
            <div class="detail-value">{{ selectedResource.type }}</div>
          </div>
          <div class="detail-item">
            <div class="detail-label">上传者</div>
            <div class="detail-value">{{ selectedResource.author }}</div>
          </div>
          <div class="detail-item">
            <div class="detail-label">上传日期</div>
            <div class="detail-value">{{ selectedResource.uploadDate }}</div>
          </div>
          <div class="detail-item">
            <div class="detail-label">浏览量</div>
            <div class="detail-value">{{ selectedResource.views }}</div>
          </div>
          <div class="detail-item">
            <div class="detail-label">下载量</div>
            <div class="detail-value">{{ selectedResource.downloads }}</div>
          </div>
          <div class="detail-item">
            <div class="detail-label">状态</div>
            <div class="detail-value">
              {{ getStatusName(selectedResource.status) }}
            </div>
          </div>
          <div class="detail-item">
            <div class="detail-label">描述</div>
            <div class="detail-value description">
              {{ selectedResource.description }}
            </div>
          </div>
          <div class="detail-item">
            <div class="detail-label">标签</div>
            <div class="detail-value tags">
              <span
                v-for="(tag, index) in selectedResource.tags"
                :key="index"
                class="tag"
              >
                {{ tag }}
              </span>
            </div>
          </div>
          <div class="detail-item">
            <div class="detail-label">文件</div>
            <div class="detail-value">
              <a href="#" class="download-link"
                >点击下载 {{ selectedResource.filename }}</a
              >
            </div>
          </div>
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
          <p>
            确定要删除资源 "{{ resourceToDelete?.title }}" 吗？此操作不可撤销。
          </p>
          <div class="form-buttons">
            <button class="cancel-btn" @click="showDeleteModal = false">
              取消
            </button>
            <button class="delete-btn" @click="deleteResource">删除</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, reactive, onMounted } from "vue";
import { getTagList, saveTag } from "@/api/tag";
import { ElMessage } from "element-plus";

export default {
  name: "ResourceManagement",
  setup() {
    // 资源数据
    const resources = ref([
      {
        id: 1,
        title: "计算机网络中的爱国情怀",
        type: "教学案例",
        author: "张教授",
        uploadDate: "2024-01-15",
        views: 1234,
        downloads: 567,
        status: "approved",
        description:
          "本教学案例通过计算机网络发展历史，介绍了我国在计算机网络领域的发展历程和爱国情怀。",
        tags: ["计算机网络", "爱国主义", "教学案例"],
        filename: "计算机网络中的爱国情怀.pdf",
      },
      {
        id: 2,
        title: "数据结构与民族精神",
        type: "教学资源",
        author: "李教授",
        uploadDate: "2024-01-20",
        views: 890,
        downloads: 456,
        status: "approved",
        description:
          "通过数据结构教学，融入中华民族传统文化元素，培养学生的民族自豪感。",
        tags: ["数据结构", "民族精神", "文化融合"],
        filename: "数据结构与民族精神.pptx",
      },
      {
        id: 3,
        title: "人工智能伦理与价值观",
        type: "研究成果",
        author: "王教授",
        uploadDate: "2024-01-25",
        views: 567,
        downloads: 234,
        status: "approved",
        description:
          "探讨人工智能发展中的伦理问题，引导学生树立正确的科技伦理观。",
        tags: ["人工智能", "伦理价值观", "科技发展"],
        filename: "人工智能伦理与价值观.pdf",
      },
      {
        id: 4,
        title: "软件工程中的团队协作精神",
        type: "教学资源",
        author: "赵教授",
        uploadDate: "2024-02-01",
        views: 432,
        downloads: 198,
        status: "pending",
        description:
          "通过软件工程项目实践，培养学生的团队协作精神和集体主义价值观。",
        tags: ["软件工程", "团队协作", "价值观教育"],
        filename: "软件工程中的团队协作精神.docx",
      },
      {
        id: 5,
        title: "数据科学与社会责任",
        type: "研究成果",
        author: "周教授",
        uploadDate: "2024-02-05",
        views: 321,
        downloads: 145,
        status: "rejected",
        description:
          "探讨数据科学发展中的社会责任问题，引导学生树立正确的社会责任感。",
        tags: ["数据科学", "社会责任", "职业道德"],
        filename: "数据科学与社会责任.pdf",
      },
    ]);

    // 分页
    const currentPage = ref(1);
    const pageSize = ref(10);
    const totalPages = computed(() =>
      Math.ceil(filteredResources.value.length / pageSize.value)
    );

    // 搜索和筛选
    const searchQuery = ref("");
    const resourceTypeFilter = ref("");

    // 弹窗状态
    const showAddResourceModal = ref(false);
    const showEditResourceModal = ref(false);
    const showResourceDetail = ref(false);
    const showDeleteModal = ref(false);
    const resourceToDelete = ref(null);
    const selectedResource = ref({});

    // 表单数据
    const resourceForm = reactive({
      id: null,
      title: "",
      type: "教学资源",
      description: "",
      tags: "",
      file: null,
    });

    // 标签数据
    const availableTags = ref([]);
    const selectedTags = ref([]);

    // 获取标签列表
    const fetchTags = async () => {
      try {
        const response = await getTagList();
        availableTags.value = response;
      } catch (error) {
        console.error("获取标签列表失败", error);
      }
    };

    // 标签类型显示
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

    // 过滤后的资源列表
    const filteredResources = computed(() => {
      return resources.value.filter((resource) => {
        // 类型筛选
        if (
          resourceTypeFilter.value &&
          resource.type !== resourceTypeFilter.value
        ) {
          return false;
        }

        // 搜索关键词
        if (searchQuery.value) {
          const query = searchQuery.value.toLowerCase();
          return (
            resource.title.toLowerCase().includes(query) ||
            resource.author.toLowerCase().includes(query) ||
            (resource.description &&
              resource.description.toLowerCase().includes(query))
          );
        }

        return true;
      });
    });

    // 搜索处理
    const handleSearch = () => {
      currentPage.value = 1;
    };

    // 获取类型样式类
    const getTypeClass = (type) => {
      const typeMap = {
        教学资源: "resource",
        教学案例: "case",
        研究成果: "research",
      };
      return typeMap[type] || "";
    };

    // 获取状态名称
    const getStatusName = (status) => {
      const statusMap = {
        approved: "已审核",
        pending: "待审核",
        rejected: "已拒绝",
      };
      return statusMap[status] || status;
    };

    // 查看资源详情
    const viewResource = (resource) => {
      selectedResource.value = { ...resource };
      showResourceDetail.value = true;
    };

    // 编辑资源
    const editResource = (resource) => {
      resourceForm.id = resource.id;
      resourceForm.title = resource.title;
      resourceForm.type = resource.type;
      resourceForm.description = resource.description || "";
      resourceForm.tags = resource.tags ? resource.tags.join(", ") : "";
      selectedTags.value = resource.tags || [];

      showEditResourceModal.value = true;
    };

    // 审核通过资源
    const approveResource = (resource) => {
      const index = resources.value.findIndex((r) => r.id === resource.id);
      if (index !== -1) {
        resources.value[index].status = "approved";
      }
    };

    // 拒绝资源
    const rejectResource = (resource) => {
      const index = resources.value.findIndex((r) => r.id === resource.id);
      if (index !== -1) {
        resources.value[index].status = "rejected";
      }
    };

    // 确认删除资源
    const confirmDelete = (resource) => {
      resourceToDelete.value = resource;
      showDeleteModal.value = true;
    };

    // 删除资源
    const deleteResource = () => {
      if (resourceToDelete.value) {
        resources.value = resources.value.filter(
          (resource) => resource.id !== resourceToDelete.value.id
        );
        showDeleteModal.value = false;
        resourceToDelete.value = null;
      }
    };

    // 处理文件上传
    const handleFileUpload = (event) => {
      resourceForm.file = event.target.files[0];
    };

    // 提交资源表单
    const submitResourceForm = async () => {
      if (showEditResourceModal.value) {
        // 编辑现有资源
        const index = resources.value.findIndex(
          (resource) => resource.id === resourceForm.id
        );
        if (index !== -1) {
          const tagsArray = resourceForm.tags
            ? resourceForm.tags.split(",").map((tag) => tag.trim())
            : [];

          resources.value[index] = {
            ...resources.value[index],
            title: resourceForm.title,
            type: resourceForm.type,
            description: resourceForm.description,
            tags: tagsArray,
          };
        }
      } else {
        // 添加新资源
        const newId =
          Math.max(...resources.value.map((resource) => resource.id)) + 1;
        const tagsArray = resourceForm.tags
          ? resourceForm.tags.split(",").map((tag) => tag.trim())
          : [];
        const filename = resourceForm.file
          ? resourceForm.file.name
          : "未知文件.pdf";

        resources.value.push({
          id: newId,
          title: resourceForm.title,
          type: resourceForm.type,
          author: "当前管理员", // 实际场景中应该是当前登录的用户
          uploadDate: new Date().toISOString().split("T")[0],
          views: 0,
          downloads: 0,
          status: "pending",
          description: resourceForm.description,
          tags: tagsArray,
          filename: filename,
        });
      }

      // 使用选择的标签替代原有tags字段处理
      const resourceData = {
        ...resourceForm,
        tags: selectedTags.value,
      };

      closeModal();
    };

    // 关闭弹窗
    const closeModal = () => {
      showAddResourceModal.value = false;
      showEditResourceModal.value = false;

      // 重置表单
      resourceForm.id = null;
      resourceForm.title = "";
      resourceForm.type = "教学资源";
      resourceForm.description = "";
      resourceForm.tags = "";
      resourceForm.file = null;
      selectedTags.value = [];
    };

    onMounted(() => {
      fetchTags();
    });

    return {
      resources,
      filteredResources,
      currentPage,
      totalPages,
      searchQuery,
      resourceTypeFilter,
      showAddResourceModal,
      showEditResourceModal,
      showResourceDetail,
      showDeleteModal,
      resourceToDelete,
      selectedResource,
      resourceForm,
      handleSearch,
      getTypeClass,
      getStatusName,
      viewResource,
      editResource,
      approveResource,
      rejectResource,
      confirmDelete,
      deleteResource,
      handleFileUpload,
      submitResourceForm,
      closeModal,
      availableTags,
      selectedTags,
      getTagTypeClass,
      getTagTypeLabel,
    };
  },
};
</script>

<style scoped>
.resource-management {
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

.resource-table {
  width: 100%;
  border-collapse: collapse;
}

.resource-table th,
.resource-table td {
  padding: 0.8rem;
  text-align: left;
  border-bottom: 1px solid #f0f0f0;
}

.resource-table th {
  background-color: #fafafa;
  font-weight: 600;
  color: #333;
}

.resource-table tr:hover {
  background-color: #f5f5f5;
}

.resource-title {
  max-width: 250px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.type-tag {
  display: inline-block;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
}

.type-tag.resource {
  background-color: #e6f7ff;
  color: #1890ff;
}

.type-tag.case {
  background-color: #f6ffed;
  color: #52c41a;
}

.type-tag.research {
  background-color: #fff7e6;
  color: #faad14;
}

.status-tag {
  display: inline-block;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
}

.status-tag.active {
  background-color: #f6ffed;
  color: #52c41a;
}

.status-tag.pending {
  background-color: #fff7e6;
  color: #faad14;
}

.status-tag.rejected {
  background-color: #fff1f0;
  color: #ff4d4f;
}

.action-buttons {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
}

.action-buttons button {
  padding: 0.3rem 0.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
  white-space: nowrap;
}

.view-btn {
  background-color: #1890ff;
  color: white;
}

.edit-btn {
  background-color: #faad14;
  color: white;
}

.approve-btn {
  background-color: #52c41a;
  color: white;
}

.reject-btn {
  background-color: #ff4d4f;
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
  width: 600px;
  max-width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.detail-modal {
  width: 700px;
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
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  outline: none;
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

/* 资源详情样式 */
.detail-item {
  margin-bottom: 1rem;
  display: flex;
  border-bottom: 1px dashed #f0f0f0;
  padding-bottom: 0.5rem;
}

.detail-label {
  width: 100px;
  font-weight: bold;
  color: #666;
  flex-shrink: 0;
}

.detail-value {
  flex: 1;
}

.detail-value.description {
  white-space: pre-line;
}

.detail-value.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag {
  display: inline-block;
  padding: 0.2rem 0.5rem;
  background-color: #f5f5f5;
  color: #666;
  border-radius: 4px;
  font-size: 0.8rem;
}

.download-link {
  color: #1890ff;
  text-decoration: none;
}

.download-link:hover {
  text-decoration: underline;
}

.tag-select-container {
  width: 100%;
}

.tag-type-indicator {
  margin-left: 8px;
  font-size: 11px;
  height: 20px;
  line-height: 20px;
}
</style>
