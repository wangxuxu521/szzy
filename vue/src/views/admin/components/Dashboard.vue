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
          <div class="stat-value">{{ summary.totalUsers || 0 }}</div>
          <div class="stat-change up">
            活跃用户: {{ summary.activeUsers || 0 }}
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">📚</div>
        <div class="stat-data">
          <h3>资源总数</h3>
          <div class="stat-value">{{ summary.totalResources || 0 }}</div>
          <div class="stat-change up">
            今日新增: {{ summary.todayResources || 0 }}
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">⬇️</div>
        <div class="stat-data">
          <h3>资源下载</h3>
          <div class="stat-value">{{ summary.totalDownloads || 0 }}</div>
          <div class="stat-change up">总下载次数</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">🔎</div>
        <div class="stat-data">
          <h3>资源审核</h3>
          <div class="stat-value">
            {{ resourceCountData.statusCount?.approved || 0 }}
          </div>
          <div class="stat-change">
            待审核: {{ resourceCountData.statusCount?.pending || 0 }}
          </div>
        </div>
      </div>
    </div>

    <div class="dashboard-row">
      <div class="dashboard-card resource-trend">
        <h3>资源上传趋势 (近30天)</h3>
        <div class="chart-container">
          <div v-if="loading" class="loading">加载中...</div>
          <div v-else ref="trendChartRef" class="echarts-container"></div>
        </div>
      </div>

      <div class="dashboard-card user-types">
        <h3>资源类型分布</h3>
        <div class="chart-container">
          <div v-if="loading" class="loading">加载中...</div>
          <div v-else ref="pieChartRef" class="echarts-container"></div>
        </div>
      </div>
    </div>

    <div class="dashboard-row">
      <div class="dashboard-card recent-actions">
        <h3>用户行为统计</h3>
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else class="action-stats">
          <div
            v-for="(count, actionType) in userActionsData.actionTypeCount"
            :key="actionType"
            class="action-stat-item"
          >
            <div class="action-type">{{ getActionTypeLabel(actionType) }}</div>
            <div class="action-count">{{ count }}</div>
            <div class="action-bar">
              <div
                class="action-bar-value"
                :style="{
                  width:
                    calculateActionBarWidth(
                      count,
                      userActionsData.totalActions
                    ) + '%',
                  backgroundColor: getActionTypeColor(actionType),
                }"
              ></div>
            </div>
          </div>
        </div>
      </div>

      <div class="dashboard-card resource-rankings">
        <h3>热门资源排行</h3>
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else class="ranking-list">
          <div
            class="ranking-item"
            v-for="(resource, index) in topResourcesData"
            :key="index"
          >
            <div class="ranking-number">{{ index + 1 }}</div>
            <div class="ranking-content">
              <div class="ranking-title">
                {{ resource.title || "资源 #" + resource.resourceId }}
              </div>
              <div class="ranking-info">
                <span>{{ resource.count }} 次操作</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, reactive, nextTick, onBeforeUnmount } from "vue";
import * as echarts from "echarts/core";
import { PieChart, BarChart, LineChart } from "echarts/charts";
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
} from "echarts/components";
import { LabelLayout } from "echarts/features";
import { CanvasRenderer } from "echarts/renderers";
import {
  getSystemSummary,
  getResourceCount,
  getResourceTrend,
  getUserActions,
  getResourceTypeCount,
} from "@/api/statistics";

// 注册必需的组件
echarts.use([
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  PieChart,
  BarChart,
  LineChart,
  LabelLayout,
  CanvasRenderer,
]);

