<template>
  <div class="resource-management">
    <div class="section-header">
      <h2>资源管理</h2>
      <div class="search-bar">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="搜索资源..."
          @keyup.enter="handleSearch"
        />
        <el-select
          v-model="resourceTypeFilter"
          placeholder="筛选类型"
          clearable
        >
          <el-option value="" label="全部类型" />
          <el-option
            v-for="type in resourceTypes"
            :key="type.typeId"
            :label="type.typeName"
            :value="type.typeId"
          />
        </el-select>
        <button class="search-btn" @click="handleSearch">搜索</button>
      </div>
      <button class="add-btn" @click="showAddResourceModal = true">
        上传资源
      </button>
    </div>

    <div class="table-container" v-loading="isLoading">
      <div v-if="!isLoading && filteredResources.length === 0" class="no-data">
        暂无数据
      </div>
      <table v-else class="resource-table">
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
              <button
                class="preview-btn"
                @click="handlePreviewResource(resource)"
              >
                预览
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

    <div class="pagination" v-if="totalPages > 1">
      <el-pagination
        background
        layout="prev, pager, next, sizes"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        :current-page="currentPage"
        :page-size="pageSize"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
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
                <div class="tag-quick-filters">
                  <div class="filter-label">快速筛选:</div>
                  <div
                    class="filter-tag"
                    @click="filterTagsByType('theme')"
                    :class="{ active: currentTagFilter === 'theme' }"
                  >
                    主题
                  </div>
                  <div
                    class="filter-tag"
                    @click="filterTagsByType('subject')"
                    :class="{ active: currentTagFilter === 'subject' }"
                  >
                    学科
                  </div>
                  <div
                    class="filter-tag"
                    @click="filterTagsByType('format')"
                    :class="{ active: currentTagFilter === 'format' }"
                  >
                    格式
                  </div>
                  <div
                    class="filter-tag"
                    @click="filterTagsByType('all')"
                    :class="{ active: currentTagFilter === 'all' }"
                  >
                    全部
                  </div>
                </div>
                <div class="popular-tags" v-if="popularTags.length > 0">
                  <div class="popular-tags-title">推荐标签:</div>
                  <div class="popular-tags-list">
                    <span
                      v-for="tag in popularTags"
                      :key="tag.tagId"
                      class="popular-tag"
                      :class="getTagTypeClass(tag.tagType)"
                      @click="addTag(tag.tagName)"
                    >
                      {{ tag.tagName }}
                    </span>
                  </div>
                </div>
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
                    v-for="tag in filteredTags"
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
              <input type="file" @change="handleFileUpload" required />
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
            </div>
          </div>
          <div class="detail-item">
            <div class="detail-label">文件</div>
            <div class="detail-value">
              <button
                class="download-btn"
                @click="handleDownload(selectedResource.id)"
              >
                下载文件 {{ selectedResource.filename }}
              </button>
              <button
                class="preview-btn"
                @click="handlePreviewResource(selectedResource)"
                style="margin-left: 10px"
              >
                在线预览
              </button>
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

    <!-- 资源预览弹窗 -->
    <el-dialog
      v-model="showPreviewModal"
      :title="previewingResource?.filename || '文件预览'"
      width="80%"
      :before-close="closePreviewModal"
      :close-on-click-modal="false"
      class="preview-modal"
    >
      <div v-loading="isLoadingPreview" class="preview-container">
        <div
          v-if="!isLoadingPreview && !previewSupported"
          class="preview-not-supported"
        >
          <el-alert
            title="该文件类型不支持在线预览"
            type="warning"
            :closable="false"
            show-icon
          />
          <p class="mt-3">文件名: {{ previewingResource?.filename }}</p>
          <el-button
            type="primary"
            @click="handleDownload(previewingResource.id)"
            class="mt-3"
          >
            下载文件
          </el-button>
        </div>

        <!-- PDF预览 -->
        <div v-if="previewType === 'pdf'" class="pdf-container">
          <iframe
            :src="previewUrl"
            width="100%"
            height="600"
            frameborder="0"
          ></iframe>
        </div>

        <!-- 图片预览 -->
        <div v-if="previewType === 'image'" class="image-container">
          <img :src="previewUrl" alt="图片预览" />
        </div>

        <!-- 视频预览 -->
        <div v-if="previewType === 'video'" class="video-container">
          <video controls width="100%">
            <source
              :src="previewUrl"
              :type="`video/${getFileExtension(previewingResource.filename)}`"
            />
            您的浏览器不支持视频播放
          </video>
        </div>

        <!-- 音频预览 -->
        <div v-if="previewType === 'audio'" class="audio-container">
          <audio controls>
            <source
              :src="previewUrl"
              :type="`audio/${getFileExtension(previewingResource.filename)}`"
            />
            您的浏览器不支持音频播放
          </audio>
        </div>

        <!-- 文本预览 -->
        <div v-if="previewType === 'text'" class="text-container">
          <pre>{{ textContent }}</pre>
        </div>

        <!-- Office预览 -->
        <div v-if="previewType === 'office'" class="office-container">
          <iframe
            :src="officePreviewUrl"
            width="100%"
            height="600"
            frameborder="0"
          ></iframe>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, reactive, onMounted } from "vue";
