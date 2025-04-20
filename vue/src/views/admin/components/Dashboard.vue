<template>
  <div class="admin-dashboard">
    <div class="dashboard-header">
      <h2>仪表盘</h2>
      <p>欢迎回来，管理员</p>
    </div>

    <div class="stats-overview">
      <div class="stat-card">
        <div class="stat-icon">👥</div>
        <div class="stat-data">
          <h3>用户总数</h3>
          <div class="stat-value">{{ stats.userCount }}</div>
          <div class="stat-change up">+{{ stats.userGrowth }}%</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">📚</div>
        <div class="stat-data">
          <h3>资源总数</h3>
          <div class="stat-value">{{ stats.resourceCount }}</div>
          <div class="stat-change up">+{{ stats.resourceGrowth }}%</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">👁️</div>
        <div class="stat-data">
          <h3>访问量</h3>
          <div class="stat-value">{{ stats.visitCount }}</div>
          <div class="stat-change up">+{{ stats.visitGrowth }}%</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">⬇️</div>
        <div class="stat-data">
          <h3>资源下载</h3>
          <div class="stat-value">{{ stats.downloadCount }}</div>
          <div class="stat-change down">-{{ stats.downloadDecrease }}%</div>
        </div>
      </div>
    </div>

    <div class="dashboard-row">
      <div class="dashboard-card recent-actions">
        <h3>最近操作</h3>
        <div class="action-list">
          <div
            class="action-item"
            v-for="(action, index) in recentActions"
            :key="index"
          >
            <div class="action-time">{{ action.time }}</div>
            <div class="action-content">
              <div class="action-user">{{ action.user }}</div>
              <div class="action-description">{{ action.description }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="dashboard-card user-types">
        <h3>用户类型分布</h3>
        <div class="chart-container">
          <div class="chart-placeholder">用户类型统计图表</div>
          <div class="chart-legend">
            <div class="legend-item">
              <div class="legend-color admin"></div>
              <div class="legend-label">管理员: {{ userTypes.admin }}%</div>
            </div>
            <div class="legend-item">
              <div class="legend-color teacher"></div>
              <div class="legend-label">教师: {{ userTypes.teacher }}%</div>
            </div>
            <div class="legend-item">
              <div class="legend-color student"></div>
              <div class="legend-label">学生: {{ userTypes.student }}%</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="dashboard-card resource-rankings">
      <h3>热门资源排行</h3>
      <div class="ranking-list">
        <div
          class="ranking-item"
          v-for="(resource, index) in topResources"
          :key="index"
        >
          <div class="ranking-number">{{ index + 1 }}</div>
          <div class="ranking-content">
            <div class="ranking-title">{{ resource.title }}</div>
            <div class="ranking-info">
              <span>{{ resource.author }}</span>
              <span>{{ resource.views }} 次浏览</span>
              <span>{{ resource.downloads }} 次下载</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from "vue";

export default {
  name: "AdminDashboard",
  setup() {
    const stats = ref({
      userCount: 1238,
      userGrowth: 5.2,
      resourceCount: 3567,
      resourceGrowth: 12.7,
      visitCount: 45892,
      visitGrowth: 8.3,
      downloadCount: 12543,
      downloadDecrease: 2.1,
    });

    const userTypes = ref({
      admin: 5,
      teacher: 35,
      student: 60,
    });

    const recentActions = ref([
      {
        time: "10:30",
        user: "张教授",
        description: '上传了资源"数据库原理与社会责任"',
      },
      {
        time: "09:15",
        user: "李教授",
        description: '更新了资源"计算机网络中的爱国情怀"',
      },
      {
        time: "昨天",
        user: "管理员",
        description: "审核通过了5个新资源",
      },
      {
        time: "昨天",
        user: "王教授",
        description: '上传了案例"程序设计与逻辑思维培养"',
      },
      {
        time: "前天",
        user: "系统",
        description: "系统自动备份完成",
      },
    ]);

    const topResources = ref([
      {
        title: "计算机网络中的爱国情怀",
        author: "张教授",
        views: 1234,
        downloads: 567,
      },
      {
        title: "数据结构与民族精神",
        author: "李教授",
        views: 1056,
        downloads: 489,
      },
      {
        title: "人工智能伦理与价值观",
        author: "王教授",
        views: 987,
        downloads: 421,
      },
      {
        title: "软件工程与团队协作",
        author: "赵教授",
        views: 876,
        downloads: 356,
      },
      {
        title: "数据科学与社会责任",
        author: "周教授",
        views: 765,
        downloads: 301,
      },
    ]);

    return {
      stats,
      userTypes,
      recentActions,
      topResources,
    };
  },
};
</script>

<style scoped>
.admin-dashboard {
  padding: 1rem 0;
}

.dashboard-header {
  margin-bottom: 2rem;
}

.dashboard-header h2 {
  font-size: 1.5rem;
  margin: 0;
  color: #333;
}

.dashboard-header p {
  color: #666;
  margin-top: 0.5rem;
}

.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  grid-gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
  display: flex;
}

