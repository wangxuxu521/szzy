<template>
  <base-layout>
    <div class="teacher-center">
      <h1 class="page-title">教师空间</h1>

      <div class="teacher-panel">
        <div class="teacher-info">
          <div class="avatar">👨‍🏫</div>
          <div class="info-details">
            <h2>{{ teacherInfo.name }}</h2>
            <p>{{ teacherInfo.department }}</p>
            <p>{{ teacherInfo.email }}</p>
          </div>
        </div>

        <div class="stats-container">
          <div class="stat-card">
            <h3>我的资源</h3>
            <div class="stat-value">{{ stats.resourceCount }}</div>
            <router-link to="/teacher/resources" class="stat-link"
              >管理资源</router-link
            >
          </div>

          <div class="stat-card">
            <h3>我的课程</h3>
            <div class="stat-value">{{ stats.courseCount }}</div>
            <router-link to="/teacher/courses" class="stat-link"
              >管理课程</router-link
            >
          </div>

          <div
            class="stat-card"
            v-for="(count, type) in stats.resourceTypeCount"
            :key="type"
          >
            <h3>{{ type }}</h3>
            <div class="stat-value">{{ count }}</div>
            <router-link
              :to="`/teacher/resources?type=${type}`"
              class="stat-link"
              >查看资源</router-link
            >
          </div>
        </div>

        <!-- 快捷操作区域 -->
        <div class="quick-actions">
          <h3 class="section-title">快捷操作</h3>
          <div class="action-buttons">
            <router-link to="/teacher/resources" class="quick-action">
              <el-button type="primary" plain>
                <el-icon><Document /></el-icon>
                资源管理
              </el-button>
            </router-link>
            <router-link to="/teacher/courses" class="quick-action">
              <el-button type="primary" plain>
                <el-icon><Reading /></el-icon>
                课程管理
              </el-button>
            </router-link>
            <router-link to="/teacher/profile" class="quick-action">
              <el-button type="primary" plain>
                <el-icon><User /></el-icon>
                个人设置
              </el-button>
            </router-link>
            <router-link
              to="/resource-management?action=upload"
              class="quick-action"
            >
              <el-button type="success" plain>
                <el-icon><Upload /></el-icon>
                上传资源
              </el-button>
            </router-link>
          </div>
        </div>
      </div>

      <div class="section">
        <h2 class="section-title">我的资源</h2>
        <div v-if="myResources.length === 0" class="empty-state">
          您还没有上传资源，点击"上传资源"按钮开始分享您的资源。
        </div>
        <div v-else class="resources-grid">
          <resource-card
            v-for="resource in myResources"
            :key="resource.id"
            :resource="resource"
          />
        </div>
        <div class="action-btn-container">
          <button class="action-btn" @click="handleUploadResource">
            上传资源
          </button>
        </div>
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
            <el-button type="primary" @click="submitForm" :loading="submitting"
              >上传</el-button
            >
          </span>
        </template>
      </el-dialog>

      <div class="section">
        <h2 class="section-title">最近活动</h2>
        <div class="activity-list">
          <div
            v-for="(activity, index) in recentActivities"
            :key="index"
            class="activity-item"
          >
            <div class="activity-time">{{ activity.time }}</div>
            <div class="activity-content">
              <div class="activity-type">{{ activity.type }}</div>
              <div class="activity-description">{{ activity.description }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </base-layout>
</template>

<script>
import { ref, reactive, computed, onMounted } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox, ElLoading } from "element-plus";
import { Document, Reading, User, Upload } from "@element-plus/icons-vue";
import BaseLayout from "@/layout/BaseLayout.vue";
import ResourceCard from "@/components/common/ResourceCard.vue";
import * as teacherApi from "@/api/teacher";
import { getAllTypes } from "@/api/type";
import { getTagList } from "@/api/tag";
import { getSystemConfigs } from "@/api/system";

