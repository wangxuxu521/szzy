<template>
  <div class="comment-form">
    <h4 class="form-title">发表评论</h4>
    <el-input
      v-model="commentContent"
      type="textarea"
      :rows="4"
      placeholder="分享你的看法..."
      resize="none"
      maxlength="500"
      show-word-limit
    />
    <div class="form-actions">
      <span class="comment-tips">评论内容将在提交后由管理员审核</span>
      <el-button
        type="primary"
        :loading="submitting"
        :disabled="!commentContent.trim() || submitting"
        @click="submitComment"
      >
        发表评论
      </el-button>
    </div>
  </div>
</template>

<script>
import { ref } from "vue";
import { ElMessage } from "element-plus";
import { addComment } from "@/api/comment";

export default {
  name: "CommentForm",
  props: {
    resourceId: {
      type: Number,
      required: true,
    },
  },
  emits: ["comment-added"],
  setup(props, { emit }) {
    const commentContent = ref("");
    const submitting = ref(false);

    // 提交评论
    const submitComment = async () => {
      if (!commentContent.value.trim()) {
        ElMessage.warning("评论内容不能为空");
        return;
      }

      submitting.value = true;

      try {
        const commentData = {
          resourceId: props.resourceId,
          content: commentContent.value.trim(),
          parentId: null, // 不是回复，parentId为null
        };

        const response = await addComment(commentData);

        if (response && (response.code === 200 || response.commentId)) {
          ElMessage.success("评论发表成功");
          commentContent.value = "";

          // 获取实际的评论对象
          const newComment = response.data || response;

          // 将新评论传递给父组件
          emit("comment-added", newComment);
        } else {
          ElMessage.error(response?.message || "评论发表失败");
        }
      } catch (error) {
        console.error("提交评论失败:", error);
        if (error.response && error.response.data) {
          ElMessage.error(
            error.response.data.message || "评论发表失败，请重试"
          );
        } else {
          ElMessage.error("评论发表失败，请重试");
        }
      } finally {
        submitting.value = false;
      }
    };

    return {
      commentContent,
      submitting,
      submitComment,
    };
  },
};
</script>

<style scoped>
.comment-form {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 30px;
}

.form-title {
  font-size: 16px;
  margin-top: 0;
  margin-bottom: 15px;
  color: #333;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
}

.comment-tips {
  color: #999;
  font-size: 12px;
}
</style>
