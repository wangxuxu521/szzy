<template>
  <div class="upload-resource-page">
    <div class="page-header">
      <h1>上传思政教学资源</h1>
      <p class="description">分享您的教学资源，促进思政教育资源共享与创新</p>
    </div>

    <div class="card">
      <resource-upload-form
        :loading="loading"
        @submit="handleSubmit"
        @cancel="goBack"
      />
    </div>

    <!-- 上传进度 -->
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

    <!-- 操作结果通知 -->
    <el-dialog
      v-model="showResultDialog"
      :title="uploadSuccess ? '上传成功' : '上传失败'"
      width="400px"
      center
      :close-on-click-modal="false"
    >
      <div class="result-content">
        <div
          class="result-icon"
          :class="{ success: uploadSuccess, error: !uploadSuccess }"
        >
          <i :class="uploadSuccess ? 'el-icon-check' : 'el-icon-close'"></i>
        </div>
        <p class="result-message">{{ resultMessage }}</p>
        <div v-if="uploadSuccess" class="resource-info">
          <p><strong>标题:</strong> {{ uploadedResource?.title || "未知" }}</p>
          <p><strong>类型:</strong> {{ uploadedResource?.type || "未知" }}</p>
          <p>
            <strong>审核状态:</strong>
            {{ getReviewStatusText(uploadedResource?.reviewStatus) }}
          </p>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleResultConfirm">确定</el-button>
          <el-button v-if="uploadSuccess" type="primary" @click="goToResource">
            查看资源
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import ResourceUploadForm from "@/views/teacher/ResourceUploadForm.vue";
import { uploadResource } from "@/api/resource";
import { ElMessage } from "element-plus";
import axios from "axios";

export default {
  name: "UploadResource",
  components: {
    ResourceUploadForm,
  },
  setup() {
    const router = useRouter();
    const loading = ref(false);
    const uploadedResource = ref(null);

    // 上传进度
    const showProgressDialog = ref(false);
    const uploadProgress = ref(0);
    const progressStatus = ref("");
    const progressText = ref("准备上传文件...");

    // 结果对话框状态
    const showResultDialog = ref(false);
    const uploadSuccess = ref(false);
    const resultMessage = ref("");

    // 获取审核状态文本
    const getReviewStatusText = (status) => {
      if (!status) return "未知";

      switch (status) {
        case "approved":
          return "已通过审核";
        case "pending":
          return "待审核";
        case "rejected":
          return "审核未通过";
        default:
          return status;
      }
    };

    // 处理表单提交
    const handleSubmit = async (formData) => {
      loading.value = true;
      showProgressDialog.value = true;
      uploadProgress.value = 0;
      progressText.value = "准备上传文件...";
      progressStatus.value = "";

      try {
        // 创建取消令牌，如果用户取消上传
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

        // 调用上传API
        const response = await uploadResource(
          formData,
          onUploadProgress,
          cancelTokenSource.token
        );

        // 处理响应结果
        if (response && (response.code === 200 || !response.code)) {
          // 上传成功
          uploadProgress.value = 100;
          progressStatus.value = "success";
          progressText.value = "上传成功！";

          uploadSuccess.value = true;
          resultMessage.value = "资源上传成功，审核通过后将对其他用户可见";

          // 保存上传的资源信息
          uploadedResource.value = response.data || response;

          // 稍等片刻显示结果
          setTimeout(() => {
            showProgressDialog.value = false;
            showResultDialog.value = true;
          }, 800);
        } else {
          // 上传失败
          progressStatus.value = "exception";
          progressText.value = "上传失败";

          uploadSuccess.value = false;
          resultMessage.value = response?.message || "资源上传失败，请稍后重试";

          // 稍等片刻显示结果
          setTimeout(() => {
            showProgressDialog.value = false;
            showResultDialog.value = true;
          }, 800);
        }
      } catch (error) {
        console.error("上传资源失败:", error);
        progressStatus.value = "exception";
        progressText.value = "上传出错";

        uploadSuccess.value = false;
        resultMessage.value = error.message || "上传过程中发生错误，请稍后重试";

        // 显示结果
        setTimeout(() => {
          showProgressDialog.value = false;
          showResultDialog.value = true;
        }, 800);
      } finally {
        loading.value = false;
      }
    };

    // 处理结果确认
    const handleResultConfirm = () => {
      showResultDialog.value = false;

      if (uploadSuccess.value) {
        // 如果上传成功，根据角色返回不同页面
        const userRole = localStorage.getItem("userRole");
        if (userRole === "admin") {
          router.push("/admin/resources");
        } else if (userRole === "teacher") {
          router.push("/teacher/resources");
        } else {
          router.push("/resources");
        }
      }
    };

    // 跳转到资源详情页
    const goToResource = () => {
      if (uploadedResource.value && uploadedResource.value.resourceId) {
        showResultDialog.value = false;
        router.push(`/resources/${uploadedResource.value.resourceId}`);
      } else {
        ElMessage.warning("无法获取资源详情，请前往资源列表查看");
        // 根据用户角色返回不同的资源列表
        const userRole = localStorage.getItem("userRole");
        if (userRole === "admin") {
          router.push("/admin/resources");
        } else if (userRole === "teacher") {
          router.push("/teacher/resources");
        } else {
          router.push("/resources");
        }
      }
    };

    // 返回上一页
    const goBack = () => {
      router.back();
    };

    return {
      loading,
      uploadedResource,
      showResultDialog,
      uploadSuccess,
      resultMessage,
      handleSubmit,
      handleResultConfirm,
      goToResource,
      goBack,
      showProgressDialog,
      uploadProgress,
      progressStatus,
      progressText,
      getReviewStatusText,
    };
  },
};
</script>

<style scoped>
.upload-resource-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 30px 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  color: #2c3e50;
  margin-bottom: 10px;
}

.page-header .description {
  color: #606266;
  font-size: 16px;
}

.card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.result-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.result-icon {
  font-size: 48px;
  margin-bottom: 20px;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.result-icon.success {
  background-color: #f0f9eb;
  color: #67c23a;
}

.result-icon.error {
  background-color: #fef0f0;
  color: #f56c6c;
}

.result-message {
  font-size: 16px;
  text-align: center;
  color: #606266;
  margin-bottom: 15px;
}

.resource-info {
  border-top: 1px dashed #dcdfe6;
  margin-top: 10px;
  padding-top: 10px;
  width: 100%;
}

.resource-info p {
  margin: 5px 0;
  font-size: 14px;
  color: #606266;
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