export default {
  name: "AdminDashboard",
  setup() {
    const loading = ref(true);
    const summary = ref({});
    const resourceCountData = ref({});
    const resourceTrendData = ref({});
    const userActionsData = ref({});
    const resourceTypeData = ref({});
    const topResourcesData = ref([]);
    const colorCache = reactive({});
    const trendChartRef = ref(null);
    const pieChartRef = ref(null);
    const trendChart = ref(null);
    const pieChart = ref(null);

    // 获取所有统计数据
    const fetchAllData = async () => {
      loading.value = true;
      try {
        // 并行请求所有数据
        const [summaryRes, resourceCountRes, resourceTrendRes, userActionsRes] =
          await Promise.all([
            getSystemSummary(),
            getResourceCount(),
            getResourceTrend(30),
            getUserActions(),
          ]);

        summary.value = summaryRes.data;
        resourceCountData.value = resourceCountRes.data;
        resourceTrendData.value = resourceTrendRes.data;
        userActionsData.value = userActionsRes.data;

        // 处理热门资源数据
        if (userActionsData.value && userActionsData.value.topResources) {
          topResourcesData.value = userActionsData.value.topResources.map(
            (item) => ({
              resourceId: item[0],
              count: item[1],
              title: `资源 #${item[0]}`, // 这里可以通过额外请求获取资源标题
            })
          );
        }

        // 数据加载完成后初始化图表
        await nextTick();
        initTrendChart();
        initPieChart();
      } catch (error) {
        console.error("获取统计数据失败:", error);
      } finally {
        loading.value = false;
      }
    };

    // 格式化日期，只显示月/日
    const formatDate = (dateStr) => {
      const parts = dateStr.split("-");
      return `${parts[1]}/${parts[2]}`;
    };

    // 计算百分比
    const calculatePercentage = (value, total) => {
      if (!total) return 0;
      return Math.round((value / total) * 100);
    };

    // 计算柱状图高度
    const calculateBarHeight = (count) => {
      const max = Object.values(
        resourceTrendData.value?.uploadTrend || {}
      ).reduce((max, current) => Math.max(max, current), 1);
      return (count / max) * 100;
    };

    // 计算操作条宽度
    const calculateActionBarWidth = (count, total) => {
      if (!total) return 0;
      return (count / total) * 100;
    };

    // 获取随机颜色，但对相同类型保持一致
    const getRandomColor = (key) => {
      if (!colorCache[key]) {
        colorCache[key] = `hsl(${Math.floor(Math.random() * 360)}, 70%, 50%)`;
      }
      return colorCache[key];
    };

    // 获取操作类型标签
    const getActionTypeLabel = (type) => {
      const labels = {
        view: "查看",
        download: "下载",
        comment: "评论",
        rate: "评分",
        share: "分享",
      };
      return labels[type] || type;
    };

    // 获取操作类型颜色
    const getActionTypeColor = (type) => {
      const colors = {
        view: "#1890ff",
        download: "#52c41a",
        comment: "#faad14",
        rate: "#722ed1",
        share: "#eb2f96",
      };
      return colors[type] || getRandomColor(type);
    };

    // 初始化趋势图表
    const initTrendChart = () => {
      if (!trendChartRef.value) return;

      // 如果图表已经存在，先销毁
      if (trendChart.value) {
        trendChart.value.dispose();
      }

      trendChart.value = echarts.init(trendChartRef.value);

      const dates = Object.keys(
        resourceTrendData.value.uploadTrend || {}
      ).sort();
      const values = dates.map(
        (date) => resourceTrendData.value.uploadTrend[date]
      );

      const option = {
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow",
          },
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true,
        },
        xAxis: {
          type: "category",
          data: dates.map((date) => formatDate(date)),
          axisLabel: {
            rotate: 45,
            interval: "auto",
          },
        },
        yAxis: {
          type: "value",
          name: "资源数量",
        },
        series: [
          {
            name: "上传资源数",
            type: "line",
            data: values,
            itemStyle: {
              color: "#1890ff",
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: "rgba(24, 144, 255, 0.5)" },
                { offset: 1, color: "rgba(24, 144, 255, 0.1)" },
              ]),
            },
            smooth: true,
          },
        ],
      };

      trendChart.value.setOption(option);

      // 添加窗口大小改变事件监听
      window.addEventListener("resize", () => {
        trendChart.value && trendChart.value.resize();
      });
    };

    // 初始化饼图
    const initPieChart = () => {
      if (!pieChartRef.value) return;

      // 如果图表已经存在，先销毁
      if (pieChart.value) {
        pieChart.value.dispose();
      }

      pieChart.value = echarts.init(pieChartRef.value);

      const typeData = resourceCountData.value.typeCount;
      if (!typeData) return;

      const pieData = Object.entries(typeData).map(([name, value]) => ({
        name,
        value,
      }));

      const option = {
        tooltip: {
          trigger: "item",
          formatter: "{a} <br/>{b}: {c} ({d}%)",
        },
        legend: {
          orient: "vertical",
          right: 10,
          top: "center",
          data: pieData.map((item) => item.name),
        },
        series: [
          {
            name: "资源类型",
            type: "pie",
            radius: ["40%", "70%"],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: "#fff",
              borderWidth: 2,
            },
            label: {
              show: false,
              position: "center",
            },
            emphasis: {
              label: {
                show: true,
                fontSize: "18",
                fontWeight: "bold",
              },
            },
            labelLine: {
              show: false,
            },
            data: pieData,
          },
        ],
      };

      pieChart.value.setOption(option);

      // 添加窗口大小改变事件监听
      window.addEventListener("resize", () => {
        pieChart.value && pieChart.value.resize();
      });
    };

    // 监听窗口大小变化，重新调整图表大小
    const handleResize = () => {
      trendChart.value?.resize();
      pieChart.value?.resize();
    };

    // 组件挂载后执行
    onMounted(() => {
      fetchAllData();
    });

    // 组件卸载前清理资源
    onBeforeUnmount(() => {
      // 销毁图表实例，避免内存泄漏
      if (trendChart.value) {
        trendChart.value.dispose();
        trendChart.value = null;
      }

      if (pieChart.value) {
        pieChart.value.dispose();
        pieChart.value = null;
      }

      // 移除事件监听器
      window.removeEventListener("resize", () => {});
    });

    return {
      loading,
      summary,
      resourceCountData,
      resourceTrendData,
      userActionsData,
      resourceTypeData,
      topResourcesData,
      formatDate,
      calculatePercentage,
      calculateBarHeight,
      calculateActionBarWidth,
      getRandomColor,
      getActionTypeLabel,
      getActionTypeColor,
      trendChartRef,
      pieChartRef,
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
  font-size: 1.8rem;
  font-weight: bold;
  margin: 0.5rem 0;
}