import axios from "axios";
import { getTagList } from "@/api/tag";
import {
  getResourceList,
  searchResources,
  uploadResource,
  updateResource,
  deleteResource,
  getResourceTypes,
  downloadResource,
  getResourcePreviewUrl,
  checkPreviewSupport,
  updateResourceReviewStatus,
} from "@/api/resource";
import { ElMessage, ElLoading } from "element-plus";
import { getAllTypes } from "@/api/type";

export default {
  name: "ResourceManagement",
  setup() {
    // 资源数据
    const resources = ref([]);
    const isLoading = ref(false);

    // 分页
    const currentPage = ref(1);
    const pageSize = ref(10);
    const total = ref(0);
    const totalPages = computed(() => Math.ceil(total.value / pageSize.value));

    // 搜索和筛选
    const searchQuery = ref("");
    const resourceTypeFilter = ref("");

    // 弹窗状态
    const showAddResourceModal = ref(false);
    const showEditResourceModal = ref(false);
    const showResourceDetail = ref(false);
    const showDeleteModal = ref(false);
    const showPreviewModal = ref(false);
    const resourceToDelete = ref(null);
    const selectedResource = ref({});
    const previewingResource = ref(null);
    const previewUrl = ref("");
    const previewType = ref("other");
    const previewSupported = ref(false);
    const isLoadingPreview = ref(false);
    const textContent = ref("");
    const officePreviewUrl = ref("");

    // 资源类型
    const resourceTypes = ref([]);

    // 加载资源类型
    const loadResourceTypes = async () => {
      try {
        const response = await getAllTypes();
        if (response && typeof response === "object") {
          if (Array.isArray(response)) {
            resourceTypes.value = response;
          } else if (response.data && Array.isArray(response.data)) {
            resourceTypes.value = response.data;
          } else if (response.code === 200 && Array.isArray(response.data)) {
            resourceTypes.value = response.data;
          }
        }

        // 确保类型数据格式正确
        if (resourceTypes.value && resourceTypes.value.length > 0) {
          // 检查是否每个类型都有typeId和typeName
          const hasCorrectFormat = resourceTypes.value.every(
            (type) =>
              typeof type === "object" && "typeId" in type && "typeName" in type
          );

          if (!hasCorrectFormat) {
            // 尝试将简单字符串转换为对象格式
            resourceTypes.value = resourceTypes.value.map((type, index) => {
              if (typeof type === "string") {
                return { typeId: index + 1, typeName: type };
              } else if (
                typeof type === "object" &&
                !("typeId" in type) &&
                !("typeName" in type)
              ) {
                // 尝试从其他字段推断
                const id = type.id || index + 1;
                const name =
                  type.name || type.type || type.value || `类型${id}`;
                return { typeId: id, typeName: name };
              }
              return type;
            });
          }
        }

        if (!resourceTypes.value || resourceTypes.value.length === 0) {
          console.error("未获取到类型数据");
          // 使用默认数据
          resourceTypes.value = [
            { typeId: 1, typeName: "计算机" },
            { typeId: 2, typeName: "通信" },
            { typeId: 3, typeName: "人工智能" },
          ];
        }
      } catch (error) {
        console.error("获取资源类型失败:", error);
        // 使用默认数据
        resourceTypes.value = [
          { typeId: 1, typeName: "计算机" },
          { typeId: 2, typeName: "通信" },
          { typeId: 3, typeName: "人工智能" },
        ];
      }
    };

    // 表单数据
    const resourceForm = reactive({
      id: null,
      title: "",
      type: "",
      typeId: null,
      description: "",
      tags: [],
      file: null,
    });

    // 标签数据
    const availableTags = ref([]);
    const selectedTags = ref([]);
    const popularTags = ref([]);
    const currentTagFilter = ref("all");

    // 过滤后的标签列表
    const filteredTags = computed(() => {
      if (currentTagFilter.value === "all") {
        return availableTags.value;
      } else {
        return availableTags.value.filter(
          (tag) => tag.tagType === currentTagFilter.value
        );
      }
    });

    // 按类型过滤标签
    const filterTagsByType = (type) => {
      currentTagFilter.value = type;
    };

    // 添加标签到已选中
    const addTag = (tagName) => {
      const cleanedTag = tagName.replace(/["'\[\]]/g, "").trim();
      if (cleanedTag && !selectedTags.value.includes(cleanedTag)) {
        selectedTags.value.push(cleanedTag);
      }
    };

    // 获取标签列表
    const fetchTags = async () => {
      try {
        const response = await getTagList();
        availableTags.value = response;
        popularTags.value = response
          .sort(() => 0.5 - Math.random())
          .slice(0, 5);
      } catch (error) {
        console.error("获取标签列表失败", error);
        availableTags.value = [
          { tagId: 1, tagName: "爱国主义", tagType: "theme" },
          { tagId: 2, tagName: "工科", tagType: "subject" },
          { tagId: 3, tagName: "PDF", tagType: "format" },
          { tagId: 4, tagName: "团队协作", tagType: "theme" },
          { tagId: 5, tagName: "文科", tagType: "subject" },
          { tagId: 6, tagName: "Word", tagType: "format" },
        ];
        popularTags.value = availableTags.value.slice(0, 3);
      }
    };

    // 获取资源列表
    const fetchResources = async () => {
      isLoading.value = true;
      try {
        const params = {
          page: currentPage.value,
          size: pageSize.value,
          query: searchQuery.value || undefined,
        };

        // 添加类型ID过滤条件
        if (resourceTypeFilter.value) {
          // 如果是数字，则作为typeId处理
          if (!isNaN(resourceTypeFilter.value)) {
            params.typeId = resourceTypeFilter.value;
          }
          // 否则按传统类型字段过滤
          else {
            params.type = resourceTypeFilter.value;
          }
        }

        const response = await getResourceList(params);

        // 处理不同格式的响应
        let resourceList = [];
        let totalCount = 0;

        if (response) {
          // 检查是否是分页格式的响应
          if (response.data && Array.isArray(response.data.data)) {
            resourceList = response.data.data;
            totalCount = response.data.total || 0;
            // 更新分页信息
            currentPage.value = response.data.page || currentPage.value;
            pageSize.value = response.data.size || pageSize.value;
            totalPages.value =
              response.data.totalPages ||
              Math.ceil(totalCount / pageSize.value);
          }
          // 检查data是否为数组
          else if (Array.isArray(response)) {
            resourceList = response;
            totalCount = response.length;
          }
          // 检查data.data是否为数组
          else if (response.data && Array.isArray(response.data)) {
            resourceList = response.data;
            totalCount =
              response.data.total || response.total || response.data.length;
          }
          // 检查data是否为单个对象
          else if (typeof response === "object" && !Array.isArray(response)) {
            resourceList = [response];
            totalCount = 1;
          }
        }

        // 格式化资源数据，确保字段名称一致
        resources.value = resourceList.map((item) => {
          return {
            resourceId: item.resourceId || item.id,
            title: item.title,
            type: item.type,
            typeId: item.typeId,
            description: item.description,
            filePath: item.filePath,
            fileName: item.fileName,
            fileSize: item.fileSize,
            format: item.format,
            uploadTime: item.uploadTime,
            downloadCount: item.downloadCount,
            viewCount: item.viewCount,
            rating: item.rating,
            uploaderId: item.uploaderId,
            reviewStatus: item.reviewStatus || item.status || "pending",
            tags: item.tags || [],
            // 以下为前端展示需要的额外数据
            author: item.author || "未知用户", // 上传者名称
            uploadDate: formatDate(item.uploadTime || new Date()),
            views: item.viewCount || 0,
            downloads: item.downloadCount || 0,
            status: item.reviewStatus || item.status || "pending",
          };
        });

        total.value = totalCount;
        totalPages.value = Math.ceil(total.value / pageSize.value);
      } catch (error) {
        console.error("获取资源列表失败:", error);
        ElMessage.error("获取资源列表失败，请稍后重试");
        resources.value = [];
        total.value = 0;
        totalPages.value = 0;
      } finally {
        isLoading.value = false;
      }
    };

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return "-";
      const date = new Date(dateString);
      return date.toLocaleDateString();
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

    // 过滤后的资源列表 - 现在通过API筛选，这个computed不再需要处理筛选逻辑
    const filteredResources = computed(() => {
      return resources.value;
    });

    // 搜索处理
    const handleSearch = () => {
      currentPage.value = 1;
      fetchResources();
    };

    // 获取类型样式类
    const getTypeClass = (type) => {
      const typeMap = {};
      if (resourceTypes.value && resourceTypes.value.length > 0) {
        const styleClasses = [
          "resource",
          "case",
          "research",
          "custom1",
          "custom2",
        ];

        resourceTypes.value.forEach((type, index) => {
          const styleIndex = index % styleClasses.length;
          typeMap[type] = styleClasses[styleIndex];
        });
      }
      return typeMap[type] || "default";
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
      selectedTags.value = parseResourceTags(resource.tags);
      showEditResourceModal.value = true;
    };

    // 审核通过资源
    const approveResource = async (resource) => {
      try {
        const loading = ElLoading.service({
          text: "审核中...",
          background: "rgba(255, 255, 255, 0.7)",
        });

        // 使用新的专用API更新审核状态
        const result = await updateResourceReviewStatus(
          resource.resourceId,
          "approved"
        );

        // 判断操作是否成功的逻辑改进
        if (result) {
          const index = resources.value.findIndex(
            (r) => r.resourceId === resource.resourceId
          );
          if (index !== -1) {
            resources.value[index].reviewStatus = "approved";
          }
          ElMessage.success("审核通过成功");
        } else {
          // 如果result为空但没有触发catch，认为是操作成功但返回数据为空
          const index = resources.value.findIndex(
            (r) => r.resourceId === resource.resourceId
          );
          if (index !== -1) {
            resources.value[index].reviewStatus = "approved";
          }
          ElMessage.success("审核通过成功");
        }

        loading.close();
      } catch (error) {
        console.error("审核失败:", error);
        ElMessage.error("审核失败: " + (error.message || "未知错误"));
      }
    };

    // 拒绝资源
    const rejectResource = async (resource) => {
      try {
        const loading = ElLoading.service({
          text: "操作中...",
          background: "rgba(255, 255, 255, 0.7)",
        });

        // 使用新的专用API更新审核状态
        const result = await updateResourceReviewStatus(
          resource.resourceId,
          "rejected"
        );

        // 判断操作是否成功的逻辑改进
        if (result) {
          const index = resources.value.findIndex(
            (r) => r.resourceId === resource.resourceId
          );
          if (index !== -1) {
            resources.value[index].reviewStatus = "rejected";
          }
          ElMessage.success("已拒绝该资源");
        } else {
          // 如果result为空但没有触发catch，认为是操作成功但返回数据为空
          const index = resources.value.findIndex(
            (r) => r.resourceId === resource.resourceId
          );
          if (index !== -1) {
            resources.value[index].reviewStatus = "rejected";
          }
          ElMessage.success("已拒绝该资源");
        }

        loading.close();
      } catch (error) {
        console.error("拒绝资源失败:", error);
        ElMessage.error("操作失败: " + (error.message || "未知错误"));
      }
    };

    // 确认删除资源
    const confirmDelete = (resource) => {
      resourceToDelete.value = resource;
      showDeleteModal.value = true;
    };

    // 删除资源
    const deleteResourceItem = async () => {
      if (resourceToDelete.value) {
        try {
          const loading = ElLoading.service({
            text: "删除中...",
            background: "rgba(255, 255, 255, 0.7)",
          });

          await deleteResource(resourceToDelete.value.id);

          resources.value = resources.value.filter(
            (resource) => resource.id !== resourceToDelete.value.id
          );

          ElMessage.success("资源已成功删除");
          showDeleteModal.value = false;
          resourceToDelete.value = null;
          loading.close();
        } catch (error) {
          console.error("删除资源失败:", error);
          ElMessage.error("删除资源失败");
        }
      }
    };

    // 处理文件上传
    const handleFileUpload = (event) => {
      resourceForm.file = event.target.files[0];
    };

    // 下载资源
    const handleDownload = async (resourceId) => {
      try {
        const loading = ElLoading.service({
          text: "下载中...",
          background: "rgba(255, 255, 255, 0.7)",
        });

        const response = await downloadResource(resourceId);

        // 处理下载文件
        const blob = new Blob([response], { type: response.type });
        const link = document.createElement("a");
        const fileName = selectedResource.value.filename || "download.file";

        link.href = URL.createObjectURL(blob);
        link.download = fileName;
        link.click();

        loading.close();
        ElMessage.success("下载成功");
      } catch (error) {
        console.error("下载失败:", error);
        ElMessage.error("下载失败");
      }
    };

    // 提交资源表单
    const submitResourceForm = async () => {
      try {
        const loading = ElLoading.service({
          text: "保存中...",
          background: "rgba(255, 255, 255, 0.7)",
        });

        const formData = new FormData();
        formData.append("title", resourceForm.value.title);
        formData.append("type", resourceForm.value.type);

        // 如果typeId存在，也添加到表单中
        if (resourceForm.value.typeId) {
          formData.append("typeId", resourceForm.value.typeId);
        }

        if (resourceForm.value.description) {
          formData.append("description", resourceForm.value.description);
        }

        // 处理标签
        if (selectedTags.value && selectedTags.value.length > 0) {
          formData.append("tags", JSON.stringify(selectedTags.value));
        }

        // 如果是编辑模式且不上传新文件，则不添加文件字段
        if (!showEditResourceModal.value && resourceForm.value.file) {
          formData.append("file", resourceForm.value.file);
        }

        let result;
        if (showEditResourceModal.value) {
          // 编辑资源
          result = await updateResource(
            selectedResource.value.resourceId,
            formData
          );
        } else {
          // 上传新资源
          result = await uploadResource(formData);
        }

        // 关闭加载动画
        loading.close();

        // 处理结果
        if (result && (result.code === 200 || result.code === 0)) {
          ElMessage.success(
            showEditResourceModal.value ? "资源更新成功" : "资源上传成功"
          );
          closeModal();
          fetchResources(); // 刷新资源列表
        } else {
          ElMessage.error(
            (showEditResourceModal.value ? "更新" : "上传") +
              "失败: " +
              (result.message || "未知错误")
          );
        }
      } catch (error) {
        console.error("表单提交失败:", error);
        ElMessage.error(
          (showEditResourceModal.value ? "更新" : "上传") +
            "失败: " +
            (error.message || "未知错误")
        );
      }
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
      resourceForm.tags = [];
      resourceForm.file = null;
      selectedTags.value = [];
    };

    // 处理页面变化
    const handlePageChange = (page) => {
      currentPage.value = page;
      fetchResources();
    };

    // 处理每页数量变化
    const handleSizeChange = (size) => {
      pageSize.value = size;
      currentPage.value = 1;
      fetchResources();
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

      // 如果已经是数组格式
      if (Array.isArray(tags)) {
        return tags
          .map((tag) => {
            return tag.replace(/["'\[\]]/g, "").trim();
          })
          .filter((tag) => tag);
      }

      // 如果是JSON格式字符串，解析为数组
      try {
        const parsedTags = JSON.parse(tags);
        if (Array.isArray(parsedTags)) {
          return parsedTags
            .map((tag) => tag.replace(/["'\[\]]/g, "").trim())
            .filter((tag) => tag);
        }
      } catch (e) {
        // 解析JSON失败，尝试其他方式
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

    // 预览资源
    const handlePreviewResource = async (resource) => {
      previewingResource.value = { ...resource };
      showPreviewModal.value = true;
      isLoadingPreview.value = true;
      previewSupported.value = false;
      textContent.value = "";
      officePreviewUrl.value = "";

      try {
        // 获取文件扩展名
        const fileExt = getFileExtension(resource.filename).toLowerCase();

        // 构建预览URL - 修正API路径
        const baseUrl = `/api/resources/preview/${resource.id}`;
        previewUrl.value = baseUrl;

        // 根据文件类型设置预览方式
        if (["pdf"].includes(fileExt)) {
          previewType.value = "pdf";
          previewSupported.value = true;
        } else if (
          ["jpg", "jpeg", "png", "gif", "bmp", "webp"].includes(fileExt)
        ) {
          previewType.value = "image";
          previewSupported.value = true;
        } else if (["mp4", "webm", "ogg", "mov"].includes(fileExt)) {
          previewType.value = "video";
          previewSupported.value = true;
        } else if (["mp3", "wav", "ogg", "aac"].includes(fileExt)) {
          previewType.value = "audio";
          previewSupported.value = true;
        } else if (
          [
            "txt",
            "md",
            "json",
            "xml",
            "html",
            "css",
            "js",
            "java",
            "py",
            "c",
            "cpp",
          ].includes(fileExt)
        ) {
          previewType.value = "text";
          previewSupported.value = true;

          // 修正获取文本内容的API请求
          try {
            const response = await axios.get(
              `/api/resources/preview/${resource.id}`,
              {
                headers: {
                  Authorization: `Bearer ${localStorage.getItem("token")}`,
                },
                responseType: "text",
              }
            );
            textContent.value = response.data;
          } catch (error) {
            console.error("获取文本内容失败:", error);
            textContent.value = "无法加载文本内容";
            previewSupported.value = true; // 仍然显示预览容器，但内容为错误信息
          }
        } else if (
          ["doc", "docx", "xls", "xlsx", "ppt", "pptx"].includes(fileExt)
        ) {
          previewType.value = "office";
          previewSupported.value = true;
          // 修正Office文件预览服务的URL
          officePreviewUrl.value = `/api/resources/office-preview/${resource.id}`;
        } else {
          previewSupported.value = false;
        }
      } catch (error) {
        console.error("预览文件失败:", error);
        ElMessage.error("预览文件失败");
        previewSupported.value = false;
      } finally {
        isLoadingPreview.value = false;
      }
    };

    // 关闭预览弹窗
    const closePreviewModal = () => {
      showPreviewModal.value = false;
      previewingResource.value = null;
      previewUrl.value = "";
      previewType.value = "other";
      textContent.value = "";
      officePreviewUrl.value = "";
    };

    // 获取文件扩展名
    const getFileExtension = (filename) => {
      if (!filename) return "";
      return filename.split(".").pop() || "";
    };

    // 格式化文件大小
    const formatFileSize = (size) => {
      if (!size) return "0 Bytes";
      const units = ["Bytes", "KB", "MB", "GB", "TB"];
      const i = Math.floor(Math.log(size) / Math.log(1024));
      return Math.round(size / Math.pow(1024, i)) + " " + units[i];
    };

    onMounted(() => {
      fetchTags();
      loadResourceTypes().then(() => {
        // 设置默认类型为第一个类型
        if (resourceTypes.value.length > 0) {
          resourceForm.type = resourceTypes.value[0];
        }
      });
      fetchResources();
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
      showPreviewModal,
      resourceToDelete,
      selectedResource,
      previewingResource,
      resourceForm,
      handleSearch,
      getTypeClass,
      getStatusName,
      viewResource,
      editResource,
      approveResource,
      rejectResource,
      confirmDelete,
      deleteResource: deleteResourceItem,
      handleFileUpload,
      submitResourceForm,
      closeModal,
      availableTags,
      filteredTags,
      selectedTags,
      popularTags,
      currentTagFilter,
      filterTagsByType,
      addTag,
      getTagTypeClass,
      getTagTypeLabel,
      getTagClass,
      getTagIcon,
      parseResourceTags,
      resourceTypes,
      handleDownload,
      handlePageChange,
      handleSizeChange,
      isLoading,
      total,
      pageSize,
      previewUrl,
      previewType,
      previewSupported,
      isLoadingPreview,
      textContent,
      officePreviewUrl,
      closePreviewModal,
      getFileExtension,
      formatFileSize,
      handlePreviewResource,
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

.search-btn {
  padding: 0.5rem 1rem;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
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
  min-height: 200px;
  position: relative;
}

.no-data {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  color: #999;
  font-size: 14px;
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
  max-height: 200px;
  overflow-y: auto;
}

.detail-value.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag {
  display: inline-flex;
  align-items: center;
  padding: 4px 8px;
  background-color: #f5f5f5;
  color: #666;
  border-radius: 16px;
  font-size: 0.8rem;
  margin-right: 0.5rem;
  margin-bottom: 0.5rem;
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

.download-btn {
  color: white;
  background-color: #1890ff;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.download-btn:hover {
  background-color: #40a9ff;
}

.tag-select-container {
  width: 100%;
  margin-bottom: 10px;
}

.tag-quick-filters {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 12px;
  gap: 8px;
}

.filter-label {
  font-size: 0.9rem;
  color: #666;
  margin-right: 6px;
}

.filter-tag {
  display: inline-block;
  padding: 4px 10px;
  background-color: #f0f0f0;
  color: #666;
  border-radius: 4px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-tag:hover {
  background-color: #e0e0e0;
}

.filter-tag.active {
  background-color: #1890ff;
  color: white;
}

.popular-tags {
  margin-bottom: 12px;
}

.popular-tags-title {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 6px;
}

.popular-tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.popular-tag {
  display: inline-block;
  padding: 3px 8px;
  border-radius: 12px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px dashed;
}

.popular-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.popular-tag.success {
  background-color: #f6ffed;
  color: #52c41a;
  border-color: #b7eb8f;
}

.popular-tag.primary {
  background-color: #e6f7ff;
  color: #1890ff;
  border-color: #91d5ff;
}

.popular-tag.warning {
  background-color: #fff7e6;
  color: #fa8c16;
  border-color: #ffd591;
}

.tag-type-indicator {
  margin-left: 8px;
  font-size: 11px;
  height: 20px;
  line-height: 20px;
}

/* 预览弹窗样式 */
.preview-modal {
  width: 90%;
  max-width: 1000px;
  max-height: 90vh;
}

.preview-container {
  min-height: 400px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.pdf-container,
.image-container,
.video-container,
.text-container,
.office-container,
.generic-preview {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.pdf-container iframe {
  width: 100%;
  height: 600px;
  border: 1px solid #eee;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.image-container img {
  max-width: 100%;
  max-height: 600px;
  object-fit: contain;
  border: 1px solid #eee;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.video-container video {
  max-width: 100%;
  max-height: 600px;
  border: 1px solid #eee;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.text-container {
  width: 100%;
  height: 600px;
  overflow-y: auto;
  background-color: #f5f5f5;
  border: 1px solid #eee;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 16px;
}

.text-content {
  white-space: pre-wrap;
  font-family: monospace;
  width: 100%;
  font-size: 14px;
  line-height: 1.6;
}

.office-container {
  width: 100%;
}

.office-preview-message {
  text-align: center;
  margin-bottom: 16px;
}

.office-alternatives {
  margin-top: 20px;
  text-align: center;
}

.generic-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px;
}

.file-icon {
  font-size: 60px;
  color: #1890ff;
  margin-bottom: 20px;
}

.file-info {
  margin-bottom: 20px;
  text-align: center;
}

.file-info p {
  margin: 5px 0;
}

.preview-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
}

.loading-spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #1890ff;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 2s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: 16px;
  color: #666;
}

.preview-not-supported {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  text-align: center;
}

.not-supported-icon {
  font-size: 60px;
  color: #faad14;
  margin-bottom: 20px;
}

.not-supported-text {
  margin-bottom: 20px;
}

.not-supported-text p {
  margin: 5px 0;
  color: #666;
}

.preview-btn {
  background-color: #13c2c2;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  padding: 0.3rem 0.5rem;
  font-size: 0.8rem;
}
</style>
