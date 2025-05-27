<script>
import { ref, reactive, computed, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage, ElLoading } from "element-plus";
import {
  getResourceList,
  searchResources,
  getResourceTypes,
} from "@/api/resource";
import BaseLayout from "@/layout/BaseLayout.vue";

export default {
  name: "ResourceLibrary",
  components: {
    BaseLayout,
  },
  setup() {
    const router = useRouter();
    const route = useRoute();

    // 状态数据
    const activeCategory = ref("全部");
    const categories = ref(["全部"]);
    const resources = ref([]);
    const searchQuery = ref("");
    const loading = ref(false);
    const showSearchSuggestions = ref(false);
    const hotSearchTags = ref([
      "思政教育",
      "计算机网络",
      "人工智能",
      "通信原理",
      "爱国主义",
    ]);
    const searchHistory = ref([]);

    // 计算筛选后的资源
    const filteredResources = computed(() => {
      // 直接返回资源列表，因为已在API请求中过滤
      console.log(
        "当前显示的资源数量:",
        resources.value.length,
        "当前类别:",
        activeCategory.value
      );
      return resources.value;
    });

    // 设置分类
    const setCategory = (category) => {
      console.log(`切换类型从 ${activeCategory.value} 到 ${category}`);
      activeCategory.value = category;

      // 更新URL，保留已有的搜索关键词参数
      const query = { ...route.query };
      if (category === "全部") {
        delete query.type;
      } else {
        query.type = category;
      }

      // 替换当前路由，保留查询参数但不添加历史记录
      router.replace({
        path: route.path,
        query,
      });

      // 无论是否有搜索关键词，都重新获取资源以应用新类型筛选
      fetchResources();
    };

    // 处理搜索
    const handleSearch = async () => {
      if (!searchQuery.value.trim()) {
        ElMessage.warning("请输入搜索关键词");
        return;
      }

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

      loading.value = true;
      try {
        const params = {
          keyword: searchQuery.value.trim(),
        };

        // 如果选择了特定类型，添加到搜索参数中
        if (activeCategory.value !== "全部") {
          params.type = activeCategory.value;
        }

        console.log("搜索参数:", params);
        const response = await searchResources(params);

        if (response && response.code === 200 && response.data) {
          resources.value = response.data.map(formatResourceData);
        } else if (response && Array.isArray(response)) {
          // 处理直接返回数组的情况
          resources.value = response.map(formatResourceData);
        } else {
          ElMessage.error("搜索资源失败");
          console.error("搜索响应格式错误:", response);
        }

        // 更新URL以包含搜索关键词
        const query = { ...route.query, keyword: searchQuery.value.trim() };
        if (activeCategory.value !== "全部") {
          query.type = activeCategory.value;
        } else {
          delete query.type;
        }

        router.replace({
          path: route.path,
          query,
        });
      } catch (error) {
        ElMessage.error("搜索资源失败");
        console.error("搜索资源出错:", error);
      } finally {
        loading.value = false;
      }
    };

    // 查看资源详情
    const viewResource = (resource) => {
      router.push({
        path: `/resources/${resource.id || resource.resourceId}`,
      });
    };

    // 获取资源类型
    const fetchResourceTypes = async () => {
      try {
        const response = await getResourceTypes();
        console.log("资源类型API响应:", response);

        if (response && response.code === 200 && response.data) {
          // 将后端返回的类型数据转化为前端需要的格式
          categories.value = ["全部", ...response.data];
          console.log("获取到的资源类型:", categories.value);
        } else if (response && Array.isArray(response)) {
          // 处理直接返回数组的情况
          categories.value = ["全部", ...response];
          console.log("获取到的资源类型(数组):", categories.value);
        } else if (response && typeof response === "object") {
          // 处理直接返回对象的情况
          categories.value = ["全部", ...Object.values(response)];
          console.log("获取到的资源类型(对象):", categories.value);
        } else {
          console.error("获取资源类型响应格式错误:", response);
          // 使用默认类型
          categories.value = ["全部", "计算机", "通信", "人工智能"];
        }
      } catch (error) {
        console.error("获取资源类型失败:", error);
        // 使用默认类型
        categories.value = ["全部", "计算机", "通信", "人工智能"];
      }
    };

    // 获取资源列表
    const fetchResources = async () => {
      loading.value = true;
      try {
        // 构建请求参数，按类型过滤
        const params = {};
        if (activeCategory.value !== "全部") {
          params.type = activeCategory.value;
        }

        console.log("获取资源列表参数:", params);
        const response = await getResourceList(params);
        console.log("资源列表API响应:", response);

        if (response && response.code === 200 && response.data) {
          resources.value = response.data.map(formatResourceData);
        } else if (response && Array.isArray(response)) {
          // 处理直接返回数组的情况
          resources.value = response.map(formatResourceData);
        } else if (
          response &&
          typeof response === "object" &&
          !("code" in response)
        ) {
          // 尝试将对象转换为数组处理
          resources.value = Object.values(response).map(formatResourceData);
        } else {
          console.error("获取资源列表响应格式错误:", response);
          ElMessage.error("获取资源列表失败");
        }
      } catch (error) {
        console.error("获取资源列表失败:", error);
        ElMessage.error("获取资源列表失败");
      } finally {
        loading.value = false;
      }
    };

    // 格式化资源数据
    const formatResourceData = (resource) => {
      console.log("正在格式化资源:", resource);
      return {
        id: resource.resourceId || resource.resource_id,
        title: resource.title,
        category: resource.type || "未分类",
        type: getResourceTypeLabel(resource.format),
        author: resource.uploaderName || "未知用户",
        views: resource.viewCount || resource.view_count || 0,
        date: formatDate(resource.uploadTime || resource.upload_time),
        description: resource.description,
        // 确保返回所有可能需要的原始数据
        resourceId: resource.resourceId || resource.resource_id,
        format: resource.format,
        filePath: resource.filePath || resource.file_path,
        fileName: resource.fileName || resource.file_name,
        downloadCount: resource.downloadCount || resource.download_count || 0,
        reviewStatus: resource.reviewStatus || resource.review_status,
        uploaderId: resource.uploaderId || resource.uploader_id,
      };
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

    // 根据文件格式获取资源类型标签
    const getResourceTypeLabel = (format) => {
      if (!format) return "其他";

      const formatLower = format.toLowerCase().replace(".", "");

      if (["pdf"].includes(formatLower)) {
        return "PDF文档";
      } else if (["doc", "docx"].includes(formatLower)) {
        return "Word文档";
      } else if (["xls", "xlsx"].includes(formatLower)) {
        return "Excel表格";
      } else if (["ppt", "pptx"].includes(formatLower)) {
        return "PPT演示";
      } else if (["jpg", "jpeg", "png", "gif"].includes(formatLower)) {
        return "图片资源";
      } else if (["mp4", "avi", "mov", "wmv"].includes(formatLower)) {
        return "视频资源";
      } else if (["mp3", "wav", "ogg"].includes(formatLower)) {
        return "音频资源";
      }

      return "其他";
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

    // 移除搜索历史
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

    // 选择搜索标签
    const selectSearchTag = (tag) => {
      searchQuery.value = tag;
      showSearchSuggestions.value = false;
      handleSearch();
    };

    // 组件挂载时初始化数据
    onMounted(async () => {
      // 加载搜索历史
      loadSearchHistory();

      // 处理URL查询参数
      if (route.query.keyword) {
        searchQuery.value = route.query.keyword;
      }

      // 获取资源类型
      await fetchResourceTypes();

      // 如果URL中有指定类型，切换到该类型
      if (route.query.type && categories.value.includes(route.query.type)) {
        activeCategory.value = route.query.type;
        console.log("从URL设置类型为:", activeCategory.value);
      }

      // 如果有搜索关键词，直接搜索
      if (searchQuery.value) {
        handleSearch();
      } else {
        // 否则获取所有资源
        fetchResources();
      }
    });

    // 监听路由变化，处理AppHeader的搜索请求
    router.afterEach((to) => {
      if (
        to.path === route.path &&
        to.query.keyword &&
        to.query.keyword !== searchQuery.value
      ) {
        searchQuery.value = to.query.keyword;
        handleSearch();
      }
    });

    // 点击查看资源详情
    const viewResourceDetail = (resource) => {
      // 确保使用正确的ID，优先使用resourceId，如果没有则使用id
      const resourceId = resource.resourceId || resource.id;
      if (!resourceId) {
        console.error("资源ID不存在", resource);
        ElMessage.error("无法查看资源详情");
        return;
      }
      router.push(`/resources/${resourceId}`);
    };

    // 点击卡片查看详情
    const handleCardClick = (resource) => {
      viewResourceDetail(resource);
    };

    return {
      activeCategory,
      categories,
      resources: filteredResources,
      searchQuery,
      loading,
      showSearchSuggestions,
      hotSearchTags,
      searchHistory,
      setCategory,
      handleSearch,
      viewResource,
      viewResourceDetail,
      handleCardClick,
      removeSearchHistory,
      clearSearchHistory,
      selectSearchTag,
    };
  },
};
</script>

<template>
  <base-layout>
    <div class="resource-library">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1>课程思政资源库</h1>
        <div class="search-bar">
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
      </div>

      <!-- 分类导航 -->
      <div class="category-nav">
        <button
          v-for="category in categories"
          :key="category"
          :class="['category-btn', { active: activeCategory === category }]"
          @click="setCategory(category)"
        >
          {{ category }}
        </button>
      </div>

      <!-- 资源列表 -->
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>
      <div v-else-if="resources.length === 0" class="empty-container">
        <div class="empty-icon">📚</div>
        <p>暂无相关资源</p>
      </div>
      <div v-else class="resources-grid">
        <div
          v-for="resource in resources"
          :key="resource.id || resource.resourceId"
          class="resource-card"
          @click="viewResourceDetail(resource)"
        >
          <div class="resource-type">{{ resource.type }}</div>
          <h3>{{ resource.title }}</h3>
          <p v-if="resource.description" class="resource-description">
            {{
              resource.description.length > 50
                ? resource.description.substring(0, 50) + "..."
                : resource.description
            }}
          </p>
          <div class="resource-meta">
            <span class="category">{{ resource.category }}</span>
            <span class="author">{{ resource.author }}</span>
          </div>
          <div class="resource-footer">
            <span class="views">👁️ {{ resource.views }}</span>
            <span class="date">{{ resource.date }}</span>
          </div>
        </div>
      </div>
    </div>
  </base-layout>
</template>

<style scoped>
.resource-library {
  background-color: #f0f2f5;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-header h1 {
  color: #2c3e50;
  margin: 0;
  font-size: 2rem;
}

.search-bar {
  display: flex;
  gap: 1rem;
  max-width: 500px;
}

.search-input-container {
  position: relative;
  width: 100%;
  display: flex;
}

.search-input-container input {
  flex: 1;
  padding: 0.7rem 1rem;
  border: 1px solid #e8e8e8;
  border-radius: 4px 0 0 4px;
  font-size: 1rem;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.search-input-container button {
  padding: 0.7rem 1.5rem;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
  transition: background-color 0.3s;
  font-weight: 500;
}

.search-input-container button:hover {
  background-color: #40a9ff;
}

/* 搜索建议样式 */
.search-suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  width: 100%;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 100;
  margin-top: 5px;
  padding: 1rem;
}

.suggestions-section {
  margin-bottom: 1rem;
}

.suggestions-section h4 {
  margin: 0 0 0.5rem 0;
  color: #666;
  font-size: 0.9rem;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.search-tag {
  display: inline-block;
  padding: 0.3rem 0.7rem;
  background-color: #f5f5f5;
  border-radius: 15px;
  font-size: 0.85rem;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.search-tag:hover {
  background-color: #e6f7ff;
  color: #1890ff;
}

.history-tag {
  display: flex;
  align-items: center;
  background-color: #f0f0f0;
}

.tag-remove {
  margin-left: 5px;
  font-size: 1rem;
  line-height: 1;
  padding: 0 3px;
  border-radius: 50%;
  color: #999;
}

.tag-remove:hover {
  color: #f56c6c;
  background-color: #fef0f0;
}

.clear-history {
  text-align: right;
  color: #999;
  font-size: 0.8rem;
  cursor: pointer;
  margin-top: 0.5rem;
}

.clear-history:hover {
  color: #1890ff;
}

.category-nav {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.category-btn {
  padding: 0.5rem 1.5rem;
  background-color: white;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  color: #666;
}

.category-btn:hover {
  color: #1890ff;
  border-color: #1890ff;
}

.category-btn.active {
  background-color: #1890ff;
  color: white;
  border-color: #1890ff;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.loading-spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #1890ff;
  border-radius: 50%;
  width: 40px;
  height: 40px;
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

.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.5;
}

.resources-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 2rem;
}

.resource-card {
  background-color: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.resource-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.resource-type {
  display: inline-block;
  padding: 0.2rem 0.5rem;
  background-color: #e6f7ff;
  color: #1890ff;
  border-radius: 4px;
  font-size: 0.9rem;
  margin-bottom: 1rem;
}

.resource-card h3 {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 1.1rem;
}

.resource-description {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 1rem;
  line-height: 1.5;
}

.resource-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
  color: #666;
  font-size: 0.9rem;
}

.resource-footer {
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }

  .search-bar {
    max-width: 100%;
  }
}
</style>
