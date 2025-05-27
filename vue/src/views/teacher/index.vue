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
          <el-form-item label="资源类型" prop="type">
            <el-select
              v-model="uploadForm.type"
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
              v-model="uploadForm.description"
              type="textarea"
              rows="3"
              placeholder="请输入资源描述"
            ></el-input>
          </el-form-item>
          <el-form-item label="资源文件" prop="file">
            <el-upload
              class="upload-file"
              :auto-upload="false"
              :on-change="handleFileChange"
              :limit="1"
              :file-list="fileList"
            >
              <el-button type="primary">选择文件</el-button>
              <template #tip>
                <div class="el-upload__tip">
                  支持各种文件格式，单个文件大小不超过20MB
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
import { ref, onMounted } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import { ElMessage, ElLoading } from "element-plus";
import { Document, Reading, User, Upload } from "@element-plus/icons-vue";
import BaseLayout from "@/layout/BaseLayout.vue";
import ResourceCard from "@/components/common/ResourceCard.vue";
import * as teacherApi from "@/api/teacher";
import { uploadResource } from "@/api/resource";

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

    // 上传对话框相关
    const uploadDialogVisible = ref(false);
    const uploadForm = ref({
      title: "",
      type: "",
      description: "",
      file: null,
    });
    const uploadRules = {
      title: [{ required: true, message: "请输入资源标题", trigger: "blur" }],
      type: [{ required: true, message: "请选择资源类型", trigger: "change" }],
      file: [{ required: true, message: "请选择上传文件", trigger: "change" }],
    };
    const fileList = ref([]);
    const uploading = ref(false);
    const resourceTypes = ref(["计算机", "通信", "人工智能"]);
    const uploadFormRef = ref(null);

    // 文件选择处理
    const handleFileChange = (file) => {
      uploadForm.value.file = file.raw;
    };

    // 提交上传
    const submitUpload = async () => {
      if (!uploadForm.value.file) {
        ElMessage.warning("请选择要上传的文件");
        return;
      }

      uploading.value = true;

      try {
        const formData = new FormData();
        formData.append("title", uploadForm.value.title);
        formData.append("type", uploadForm.value.type);
        formData.append("description", uploadForm.value.description);
        formData.append("file", uploadForm.value.file);

        const res = await uploadResource(formData);

        if (res.code === 200) {
          ElMessage.success("资源上传成功");
          uploadDialogVisible.value = false;
          // 重新获取资源列表
          fetchTeacherResources();
          fetchStatistics();
        } else {
          ElMessage.error(res.message || "上传失败");
        }
      } catch (error) {
        console.error("上传资源失败:", error);
        ElMessage.error("上传失败，请稍后重试");
      } finally {
        uploading.value = false;
      }
    };

    // 上传资源
    const handleUploadResource = () => {
      // 重置表单
      uploadForm.value = {
        title: "",
        type: "",
        description: "",
        file: null,
      };
      fileList.value = [];
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
      handleUploadResource,
      // 上传对话框相关
      uploadDialogVisible,
      uploadForm,
      uploadRules,
      fileList,
      uploading,
      resourceTypes,
      uploadFormRef,
      handleFileChange,
      submitUpload,
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
