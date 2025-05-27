<template>
  <base-layout>
    <section class="hero">
      <h1>{{ siteTitle }}</h1>
      <p>{{ siteDescription }}</p>
      <div class="hero-search">
        <div class="search-input-container">
          <input
            type="text"
            v-model="searchQuery"
            placeholder="输入关键词搜索资源..."
            @keyup.enter="handleSearch"
            @focus="showSearchSuggestions = true"
            @blur="setTimeout(() => (showSearchSuggestions = false), 200)"
          />
          <button @click="handleSearch">立即搜索</button>

          <!-- 搜索建议下拉框 -->
          <div class="search-suggestions" v-if="showSearchSuggestions">
            <div class="suggestions-section">
              <h4>热门搜索</h4>
              <div class="tags-container">
                <span
                  v-for="(tag, index) in hotSearchTags"
                  :key="'hot-' + index"
                  class="search-tag"
                  @click="selectSearchTag(tag)"
                  >{{ tag }}</span
                >
              </div>
            </div>
            <div class="suggestions-section" v-if="searchHistory.length > 0">
              <h4>搜索历史</h4>
              <div class="tags-container">
                <span
                  v-for="(tag, index) in searchHistory"
                  :key="'history-' + index"
                  class="search-tag history-tag"
                  @click="selectSearchTag(tag)"
                >
                  {{ tag }}
                  <span
                    class="tag-remove"
                    @click.stop="removeSearchHistory(index)"
                    >×</span
                  >
                </span>
              </div>
              <div class="clear-history" @click="clearSearchHistory">
                清空历史记录
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 通知公告 -->
    <section class="announcements">
      <div class="section-header">
        <h2>通知公告</h2>
        <router-link to="/announcements" class="more-link"
          >查看更多 ></router-link
        >
      </div>
      <div v-if="loadingAnnouncements" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>
      <div v-else class="announcement-list">
        <div
          v-for="(announcement, index) in announcements"
          :key="index"
          class="announcement-item"
          :class="{ important: announcement.important }"
          @click="viewAnnouncement(announcement)"
        >
          <span class="announcement-badge" v-if="announcement.important"
            >重要</span
          >
          <div class="announcement-content">
            <h3>{{ announcement.title }}</h3>
            <span class="announcement-date">{{
              formatDate(announcement.date)
            }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 特性展示 -->
    <section class="features">
      <h2 class="section-title">资源分类</h2>
      <div v-if="loadingFeatures" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>
      <div v-else class="feature-grid">
        <div
          v-for="(feature, index) in features"
          :key="index"
          class="feature-item"
          @click="navigateToCategory(feature.title)"
        >
          <div class="feature-icon">{{ feature.icon }}</div>
          <div class="feature-text">
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.description }}</p>
            <div class="feature-count">{{ feature.count }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 热门资源 -->
    <section class="hot-resources">
      <div class="section-header">
        <h2>热门资源</h2>
        <router-link to="/resources" class="more-link">更多资源 ></router-link>
      </div>
      <div v-if="loadingResources" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>
      <div v-else-if="hotResources.length === 0" class="empty-container">
        <el-empty description="暂无热门资源" />
      </div>
      <div v-else class="resources-grid">
        <resource-card
          v-for="(resource, index) in hotResources"
          :key="resource.id || index"
          :resource="resource"
          @click="viewResource(resource.id)"
        />
      </div>
    </section>

    <!-- 系统公告对话框 -->
    <el-dialog
      v-model="announcementDialogVisible"
      :title="currentAnnouncement.title"
      width="600px"
    >
      <div class="announcement-dialog-content">
        <p class="announcement-date">
          发布时间：{{ formatDate(currentAnnouncement.date) }}
        </p>
        <div
          class="announcement-body"
          v-html="currentAnnouncement.content"
        ></div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="announcementDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </base-layout>
</template>

<script>
import { ref, reactive, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { useStore } from "vuex";
import { ElMessage } from "element-plus";
import BaseLayout from "@/layout/BaseLayout.vue";
import ResourceCard from "@/components/common/ResourceCard.vue";
import { systemApi } from "@/api";

export default {
  name: "HomePage",
  components: {
    BaseLayout,
    ResourceCard,
  },
  setup() {
    const store = useStore();
    const router = useRouter();
    const searchQuery = ref("");
    const hotResources = ref([]);
    const loadingResources = ref(false);
    const loadingAnnouncements = ref(false);
    const loadingFeatures = ref(false);
    const announcements = ref([]);
    const announcementDialogVisible = ref(false);
    const currentAnnouncement = ref({});
    const systemConfig = ref({});
    const showSearchSuggestions = ref(false);
    const hotSearchTags = ref([
      "思政教育",
      "计算机网络",
      "人工智能",
      "通信原理",
      "爱国主义",
    ]);
    const searchHistory = ref([]);

    // 网站基本信息
    const siteTitle = computed(() => {
      return systemConfig.value.site_title || "课程思政资源管理系统";
    });

    const siteDescription = computed(() => {
      return (
        systemConfig.value.site_description ||
        "整合优质思政教学资源，助力课程思政建设"
      );
    });

    // 功能分类统计
    const features = ref([
      {
        title: "思政资源库",
        description: "丰富的课程思政教学资源",
        icon: "📚",
        count: "...",
      },
      {
        title: "通信",
        description: "优秀课程思政教学案例分享",
        icon: "🎯",
        count: "...",
      },
      {
        title: "人工智能",
        description: "课程思政教学研究与成果",
        icon: "🔍",
        count: "...",
      },
    ]);

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

    // 从localStorage加载搜索历史
    const loadSearchHistory = () => {
      try {
        const history = localStorage.getItem("searchHistory");
        if (history) {
          searchHistory.value = JSON.parse(history);
        }
      } catch (e) {
        console.error("加载搜索历史失败", e);
        searchHistory.value = [];
      }
    };

    // 选择搜索标签
    const selectSearchTag = (tag) => {
      searchQuery.value = tag;
      handleSearch();
    };

    // 移除搜索历史项
    const removeSearchHistory = (index) => {
      searchHistory.value.splice(index, 1);
      localStorage.setItem(
        "searchHistory",
        JSON.stringify(searchHistory.value)
      );
    };

    // 清空搜索历史
    const clearSearchHistory = () => {
      searchHistory.value = [];
      localStorage.removeItem("searchHistory");
    };

    // 搜索资源
    const handleSearch = () => {
      if (searchQuery.value.trim()) {
        // 记录搜索词到localStorage
        try {
          if (!searchHistory.value.includes(searchQuery.value.trim())) {
            searchHistory.value.unshift(searchQuery.value.trim());
            // 只保留最近10条搜索记录
            if (searchHistory.value.length > 10) {
              searchHistory.value.pop();
            }
            localStorage.setItem(
              "searchHistory",
              JSON.stringify(searchHistory.value)
            );
          }
        } catch (e) {
          console.error("保存搜索历史失败", e);
        }

        // 跳转到资源页面并带上搜索参数
        router.push({
          path: "/resources",
          query: { keyword: searchQuery.value.trim() },
        });
      } else {
        ElMessage.warning("请输入搜索关键词");
      }
    };

    // 查看公告详情
    const viewAnnouncement = (announcement) => {
      currentAnnouncement.value = announcement;
      announcementDialogVisible.value = true;
    };

    // 导航到分类页面
    const navigateToCategory = (category) => {
      router.push({
        path: "/resources",
        query: { type: category },
      });
    };

    // 查看资源详情
    const viewResource = (resourceId) => {
      router.push({
        path: `/resources/${resourceId}`,
      });
    };

    // 获取热门资源
    const fetchHotResources = async () => {
      loadingResources.value = true;
      try {
        // 使用store中的action获取热门资源
        await store.dispatch("resource/getHotResources", 6);
        const storeResources = store.getters["resource/hotResources"];

        console.log("获取到的热门资源:", storeResources);

        if (storeResources && storeResources.length > 0) {
          // 转换API返回的数据格式为组件需要的格式
          hotResources.value = storeResources.map((resource) => ({
            id: resource.resourceId,
            title: resource.title,
            type: resource.type || "未分类",
            views: resource.viewCount || 0,
            author: resource.uploaderName || "未知用户",
            description: resource.description,
            tags: resource.tags
              ? typeof resource.tags === "string"
                ? JSON.parse(resource.tags)
                : resource.tags
              : [],
          }));
        } else {
          console.log("未获取到热门资源数据，使用备用数据");
          // 使用备用数据
          hotResources.value = [
            {
              id: 1,
              title: "计算机网络中的爱国情怀",
              type: "计算机",
              views: 1234,
              author: "张教授",
              tags: ["计算机网络", "爱国主义"],
            },
            {
              id: 2,
              title: "数据结构与民族精神",
              type: "通信",
              views: 890,
              author: "李教授",
              tags: ["数据结构", "民族精神"],
            },
            {
              id: 3,
              title: "人工智能伦理与价值观",
              type: "人工智能",
              views: 567,
              author: "王教授",
              tags: ["人工智能", "伦理价值观"],
            },
          ];
        }
      } catch (error) {
        console.error("获取热门资源失败:", error);
        // 使用备用数据
        hotResources.value = [
          {
            id: 1,
            title: "计算机网络中的爱国情怀",
            type: "计算机",
            views: 1234,
            author: "张教授",
            tags: ["计算机网络", "爱国主义"],
          },
          {
            id: 2,
            title: "数据结构与民族精神",
            type: "通信",
            views: 890,
            author: "李教授",
            tags: ["数据结构", "民族精神"],
          },
          {
            id: 3,
            title: "人工智能伦理与价值观",
            type: "人工智能",
            views: 567,
            author: "王教授",
            tags: ["人工智能", "伦理价值观"],
          },
        ];
      } finally {
        loadingResources.value = false;
      }
    };

    // 获取系统公告
    const fetchAnnouncements = async () => {
      loadingAnnouncements.value = true;
      try {
        // 使用API端点获取公告
        const response = await systemApi.getAnnouncements(3);

        if (response && response.code === 200 && response.data) {
          announcements.value = response.data.map((item) => ({
            id: item.id,
            title: item.title,
            date: item.publishTime || item.createTime,
            important: item.type > 0, // 类型大于0的为重要公告
            content: item.content,
          }));
        } else {
          console.error("获取公告数据格式错误:", response);
          // 使用备用数据
          announcements.value = [
            {
              id: 1,
              title: "关于开展2024年课程思政示范课程建设的通知",
              date: "2024-03-20",
              important: true,
              content:
                "<p>为深入贯彻习近平新时代中国特色社会主义思想和党的二十大精神，落实立德树人根本任务，推进课程思政建设，现面向全校开展2024年课程思政示范课程建设工作。</p>",
            },
            {
              id: 2,
              title: "2024年春季课程思政教学研讨会通知",
              date: "2024-03-18",
              important: false,
              content:
                "<p>为促进课程思政教学经验交流，提升教师课程思政教学能力，学校定于4月15日举办2024年春季课程思政教学研讨会。</p>",
            },
            {
              id: 3,
              title: "优秀课程思政案例征集通知",
              date: "2024-03-15",
              important: true,
              content:
                "<p>为展示我校课程思政建设成果，促进优秀教学经验交流与推广，现面向全校教师征集优秀课程思政案例。</p>",
            },
          ];
        }
      } catch (error) {
        console.error("获取系统公告失败:", error);
        // 使用备用数据
        announcements.value = [
          {
            id: 1,
            title: "关于开展2024年课程思政示范课程建设的通知",
            date: "2024-03-20",
            important: true,
            content:
              "<p>为深入贯彻习近平新时代中国特色社会主义思想和党的二十大精神，落实立德树人根本任务，推进课程思政建设，现面向全校开展2024年课程思政示范课程建设工作。</p>",
          },
          {
            id: 2,
            title: "2024年春季课程思政教学研讨会通知",
            date: "2024-03-18",
            important: false,
            content:
              "<p>为促进课程思政教学经验交流，提升教师课程思政教学能力，学校定于4月15日举办2024年春季课程思政教学研讨会。</p>",
          },
          {
            id: 3,
            title: "优秀课程思政案例征集通知",
            date: "2024-03-15",
            important: true,
            content:
              "<p>为展示我校课程思政建设成果，促进优秀教学经验交流与推广，现面向全校教师征集优秀课程思政案例。</p>",
          },
        ];
      } finally {
        loadingAnnouncements.value = false;
      }
    };

    // 获取各类资源数量
    const fetchResourceTypeCount = async () => {
      loadingFeatures.value = true;
      try {
        // 统计API获取类型数量
        const response = await systemApi.getResourceTypeCount();

        if (response && response.code === 200 && response.data) {
          // 更新特性展示中的数量
          const typeCount = response.data;
          console.log("资源类型统计:", typeCount);

          // 更新features中对应类型的数量
          features.value = [
            {
              title: "计算机",
              description: "丰富的课程思政教学资源",
              icon: "📚",
              count: typeCount["计算机"] || 0,
            },
            {
              title: "通信",
              description: "优秀课程思政教学案例分享",
              icon: "🎯",
              count: typeCount["通信"] || 0,
            },
            {
              title: "人工智能",
              description: "课程思政教学研究与成果",
              icon: "🔍",
              count: typeCount["人工智能"] || 0,
            },
          ];
        } else {
          console.error("获取资源类型统计数据格式错误:", response);
        }
      } catch (error) {
        console.error("获取资源类型统计失败:", error);
        // 默认数量设置为0
        features.value = features.value.map((feature) => ({
          ...feature,
          count: 0,
        }));
      } finally {
        loadingFeatures.value = false;
      }
    };

    // 获取系统配置
    const fetchSystemConfig = async () => {
      try {
        const res = await systemApi.getSystemConfigMap();
        if (res.code === 200 && res.data) {
          systemConfig.value = res.data;
        }
      } catch (error) {
        console.error("获取系统配置失败:", error);
      }
    };

    onMounted(() => {
      // 并行加载所有数据
      Promise.all([
        fetchHotResources(),
        fetchAnnouncements(),
        fetchResourceTypeCount(),
        fetchSystemConfig(),
      ]);

      // 加载搜索历史
      loadSearchHistory();
    });

    return {
      searchQuery,
      features,
      announcements,
      hotResources,
      loadingResources,
      loadingAnnouncements,
      loadingFeatures,
      announcementDialogVisible,
      currentAnnouncement,
      siteTitle,
      siteDescription,
      formatDate,
      handleSearch,
      viewAnnouncement,
      navigateToCategory,
      viewResource,
      showSearchSuggestions,
      hotSearchTags,
      searchHistory,
      selectSearchTag,
      removeSearchHistory,
      clearSearchHistory,
    };
  },
};
</script>

<style scoped>
.hero {
  background: linear-gradient(to right, #1890ff, #36cfc9);
  color: white;
  padding: 5rem 2rem;
  text-align: center;
  border-radius: 8px;
  margin-bottom: 2rem;
}

.hero h1 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.hero p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
}

.hero-search {
  display: flex;
  max-width: 600px;
  margin: 0 auto;
}

.hero-search input {
  flex: 1;
  padding: 0.8rem 1rem;
  border: none;
  border-radius: 4px 0 0 4px;
  outline: none;
  font-size: 1rem;
}

.hero-search button {
  padding: 0.8rem 1.5rem;
  background-color: #001529;
  color: white;
  border: none;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.3s;
}

.hero-search button:hover {
  background-color: #003a70;
}

section {
  margin-bottom: 3rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.section-header h2 {
  font-size: 1.8rem;
  margin: 0;
  color: #333;
}

.more-link {
  color: #1890ff;
  text-decoration: none;
}

.announcement-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  grid-gap: 1.5rem;
}

.announcement-item {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
  position: relative;
  transition: transform 0.3s;
  cursor: pointer;
}

.announcement-item:hover {
  transform: translateY(-5px);
}

.announcement-item.important {
  border-left: 4px solid #ff4d4f;
}

.announcement-badge {
  position: absolute;
  top: 0;
  right: 0;
  background-color: #ff4d4f;
  color: white;
  padding: 0.3rem 0.8rem;
  border-radius: 0 0 0 8px;
  font-size: 0.8rem;
}

.announcement-content h3 {
  margin-top: 0;
  margin-bottom: 0.8rem;
  color: #333;
}

.announcement-date {
  color: #999;
  font-size: 0.9rem;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  grid-gap: 2rem;
}

.feature-item {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  display: flex;
  align-items: flex-start;
  transition: transform 0.3s;
  cursor: pointer;
}

.feature-item:hover {
  transform: translateY(-5px);
}

.feature-icon {
  font-size: 2.5rem;
  margin-right: 1.5rem;
}

.feature-text {
  flex: 1;
}

.feature-text h3 {
  margin-top: 0;
  margin-bottom: 0.5rem;
  color: #333;
}

.feature-text p {
  color: #666;
  margin-bottom: 1rem;
}

.feature-count {
  font-size: 1.2rem;
  font-weight: bold;
  color: #1890ff;
}

.resources-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  grid-gap: 1.5rem;
}

.loading-container {
  padding: 2rem;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.empty-container {
  padding: 3rem;
  text-align: center;
}

.announcement-dialog-content {
  padding: 1rem 0;
}

.announcement-body {
  margin-top: 1rem;
  line-height: 1.6;
}

.search-input-container {
  position: relative;
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
}

.search-suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  width: 100%;
  background-color: white;
  border-radius: 0 0 8px 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 100;
  padding: 15px;
  margin-top: 5px;
}

.suggestions-section {
  margin-bottom: 15px;
}

.suggestions-section h4 {
  font-size: 14px;
  color: #666;
  margin: 0 0 10px 0;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.search-tag {
  display: inline-flex;
  align-items: center;
  background-color: #f5f5f5;
  color: #333;
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.search-tag:hover {
  background-color: #e0e0e0;
}

.history-tag {
  background-color: #fff;
  border: 1px solid #e0e0e0;
  padding-right: 8px;
}

.tag-remove {
  margin-left: 5px;
  font-size: 16px;
  font-weight: bold;
  color: #999;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  border-radius: 50%;
}

.tag-remove:hover {
  background-color: #f0f0f0;
  color: #666;
}

.clear-history {
  text-align: center;
  font-size: 13px;
  color: #999;
  margin-top: 10px;
  cursor: pointer;
}

.clear-history:hover {
  color: #666;
  text-decoration: underline;
}
</style>
