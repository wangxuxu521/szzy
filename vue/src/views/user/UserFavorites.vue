<template>
  <div class="user-favorites">
    <h2 class="page-title">我的收藏</h2>

    <div class="favorites-container" v-loading="loading">
      <div v-if="!loading && favorites.length === 0" class="empty-favorites">
        <el-empty description="暂无收藏资源" :image-size="200">
          <template #description>
            <p>您还没有收藏任何资源</p>
          </template>
          <el-button type="primary" @click="goToResourceLibrary"
            >浏览资源库</el-button
          >
        </el-empty>
      </div>

      <div v-else class="favorites-list">
        <el-card
          v-for="favorite in favorites"
          :key="favorite.id"
          class="favorite-item"
          shadow="hover"
        >
          <div class="favorite-content">
            <div class="favorite-main">
              <h3
                class="favorite-title"
                @click="viewResource(favorite.resourceId)"
              >
                {{ favorite.resourceTitle }}
              </h3>

              <div class="favorite-meta">
                <el-tag
                  size="small"
                  :type="getTypeTagType(favorite.resourceType)"
                  class="resource-type"
                >
                  {{ favorite.resourceType }}
                </el-tag>

                <span class="favorite-format">
                  <i class="el-icon-document"></i> {{ favorite.resourceFormat }}
                </span>

                <span class="favorite-date">
                  <i class="el-icon-time"></i>
                  {{ formatDate(favorite.createTime) }}
                </span>
              </div>
            </div>

            <div class="favorite-actions">
              <el-button
                type="primary"
                size="small"
                @click="viewResource(favorite.resourceId)"
              >
                查看
              </el-button>

              <el-button
                type="danger"
                size="small"
                @click="cancelFavorite(favorite)"
              >
                取消收藏
              </el-button>
            </div>
          </div>
        </el-card>
      </div>

      <div class="pagination" v-if="totalPages > 1">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :current-page.sync="currentPage"
          :page-size="pageSize"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getCurrentUserFavorites,
  cancelFavorite as cancelFavoriteApi,
} from "@/api/favorite";

export default {
  name: "UserFavorites",
  setup() {
    const router = useRouter();

    // 收藏数据
    const favorites = ref([]);
    const loading = ref(true);

    // 分页
    const currentPage = ref(1);
    const pageSize = ref(10);
    const total = ref(0);
    const totalPages = computed(() => Math.ceil(total.value / pageSize.value));

    // 加载收藏列表
    const loadFavorites = async () => {
      loading.value = true;

      try {
        const response = await getCurrentUserFavorites();

        if (Array.isArray(response)) {
          favorites.value = response;
          total.value = response.length;
        } else {
          favorites.value = [];
          total.value = 0;
        }
      } catch (error) {
        console.error("获取收藏列表失败:", error);
        ElMessage.error("获取收藏列表失败，请重试");
        favorites.value = [];
        total.value = 0;
      } finally {
        loading.value = false;
      }
    };

    // 取消收藏
    const cancelFavorite = (favorite) => {
      ElMessageBox.confirm("确定要取消收藏该资源吗？", "取消收藏", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(async () => {
          try {
            await cancelFavoriteApi(favorite.resourceId);
            ElMessage.success("已取消收藏");

            // 从列表中移除
            favorites.value = favorites.value.filter(
              (item) => item.id !== favorite.id
            );
            total.value -= 1;
          } catch (error) {
            console.error("取消收藏失败:", error);
            ElMessage.error("取消收藏失败，请重试");
          }
        })
        .catch(() => {
          // 用户取消操作
        });
    };

    // 查看资源详情
    const viewResource = (resourceId) => {
      router.push(`/resources/${resourceId}`);
    };

    // 前往资源库
    const goToResourceLibrary = () => {
      router.push("/resources");
    };

    // 处理页面变化
    const handlePageChange = (page) => {
      currentPage.value = page;
    };

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return "-";

      const date = new Date(dateString);
      return date.toLocaleDateString("zh-CN", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
      });
    };

    // 获取资源类型标签样式
    const getTypeTagType = (type) => {
      const typeMap = {
        计算机: "primary",
        通信: "success",
        人工智能: "warning",
        嵌入式: "danger",
      };
      return typeMap[type] || "info";
    };

    onMounted(() => {
      loadFavorites();
    });

    return {
      favorites,
      loading,
      currentPage,
      pageSize,
      total,
      totalPages,
      cancelFavorite,
      viewResource,
      goToResourceLibrary,
      handlePageChange,
      formatDate,
      getTypeTagType,
    };
  },
};
</script>

<style scoped>
.user-favorites {
  padding: 20px;
}

.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.favorites-container {
  min-height: 400px;
}

.empty-favorites {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}

.favorites-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.favorite-item {
  transition: all 0.3s;
}

.favorite-item:hover {
  transform: translateY(-3px);
}

.favorite-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.favorite-main {
  flex: 1;
}

.favorite-title {
  font-size: 18px;
  margin: 0 0 10px;
  color: #333;
  cursor: pointer;
}

.favorite-title:hover {
  color: #409eff;
}

.favorite-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  color: #666;
  font-size: 14px;
}

.resource-type {
  margin-right: 5px;
}

.favorite-format,
.favorite-date {
  display: flex;
  align-items: center;
}

.favorite-format i,
.favorite-date i {
  margin-right: 5px;
}

.favorite-actions {
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
 