.stat-change {
  font-size: 0.8rem;
}

.stat-change.up {
  color: #52c41a;
}

.stat-change.down {
  color: #f5222d;
}

.dashboard-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(45%, 1fr));
  grid-gap: 1.5rem;
  margin-bottom: 1.5rem;
}

.dashboard-card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
}

.dashboard-card h3 {
  margin-top: 0;
  margin-bottom: 1.5rem;
  font-size: 1.1rem;
  color: #333;
}

.chart-container {
  height: 250px;
  position: relative;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #999;
}

/* 趋势图表样式 */
.trend-chart {
  display: flex;
  height: 200px;
  align-items: flex-end;
  gap: 2px;
}

.trend-bar {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
}

.trend-bar-value {
  width: 80%;
  background-color: #1890ff;
  border-radius: 2px 2px 0 0;
  transition: height 0.3s ease;
}

.trend-bar-date {
  font-size: 10px;
  margin-top: 5px;
  transform: rotate(-45deg);
  white-space: nowrap;
}

/* 饼图图例样式 */
.pie-chart-legend {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.legend-item {
  display: flex;
  align-items: center;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  margin-right: 8px;
}

.legend-label {
  font-size: 0.9rem;
}

/* 用户行为统计样式 */
.action-stats {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.action-stat-item {
  display: flex;
  flex-direction: column;
}

.action-type {
  font-weight: bold;
  margin-bottom: 5px;
}

.action-count {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 5px;
}

.action-bar {
  height: 8px;
  background-color: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.action-bar-value {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s ease;
}

/* 排行榜样式 */
.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.ranking-item {
  display: flex;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.ranking-number {
  width: 24px;
  height: 24px;
  background-color: #f0f0f0;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
  margin-right: 12px;
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
  margin-bottom: 5px;
}

.ranking-info {
  font-size: 0.8rem;
  color: #999;
}

.ranking-info span {
  margin-right: 10px;
}

.echarts-container {
  width: 100%;
  height: 100%;
}
</style>