.stat-icon {
  font-size: 2.5rem;
  margin-right: 1rem;
}

.stat-data {
  flex: 1;
}

.stat-data h3 {
  margin: 0;
  font-size: 0.9rem;
  color: #666;
}

.stat-value {
  font-size: 2rem;
  font-weight: bold;
  margin: 0.5rem 0;
}

.stat-change {
  font-size: 0.9rem;
  font-weight: bold;
}

.stat-change.up {
  color: #52c41a;
}

.stat-change.down {
  color: #f5222d;
}

.dashboard-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-gap: 1.5rem;
  margin-bottom: 2rem;
}

.dashboard-card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
}

.dashboard-card h3 {
  margin-top: 0;
  margin-bottom: 1rem;
  font-size: 1.2rem;
  color: #333;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 0.8rem;
}

.action-list {
  max-height: 300px;
  overflow-y: auto;
}

.action-item {
  display: flex;
  padding: 0.8rem 0;
  border-bottom: 1px solid #f0f0f0;
}

.action-item:last-child {
  border-bottom: none;
}

.action-time {
  width: 60px;
  color: #999;
  flex-shrink: 0;
}

.action-content {
  flex: 1;
}

.action-user {
  font-weight: bold;
  margin-bottom: 0.3rem;
}

.action-description {
  color: #666;
}

.chart-container {
  height: 300px;
  display: flex;
  flex-direction: column;
}

.chart-placeholder {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f9f9f9;
  border-radius: 4px;
  color: #999;
}

.chart-legend {
  margin-top: 1rem;
}

.legend-item {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
}

.legend-color {
  width: 20px;
  height: 20px;
  border-radius: 4px;
  margin-right: 0.5rem;
}

.legend-color.admin {
  background-color: #1890ff;
}

.legend-color.teacher {
  background-color: #52c41a;
}

.legend-color.student {
  background-color: #faad14;
}

.ranking-list {
  max-height: 400px;
  overflow-y: auto;
}

.ranking-item {
  display: flex;
  padding: 1rem 0;
  border-bottom: 1px solid #f0f0f0;
}

.ranking-item:last-child {
  border-bottom: none;
}

.ranking-number {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-right: 1rem;
}

.ranking-item:nth-child(1) .ranking-number {
  background-color: #f5222d;
  color: white;
}

.ranking-item:nth-child(2) .ranking-number {
  background-color: #fa8c16;
  color: white;
}

.ranking-item:nth-child(3) .ranking-number {
  background-color: #faad14;
  color: white;
}

.ranking-content {
  flex: 1;
}

.ranking-title {
  font-weight: bold;
  margin-bottom: 0.5rem;
}

.ranking-info {
  color: #666;
  font-size: 0.9rem;
}

.ranking-info span {
  margin-right: 1rem;
}

@media (max-width: 768px) {
  .dashboard-row {
    grid-template-columns: 1fr;
  }
}
</style>
