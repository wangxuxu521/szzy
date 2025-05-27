<template>
  <div class="resource-rating">
    <div class="rating-container">
      <div class="current-rating">
        <span class="rating-value">{{ averageRating }}</span>
        <el-rate
          v-model="displayRating"
          disabled
          show-score
          text-color="#ff9900"
          score-template="{value}"
        ></el-rate>
        <span class="rating-count">({{ ratingCount }}人评分)</span>
      </div>
      <div v-if="isLoggedIn" class="user-rating">
        <span class="rating-label">我的评分：</span>
        <el-rate
          v-model="userRating"
          @change="handleRatingChange"
          :disabled="submitting"
        ></el-rate>
      </div>
      <div v-else class="login-to-rate">
        <router-link to="/login">登录</router-link> 后才能评分
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from "vue";
import { useStore } from "vuex";
import { ElMessage } from "element-plus";
import { rateResource, getUserResourceRating } from "@/api/resource";

export default {
  name: "ResourceRating",
  props: {
    resourceId: {
      type: Number,
      required: true,
    },
    initialRating: {
      type: Number,
      default: 0,
    },
    initialRatingCount: {
      type: Number,
      default: 0,
    },
  },
  emits: ["rating-changed"],
  setup(props, { emit }) {
    const store = useStore();
    const isLoggedIn = computed(() => store.getters["user/isLoggedIn"]);

    // 评分数据
    const userRating = ref(0);
    const averageRating = ref(props.initialRating || 0);
    const ratingCount = ref(props.initialRatingCount || 0);
    const submitting = ref(false);

    // 显示评分（四舍五入到1位小数）
    const displayRating = computed(() => {
      return Number(averageRating.value.toFixed(1));
    });

    // 处理评分变化
    const handleRatingChange = async (value) => {
      if (!isLoggedIn.value) {
        ElMessage.warning("请先登录后再评分");
        return;
      }

      submitting.value = true;
      try {
        // 调用评分API
        const response = await rateResource(props.resourceId, value);

        if (response && response.code === 200) {
          ElMessage.success("评分成功");

          // 更新资源评分显示
          if (response.data) {
            if (response.data.rating !== undefined) {
              averageRating.value = response.data.rating;
            }
            if (response.data.ratingCount !== undefined) {
              ratingCount.value = response.data.ratingCount;
            }
          }

          // 通知父组件评分已更改
          emit("rating-changed", {
            rating: averageRating.value,
            ratingCount: ratingCount.value,
          });
        } else {
          ElMessage.error(response?.message || "评分失败");
        }
      } catch (error) {
        console.error("评分失败:", error);
        ElMessage.error("评分失败，请稍后重试");
      } finally {
        submitting.value = false;
      }
    };

    // 获取用户对资源的评分
    const loadUserRating = async () => {
      if (!isLoggedIn.value) return;

      try {
        const rating = await getUserResourceRating(props.resourceId);
        userRating.value = rating || 0;
      } catch (error) {
        console.error("获取用户评分失败:", error);
      }
    };

    onMounted(() => {
      // 初始化用户评分
      loadUserRating();
    });

    return {
      isLoggedIn,
      userRating,
      averageRating,
      displayRating,
      ratingCount,
      submitting,
      handleRatingChange,
    };
  },
};
</script>

<style scoped>
.resource-rating {
  margin-bottom: 20px;
}

.rating-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.current-rating {
  display: flex;
  align-items: center;
  gap: 10px;
}

.rating-value {
  font-size: 24px;
  font-weight: bold;
  color: #ff9900;
}

.rating-count {
  color: #999;
  font-size: 14px;
}

.user-rating {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background-color: #f8f8f8;
  border-radius: 4px;
}

.rating-label {
  color: #666;
}

.login-to-rate {
  text-align: center;
  padding: 10px;
  background-color: #f8f8f8;
  border-radius: 4px;
}

.login-to-rate a {
  color: #409eff;
  text-decoration: none;
  font-weight: bold;
}
</style>
