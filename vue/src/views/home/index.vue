<template>
  <base-layout>
    <section class="hero">
      <h1>课程思政资源管理系统</h1>
      <p>整合优质思政教学资源，助力课程思政建设</p>
      <div class="hero-search">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="输入关键词搜索资源..."
          @keyup.enter="handleSearch"
        />
        <button @click="handleSearch">立即搜索</button>
      </div>
    </section>

    <!-- 通知公告 -->
    <section class="announcements">
      <div class="section-header">
        <h2>通知公告</h2>
        <a href="#" class="more-link">查看更多 ></a>
      </div>
      <div class="announcement-list">
        <div
          v-for="(announcement, index) in announcements"
          :key="index"
          class="announcement-item"
          :class="{ important: announcement.important }"
        >
          <span class="announcement-badge" v-if="announcement.important"
            >重要</span
          >
          <div class="announcement-content">
            <h3>{{ announcement.title }}</h3>
            <span class="announcement-date">{{ announcement.date }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 特性展示 -->
    <section class="features">
      <h2 class="section-title">系统功能</h2>
      <div class="feature-grid">
        <div
          v-for="(feature, index) in features"
          :key="index"
          class="feature-item"
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
      <div class="resources-grid">
        <resource-card
          v-for="(resource, index) in hotResources"
          :key="index"
          :resource="resource"
        />
      </div>
    </section>
  </base-layout>
</template>

<script>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useStore } from "vuex";
import BaseLayout from "@/layout/BaseLayout.vue";
import ResourceCard from "@/components/common/ResourceCard.vue";

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

    const features = ref([
      {
        title: "思政资源库",
        description: "丰富的课程思政教学资源",
        icon: "📚",
        count: "1,234",
      },
      {
        title: "通信",
        description: "优秀课程思政教学案例分享",
        icon: "🎯",
        count: "328",
      },
      {
        title: "人工智能",
        description: "课程思政教学研究与成果",
        icon: "🔍",
        count: "156",
      },
    ]);

    const announcements = ref([
      {
        title: "关于开展2024年课程思政示范课程建设的通知",
        date: "2024-03-20",
        important: true,
      },
      {
        title: "2024年春季课程思政教学研讨会通知",
        date: "2024-03-18",
        important: false,
      },
      {
        title: "优秀课程思政案例征集通知",
        date: "2024-03-15",
        important: true,
      },
    ]);

    const handleSearch = () => {
      if (searchQuery.value.trim()) {
        router.push({
          path: "/resources",
          query: { keyword: searchQuery.value.trim() },
        });
      }
    };

    onMounted(() => {
      // 获取热门资源
      store
        .dispatch("resource/getHotResources", 5)
        .then((response) => {
          hotResources.value = response || [];
        })
        .catch((error) => {
          console.error("获取热门资源失败:", error);
          // 使用模拟数据
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
        });
    });

    return {
      searchQuery,
      features,
      announcements,
      hotResources,
      handleSearch,
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
</style>
