<template>
  <div class="resource-management">
    <div class="section-header">
      <h2>思政资源库</h2>
      <div class="search-bar">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="搜索思政资源..."
          @input="handleSearch"
        />
        <select v-model="resourceTypeFilter">
          <option value="">所有类型</option>
          <option v-for="type in resourceTypes" :key="type" :value="type">
            {{ type }}
          </option>
        </select>
      </div>
      <button
        class="add-btn"
        @click="showAddResourceModal = true"
        v-if="isLoggedIn && (userRole === 'teacher' || userRole === 'admin')"
      >
        上传资源
      </button>
    </div>

    <div class="filter-tags">
      <div v-if="activeFilters.length > 0" class="active-filters">
        <span>当前筛选:</span>
        <div class="tag" v-for="(filter, index) in activeFilters" :key="index">
          {{ filter }}
          <button class="remove-filter" @click="removeFilter(filter)">×</button>
        </div>
        <button class="clear-filters" @click="clearFilters">清除全部</button>
      </div>
    </div>

    <!-- 资源卡片列表 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>加载资源中...</p>
    </div>

    <div v-else class="resources-container">
      <div v-if="filteredResources.length === 0" class="no-resources">
        <p>暂无符合条件的资源，请尝试其他筛选条件</p>
      </div>
      <div v-else class="resource-grid">
        <div
          v-for="resource in paginatedResources"
          :key="resource.id"
          class="resource-card"
          @click="viewResource(resource)"
        >
          <div class="card-header">
            <span class="type-tag" :class="getTypeClass(resource.type)">
              {{ resource.type }}
            </span>
            <span class="views">
              <i class="icon-eye"></i> {{ resource.views }}
            </span>
          </div>
          <h3 class="card-title">{{ resource.title }}</h3>
          <div class="card-meta">
            <span class="author">{{ resource.author }}</span>
            <span class="date">{{ resource.uploadDate }}</span>
          </div>
          <p class="card-desc">{{ truncateText(resource.description, 80) }}</p>
          <div class="card-tags">
            <span
              v-for="(tag, tagIndex) in parseResourceTags(resource.tags)"
              :key="tagIndex"
              class="tag"
              :class="getTagClass(tag)"
            >
              <span class="tag-icon">{{ getTagIcon(tag) }}</span>
              {{ tag }}
            </span>
          </div>
          <div class="card-actions">
            <button
              class="action-btn preview-btn"
              @click.stop="showPreview(resource)"
              title="预览资源"
            >
              预览
            </button>
            <button
              class="action-btn download-btn"
              @click.stop="downloadResource(resource)"
              title="下载资源"
            >
              下载
            </button>
            <button
              v-if="
                userRole === 'admin' ||
                (isLoggedIn && resource.authorId === userId)
              "
              class="action-btn edit-btn"
              @click.stop="editResource(resource)"
              title="编辑资源"
            >
              编辑
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
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
      @click="closeModal"
    >
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>{{ showEditResourceModal ? "编辑资源" : "上传思政资源" }}</h3>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitResourceForm">
            <div class="form-group">
              <label>标题 <span class="required">*</span></label>
              <input type="text" v-model="resourceForm.title" required />
            </div>
            <div class="form-group">
              <label>类型 <span class="required">*</span></label>
              <select v-model="resourceForm.type" required>
                <option v-for="type in resourceTypes" :key="type" :value="type">
                  {{ type }}
                </option>
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
            <div
              class="form-group"
              v-if="!showEditResourceModal || resourceForm.replaceFile"
            >
              <label>上传文件 <span class="required">*</span></label>
              <input type="file" @change="handleFileUpload" required />
              <p class="file-hint">
                支持PDF、Word、PPT、Excel等格式，文件大小不超过20MB
              </p>
            </div>
            <div
              class="form-group"
              v-if="showEditResourceModal && !resourceForm.replaceFile"
            >
              <label>当前文件</label>
              <div class="current-file">
                <span>{{ resourceForm.filename }}</span>
                <button
                  type="button"
                  class="replace-file-btn"
                  @click="resourceForm.replaceFile = true"
                >
                  更换文件
                </button>
              </div>
            </div>
            <div class="form-buttons">
              <button type="button" class="cancel-btn" @click="closeModal">
                取消
              </button>
              <button type="submit" class="submit-btn" :disabled="uploading">
                {{ uploading ? "上传中..." : "保存" }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 资源详情弹窗 -->
    <div
      v-if="showResourceDetail"
      class="modal-overlay"
      @click="showResourceDetail = false"
    >
      <div class="modal detail-modal" @click.stop>
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
            <div class="detail-value">
              <span
                class="type-tag"
                :class="getTypeClass(selectedResource.type)"
              >
                {{ selectedResource.type }}
              </span>
            </div>
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
            <div class="detail-label">描述</div>
            <div class="detail-value description">
              {{ selectedResource.description || "无描述" }}
            </div>
          </div>
          <div class="detail-item">
            <div class="detail-label">标签</div>
            <div class="detail-value tags">
              <span
                v-for="(tag, index) in parseResourceTags(selectedResource.tags)"
                :key="index"
                class="tag"
                :class="getTagClass(tag)"
              >
                <span class="tag-icon">{{ getTagIcon(tag) }}</span>
                {{ tag }}
              </span>
              <span
                v-if="
                  !selectedResource.tags ||
                  parseResourceTags(selectedResource.tags).length === 0
                "
              >
                无标签
              </span>
            </div>
          </div>
          <div class="detail-item">
            <div class="detail-label">文件</div>
            <div class="detail-value">
              <button
                class="preview-btn-large"
                @click="showPreview(selectedResource)"
              >
                在线预览
              </button>
              <button
                class="download-btn-large"
                @click="downloadResource(selectedResource)"
              >
                下载文件
              </button>
            </div>
          </div>
          <div
            v-if="
              userRole === 'admin' ||
              (isLoggedIn && selectedResource.authorId === userId)
            "
            class="detail-actions"
          >
            <button class="edit-btn" @click="editResource(selectedResource)">
              编辑资源
            </button>
            <button class="delete-btn" @click="confirmDelete(selectedResource)">
              删除资源
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 删除确认弹窗 -->
    <div v-if="showDeleteModal" class="modal-overlay">
      <div class="modal confirmation-modal" @click.stop>
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
            <button
              class="delete-btn"
              @click="deleteResource"
              :disabled="deleting"
            >
              {{ deleting ? "删除中..." : "删除" }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 提示弹窗 -->
    <div
      v-if="notification.show"
      class="notification"
      :class="notification.type"
    >
      <span>{{ notification.message }}</span>
      <button class="close-btn" @click="notification.show = false">×</button>
    </div>

    <!-- 文件预览弹窗 -->
    <div v-if="showFilePreview" class="modal-overlay" @click="closeFilePreview">
      <FileViewer
        :resourceId="
          previewResource.id ||
          previewResource.resourceId ||
          previewResource.resource_id
        "
        :fileName="
          previewResource.filename ||
          previewResource.file_name ||
          previewResource.fileName ||
          previewResource.file
        "
        :fileUrl="previewUrl"
        :fileType="
          getFileType(
            previewResource.filename ||
              previewResource.file_name ||
              previewResource.fileName ||
              previewResource.file
          )
        "
        @close="closeFilePreview"
        @click.stop
      />
    </div>
  </div>
</template>

<script>
import { ref, computed, reactive, onMounted, watch } from "vue";
import { useStore } from "vuex";
import FileViewer from "@/components/FileViewer.vue";
import {
  getResourceList,
  getResourceDetail,
  uploadResource,
  updateResource,
  deleteResource as apiDeleteResource,
  searchResources,
  getResourcePreviewUrl,
  checkPreviewSupport,
  getResourceTypes,
} from "@/api/resource";
import { getTagList } from "@/api/tag";

export default {
  name: "ResourceManagement",
  components: {
    FileViewer,
  },
  setup() {
    const store = useStore();

    // 用户信息
    const isLoggedIn = computed(() => store.getters["user/isLoggedIn"]);
    const userRole = computed(() => store.getters["user/userRole"]);
    const username = computed(() => store.getters["user/username"]);
    const userId = computed(() => store.getters["user/userId"]);

    // 资源数据
    const resources = ref([]);
    const loading = ref(true);
    const error = ref(null);

    // 分页
    const currentPage = ref(1);
    const pageSize = ref(8);
    const totalPages = computed(
      () => Math.ceil(filteredResources.value.length / pageSize.value) || 1
    );

    const paginatedResources = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value;
      const end = start + pageSize.value;
      return filteredResources.value.slice(start, end);
    });

    // 搜索和筛选
    const searchQuery = ref("");
    const resourceTypeFilter = ref("");
    const activeFilters = computed(() => {
      const filters = [];
      if (resourceTypeFilter.value) filters.push(resourceTypeFilter.value);
      if (searchQuery.value) filters.push(`搜索: ${searchQuery.value}`);
      return filters;
    });

    // 弹窗状态
    const showAddResourceModal = ref(false);
    const showEditResourceModal = ref(false);
    const showResourceDetail = ref(false);
    const showDeleteModal = ref(false);
    const resourceToDelete = ref(null);
    const selectedResource = ref({});
    const uploading = ref(false);
    const deleting = ref(false);

    // 通知提示
    const notification = reactive({
      show: false,
      message: "",
      type: "success",
      timeout: null,
    });

    // 表单数据
    const resourceForm = reactive({
      id: null,
      title: "",
      type: "",
      description: "",
      tags: "",
      file: null,
      filename: "",
      replaceFile: false,
    });

    // 标签数据
    const availableTags = ref([]);
    const selectedTags = ref([]);

    // 预览状态
    const showFilePreview = ref(false);
    const previewResource = ref({});
    const previewUrl = ref("");

    // 资源类型
    const resourceTypes = ref([]);

    // 加载资源类型
    const loadResourceTypes = async () => {
      try {
        const response = await getResourceTypes();
        // 兼容不同的响应格式
        if (response && typeof response === "object") {
          if (Array.isArray(response)) {
            resourceTypes.value = response;
          } else if (response.data && Array.isArray(response.data)) {
            resourceTypes.value = response.data;
          }
        }

        // 如果没有获取到类型数据或数据为空，使用默认类型
        if (!resourceTypes.value || resourceTypes.value.length === 0) {
          resourceTypes.value = ["计算机", "通信", "人工智能"];
        }
      } catch (error) {
        console.error("获取资源类型失败:", error);
        // 使用默认类型
        resourceTypes.value = ["计算机", "通信", "人工智能"];
      }
    };

    // 获取标签列表
    const fetchTags = async () => {
      try {
        const response = await getTagList();
        console.log("获取标签列表响应:", response);

        if (response && typeof response === "object") {
          if (Array.isArray(response)) {
            availableTags.value = response;
          } else if ("data" in response) {
            availableTags.value = response.data || [];
          } else if ("tags" in response) {
            availableTags.value = response.tags || [];
          } else {
            // 尝试查找任何看起来像标签数组的字段
            const possibleArrayField = Object.keys(response).find(
              (key) =>
                Array.isArray(response[key]) &&
                response[key].length > 0 &&
                typeof response[key][0] === "object" &&
                ("tagName" in response[key][0] ||
                  "tag_name" in response[key][0])
            );

            if (possibleArrayField) {
              availableTags.value = response[possibleArrayField];
            } else {
              availableTags.value = [];
              console.warn("未识别的标签响应格式:", response);
            }
          }
        } else {
          availableTags.value = [];
        }

        // 如果没有获取到标签数据，使用测试数据
        if (availableTags.value.length === 0) {
          availableTags.value = [
            { tagId: 1, tagName: "爱国主义", tagType: "theme" },
            { tagId: 2, tagName: "工科", tagType: "subject" },
            { tagId: 3, tagName: "PDF", tagType: "format" },
            { tagId: 4, tagName: "团队协作", tagType: "theme" },
            { tagId: 5, tagName: "文科", tagType: "subject" },
            { tagId: 6, tagName: "Word", tagType: "format" },
          ];
        }
      } catch (error) {
        console.error("获取标签列表失败", error);
        // 使用测试数据
        availableTags.value = [
          { tagId: 1, tagName: "爱国主义", tagType: "theme" },
          { tagId: 2, tagName: "工科", tagType: "subject" },
          { tagId: 3, tagName: "PDF", tagType: "format" },
          { tagId: 4, tagName: "团队协作", tagType: "theme" },
          { tagId: 5, tagName: "文科", tagType: "subject" },
          { tagId: 6, tagName: "Word", tagType: "format" },
        ];
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
              resource.description.toLowerCase().includes(query)) ||
            (resource.tags &&
              resource.tags.some((tag) => tag.toLowerCase().includes(query)))
          );
        }

        return true;
      });
    });

    // 加载资源数据
    const loadResources = async () => {
      loading.value = true;
      error.value = null;

      try {
        console.log("开始加载资源列表...");
        const response = await getResourceList();
        console.log("获取资源列表响应:", response);

        // 处理不同的响应格式
        if (response && typeof response === "object") {
          if ("data" in response) {
            // 标准格式响应，包含data字段
            resources.value = response.data || [];
          } else {
            // 直接返回数据数组
            resources.value = Array.isArray(response) ? response : [];
          }
        } else {
          // 未知格式
          resources.value = [];
          console.error("资源列表响应格式异常:", response);
        }

        console.log("处理后的资源列表:", resources.value);

        // 处理每个资源的标签，清除标点符号
        resources.value.forEach((resource) => {
          if (resource.tags) {
            resource.tags = parseResourceTags(resource.tags);
          }
        });

        // 如果没有资源，添加测试数据（开发阶段使用）
        if (resources.value.length === 0) {
          console.log("没有资源数据，添加测试数据用于展示");
          resources.value = [
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
          ];
        }
      } catch (err) {
        console.error("加载资源失败:", err);
        error.value = "加载资源失败，请稍后重试";
        showNotification(
          "加载资源失败: " + (err.message || "未知错误"),
          "error"
        );
      } finally {
        loading.value = false;
      }
    };

    // 搜索处理
    const handleSearch = () => {
      currentPage.value = 1;
    };

    const removeFilter = (filter) => {
      if (filter.startsWith("搜索:")) {
        searchQuery.value = "";
      } else {
        resourceTypeFilter.value = "";
      }
    };

    const clearFilters = () => {
      searchQuery.value = "";
      resourceTypeFilter.value = "";
    };

    // 获取类型样式类
    const getTypeClass = (type) => {
      // 创建一个颜色映射对象
      const typeMap = {};

      // 动态生成类型映射
      if (resourceTypes.value && resourceTypes.value.length > 0) {
        // 预定义的一些样式类
        const styleClasses = [
          "resource",
          "case",
          "research",
          "custom1",
          "custom2",
        ];

        resourceTypes.value.forEach((type, index) => {
          // 循环使用预定义样式类，如果样式类不够用，则使用默认样式
          const styleIndex = index % styleClasses.length;
          typeMap[type] = styleClasses[styleIndex];
        });
      }

      // 如果找不到对应的样式类，返回默认样式
      return typeMap[type] || "default";
    };

    // 截断文本
    const truncateText = (text, maxLength) => {
      if (!text) return "";
      return text.length > maxLength
        ? text.substring(0, maxLength) + "..."
        : text;
    };

    // 查看资源详情
    const viewResource = async (resource) => {
      try {
        // 增加浏览量需要在后端实现
        const res = await getResourceDetail(resource.id);
        selectedResource.value = res.data || resource;
        showResourceDetail.value = true;
      } catch (err) {
        console.error("获取资源详情失败:", err);
        showNotification("获取资源详情失败", "error");
      }
    };

    // 下载资源
    const downloadResource = async (resource) => {
      try {
        // 使用window.open直接下载
        const downloadUrl = `/api/resources/download/${
          resource.resourceId || resource.id
        }`;
        window.open(downloadUrl, "_blank");
        showNotification("开始下载资源", "success");
      } catch (err) {
        console.error("下载资源失败:", err);
        showNotification("下载资源失败", "error");
      }
    };

    // 编辑资源
    const editResource = (resource) => {
      resourceForm.id = resource.id;
      resourceForm.title = resource.title;
      resourceForm.type = resource.type;
      resourceForm.description = resource.description || "";
      resourceForm.tags = resource.tags ? resource.tags.join(", ") : "";
      resourceForm.filename = resource.filename || "";
      resourceForm.replaceFile = false;

      // 设置标签
      selectedTags.value = resource.tags || [];

      showResourceDetail.value = false;
      showEditResourceModal.value = true;
    };

    // 确认删除资源
    const confirmDelete = (resource) => {
      resourceToDelete.value = resource;
      showResourceDetail.value = false;
      showDeleteModal.value = true;
    };

    // 删除资源
    const deleteResource = async () => {
      if (!resourceToDelete.value) return;

      deleting.value = true;
      try {
        await apiDeleteResource(resourceToDelete.value.id);

        // 从列表中移除
        resources.value = resources.value.filter(
          (resource) => resource.id !== resourceToDelete.value.id
        );

        showDeleteModal.value = false;
        resourceToDelete.value = null;
        showNotification("资源已成功删除", "success");
      } catch (err) {
        console.error("删除资源失败:", err);
        showNotification("删除资源失败，请稍后重试", "error");
      } finally {
        deleting.value = false;
      }
    };

    // 处理文件上传
    const handleFileUpload = (event) => {
      resourceForm.file = event.target.files[0];
    };

    // 提交资源表单
    const submitResourceForm = async () => {
      if (uploading.value) return;
      uploading.value = true;

      try {
        const formData = new FormData();
        formData.append("title", resourceForm.title);
        formData.append("type", resourceForm.type);
        formData.append("description", resourceForm.description);

        // 清除标签中的标点符号
        const cleanedTags = selectedTags.value
          .map((tag) => tag.replace(/["'\[\]]/g, "").trim())
          .filter((tag) => tag);

        // 使用处理过的标签
        formData.append("tags", JSON.stringify(cleanedTags));

        if (resourceForm.file) {
          formData.append("file", resourceForm.file);
        }

        if (showEditResourceModal.value) {
          // 编辑现有资源
          if (resourceForm.replaceFile && resourceForm.file) {
            formData.append("file", resourceForm.file);
          }

          await updateResource(resourceForm.id, formData);

          // 更新本地列表
          const index = resources.value.findIndex(
            (resource) => resource.id === resourceForm.id
          );

          if (index !== -1) {
            resources.value[index] = {
              ...resources.value[index],
              title: resourceForm.title,
              type: resourceForm.type,
              description: resourceForm.description,
              tags: selectedTags.value,
            };
          }

          showNotification("资源已成功更新", "success");
        } else {
          // 添加新资源
          if (resourceForm.file) {
            formData.append("file", resourceForm.file);
          }

          const res = await uploadResource(formData);

          // 添加到列表
          if (res.data) {
            resources.value.unshift(res.data);
          }

          showNotification("资源已成功上传", "success");
        }

        closeModal();
      } catch (err) {
        console.error("提交资源失败:", err);
        showNotification("提交资源失败，请稍后重试", "error");
      } finally {
        uploading.value = false;
      }
    };

    // 显示通知
    const showNotification = (message, type = "success") => {
      if (notification.timeout) {
        clearTimeout(notification.timeout);
      }

      notification.message = message;
      notification.type = type;
      notification.show = true;

      notification.timeout = setTimeout(() => {
        notification.show = false;
      }, 3000);
    };

    // 关闭弹窗
    const closeModal = () => {
      showAddResourceModal.value = false;
      showEditResourceModal.value = false;

      // 重置表单
      resourceForm.id = null;
      resourceForm.title = "";
      resourceForm.type = "";
      resourceForm.description = "";
      resourceForm.tags = "";
      resourceForm.file = null;
      resourceForm.filename = "";
      resourceForm.replaceFile = false;

      // 重置标签
      selectedTags.value = [];
    };

    // 监听搜索参数变化
    watch([searchQuery, resourceTypeFilter], () => {
      currentPage.value = 1;
    });

    // 获取文件类型
    const getFileType = (filename) => {
      if (!filename) return "";

      const extension = filename.split(".").pop().toLowerCase();

      if (["pdf"].includes(extension)) {
        return "pdf";
      } else if (
        ["jpg", "jpeg", "png", "gif", "bmp", "webp"].includes(extension)
      ) {
        return "image";
      } else if (
        ["txt", "log", "md", "json", "xml", "html", "css", "js"].includes(
          extension
        )
      ) {
        return "text";
      } else if (
        ["doc", "docx", "xls", "xlsx", "ppt", "pptx"].includes(extension)
      ) {
        return "office";
      } else if (
        ["mp4", "webm", "ogg", "avi", "mov", "wmv", "flv", "mkv"].includes(
          extension
        )
      ) {
        return "video";
      }

      return "other";
    };

    // 预览资源
    const showPreview = async (resource) => {
      previewResource.value = resource;

      // 尝试多种可能的文件名字段
      const fileName =
        resource.filename ||
        resource.file_name ||
        resource.fileName ||
        resource.file;
      const id = resource.id || resource.resourceId || resource.resource_id;

      if (!fileName) {
        console.error("资源对象:", resource);
        showNotification("无法获取文件名，请检查资源信息", "error");
        return;
      }

      if (!id) {
        console.error("资源对象:", resource);
        showNotification("无法获取资源ID，请检查资源信息", "error");
        return;
      }

      try {
        // 检查文件是否支持预览
        const response = await checkPreviewSupport(id, fileName);
        console.log("预览支持检查响应:", response);

        if (response && response.supported) {
          // 获取预览URL
          previewUrl.value = getResourcePreviewUrl(id);
          showFilePreview.value = true;
        } else {
          // 不支持预览的文件类型
          showNotification(`不支持预览该文件类型：${fileName}`, "error");

          // 提示用户下载
          setTimeout(() => {
            if (confirm(`文件 ${fileName} 不支持在线预览，是否下载查看？`)) {
              downloadResource(resource);
            }
          }, 500);
        }
      } catch (error) {
        console.error("检查预览支持失败:", error);
        showNotification("检查预览支持失败，请尝试下载文件", "error");
      }
    };

    // 关闭文件预览
    const closeFilePreview = () => {
      showFilePreview.value = false;
      previewUrl.value = "";
    };

    // 根据标签内容确定标签类型和样式
    const getTagClass = (tag) => {
      // 主题相关标签
      if (
        tag.includes("主义") ||
        tag.includes("精神") ||
        tag.includes("价值观") ||
        tag.includes("伦理")
      ) {
        return "tag-theme";
      }
      // 学科相关标签
      else if (
        tag.includes("计算机") ||
        tag.includes("网络") ||
        tag.includes("人工智能") ||
        tag.includes("数据") ||
        tag.includes("结构") ||
        tag.includes("工程")
      ) {
        return "tag-subject";
      }
      // 格式相关标签
      else if (
        tag.includes("PDF") ||
        tag.includes("PPT") ||
        tag.includes("Word") ||
        tag.includes("Excel") ||
        tag.includes("视频")
      ) {
        return "tag-format";
      }
      // 默认样式
      return "tag-default";
    };

    // 为不同类型的标签提供图标
    const getTagIcon = (tag) => {
      // 主题相关标签
      if (
        tag.includes("主义") ||
        tag.includes("精神") ||
        tag.includes("价值观") ||
        tag.includes("伦理")
      ) {
        return "🔮";
      }
      // 学科相关标签
      else if (
        tag.includes("计算机") ||
        tag.includes("网络") ||
        tag.includes("人工智能") ||
        tag.includes("数据") ||
        tag.includes("结构") ||
        tag.includes("工程")
      ) {
        return "📚";
      }
      // 格式相关标签
      else if (
        tag.includes("PDF") ||
        tag.includes("PPT") ||
        tag.includes("Word") ||
        tag.includes("Excel") ||
        tag.includes("视频")
      ) {
        return "📄";
      }
      // 默认图标
      return "🏷️";
    };

    // 处理标签数据，确保是数组格式
    const parseResourceTags = (tags) => {
      if (!tags) return [];

      // 如果已经是数组格式，处理每个标签去除标点符号
      if (Array.isArray(tags)) {
        return tags
          .map((tag) => {
            // 去除引号、方括号等标点符号
            return tag.replace(/["'\[\]]/g, "").trim();
          })
          .filter((tag) => tag);
      }

      // 如果是逗号分隔的字符串，转换为数组
      if (typeof tags === "string") {
        return tags
          .split(",")
          .map((tag) => tag.replace(/["'\[\]]/g, "").trim())
          .filter((tag) => tag);
      }

      // 其他情况返回空数组
      return [];
    };

    // 组件挂载时加载资源和标签列表
    onMounted(() => {
      loadResources();
      fetchTags();
      loadResourceTypes().then(() => {
        // 设置默认类型为第一个类型
        if (resourceTypes.value.length > 0) {
          resourceForm.type = resourceTypes.value[0];
        }
      });
    });

    return {
      resources,
      filteredResources,
      paginatedResources,
      currentPage,
      totalPages,
      searchQuery,
      resourceTypeFilter,
      activeFilters,
      showAddResourceModal,
      showEditResourceModal,
      showResourceDetail,
      showDeleteModal,
      resourceToDelete,
      selectedResource,
      resourceForm,
      loading,
      error,
      uploading,
      deleting,
      isLoggedIn,
      userRole,
      username,
      userId,
      handleSearch,
      removeFilter,
      clearFilters,
      getTypeClass,
      truncateText,
      viewResource,
      downloadResource,
      editResource,
      confirmDelete,
      deleteResource,
      handleFileUpload,
      submitResourceForm,
      closeModal,
      notification,
      availableTags,
      selectedTags,
      getTagTypeClass,
      getTagTypeLabel,
      showFilePreview,
      previewResource,
      previewUrl,
      getFileType,
      showPreview,
      closeFilePreview,
      getTagClass,
      getTagIcon,
      parseResourceTags,
      resourceTypes,
    };
  },
};
</script>

<style scoped>
.resource-management {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
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
  font-size: 1.8rem;
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
  font-weight: bold;
}

.filter-tags {
  margin-bottom: 1.5rem;
}

.active-filters {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.active-filters span {
  color: #666;
}

.tag {
  display: inline-flex;
  align-items: center;
  background-color: #f5f5f5;
  color: #666;
  padding: 4px 8px;
  border-radius: 16px;
  font-size: 0.8rem;
  transition: all 0.2s ease;
  border: 1px solid transparent;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 3px 5px rgba(0, 0, 0, 0.1);
}

.tag-icon {
  margin-right: 4px;
  font-size: 0.9rem;
}

.tag-theme {
  background-color: #f6ffed;
  color: #52c41a;
  border-color: #b7eb8f;
}

.tag-subject {
  background-color: #e6f7ff;
  color: #1890ff;
  border-color: #91d5ff;
}

.tag-format {
  background-color: #fff7e6;
  color: #fa8c16;
  border-color: #ffd591;
}

.tag-default {
  background-color: #f5f5f5;
  color: #666;
  border-color: #d9d9d9;
}

.remove-filter {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 0.9rem;
  margin-left: 5px;
  color: #999;
}

.clear-filters {
  background: none;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 0.2rem 0.5rem;
  font-size: 0.8rem;
  color: #666;
  cursor: pointer;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem 0;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f0f0f0;
  border-radius: 50%;
  border-top: 4px solid #1890ff;
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

.no-resources {
  text-align: center;
  padding: 3rem 0;
  color: #666;
}

.resource-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(270px, 1fr));
  gap: 1.5rem;
}

.resource-card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 1.2rem;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  display: flex;
  flex-direction: column;
  height: 280px;
}

.resource-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.8rem;
}

.views {
  color: #999;
  font-size: 0.8rem;
}

.card-title {
  margin: 0 0 0.5rem 0;
  font-size: 1.1rem;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.3;
  height: 2.6em;
}

.card-meta {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 0.8rem;
  margin-bottom: 0.8rem;
}

.card-desc {
  color: #666;
  font-size: 0.9rem;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  flex-grow: 1;
  margin-bottom: 0.8rem;
}

.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 1rem;
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

.card-actions {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}

.action-btn {
  flex: 1;
  padding: 0.4rem 0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  text-align: center;
}

.preview-btn {
  background-color: #52c41a;
  color: white;
}

.preview-btn-large {
  padding: 0.5rem 1rem;
  background-color: #52c41a;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  margin-right: 10px;
}

.download-btn {
  background-color: #1890ff;
  color: white;
}

.edit-btn {
  background-color: #faad14;
  color: white;
}

.download-btn-large {
  padding: 0.5rem 1rem;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 2rem;
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

.required {
  color: #ff4d4f;
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

.file-hint {
  font-size: 0.8rem;
  color: #999;
  margin-top: 0.5rem;
}

.current-file {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.replace-file-btn {
  background: none;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 0.3rem 0.5rem;
  color: #666;
  cursor: pointer;
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

.submit-btn:disabled {
  background-color: #91caff;
  cursor: not-allowed;
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

.detail-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 1.5rem;
  border-top: 1px solid #f0f0f0;
  padding-top: 1.5rem;
}

.detail-actions button {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.delete-btn {
  background-color: #ff4d4f;
  color: white;
}

.delete-btn:disabled {
  background-color: #ffa39e;
  cursor: not-allowed;
}

/* 通知样式 */
.notification {
  position: fixed;
  bottom: 20px;
  right: 20px;
  padding: 1rem;
  border-radius: 4px;
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  justify-content: space-between;
  z-index: 1100;
  min-width: 300px;
}

.notification.success {
  background-color: #52c41a;
}

.notification.error {
  background-color: #ff4d4f;
}

.notification .close-btn {
  color: white;
  font-size: 1rem;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .resource-management {
    padding: 1rem;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .search-bar {
    width: 100%;
  }

  .search-bar input {
    flex: 1;
  }

  .resource-grid {
    grid-template-columns: 1fr;
  }

  .modal {
    width: 95%;
  }
}

/* 标签相关样式 */
.tag-select-container {
  width: 100%;
}

.tag-type-indicator {
  margin-left: 8px;
  font-size: 11px;
  height: 20px;
  line-height: 18px;
}

:deep(.el-select .el-select__tags .el-tag) {
  background-color: #e6f7ff;
  border-color: #91d5ff;
  color: #1890ff;
  margin: 2px 4px 2px 0;
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-select .el-input__wrapper) {
  background-color: #fff;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  padding: 0 11px;
  width: 100%;
}

:deep(.el-select .el-input__inner) {
  height: 36px;
  line-height: 36px;
}

:deep(.el-select .el-tag__close) {
  color: #1890ff;
  background-color: transparent;
}

:deep(.el-select .el-tag__close:hover) {
  background-color: #1890ff;
  color: #fff;
}
</style>