export default {
  name: "TeacherCenter",
  components: {
    BaseLayout,
    ResourceCard,
    Document,
    Reading,
    User,
    Upload,
  },
  setup() {
    const store = useStore();
    const router = useRouter();

    // 数据
    const teacherInfo = ref({
      name: "",
      department: "",
      email: "",
    });

    const stats = ref({
      resourceCount: 0,
      courseCount: 0,
      resourceTypeCount: {},
    });

    const myResources = ref([]);
    const recentActivities = ref([]);
    const loading = ref(false);

    // 资源类型和标签
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

    // 上传对话框相关
    const uploadDialogVisible = ref(false);
    const uploadForm = ref({
      title: "",
      typeId: "",
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
    const submitting = ref(false);
    const uploadFormRef = ref(null);

    // 文件信息
    const fileInfo = reactive({
      size: null,
      format: null,
    });

    // 最大文件大小(MB)
    const maxFileSize = ref(20);

    // 允许的文件格式
    const allowedFileTypes = ref([]);

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

    // 提交表单
    const submitForm = async () => {
      uploadFormRef.value.validate(async (valid) => {
        if (!valid) {
          return false;
        }

        try {
          submitting.value = true;
          const formData = new FormData();

          // 添加表单字段到FormData
          formData.append("title", uploadForm.value.title);
          formData.append("typeId", uploadForm.value.typeId);
          formData.append("description", uploadForm.value.description || "");

          // 如果有标签，添加到FormData
          if (uploadForm.value.tags && uploadForm.value.tags.length > 0) {
            formData.append("tags", JSON.stringify(uploadForm.value.tags));
          }

          // 添加文件
          if (uploadForm.value.file) {
            formData.append("file", uploadForm.value.file);
          }

          const res = await teacherApi.uploadTeacherResource(formData);

          if (res && (res.code === 200 || res.code === 0)) {
            ElMessage.success("资源已成功上传");
            uploadDialogVisible.value = false;
            // 重新获取资源列表
            fetchTeacherResources();
            fetchStatistics();
          } else {
            ElMessage.error(res?.message || "上传失败，请稍后重试");
          }
        } catch (error) {
          console.error("提交资源表单失败:", error);
          ElMessage.error("上传失败，请稍后重试");
        } finally {
          submitting.value = false;
        }
      });
    };

    // 上传资源
    const handleUploadResource = () => {
      // 重置表单
      uploadForm.value = {
        title: "",
        typeId: "",
        description: "",
        tags: [],
        file: null,
      };
      fileList.value = [];
      fileInfo.size = null;
      fileInfo.format = null;
      uploadDialogVisible.value = true;
    };

    // 获取教师信息
    const fetchTeacherInfo = async () => {
      try {
        const res = await teacherApi.getTeacherInfo();
        if (res.code === 200 && res.data) {
          teacherInfo.value = {
            name: res.data.name || res.data.username,
            department: res.data.department || "未设置院系",
            email: res.data.email || "未设置邮箱",
          };
        }
      } catch (error) {
        console.error("获取教师信息失败:", error);
      }
    };

    // 加载系统配置
    const loadSystemConfig = async () => {
      try {
        const response = await getSystemConfigs();
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

    // 获取统计数据
    const fetchStatistics = async () => {
      try {
        const res = await teacherApi.getTeacherStatistics();
        if (res && res.code === 200 && res.data) {
          stats.value = {
            resourceCount: res.data.resourceCount || 0,
            courseCount: res.data.courseCount || 0,
            resourceTypeCount: res.data.resourceTypeCount || {},
          };
        } else if (res && typeof res === "object" && !("code" in res)) {
          // 直接返回对象的情况
          stats.value = {
            resourceCount: res.resourceCount || 0,
            courseCount: res.courseCount || 0,
            resourceTypeCount: res.resourceTypeCount || {},
          };
        } else {
          // 无效响应时使用默认值
          stats.value = {
            resourceCount: 0,
            courseCount: 0,
            resourceTypeCount: {},
          };
        }
      } catch (error) {
        console.error("获取统计数据失败:", error);
        // 出错时使用默认值
        stats.value = {
          resourceCount: 0,
          courseCount: 0,
          resourceTypeCount: {},
        };
      }
    };

    // 获取教师资源
    const fetchTeacherResources = async () => {
      try {
        const res = await teacherApi.getTeacherResources();
        if (res.code === 200 && res.data) {
          myResources.value = res.data.map((resource) => ({
            id: resource.resourceId || resource.resource_id,
            title: resource.title,
            type: resource.type || "未分类",
            views: resource.viewCount || resource.view_count || 0,
            downloadCount:
              resource.downloadCount || resource.download_count || 0,
            author:
              resource.uploaderName ||
              resource.uploader_name ||
              teacherInfo.value.name,
            uploaderName: resource.uploaderName || resource.uploader_name,
            uploaderId: resource.uploaderId || resource.uploader_id,
            tags: resource.tags
              ? typeof resource.tags === "string"
                ? JSON.parse(resource.tags)
                : resource.tags
              : [],
            description: resource.description,
            format: resource.format,
            reviewStatus: resource.reviewStatus || resource.review_status,
          }));
        }
      } catch (error) {
        console.error("获取教师资源失败:", error);
      }
    };

    // 获取活动记录
    const fetchActivities = async () => {
      try {
        const res = await teacherApi.getTeacherActivities(5);
        if (res.code === 200 && res.data) {
          recentActivities.value = res.data.map((activity) => ({
            time: formatDate(activity.actionTime),
            type: formatActionType(activity.actionType),
            description:
              activity.commentContent ||
              `操作了资源"${activity.resourceTitle || "未知资源"}"`,
          }));
        }
      } catch (error) {
        console.error("获取活动记录失败:", error);
        // 如果API失败，使用默认数据
        recentActivities.value = [
          {
            time: "2024-03-28",
            type: "上传资源",
            description: '上传了教学资源"数据库原理与社会责任"',
          },
          {
            time: "2024-03-26",
            type: "评论",
            description: '评论了"人工智能伦理与价值观"',
          },
        ];
      }
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

    // 格式化操作类型
    const formatActionType = (actionType) => {
      const typeMap = {
        view: "查看",
        download: "下载",
        upload: "上传",
        comment: "评论",
        like: "点赞",
      };
      return typeMap[actionType] || actionType;
    };

    // 初始化数据
    const initData = async () => {
      loading.value = true;
      const loadingInstance = ElLoading.service({
        fullscreen: true,
        text: "加载中...",
      });

      try {
        await Promise.all([
          fetchTeacherInfo(),
          fetchStatistics(),
          fetchTeacherResources(),
          fetchActivities(),
          loadSystemConfig(),
          loadResourceTypes(),
          loadTags(),
        ]);
      } catch (error) {
        console.error("初始化数据失败:", error);
        ElMessage.error("加载数据失败，请稍后重试");
      } finally {
        loading.value = false;
        loadingInstance.close();
      }
    };

    onMounted(() => {
      initData();
    });

    return {
      teacherInfo,
      stats,
      myResources,
      recentActivities,
      uploadDialogVisible,
      uploadForm,
      uploadRules,
      fileList,
      submitting,
      uploadFormRef,
      types,
      availableTags,
      themeTagOptions,
      subjectTagOptions,
      formatTagOptions,
      fileInfo,
      maxFileSize,
      handleFileChange,
      handleFileRemove,
      handleExceed,
      confirmRemoveFile,
      submitForm,
      handleUploadResource,
      formatFileSize,
      validateFileType,
    };
  },
};
</script>

<style scoped>
.teacher-center {
  padding: 1rem 0;
}

.page-title {
  font-size: 2rem;
  margin-bottom: 2rem;
  color: #333;
}

.teacher-panel {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  margin-bottom: 2rem;
}

.teacher-info {
  display: flex;
  align-items: center;
  margin-bottom: 2rem;
}

.avatar {
  font-size: 4rem;
  background-color: #f0f2f5;
  border-radius: 50%;
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 2rem;
}

.info-details h2 {
  margin-top: 0;
  margin-bottom: 0.5rem;
  color: #333;
}

.info-details p {
  margin: 0.3rem 0;
  color: #666;
}

.stats-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  grid-gap: 2rem;
}

.stat-card {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 1.5rem;
  text-align: center;
}

.stat-card h3 {
  margin-top: 0;
  color: #666;
  font-size: 1rem;
}

.stat-value {
  font-size: 2.5rem;
  font-weight: bold;
  color: #1890ff;
  margin: 1rem 0;
}

.stat-link {
  color: #1890ff;
  text-decoration: none;
}

.section {
  margin-bottom: 3rem;
}

.section-title {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  border-bottom: 1px solid #eee;
  padding-bottom: 0.5rem;
}

.empty-state {
  padding: 3rem;
  text-align: center;
  background-color: #f9f9f9;
  border-radius: 8px;
  color: #666;
}

.resources-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  grid-gap: 1.5rem;
}

.action-btn-container {
  margin-top: 1.5rem;
  text-align: center;
}

.action-btn {
  padding: 0.8rem 2rem;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.3s;
}

.action-btn:hover {
  background-color: #40a9ff;
}

.activity-list {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.activity-item {
  display: flex;
  padding: 1.5rem;
  border-bottom: 1px solid #f0f0f0;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-time {
  width: 100px;
  color: #999;
  flex-shrink: 0;
}

.activity-content {
  flex: 1;
}

.activity-type {
  font-weight: bold;
  margin-bottom: 0.5rem;
  color: #333;
}

.activity-description {
  color: #666;
}

.quick-actions {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  margin-bottom: 2rem;
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 1.5rem;
}

.quick-action {
  text-align: center;
}

.quick-action button {
  padding: 0.8rem 2rem;
  background-color: #fff;
  color: #1890ff;
  border: 1px solid #1890ff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.3s;
}

.quick-action button:hover {
  background-color: #f0f0f0;
}
</style>
