<template>
  <div class="statistics-analysis">
    <h2>统计分析</h2>

    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>资源上传趋势</span>
              <el-radio-group
                v-model="trendDays"
                @change="fetchResourceTrend"
                size="small"
              >
                <el-radio-button :label="7">近7天</el-radio-button>
                <el-radio-button :label="30">近30天</el-radio-button>
                <el-radio-button :label="90">近90天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container" ref="uploadTrendChartRef"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>资源类型分布</span>
            </div>
          </template>
          <div class="chart-container" ref="typeDistributionChartRef"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>资源格式分布</span>
            </div>
          </template>
          <div class="chart-container" ref="formatDistributionChartRef"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>下载统计</span>
            </div>
          </template>
          <div class="chart-container" ref="downloadChartRef"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>最活跃用户 TOP10</span>
            </div>
          </template>
          <el-table :data="topUsers" stripe style="width: 100%">
            <el-table-column prop="userId" label="用户ID" width="100" />
            <el-table-column prop="count" label="操作次数" width="100" />
            <el-table-column prop="percent" label="占比">
              <template #default="scope">
                <el-progress
                  :percentage="scope.row.percent"
                  :format="(format) => `${format}%`"
                ></el-progress>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>最热门资源 TOP10</span>
            </div>
          </template>
          <el-table :data="topResources" stripe style="width: 100%">
            <el-table-column prop="resourceId" label="资源ID" width="100" />
            <el-table-column prop="count" label="访问次数" width="100" />
            <el-table-column prop="percent" label="占比">
              <template #default="scope">
                <el-progress
                  :percentage="scope.row.percent"
                  :format="(format) => `${format}%`"
                ></el-progress>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, reactive, onMounted, onBeforeUnmount } from "vue";
import { ElMessage } from "element-plus";
import { systemApi } from "@/api";
import * as echarts from "echarts/core";
import { LineChart, BarChart, PieChart } from "echarts/charts";
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
} from "echarts/components";
import { CanvasRenderer } from "echarts/renderers";

// 注册必须的组件
echarts.use([
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  LineChart,
  BarChart,
  PieChart,
  CanvasRenderer,
]);

