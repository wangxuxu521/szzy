<template>
  <base-layout>
    <div class="student-center">
      <h1 class="page-title">学生空间</h1>

      <div class="student-panel">
        <div class="student-info">
          <div class="avatar">👨‍🎓</div>
          <div class="info-details">
            <h2>{{ studentInfo.name }}</h2>
            <p>{{ studentInfo.department }}</p>
            <p>{{ studentInfo.email }}</p>
          </div>
        </div>

        <div class="stats-container">
          <div class="stat-card">
            <h3>已学课程</h3>
            <div class="stat-value">{{ stats.courseCount }}</div>
            <router-link to="/resources" class="stat-link"
              >查看课程</router-link
            >
          </div>

          <div class="stat-card">
            <h3>收藏资源</h3>
            <div class="stat-value">{{ stats.favoriteCount }}</div>
            <router-link to="/resources" class="stat-link"
              >查看收藏</router-link
            >
          </div>

          <div class="stat-card">
            <h3>学习时长</h3>
            <div class="stat-value">{{ stats.studyHours }}h</div>
            <router-link to="/resources" class="stat-link"
              >学习记录</router-link
            >
          </div>
        </div>
      </div>

      <div class="section">
        <h2 class="section-title">收藏资源</h2>
        <div v-if="favoriteResources.length === 0" class="empty-state">
          您还没有收藏资源，浏览资源库并收藏感兴趣的内容。
        </div>
        <div v-else class="resources-grid">
          <resource-card
            v-for="resource in favoriteResources"
            :key="resource.id"
            :resource="resource"
          />
        </div>
        <div class="action-btn-container">
          <button class="action-btn" @click="goToResources">浏览资源库</button>
        </div>
      </div>

      <div class="section">
        <h2 class="section-title">最近学习</h2>
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
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useStore } from "vuex";
import BaseLayout from "@/layout/BaseLayout.vue";
import ResourceCard from "@/components/common/ResourceCard.vue";

export default {
  name: "StudentCenter",
  components: {
    BaseLayout,
    ResourceCard,
  },
  setup() {
    const store = useStore();
    const router = useRouter();

    // 模拟的学生信息
    const studentInfo = ref({
      name: "李同学",
      department: "计算机科学与技术学院",
      email: "student@example.com",
    });

    // 模拟的统计数据
    const stats = ref({
      courseCount: 8,
      favoriteCount: 12,
      studyHours: 36,
    });

    // 模拟的收藏资源
    const favoriteResources = ref([
      {
        id: 1,
        title: "计算机网络中的爱国情怀",
        type: "教学案例",
        views: 1234,
        author: "张教授",
        tags: ["计算机网络", "爱国主义"],
      },
      {
        id: 2,
        title: "数据结构与民族精神",
        type: "教学资源",
        views: 890,
        author: "李教授",
        tags: ["数据结构", "民族精神"],
      },
    ]);

    // 模拟的最近活动
    const recentActivities = ref([
      {
        time: "2024-03-28",
        type: "学习",
        description: '学习了"数据库原理与社会责任"课程',
      },
      {
        time: "2024-03-26",
        type: "收藏",
        description: '收藏了"人工智能伦理与价值观"资源',
      },
      {
        time: "2024-03-25",
        type: "下载",
        description: '下载了教学案例"计算机网络安全与国家安全"',
      },
    ]);

    const goToResources = () => {
      router.push("/resources");
    };

    return {
      studentInfo,
      stats,
      favoriteResources,
      recentActivities,
      goToResources,
    };
  },
};
</script>

<style scoped>
.student-center {
  padding: 1rem 0;
}

.page-title {
  font-size: 2rem;
  margin-bottom: 2rem;
  color: #333;
}

.student-panel {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  margin-bottom: 2rem;
}

.student-info {
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
</style>
