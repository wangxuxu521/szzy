<template>
  <div class="comment-list">
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated />
    </div>

    <div v-else-if="comments.length === 0" class="empty-comments">
      <el-empty description="暂无评论" />
    </div>

    <div v-else class="comments-container">
      <div
        v-for="comment in comments"
        :key="comment.commentId"
        class="comment-item"
      >
        <div class="comment-avatar">
          <el-avatar
            :size="40"
            :src="comment.userAvatar"
            :alt="comment.username"
          >
            {{ getAvatarText(comment.username) }}
          </el-avatar>
        </div>

        <div class="comment-content">
          <div class="comment-header">
            <span class="comment-author">{{ comment.username }}</span>
            <span class="comment-time">{{
              formatTime(comment.createTime)
            }}</span>
          </div>

          <div class="comment-text">{{ comment.content }}</div>

          <div class="comment-actions">
            <el-button
              v-if="canReply"
              type="text"
              size="small"
              @click="handleReply(comment)"
            >
              回复
            </el-button>

            <el-button
              v-if="canDelete(comment)"
              type="text"
              size="small"
              @click="handleDelete(comment)"
            >
              删除
            </el-button>
          </div>

          <!-- 子评论/回复区域 -->
          <div
            v-if="comment.replies && comment.replies.length > 0"
            class="comment-replies"
          >
            <div
              v-for="reply in comment.replies"
              :key="reply.commentId"
              class="reply-item"
            >
              <div class="reply-avatar">
                <el-avatar :size="30" :src="reply.userAvatar">
                  {{ getAvatarText(reply.username) }}
                </el-avatar>
              </div>

              <div class="reply-content">
                <div class="reply-header">
                  <span class="reply-author">{{ reply.username }}</span>
                  <span class="reply-time">{{
                    formatTime(reply.createTime)
                  }}</span>
                </div>

                <div class="reply-text">{{ reply.content }}</div>

                <div class="reply-actions">
                  <el-button
                    v-if="canReply"
                    type="text"
                    size="small"
                    @click="handleReply(reply, comment)"
                  >
                    回复
                  </el-button>

                  <el-button
                    v-if="canDelete(reply)"
                    type="text"
                    size="small"
                    @click="handleDelete(reply)"
                  >
                    删除
                  </el-button>
                </div>
              </div>
            </div>
          </div>

          <!-- 回复表单 -->
          <div
            v-if="replyTo.commentId === comment.commentId"
            class="reply-form"
          >
            <el-input
              v-model="replyContent"
              type="textarea"
              :rows="2"
              placeholder="回复评论..."
              resize="none"
            />
            <div class="reply-form-actions">
              <el-button size="small" @click="cancelReply">取消</el-button>
              <el-button
                type="primary"
                size="small"
                :disabled="!replyContent.trim()"
                @click="submitReply"
              >
                提交回复
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from "vue";
import { useStore } from "vuex";
import { ElMessage, ElMessageBox } from "element-plus";
import { addComment, deleteComment } from "@/api/comment";

export default {
  name: "CommentList",
  props: {
    comments: {
      type: Array,
      default: () => [],
    },
    loading: {
      type: Boolean,
      default: false,
    },
    resourceId: {
      type: Number,
      required: true,
    },
  },
  emits: ["comment-deleted", "reply-added"],
  setup(props, { emit }) {
    const store = useStore();

    // 用户信息
    const isLoggedIn = computed(() => store.getters["user/isLoggedIn"]);
    const currentUserId = computed(() => store.getters["user/userId"]);
    const isAdmin = computed(() => store.getters["user/isAdmin"]);

    // 回复相关
    const replyTo = ref({});
    const replyContent = ref("");
    const replyParent = ref(null);

    // 是否可以回复
    const canReply = computed(() => isLoggedIn.value);

    // 是否可以删除评论
    const canDelete = (comment) => {
      // 管理员可以删除任何评论
      if (isAdmin.value) return true;

      // 用户可以删除自己的评论
      return isLoggedIn.value && comment.userId === currentUserId.value;
    };

    // 处理回复
    const handleReply = (comment, parent = null) => {
      if (!isLoggedIn.value) {
        ElMessage.warning("请先登录后再回复评论");
        return;
      }

      replyTo.value = comment;
      replyContent.value = "";
      replyParent.value = parent;
    };

    // 取消回复
    const cancelReply = () => {
      replyTo.value = {};
      replyContent.value = "";
      replyParent.value = null;
    };

    // 提交回复
    const submitReply = async () => {
      if (!replyContent.value.trim()) {
        ElMessage.warning("回复内容不能为空");
        return;
      }

      try {
        const commentData = {
          resourceId: props.resourceId,
          content: replyContent.value.trim(),
          parentId: replyParent.value
            ? replyParent.value.commentId
            : replyTo.value.commentId,
        };

        const response = await addComment(commentData);

        if (response && (response.code === 200 || response.commentId)) {
          ElMessage.success("回复发表成功");
          // 获取实际的评论对象
          const newReply = response.data || response;

          emit("reply-added", newReply);
          cancelReply();
        } else {
          ElMessage.error(response?.message || "回复发表失败");
        }
      } catch (error) {
        console.error("提交回复失败:", error);
        if (error.response && error.response.data) {
          ElMessage.error(
            error.response.data.message || "回复发表失败，请重试"
          );
        } else {
          ElMessage.error("回复发表失败，请重试");
        }
      }
    };

    // 处理删除评论
    const handleDelete = (comment) => {
      ElMessageBox.confirm("确定要删除此评论吗？删除后无法恢复。", "删除确认", {
        confirmButtonText: "确认删除",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(async () => {
          try {
            await deleteComment(comment.commentId);
            ElMessage.success("评论已删除");
            emit("comment-deleted", comment.commentId);
          } catch (error) {
            console.error("删除评论失败:", error);
            ElMessage.error("删除评论失败，请重试");
          }
        })
        .catch(() => {
          // 用户取消删除
        });
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

    // 获取头像文本（用户名首字母）
    const getAvatarText = (username) => {
      if (!username) return "";
      return username.charAt(0).toUpperCase();
    };

    return {
      isLoggedIn,
      currentUserId,
      canReply,
      canDelete,
      replyTo,
      replyContent,
      replyParent,
      handleReply,
      cancelReply,
      submitReply,
      handleDelete,
      formatTime,
      getAvatarText,
    };
  },
};
</script>

<style scoped>
.comment-list {
  margin: 20px 0;
}

.loading-container {
  padding: 20px 0;
}

.empty-comments {
  padding: 20px 0;
  text-align: center;
}

.comments-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.comment-author {
  font-weight: bold;
  color: #333;
}

.comment-time {
  color: #999;
  font-size: 12px;
}

.comment-text {
  margin-bottom: 8px;
  line-height: 1.5;
  white-space: pre-line;
  color: #333;
}

.comment-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.comment-replies {
  background-color: #f9f9f9;
  border-radius: 4px;
  padding: 10px;
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.reply-item {
  display: flex;
  gap: 10px;
}

.reply-content {
  flex: 1;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.reply-author {
  font-weight: bold;
  font-size: 14px;
  color: #333;
}

.reply-time {
  color: #999;
  font-size: 12px;
}

.reply-text {
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 5px;
  white-space: pre-line;
  color: #333;
}

.reply-actions {
  display: flex;
  gap: 10px;
}

.reply-form {
  margin-top: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
  padding: 10px;
}

.reply-form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
  gap: 10px;
}
</style>