export default {
  name: "StatisticsAnalysis",
  setup() {
    const trendDays = ref(30);
    const topUsers = ref([]);
    const topResources = ref([]);

    // 图表引用
    const uploadTrendChartRef = ref(null);
    const typeDistributionChartRef = ref(null);
    const formatDistributionChartRef = ref(null);
    const downloadChartRef = ref(null);

    // 图表实例
    let uploadTrendChart = null;
    let typeDistributionChart = null;
    let formatDistributionChart = null;
    let downloadChart = null;

    // 资源统计数据
    const resourceCount = reactive({
      total: 0,
      statusCount: {},
      typeCount: {},
      formatCount: {},
    });

    // 资源趋势数据
    const resourceTrend = reactive({
      uploadTrend: {},
    });

    // 下载统计数据
    const downloadStats = reactive({
      totalDownloads: 0,
      dailyDownloads: {},
    });

    // 初始化图表
    const initCharts = () => {
      // 创建上传趋势图表
      uploadTrendChart = echarts.init(uploadTrendChartRef.value);

      // 创建资源类型分布图表
      typeDistributionChart = echarts.init(typeDistributionChartRef.value);

      // 创建资源格式分布图表
      formatDistributionChart = echarts.init(formatDistributionChartRef.value);

      // 创建下载统计图表
      downloadChart = echarts.init(downloadChartRef.value);

      // 窗口大小改变时，重新调整图表大小
      window.addEventListener("resize", () => {
        uploadTrendChart.resize();
        typeDistributionChart.resize();
        formatDistributionChart.resize();
        downloadChart.resize();
      });
    };

    // 渲染上传趋势图表
    const renderUploadTrendChart = () => {
      if (!uploadTrendChart) return;

      const dates = Object.keys(resourceTrend.uploadTrend).reverse();
      const counts = dates.map((date) => resourceTrend.uploadTrend[date]);

      const option = {
        title: {
          text: "资源上传趋势",
        },
        tooltip: {
          trigger: "axis",
        },
        xAxis: {
          type: "category",
          data: dates,
        },
        yAxis: {
          type: "value",
        },
        series: [
          {
            name: "上传数量",
            type: "line",
            data: counts,
            smooth: true,
            lineStyle: {
              width: 3,
            },
            itemStyle: {
              color: "#409EFF",
            },
            areaStyle: {
              color: {
                type: "linear",
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  {
                    offset: 0,
                    color: "rgba(64, 158, 255, 0.5)",
                  },
                  {
                    offset: 1,
                    color: "rgba(64, 158, 255, 0.1)",
                  },
                ],
              },
            },
          },
        ],
      };

      uploadTrendChart.setOption(option);
    };

    // 渲染资源类型分布图表
    const renderTypeDistributionChart = () => {
      if (!typeDistributionChart) return;

      const types = Object.keys(resourceCount.typeCount);
      const data = types.map((type) => ({
        name: type,
        value: resourceCount.typeCount[type],
      }));

      const option = {
        title: {
          text: "资源类型分布",
        },
        tooltip: {
          trigger: "item",
          formatter: "{a} <br/>{b}: {c} ({d}%)",
        },
        legend: {
          orient: "vertical",
          right: 10,
          top: "center",
          data: types,
        },
        series: [
          {
            name: "资源类型",
            type: "pie",
            radius: ["50%", "70%"],
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
                fontSize: 16,
                fontWeight: "bold",
              },
            },
            labelLine: {
              show: false,
            },
            data: data,
          },
        ],
      };

      typeDistributionChart.setOption(option);
    };

    // 渲染资源格式分布图表
    const renderFormatDistributionChart = () => {
      if (!formatDistributionChart) return;

      const formats = Object.keys(resourceCount.formatCount);
      const data = formats.map((format) => ({
        name: format,
        value: resourceCount.formatCount[format],
      }));

      const option = {
        title: {
          text: "资源格式分布",
        },
        tooltip: {
          trigger: "item",
          formatter: "{a} <br/>{b}: {c} ({d}%)",
        },
        legend: {
          orient: "vertical",
          right: 10,
          top: "center",
          data: formats,
        },
        series: [
          {
            name: "资源格式",
            type: "pie",
            radius: ["50%", "70%"],
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
                fontSize: 16,
                fontWeight: "bold",
              },
            },
            labelLine: {
              show: false,
            },
            data: data,
          },
        ],
      };

      formatDistributionChart.setOption(option);
    };

    // 渲染下载统计图表
    const renderDownloadChart = () => {
      if (!downloadChart) return;

      const dates = Object.keys(downloadStats.dailyDownloads).reverse();
      const counts = dates.map((date) => downloadStats.dailyDownloads[date]);

      const option = {
        title: {
          text: "资源下载统计",
        },
        tooltip: {
          trigger: "axis",
        },
        xAxis: {
          type: "category",
          data: dates,
        },
        yAxis: {
          type: "value",
        },
        series: [
          {
            name: "下载次数",
            type: "bar",
            data: counts,
            itemStyle: {
              color: "#67C23A",
            },
          },
        ],
      };

      downloadChart.setOption(option);
    };

    // 获取资源统计数据
    const fetchResourceCount = async () => {
      try {
        const res = await systemApi.getResourceCount();
        if (res.code === 200 && res.data) {
          resourceCount.total = res.data.total || 0;
          resourceCount.statusCount = res.data.statusCount || {};
          resourceCount.typeCount = res.data.typeCount || {};
          resourceCount.formatCount = res.data.formatCount || {};

          renderTypeDistributionChart();
          renderFormatDistributionChart();
        }
      } catch (error) {
        console.error("获取资源统计失败", error);
        ElMessage.error("获取资源统计失败");
      }
    };

    // 获取资源趋势数据
    const fetchResourceTrend = async () => {
      try {
        const res = await systemApi.getResourceTrend(trendDays.value);
        if (res.code === 200 && res.data) {
          resourceTrend.uploadTrend = res.data.uploadTrend || {};
          renderUploadTrendChart();
        }
      } catch (error) {
        console.error("获取资源趋势失败", error);
        ElMessage.error("获取资源趋势失败");
      }
    };

    // 获取用户操作统计
    const fetchUserActions = async () => {
      try {
        const res = await systemApi.getUserActions();
        if (res.code === 200 && res.data) {
          // 处理最活跃用户数据
          if (res.data.topUsers && res.data.topUsers.length > 0) {
            const total = res.data.totalActions || 1;
            topUsers.value = res.data.topUsers.map((user) => ({
              userId: user.key,
              count: user.value,
              percent: Math.round((user.value / total) * 100),
            }));
          }

          // 处理最热门资源数据
          if (res.data.topResources && res.data.topResources.length > 0) {
            const total = res.data.totalActions || 1;
            topResources.value = res.data.topResources.map((resource) => ({
              resourceId: resource.key,
              count: resource.value,
              percent: Math.round((resource.value / total) * 100),
            }));
          }
        }
      } catch (error) {
        console.error("获取用户操作统计失败", error);
        ElMessage.error("获取用户操作统计失败");
      }
    };

    // 获取下载统计数据
    const fetchDownloadStatistics = async () => {
      try {
        const res = await systemApi.getDownloadStatistics();
        if (res.code === 200 && res.data) {
          downloadStats.totalDownloads = res.data.totalDownloads || 0;
          downloadStats.dailyDownloads = res.data.dailyDownloads || {};
          renderDownloadChart();
        }
      } catch (error) {
        console.error("获取下载统计失败", error);
        ElMessage.error("获取下载统计失败");
      }
    };

    // 加载所有统计数据
    const loadAllStatistics = () => {
      fetchResourceCount();
      fetchResourceTrend();
      fetchUserActions();
      fetchDownloadStatistics();
    };

    onMounted(() => {
      initCharts();
      loadAllStatistics();
    });

    onBeforeUnmount(() => {
      // 销毁图表实例，释放资源
      if (uploadTrendChart) {
        uploadTrendChart.dispose();
        uploadTrendChart = null;
      }
      if (typeDistributionChart) {
        typeDistributionChart.dispose();
        typeDistributionChart = null;
      }
      if (formatDistributionChart) {
        formatDistributionChart.dispose();
        formatDistributionChart = null;
      }
      if (downloadChart) {
        downloadChart.dispose();
        downloadChart = null;
      }

      // 移除窗口大小变化事件监听
      window.removeEventListener("resize", () => {});
    });

    return {
      trendDays,
      topUsers,
      topResources,
      uploadTrendChartRef,
      typeDistributionChartRef,
      formatDistributionChartRef,
      downloadChartRef,
      fetchResourceTrend,
    };
  },
};
</script>

<style scoped>
.statistics-analysis {
  padding: 1rem;
}

.chart-card {
  margin-bottom: 1.5rem;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 300px;
  width: 100%;
}
</style>
