<template>
  <div class="announcement-list-page">
    <h1 class="page-title">通知公告</h1>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索公告标题"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <!-- 公告列表 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>
    <div v-else-if="filteredAnnouncements.length === 0" class="empty-container">
      <el-empty description="暂无公告" />
    </div>
    <div v-else class="announcements-grid">
      <div
        v-for="announcement in filteredAnnouncements"
        :key="announcement.id"
        class="announcement-card"
        :class="{ important: announcement.important }"
        @click="viewAnnouncementDetail(announcement)"
      >
        <div class="announcement-header">
          <span v-if="announcement.important" class="important-tag">重要</span>
          <h3 class="announcement-title">{{ announcement.title }}</h3>
        </div>
        <div class="announcement-content">
          <p v-if="announcement.content" class="announcement-summary">
            {{ stripHtml(announcement.content).substring(0, 100)
            }}{{ stripHtml(announcement.content).length > 100 ? "..." : "" }}
          </p>
        </div>
        <div class="announcement-footer">
          <span class="announcement-date"
            >发布时间: {{ formatDate(announcement.date) }}</span
          >
        </div>
      </div>
    </div>

    <!-- 分页控件 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalAnnouncements"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 公告详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="currentAnnouncement.title"
      width="700px"
    >
      <div class="announcement-dialog-content">
        <div class="announcement-meta">
          <span class="announcement-date"
            >发布时间: {{ formatDate(currentAnnouncement.date) }}</span
          >
        </div>
        <div
          class="announcement-body"
          v-html="currentAnnouncement.content"
        ></div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { Search } from "@element-plus/icons-vue";
import { getAnnouncements } from "@/api/system";

export default {
  name: "AnnouncementList",
  setup() {
    // 数据
    const announcements = ref([]);
    const loading = ref(false);
    const searchKeyword = ref("");
    const currentPage = ref(1);
    const pageSize = ref(10);
    const totalAnnouncements = ref(0);
    const dialogVisible = ref(false);
    const currentAnnouncement = ref({});

    // 计算筛选后的公告
    const filteredAnnouncements = computed(() => {
      let result = [...announcements.value];

      if (searchKeyword.value) {
        const keyword = searchKeyword.value.toLowerCase();
        result = result.filter(
          (item) =>
            item.title.toLowerCase().includes(keyword) ||
            stripHtml(item.content).toLowerCase().includes(keyword)
        );
      }

      // 分页
      totalAnnouncements.value = result.length;
      const start = (currentPage.value - 1) * pageSize.value;
      const end = start + pageSize.value;
      return result.slice(start, end);
    });

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return "";
      const date = new Date(dateString);
      return date.toLocaleDateString("zh-CN", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
      });
    };

    // 移除HTML标签
    const stripHtml = (html) => {
      if (!html) return "";
      return html.replace(/<[^>]*>/g, "");
    };

    // 获取公告列表
    const fetchAnnouncements = async () => {
      loading.value = true;
      try {
        const response = await getAnnouncements(100); // 获取较多公告

        if (response && response.code === 200 && response.data) {
          announcements.value = response.data.map((item) => ({
            id: item.id,
            title: item.title,
            content: item.content,
            date: item.publishTime || item.createTime,
            important: item.type > 0, // 类型大于0的为重要公告
          }));
          totalAnnouncements.value = announcements.value.length;
        } else {
          ElMessage.error("获取公告列表失败");
        }
      } catch (error) {
        console.error("获取公告列表失败:", error);
        ElMessage.error("获取公告列表失败");
      } finally {
        loading.value = false;
      }
    };

    // 查看公告详情
    const viewAnnouncementDetail = (announcement) => {
      currentAnnouncement.value = announcement;
      dialogVisible.value = true;
    };

    // 搜索
    const handleSearch = () => {
      currentPage.value = 1;
    };

    // 重置搜索
    const resetSearch = () => {
      searchKeyword.value = "";
      currentPage.value = 1;
    };

    // 分页
    const handleSizeChange = (val) => {
      pageSize.value = val;
      currentPage.value = 1;
    };

    const handleCurrentChange = (val) => {
      currentPage.value = val;
    };

    // 生命周期钩子
    onMounted(() => {
      fetchAnnouncements();
    });

    return {
      announcements,
      filteredAnnouncements,
      loading,
      searchKeyword,
      currentPage,
      pageSize,
      totalAnnouncements,
      dialogVisible,
      currentAnnouncement,
      formatDate,
      stripHtml,
      viewAnnouncementDetail,
      handleSearch,
      resetSearch,
      handleSizeChange,
      handleCurrentChange,
    };
  },
};
</script>

<style scoped>
.announcement-list-page {
  padding: 2rem;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-title {
  margin-bottom: 2rem;
  color: #303133;
  font-size: 2rem;
  text-align: center;
}

.search-bar {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.loading-container,
.empty-container {
  padding: 2rem;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.announcements-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.announcement-card {
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.announcement-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px 0 rgba(0, 0, 0, 0.15);
}

.announcement-card.important {
  border-left: 4px solid #f56c6c;
}

.announcement-header {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
}

.important-tag {
  background-color: #f56c6c;
  color: white;
  padding: 0.2rem 0.5rem;
  border-radius: 2px;
  font-size: 0.8rem;
  margin-right: 0.8rem;
}

.announcement-title {
  margin: 0;
  font-size: 1.2rem;
  color: #303133;
}

.announcement-content {
  margin-bottom: 1rem;
}

.announcement-summary {
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

.announcement-footer {
  display: flex;
  justify-content: space-between;
  color: #909399;
  font-size: 0.9rem;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
}

.announcement-dialog-content {
  padding: 1rem 0;
}

.announcement-meta {
  margin-bottom: 1.5rem;
  color: #909399;
  font-size: 0.9rem;
}

.announcement-body {
  line-height: 1.8;
  color: #303133;
}

@media (max-width: 768px) {
  .search-bar {
    flex-direction: column;
    gap: 0.5rem;
  }

  .page-title {
    font-size: 1.5rem;
    margin-bottom: 1.5rem;
  }
}
</style>
