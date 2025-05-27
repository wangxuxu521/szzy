<template>
  <div class="user-resources">
    <div class="page-header">
      <h2 class="page-title">我的资源</h2>
      <div class="page-actions">
        <el-button type="primary" @click="goToUpload">上传资源</el-button>
        <el-input
          v-model="searchQuery"
          placeholder="搜索我的资源..."
          class="search-input"
          clearable
          @keyup.enter="searchResources"
        >
          <template #suffix>
            <el-icon class="el-input__icon" @click="searchResources">
              <el-icon-search />
            </el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <el-tabs v-model="activeTab" @tab-click="handleTabChange">
      <el-tab-pane label="全部资源" name="all"></el-tab-pane>
      <el-tab-pane label="已审核" name="approved"></el-tab-pane>
      <el-tab-pane label="待审核" name="pending"></el-tab-pane>
      <el-tab-pane label="已拒绝" name="rejected"></el-tab-pane>
    </el-tabs>

    <!-- 加载中状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 空状态 -->
    <el-empty v-else-if="resources.length === 0" description="暂无资源">
      <el-button type="primary" @click="goToUpload">立即上传</el-button>
    </el-empty>

    <!-- 资源列表 -->
    <div v-else class="resource-list">
      <el-card
        v-for="resource in resources"
        :key="resource.id"
        class="resource-card"
      >
        <div class="resource-header">
          <div class="resource-info">
            <div
              class="resource-type-tag"
              :class="getStatusClass(resource.reviewStatus)"
            >
              {{ getStatusLabel(resource.reviewStatus) }}
            </div>
            <h3 class="resource-title" @click="viewResourceDetail(resource)">
              {{ resource.title }}
            </h3>
          </div>
          <div class="resource-actions">
            <el-dropdown
              trigger="click"
              @command="handleCommand($event, resource)"
            >
              <el-button type="text">
                <el-icon>
                  <el-icon-more />
                </el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="view">查看详情</el-dropdown-item>
                  <el-dropdown-item command="edit">编辑资源</el-dropdown-item>
                  <el-dropdown-item
                    v-if="resource.reviewStatus === 'rejected'"
                    command="resubmit"
                  >
                    重新提交
                  </el-dropdown-item>
                  <el-dropdown-item command="download"
                    >下载资源</el-dropdown-item
                  >
                  <el-dropdown-item command="delete" divided
                    >删除资源</el-dropdown-item
                  >
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>

        <div class="resource-content">
          <div class="resource-icon" :class="getFileTypeClass(resource.format)">
            {{ getFileTypeIcon(resource.format) }}
          </div>
          <div class="resource-details">
            <p v-if="resource.description" class="resource-description">
              {{ resource.description }}
            </p>
            <div class="resource-meta">
              <span>
                <el-icon><el-icon-document /></el-icon>
                {{ resource.fileName || "未知文件名" }}
              </span>
              <span>
                <el-icon><el-icon-upload /></el-icon>
                {{ formatDate(resource.uploadTime) }}
              </span>
              <span>
                <el-icon><el-icon-view /></el-icon>
                {{ resource.viewCount || 0 }} 次浏览
              </span>
              <span>
                <el-icon><el-icon-download /></el-icon>
                {{ resource.downloadCount || 0 }} 次下载
              </span>
            </div>
          </div>
        </div>

        <!-- 审核失败原因 -->
        <div
          v-if="resource.reviewStatus === 'rejected' && resource.rejectReason"
          class="reject-reason"
        >
          <el-alert
            :title="'拒绝原因: ' + resource.rejectReason"
            type="error"
            show-icon
            :closable="false"
          />
        </div>
      </el-card>
    </div>

    <!-- 分页 -->
    <div class="pagination-container" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 删除确认对话框 -->
    <el-dialog v-model="deleteDialogVisible" title="删除资源" width="30%">
      <span
        >确定要删除资源 "{{ resourceToDelete?.title }}"
        吗？此操作不可恢复。</span
      >
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDelete">确定删除</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Search as ElIconSearch,
  Document as ElIconDocument,
  Upload as ElIconUpload,
  View as ElIconView,
  Download as ElIconDownload,
  More as ElIconMore,
} from "@element-plus/icons-vue";

