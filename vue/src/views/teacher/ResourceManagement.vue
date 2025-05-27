<template>
  <div class="resource-management">
    <div class="page-header">
      <h1>资源管理</h1>
      <div class="header-actions">
        <el-button type="primary" @click="handleUploadResource"
          >上传资源</el-button
        >
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-container">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索资源标题"
        class="search-input"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-select
        v-model="filterType"
        placeholder="资源类型"
        clearable
        @change="handleFilterChange"
        class="filter-select"
      >
        <el-option
          v-for="type in resourceTypes"
          :key="type"
          :label="type"
          :value="type"
        />
      </el-select>

      <el-select
        v-model="filterStatus"
        placeholder="审核状态"
        clearable
        @change="handleFilterChange"
        class="filter-select"
      >
        <el-option label="待审核" value="pending" />
        <el-option label="已通过" value="approved" />
        <el-option label="已拒绝" value="rejected" />
      </el-select>

      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="resetFilter">重置</el-button>
    </div>

    <!-- 资源列表 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>
    <div v-else-if="filteredResources.length === 0" class="empty-container">
      <el-empty description="暂无资源" />
      <el-button type="primary" @click="handleUploadResource"
        >上传第一个资源</el-button
      >
    </div>
    <div v-else class="resource-list">
      <el-table :data="filteredResources" border style="width: 100%">
        <el-table-column label="标题" prop="title" min-width="200">
          <template #default="{ row }">
            <div class="resource-title">
              <span>{{ row.title }}</span>
              <el-tag v-if="row.type" size="small" class="resource-tag">{{
                row.type
              }}</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="上传时间" prop="uploadTime" width="150">
          <template #default="{ row }">
            {{ formatDate(row.uploadTime) }}
          </template>
        </el-table-column>
        <el-table-column label="上传者" prop="uploaderName" width="120">
          <template #default="{ row }">
            {{ row.uploaderName || "未知用户" }}
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="reviewStatus" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.reviewStatus)" effect="light">
              {{ getStatusText(row.reviewStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="浏览/下载" width="120">
          <template #default="{ row }">
            {{ row.viewCount || 0 }} / {{ row.downloadCount || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleViewResource(row)"
            >
              查看
            </el-button>
            <el-button
              type="primary"
              size="small"
              plain
              @click="handleEditResource(row)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteResource(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 资源上传对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="上传资源"
      width="600px"
      append-to-body
      :close-on-click-modal="false"
    >
      <el-form
        ref="uploadFormRef"
        :model="uploadForm"
        :rules="uploadRules"
        label-width="100px"
      >
        <el-form-item label="资源标题" prop="title">
          <el-input
            v-model="uploadForm.title"
            placeholder="请输入资源标题"
          ></el-input>
        </el-form-item>
        <el-form-item label="资源类型" prop="typeId">
          <el-select
            v-model="uploadForm.typeId"
            placeholder="请选择资源类型"
            style="width: 100%"
            filterable
          >
            <el-option
              v-for="type in types"
              :key="type.typeId"
              :label="type.typeName"
              :value="type.typeId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="资源描述" prop="description">
          <el-input
            v-model="uploadForm.description"
            type="textarea"
            rows="3"
            placeholder="请输入资源描述"
          ></el-input>
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-select
            v-model="uploadForm.tags"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="请选择或输入标签"
            style="width: 100%"
          >
            <el-option-group label="主题标签">
              <el-option
                v-for="tag in themeTagOptions"
                :key="tag.value"
                :label="tag.label"
                :value="tag.value"
              >
                <span>{{ tag.label }}</span>
                <el-tag size="small" type="success">主题</el-tag>
              </el-option>
            </el-option-group>
            <el-option-group label="学科标签">
              <el-option
                v-for="tag in subjectTagOptions"
                :key="tag.value"
                :label="tag.label"
                :value="tag.value"
              >
                <span>{{ tag.label }}</span>
                <el-tag size="small" type="primary">学科</el-tag>
              </el-option>
            </el-option-group>
            <el-option-group label="格式标签">
              <el-option
                v-for="tag in formatTagOptions"
                :key="tag.value"
                :label="tag.label"
                :value="tag.value"
              >
                <span>{{ tag.label }}</span>
                <el-tag size="small" type="info">格式</el-tag>
              </el-option>
            </el-option-group>
          </el-select>
          <div class="form-tip">多个标签有助于其他用户更容易找到您的资源</div>
        </el-form-item>
        <el-form-item label="资源文件" prop="file">
          <el-upload
            class="upload-file"
            :auto-upload="false"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :before-remove="confirmRemoveFile"
            :limit="1"
            :file-list="fileList"
            :on-exceed="handleExceed"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持各种文件格式，单个文件大小不超过{{ maxFileSize }}MB
                <div v-if="fileInfo.size" class="file-info">
                  <span class="file-size"
                    >文件大小: {{ formatFileSize(fileInfo.size) }}</span
                  >
                  <span class="file-format">格式: {{ fileInfo.format }}</span>
                </div>
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="uploadDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUpload" :loading="uploading"
            >上传</el-button
          >
        </span>
      </template>
    </el-dialog>

    <!-- 上传进度对话框 -->
    <el-dialog
      v-model="showProgressDialog"
      title="正在上传"
      width="400px"
      :close-on-click-modal="false"
      :show-close="false"
    >
      <div class="progress-container">
        <el-progress
          :percentage="uploadProgress"
          :status="progressStatus"
        ></el-progress>
        <p class="progress-text">{{ progressText }}</p>
      </div>
    </el-dialog>

    <!-- 资源编辑对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑资源"
      width="600px"
      append-to-body
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="资源标题" prop="title">
          <el-input
            v-model="editForm.title"
            placeholder="请输入资源标题"
          ></el-input>
        </el-form-item>
        <el-form-item label="资源类型" prop="type">
          <el-select
            v-model="editForm.type"
            placeholder="请选择资源类型"
            style="width: 100%"
          >
            <el-option
              v-for="type in resourceTypes"
              :key="type"
              :label="type"
              :value="type"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="资源描述" prop="description">
          <el-input
            v-model="editForm.description"
            type="textarea"
            rows="3"
            placeholder="请输入资源描述"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit" :loading="editing"
            >保存</el-button
          >
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search } from "@element-plus/icons-vue";
import { getTeacherResources } from "@/api/teacher";
import { uploadResource, updateResource, deleteResource } from "@/api/resource";
import { getTagList } from "@/api/tag";
import { getAllTypes } from "@/api/type";
import { getSystemConfig } from "@/api/system";
import axios from "axios";

export default {
  name: "ResourceManagement",
  components: {
    Search,
  },
  setup() {
    const router = useRouter();

    // 数据
    const resources = ref([]);
    const loading = ref(false);
    const resourceTypes = ref(["计算机", "通信", "人工智能"]);
    const types = ref([]);
    const availableTags = ref([]);

    // 按标签类型分组
    const themeTagOptions = computed(() => {
      return availableTags.value
        .filter((tag) => tag.tagType === "theme")
        .map((tag) => ({
          label: tag.tagName,
          value: tag.tagName,
        }));
    });

    const subjectTagOptions = computed(() => {
      return availableTags.value
        .filter((tag) => tag.tagType === "subject")
        .map((tag) => ({
          label: tag.tagName,
          value: tag.tagName,
        }));
    });

    const formatTagOptions = computed(() => {
      return availableTags.value
        .filter((tag) => tag.tagType === "format")
        .map((tag) => ({
          label: tag.tagName,
          value: tag.tagName,
        }));
    });

    // 搜索和筛选
    const searchKeyword = ref("");
    const filterType = ref("");
    const filterStatus = ref("");

    // 文件信息
    const fileInfo = reactive({
      size: null,
      format: null,
    });

    // 最大文件大小(MB)
    const maxFileSize = ref(20);

    // 允许的文件格式
    const allowedFileTypes = ref([]);

    // 上传进度
    const showProgressDialog = ref(false);
    const uploadProgress = ref(0);
    const progressStatus = ref("");
    const progressText = ref("准备上传文件...");

    // 上传对话框
    const uploadDialogVisible = ref(false);
    const uploadForm = ref({
      title: "",
      type: "",
      typeId: null,
      description: "",
      tags: [],
      file: null,
    });
    const uploadRules = {
      title: [{ required: true, message: "请输入资源标题", trigger: "blur" }],
      typeId: [
        { required: true, message: "请选择资源类型", trigger: "change" },
      ],
      file: [{ required: true, message: "请选择上传文件", trigger: "change" }],
    };
    const fileList = ref([]);
    const uploading = ref(false);
    const uploadFormRef = ref(null);

    // 编辑对话框
    const editDialogVisible = ref(false);
    const editForm = ref({
      resourceId: null,
      title: "",
      type: "",
      description: "",
    });
    const editRules = {
      title: [{ required: true, message: "请输入资源标题", trigger: "blur" }],
      type: [{ required: true, message: "请选择资源类型", trigger: "change" }],
    };
    const editing = ref(false);
    const editFormRef = ref(null);

    // 过滤后的资源列表
    const filteredResources = computed(() => {
      let result = [...resources.value];

      // 关键词搜索
      if (searchKeyword.value) {
        const keyword = searchKeyword.value.toLowerCase();
        result = result.filter(
          (item) =>
            (item.title && item.title.toLowerCase().includes(keyword)) ||
            (item.description &&
              item.description.toLowerCase().includes(keyword))
        );
      }

      // 类型筛选
      if (filterType.value) {
        result = result.filter((item) => item.type === filterType.value);
      }

      // 状态筛选
      if (filterStatus.value) {
        result = result.filter(
          (item) => item.reviewStatus === filterStatus.value
        );
      }

      return result;
    });

    // 获取资源列表
    const fetchResources = async () => {
      loading.value = true;
      try {
        const res = await getTeacherResources();
        if (res && res.code === 200) {
          resources.value = res.data || [];

          // 处理资源数据中可能的驼峰式命名和下划线命名混用问题
          resources.value = resources.value.map((resource) => {
            return {
              resourceId: resource.resourceId || resource.resource_id,
              title: resource.title,
              description: resource.description,
              filePath: resource.filePath || resource.file_path,
              fileName: resource.fileName || resource.file_name,
              fileSize: resource.fileSize || resource.file_size,
              format: resource.format,
              uploadTime: resource.uploadTime || resource.upload_time,
              downloadCount:
                resource.downloadCount || resource.download_count || 0,
              viewCount: resource.viewCount || resource.view_count || 0,
              type: resource.type,
              reviewStatus:
                resource.reviewStatus || resource.review_status || "pending",
              tags: resource.tags,
              uploaderName: resource.uploaderName || resource.uploader_name,
              uploaderId: resource.uploaderId || resource.uploader_id,
            };
          });
        } else {
          ElMessage.error(res?.message || "获取资源失败");
        }
      } catch (error) {
        console.error("获取资源失败:", error);
        ElMessage.error("获取资源失败，请稍后重试");
      } finally {
        loading.value = false;
      }
    };

    // 验证文件类型
    const validateFileType = (fileName) => {
      if (!fileName) return false;

      const extension = fileName.split(".").pop().toLowerCase();
      if (!extension) return false;

      return allowedFileTypes.value.includes(extension);
    };

    // 格式化文件大小
    const formatFileSize = (size) => {
      if (!size) return "未知大小";

      if (size < 1024) {
        return size + " B";
      } else if (size < 1024 * 1024) {
        return (size / 1024).toFixed(2) + " KB";
      } else if (size < 1024 * 1024 * 1024) {
        return (size / (1024 * 1024)).toFixed(2) + " MB";
      } else {
        return (size / (1024 * 1024 * 1024)).toFixed(2) + " GB";
      }
    };

    // 加载系统配置
    const loadSystemConfig = async () => {
      try {
        const response = await getSystemConfig();
        if (response && response.code === 200 && response.data) {
          const configs = response.data;

          // 获取文件大小限制
          const sizeLimit = configs.find(
            (c) => c.configKey === "upload_size_limit"
          );
          if (sizeLimit && sizeLimit.configValue) {
            maxFileSize.value = parseInt(sizeLimit.configValue) || 20;
          }

          // 获取允许的文件类型
          const fileTypes = configs.find(
            (c) => c.configKey === "allowed_file_types"
          );
          if (fileTypes && fileTypes.configValue) {
            allowedFileTypes.value = fileTypes.configValue
              .split(",")
              .map((t) => t.trim());
          } else {
            allowedFileTypes.value = [
              "pdf",
              "doc",
              "docx",
              "ppt",
              "pptx",
              "xls",
              "xlsx",
              "txt",
              "jpg",
              "jpeg",
              "png",
              "gif",
              "mp4",
              "mp3",
              "zip",
              "rar",
            ];
          }
        }
      } catch (error) {
        console.error("获取系统配置失败:", error);
      }
    };

    // 加载资源类型
    const loadResourceTypes = async () => {
      try {
        const response = await getAllTypes();

        if (response && typeof response === "object") {
          if (Array.isArray(response)) {
            types.value = response;
          } else if (response.data && Array.isArray(response.data)) {
            types.value = response.data;
          }
        }
      } catch (error) {
        console.error("获取资源类型失败:", error);
      }
    };

    // 加载标签数据
    const loadTags = async () => {
      try {
        const response = await getTagList();

        if (response && typeof response === "object") {
          if (Array.isArray(response)) {
            availableTags.value = response;
          } else if (response.data && Array.isArray(response.data)) {
            availableTags.value = response.data;
          }
        }
      } catch (error) {
        console.error("获取标签列表失败:", error);
        // 默认标签
        availableTags.value = [
          { tagId: 1, tagName: "爱国主义", tagType: "theme" },
          { tagId: 2, tagName: "工科", tagType: "subject" },
          { tagId: 3, tagName: "PDF", tagType: "format" },
          { tagId: 4, tagName: "爱国教育", tagType: "subject" },
        ];
      }
    };

    // 搜索处理
    const handleSearch = () => {
      // 已通过计算属性自动筛选
    };

    // 筛选变更
    const handleFilterChange = () => {
      // 已通过计算属性自动筛选
    };

    // 重置筛选
    const resetFilter = () => {
      searchKeyword.value = "";
      filterType.value = "";
      filterStatus.value = "";
    };

    // 处理文件超出限制
    const handleExceed = () => {
      ElMessage.warning("只能上传一个文件");
    };

    // 确认移除文件
    const confirmRemoveFile = (file) => {
      return ElMessageBox.confirm(`确认移除 ${file.name}？`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => true)
        .catch(() => false);
    };

    // 文件选择处理
    const handleFileChange = (file) => {
      uploadForm.value.file = file.raw;
      fileInfo.size = file.raw.size;
      fileInfo.format = file.raw.name.split(".").pop().toLowerCase();

      // 验证文件大小
      const maxSize = maxFileSize.value * 1024 * 1024; // 转换为字节
      if (file.raw.size > maxSize) {
        ElMessage.error(`文件大小不能超过${maxFileSize.value}MB`);
        handleFileRemove();
        return false;
      }

      // 验证文件类型
      if (!validateFileType(file.raw.name)) {
        ElMessage.error(`不支持的文件格式: ${fileInfo.format}`);
        handleFileRemove();
        return false;
      }

      return true;
    };

    // 处理文件移除
    const handleFileRemove = () => {
      uploadForm.value.file = null;
      fileInfo.size = null;
      fileInfo.format = null;
      fileList.value = [];
    };

    // 查看资源
    const handleViewResource = (resource) => {
      router.push(`/resources/${resource.resourceId}`);
    };

    // 编辑资源
    const handleEditResource = (resource) => {
      editForm.value = {
        resourceId: resource.resourceId,
        title: resource.title,
        type: resource.type,
        description: resource.description || "",
      };
      editDialogVisible.value = true;
    };

    // 删除资源
    const handleDeleteResource = (resource) => {
      ElMessageBox.confirm(
        `确定要删除资源"${resource.title}"吗？`,
        "删除确认",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(async () => {
          try {
            const res = await deleteResource(resource.resourceId);
            if (res.code === 200) {
              ElMessage.success("资源删除成功");
              fetchResources();
            } else {
              ElMessage.error(res.message || "删除失败");
            }
          } catch (error) {
            console.error("删除资源失败:", error);
            ElMessage.error("删除失败，请稍后重试");
          }
        })
        .catch(() => {
          ElMessage.info("已取消删除");
        });
    };

    // 上传资源
    const handleUploadResource = () => {
      // 重置表单
      uploadForm.value = {
        title: "",
        type: "",
        typeId: null,
        description: "",
        tags: [],
        file: null,
      };
      fileList.value = [];
      fileInfo.size = null;
      fileInfo.format = null;
      uploadDialogVisible.value = true;
    };

    // 提交上传
    const submitUpload = async () => {
      if (!uploadFormRef.value) return;

      await uploadFormRef.value.validate(async (valid) => {
        if (!valid) return;

        if (!uploadForm.value.file) {
          ElMessage.warning("请选择要上传的文件");
          return;
        }

        uploading.value = true;
        uploadDialogVisible.value = false;
        showProgressDialog.value = true;
        uploadProgress.value = 0;
        progressText.value = "准备上传文件...";
        progressStatus.value = "";

        try {
          const formData = new FormData();
          formData.append("title", uploadForm.value.title);

          // 添加typeId字段
          if (uploadForm.value.typeId) {
            formData.append("typeId", uploadForm.value.typeId);
          }

          // 同时保留type字段向后兼容
          const selectedType = types.value.find(
            (t) => t.typeId === uploadForm.value.typeId
          );
          if (selectedType) {
            formData.append("type", selectedType.typeName);
          } else {
            formData.append("type", uploadForm.value.type || "");
          }

          formData.append("description", uploadForm.value.description || "");
          formData.append("file", uploadForm.value.file);

          // 处理标签 - 转换为JSON字符串
          if (uploadForm.value.tags && uploadForm.value.tags.length > 0) {
            formData.append("tags", JSON.stringify(uploadForm.value.tags));
          }

          // 创建取消令牌
          const cancelTokenSource = axios.CancelToken.source();

          // 创建上传进度事件
          const onUploadProgress = (progressEvent) => {
            const percentCompleted = Math.round(
              (progressEvent.loaded * 100) / progressEvent.total
            );
            uploadProgress.value = percentCompleted;
            progressText.value = `上传中... ${percentCompleted}%`;

            // 更新状态
            if (percentCompleted < 100) {
              progressStatus.value = "";
            } else {
              progressStatus.value = "success";
              progressText.value = "处理中，请稍候...";
            }
          };

          const res = await uploadResource(
            formData,
            onUploadProgress,
            cancelTokenSource.token
          );

          if (res.code === 200) {
            // 上传成功
            uploadProgress.value = 100;
            progressStatus.value = "success";
            progressText.value = "上传成功！";

            setTimeout(() => {
              showProgressDialog.value = false;
              ElMessage.success("资源上传成功");
              fetchResources();
            }, 800);
          } else {
            // 上传失败
            progressStatus.value = "exception";
            progressText.value = "上传失败";

            setTimeout(() => {
              showProgressDialog.value = false;
              ElMessage.error(res.message || "上传失败");
            }, 800);
          }
        } catch (error) {
          console.error("上传资源失败:", error);
          progressStatus.value = "exception";
          progressText.value = "上传出错";

          setTimeout(() => {
            showProgressDialog.value = false;
            ElMessage.error("上传失败，请稍后重试");
          }, 800);
        } finally {
          uploading.value = false;
        }
      });
    };

    // 提交编辑
    const submitEdit = async () => {
      if (!editFormRef.value) return;

      await editFormRef.value.validate(async (valid) => {
        if (!valid) return;

        editing.value = true;

        try {
          const res = await updateResource(editForm.value.resourceId, {
            title: editForm.value.title,
            type: editForm.value.type,
            description: editForm.value.description || "",
          });

          if (res.code === 200) {
            ElMessage.success("资源更新成功");
            editDialogVisible.value = false;
            fetchResources();
          } else {
            ElMessage.error(res.message || "更新失败");
          }
        } catch (error) {
          console.error("更新资源失败:", error);
          ElMessage.error("更新失败，请稍后重试");
        } finally {
          editing.value = false;
        }
      });
    };

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return "";
      const date = new Date(dateString);
      return date.toLocaleDateString("zh-CN", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
      });
    };

    // 获取状态类型
    const getStatusType = (status) => {
      switch (status) {
        case "approved":
          return "success";
        case "rejected":
          return "danger";
        default:
          return "warning";
      }
    };

    // 获取状态文本
    const getStatusText = (status) => {
      if (!status) return "未知";
      switch (status) {
        case "approved":
          return "已通过";
        case "rejected":
          return "已拒绝";
        case "pending":
          return "待审核";
        default:
          return status;
      }
    };

    onMounted(() => {
      fetchResources();
      loadSystemConfig();
      loadResourceTypes();
      loadTags();
    });

    return {
      resources,
      filteredResources,
      loading,
      resourceTypes,
      searchKeyword,
      filterType,
      filterStatus,
      uploadDialogVisible,
      uploadForm,
      uploadRules,
      fileList,
      uploading,
      uploadFormRef,
      editDialogVisible,
      editForm,
      editRules,
      editing,
      editFormRef,
      types,
      availableTags,
      themeTagOptions,
      subjectTagOptions,
      formatTagOptions,
      fileInfo,
      maxFileSize,
      showProgressDialog,
      uploadProgress,
      progressStatus,
      progressText,
      handleSearch,
      handleFilterChange,
      resetFilter,
      handleFileChange,
      handleFileRemove,
      handleExceed,
      confirmRemoveFile,
      handleViewResource,
      handleEditResource,
      handleDeleteResource,
      handleUploadResource,
      submitUpload,
      submitEdit,
      formatDate,
      getStatusType,
      getStatusText,
      formatFileSize,
      validateFileType,
    };
  },
};
</script>

<style scoped>
.resource-management {
  padding: 1rem 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.page-header h1 {
  margin: 0;
  font-size: 1.8rem;
  color: #333;
}

.filter-container {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.search-input {
  width: 300px;
}

.filter-select {
  width: 150px;
}

.loading-container,
.empty-container {
  padding: 2rem;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.empty-container {
  text-align: center;
}

.empty-container .el-button {
  margin-top: 1rem;
}

.resource-list {
  margin-bottom: 2rem;
}

.resource-title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.resource-tag {
  margin-left: 0.5rem;
}

.upload-file {
  width: 100%;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.file-info {
  margin-top: 5px;
  font-size: 12px;
  display: flex;
  gap: 10px;
}

.file-size,
.file-format {
  background-color: #f0f9eb;
  color: #67c23a;
  padding: 2px 5px;
  border-radius: 3px;
}

.file-format {
  background-color: #f4f4f5;
  color: #909399;
}

.progress-container {
  padding: 20px 0;
}

.progress-text {
  margin-top: 15px;
  text-align: center;
  color: #606266;
}
</style>
