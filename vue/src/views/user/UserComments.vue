<template>
  <div class="user-comments">
    <h2 class="page-title">我的评论</h2>

    <div class="comments-container" v-loading="loading">
      <div v-if="!loading && comments.length === 0" class="empty-comments">
        <el-empty description="暂无评论" :image-size="200">
          <template #description>
            <p>您还没有发表任何评论</p>
          </template>
          <el-button type="primary" @click="goToResourceLibrary"
            >浏览资源库</el-button
          >
        </el-empty>
      </div>

      <div v-else class="comments-list">
        <el-card
          v-for="comment in comments"
          :key="comment.commentId"
          class="comment-item"
          shadow="hover"
        >
          <div class="comment-header">
            <div
              class="resource-info"
              @click="viewResource(comment.resourceId)"
            >
              <i class="el-icon-document"></i>
              <span class="resource-title">{{ comment.resourceTitle }}</span>
            </div>
            <div class="comment-time">
              <i class="el-icon-time"></i>
              <span>{{ formatTime(comment.createTime) }}</span>
            </div>
          </div>

          <div class="comment-content">
            {{ comment.content }}
          </div>

          <div class="comment-footer">
            <div class="comment-status">
              <el-tag
                :type="comment.status === 1 ? 'success' : 'info'"
                size="small"
              >
                {{ comment.status === 1 ? "已审核" : "待审核" }}
              </el-tag>
            </div>

            <div class="comment-actions">
              <el-button
                type="primary"
                size="small"
                @click="viewResource(comment.resourceId)"
              >
                查看资源
              </el-button>

              <el-button
                type="danger"
                size="small"
                @click="deleteComment(comment)"
              >
                删除评论
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
import { getUserComments, deleteComment } from "@/api/comment";
import { useStore } from "vuex";

export default {
  name: "UserComments",
  setup() {
    const router = useRouter();
    const store = useStore();

    // 评论数据
    const comments = ref([]);
    const loading = ref(true);

    // 用户信息
    const userId = computed(() => store.getters["user/userId"]);

    // 分页
    const currentPage = ref(1);
    const pageSize = ref(10);
    const total = ref(0);
    const totalPages = computed(() => Math.ceil(total.value / pageSize.value));

    // 加载评论列表
    const loadComments = async () => {
      if (!userId.value) return;

      loading.value = true;

      try {
        const response = await getUserComments(userId.value);

        if (Array.isArray(response)) {
          comments.value = response;
          total.value = response.length;
        } else {
          comments.value = [];
          total.value = 0;
        }
      } catch (error) {
        console.error("获取评论列表失败:", error);
        ElMessage.error("获取评论列表失败，请重试");
        comments.value = [];
        total.value = 0;
      } finally {
        loading.value = false;
      }
    };

    // 删除评论
    const deleteComment = (comment) => {
      ElMessageBox.confirm("确定要删除这条评论吗？", "删除评论", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(async () => {
          try {
            await deleteComment(comment.commentId);
            ElMessage.success("评论已删除");

            // 从列表中移除
            comments.value = comments.value.filter(
              (item) => item.commentId !== comment.commentId
            );
            total.value -= 1;
          } catch (error) {
            console.error("删除评论失败:", error);
            ElMessage.error("删除评论失败，请重试");
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

    // 格式化时间
    const formatTime = (dateString) => {
      if (!dateString) return "";

      const date = new Date(dateString);
      const now = new Date();
      const diff = now - date; // 毫秒差

      // 如果是24小时内
      if (diff < 24 * 60 * 60 * 1000) {
        // 一小时内
        if (diff < 60 * 60 * 1000) {
          const minutes = Math.floor(diff / (60 * 1000));
          return minutes <= 0 ? "刚刚" : `${minutes}分钟前`;
        }
        // 一天内
        const hours = Math.floor(diff / (60 * 60 * 1000));
        return `${hours}小时前`;
      }

      // 如果是一周内
      if (diff < 7 * 24 * 60 * 60 * 1000) {
        const days = Math.floor(diff / (24 * 60 * 60 * 1000));
        return `${days}天前`;
      }

      // 如果是更早
      return date.toLocaleDateString("zh-CN", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
      });
    };

    onMounted(() => {
      if (userId.value) {
        loadComments();
      }
    });

    return {
      comments,
      loading,
      currentPage,
      pageSize,
      total,
      totalPages,
      deleteComment,
      viewResource,
      goToResourceLibrary,
      handlePageChange,
      formatTime,
    };
  },
};
</script>

<style scoped>
.user-comments {
  padding: 20px;
}

.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.comments-container {
  min-height: 400px;
}

.empty-comments {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.comment-item {
  transition: all 0.3s;
}

.comment-item:hover {
  transform: translateY(-3px);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
  color: #666;
}

.resource-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.resource-info:hover .resource-title {
  color: #409eff;
}

.resource-title {
  margin-left: 5px;
  transition: color 0.3s;
}

.comment-time {
  display: flex;
  align-items: center;
}

.comment-time i {
  margin-right: 5px;
}

.comment-content {
  margin: 15px 0;
  font-size: 16px;
  line-height: 1.6;
  color: #333;
  white-space: pre-line;
}

.comment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.comment-actions {
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