export default {
  name: "UserResources",
  components: {
    ElIconSearch,
    ElIconDocument,
    ElIconUpload,
    ElIconView,
    ElIconDownload,
    ElIconMore,
  },
  setup() {
    const router = useRouter();
    const searchQuery = ref("");
    const resources = ref([]);
    const loading = ref(false);
    const activeTab = ref("all");
    const currentPage = ref(1);
    const pageSize = ref(10);
    const total = ref(0);
    const deleteDialogVisible = ref(false);
    const resourceToDelete = ref(null);

    // 获取用户资源列表
    const fetchUserResources = async () => {
      loading.value = true;
      try {
        // 构建请求参数
        const params = {
          page: currentPage.value - 1, // 后端分页从0开始
          size: pageSize.value,
          status: activeTab.value !== "all" ? activeTab.value : undefined,
          keyword: searchQuery.value || undefined,
        };

        // 构建查询字符串
        const queryParams = new URLSearchParams();
        Object.entries(params).forEach(([key, value]) => {
          if (value !== undefined) queryParams.append(key, value);
        });

        // 调用API获取资源列表
        const response = await fetch(
          `/api/resources/user?${queryParams.toString()}`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );

        if (!response.ok) {
          throw new Error("获取资源失败");
        }

        const data = await response.json();

        // 更新数据
        resources.value = data.content || [];
        total.value = data.totalElements || resources.value.length;
      } catch (error) {
        console.error("获取用户资源列表失败:", error);
        ElMessage.error("获取资源列表失败，请稍后重试");

        // 使用模拟数据
        resources.value = generateMockData();
        total.value = resources.value.length;
      } finally {
        loading.value = false;
      }
    };

    // 生成模拟数据
    const generateMockData = () => {
      return [
        {
          id: 1,
          resourceId: 1,
          title: "计算机网络基础教程",
          description: "本资源包含计算机网络基础知识，适合初学者学习。",
          fileName: "计算机网络基础.pdf",
          format: ".pdf",
          uploadTime: new Date().toISOString(),
          viewCount: 120,
          downloadCount: 45,
          reviewStatus: "approved",
        },
        {
          id: 2,
          resourceId: 2,
          title: "数据结构课件",
          description:
            "数据结构与算法分析课程课件，包含各种数据结构的实现和分析。",
          fileName: "数据结构课件.pptx",
          format: ".pptx",
          uploadTime: new Date().toISOString(),
          viewCount: 89,
          downloadCount: 36,
          reviewStatus: "pending",
        },
        {
          id: 3,
          resourceId: 3,
          title: "软件工程实践指南",
          description:
            "软件工程实践指南，涵盖需求分析、设计、开发、测试等环节。",
          fileName: "软件工程实践指南.docx",
          format: ".docx",
          uploadTime: new Date().toISOString(),
          viewCount: 56,
          downloadCount: 28,
          reviewStatus: "rejected",
          rejectReason: "内容与课程思政主题不相符",
        },
      ];
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

    // 获取状态标签
    const getStatusLabel = (status) => {
      const statusMap = {
        approved: "已审核",
        pending: "审核中",
        rejected: "已拒绝",
      };
      return statusMap[status] || "未知状态";
    };

    // 获取状态样式类
    const getStatusClass = (status) => {
      const classMap = {
        approved: "status-approved",
        pending: "status-pending",
        rejected: "status-rejected",
      };
      return classMap[status] || "";
    };

    // 获取文件类型图标
    const getFileTypeIcon = (format) => {
      if (!format) return "📄";

      const formatLower = format.toLowerCase();

      if (formatLower.includes("pdf")) return "📕";
      if (formatLower.includes("doc")) return "📘";
      if (formatLower.includes("ppt")) return "📙";
      if (formatLower.includes("xls")) return "📗";
      if (formatLower.includes("zip") || formatLower.includes("rar"))
        return "📦";
      if (
        formatLower.includes("jpg") ||
        formatLower.includes("png") ||
        formatLower.includes("gif")
      )
        return "🖼️";
      if (formatLower.includes("mp4") || formatLower.includes("avi"))
        return "🎬";
      if (formatLower.includes("mp3") || formatLower.includes("wav"))
        return "🎵";

      return "📄";
    };

    // 获取文件类型样式类
    const getFileTypeClass = (format) => {
      if (!format) return "file-other";

      const formatLower = format.toLowerCase();

      if (formatLower.includes("pdf")) return "file-pdf";
      if (formatLower.includes("doc")) return "file-word";
      if (formatLower.includes("ppt")) return "file-ppt";
      if (formatLower.includes("xls")) return "file-excel";
      if (formatLower.includes("zip") || formatLower.includes("rar"))
        return "file-archive";
      if (
        formatLower.includes("jpg") ||
        formatLower.includes("png") ||
        formatLower.includes("gif")
      )
        return "file-image";
      if (formatLower.includes("mp4") || formatLower.includes("avi"))
        return "file-video";
      if (formatLower.includes("mp3") || formatLower.includes("wav"))
        return "file-audio";

      return "file-other";
    };

    // 处理标签页变化
    const handleTabChange = () => {
      currentPage.value = 1;
      fetchUserResources();
    };

    // 处理页码变化
    const handleCurrentChange = (page) => {
      currentPage.value = page;
      fetchUserResources();
    };

    // 处理每页数量变化
    const handleSizeChange = (size) => {
      pageSize.value = size;
      currentPage.value = 1;
      fetchUserResources();
    };

    // 搜索资源
    const searchResources = () => {
      currentPage.value = 1;
      fetchUserResources();
    };

    // 查看资源详情
    const viewResourceDetail = (resource) => {
      router.push(`/resources/${resource.resourceId}`);
    };

    // 跳转到上传资源页面
    const goToUpload = () => {
      router.push("/upload-resource");
    };

    // 处理下拉菜单命令
    const handleCommand = (command, resource) => {
      switch (command) {
        case "view":
          viewResourceDetail(resource);
          break;
        case "edit":
          router.push({
            path: "/upload-resource",
            query: { id: resource.resourceId },
          });
          break;
        case "resubmit":
          handleResubmit(resource);
          break;
        case "download":
          handleDownload(resource);
          break;
        case "delete":
          resourceToDelete.value = resource;
          deleteDialogVisible.value = true;
          break;
      }
    };

    // 重新提交资源
    const handleResubmit = (resource) => {
      router.push({
        path: "/upload-resource",
        query: { id: resource.resourceId, resubmit: true },
      });
    };

    // 下载资源
    const handleDownload = async (resource) => {
      try {
        // 显示加载中
        const loading = ElLoading.service({
          text: "正在下载...",
          background: "rgba(255, 255, 255, 0.7)",
        });

        // 调用下载API
        const response = await fetch(
          `/api/resources/download/${resource.resourceId}`,
          {
            method: "GET",
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );

        if (!response.ok) {
          throw new Error("下载失败");
        }

        // 处理文件下载
        const blob = await response.blob();
        const url = URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = url;
        link.download = resource.fileName || `资源_${resource.resourceId}`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);

        // 关闭加载
        loading.close();
        ElMessage.success("下载成功");
      } catch (error) {
        console.error("下载资源失败:", error);
        ElMessage.error("下载失败，请稍后重试");
      }
    };

    // 确认删除资源
    const confirmDelete = async () => {
      if (!resourceToDelete.value) return;

      try {
        // 调用删除API
        const response = await fetch(
          `/api/resources/${resourceToDelete.value.resourceId}`,
          {
            method: "DELETE",
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );

        if (!response.ok) {
          throw new Error("删除失败");
        }

        ElMessage.success("资源已成功删除");

        // 关闭对话框
        deleteDialogVisible.value = false;

        // 重新加载资源列表
        fetchUserResources();
      } catch (error) {
        console.error("删除资源失败:", error);
        ElMessage.error("删除失败，请稍后重试");
      }
    };

    // 组件挂载时加载资源列表
    onMounted(() => {
      fetchUserResources();
    });

    return {
      searchQuery,
      resources,
      loading,
      activeTab,
      currentPage,
      pageSize,
      total,
      deleteDialogVisible,
      resourceToDelete,
      formatDate,
      getStatusLabel,
      getStatusClass,
      getFileTypeIcon,
      getFileTypeClass,
      handleTabChange,
      handleCurrentChange,
      handleSizeChange,
      searchResources,
      viewResourceDetail,
      goToUpload,
      handleCommand,
      confirmDelete,
    };
  },
};
</script>

<style scoped>
.user-resources {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.page-actions {
  display: flex;
  gap: 15px;
}

.search-input {
  width: 250px;
}

.loading-container {
  padding: 20px;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.resource-list {
  margin-top: 20px;
}

.resource-card {
  margin-bottom: 15px;
}

.resource-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.resource-info {
  flex: 1;
}

.resource-type-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  margin-bottom: 8px;
}

.status-approved {
  background-color: #f0f9eb;
  color: #67c23a;
}

.status-pending {
  background-color: #f4f4f5;
  color: #909399;
}

.status-rejected {
  background-color: #fef0f0;
  color: #f56c6c;
}

.resource-title {
  margin: 0;
  font-size: 18px;
  color: #333;
  cursor: pointer;
}

.resource-title:hover {
  color: #409eff;
}

.resource-content {
  display: flex;
  margin-bottom: 15px;
}

.resource-icon {
  font-size: 36px;
  margin-right: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50px;
  height: 50px;
}

.file-pdf {
  color: #f56c6c;
}
.file-word {
  color: #409eff;
}
.file-ppt {
  color: #ff9800;
}
.file-excel {
  color: #67c23a;
}
.file-image {
  color: #9c27b0;
}
.file-video {
  color: #e91e63;
}
.file-audio {
  color: #00bcd4;
}
.file-archive {
  color: #795548;
}
.file-other {
  color: #607d8b;
}

.resource-details {
  flex: 1;
}

.resource-description {
  margin: 0 0 10px 0;
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

.resource-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  font-size: 13px;
  color: #999;
}

.resource-meta span {
  display: flex;
  align-items: center;
}

.resource-meta .el-icon {
  margin-right: 5px;
}

.reject-reason {
  margin-top: 15px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .page-actions {
    width: 100%;
    flex-direction: column;
  }

  .search-input {
    width: 100%;
  }

  .resource-meta {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
