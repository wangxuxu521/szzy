<template>
  <div class="favorite-button">
    <el-button
      v-if="isLoggedIn"
      :type="isFavorite ? 'danger' : 'warning'"
      :icon="isFavorite ? 'el-icon-star-on' : 'el-icon-star-off'"
      :loading="loading"
      @click="toggleFavorite"
    >
      {{ isFavorite ? "已收藏" : "收藏" }}
      <span v-if="showCount" class="favorite-count">({{ favoriteCount }})</span>
    </el-button>

    <el-tooltip
      v-else
      content="请先登录后再收藏"
      placement="top"
      :hide-after="2000"
    >
      <el-button type="warning" icon="el-icon-star-off" @click="goToLogin">
        收藏
        <span v-if="showCount" class="favorite-count"
          >({{ favoriteCount }})</span
        >
      </el-button>
    </el-tooltip>
  </div>
</template>

<script>
import { ref, computed, onMounted } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import {
  checkFavorite,
  addFavorite,
  cancelFavorite,
  countResourceFavorites,
} from "@/api/favorite";

export default {
  name: "FavoriteButton",
  props: {
    resourceId: {
      type: Number,
      required: true,
    },
    isFavorite: {
      type: Boolean,
      default: false,
    },
    favoriteCount: {
      type: Number,
      default: 0,
    },
    showCount: {
      type: Boolean,
      default: true,
    },
  },
  emits: ["favorite-changed"],
  setup(props, { emit }) {
    const store = useStore();
    const router = useRouter();

    // 用户信息
    const isLoggedIn = computed(() => store.getters["user/isLoggedIn"]);

    // 收藏状态
    const isFavoriteState = ref(props.isFavorite);
    const favoriteCountState = ref(props.favoriteCount);
    const loading = ref(false);

    // 切换收藏状态
    const toggleFavorite = async () => {
      if (!isLoggedIn.value) {
        ElMessage.warning("请先登录后再收藏");
        return;
      }

      loading.value = true;

      try {
        // 根据当前状态选择API
        const result = isFavoriteState.value
          ? await cancelFavorite(props.resourceId)
          : await addFavorite(props.resourceId);

        // 更新本地状态
        isFavoriteState.value = !isFavoriteState.value;

        // 更新收藏数量
        favoriteCountState.value = isFavoriteState.value
          ? favoriteCountState.value + 1
          : Math.max(0, favoriteCountState.value - 1);

        // 通知父组件
        emit("favorite-changed", isFavoriteState.value);

        // 显示提示
        ElMessage.success(isFavoriteState.value ? "收藏成功" : "已取消收藏");
      } catch (error) {
        console.error("收藏操作失败:", error);
        ElMessage.error("操作失败，请重试");
      } finally {
        loading.value = false;
      }
    };

    // 跳转到登录页
    const goToLogin = () => {
      router.push({
        path: "/login",
        query: { redirect: router.currentRoute.value.fullPath },
      });
    };

    // 检查收藏状态
    const checkFavoriteStatus = async () => {
      if (!isLoggedIn.value) return;

      try {
        const status = await checkFavorite(props.resourceId);
        isFavoriteState.value = status;
      } catch (error) {
        console.error("检查收藏状态失败:", error);
      }
    };

    // 获取收藏数量
    const loadFavoriteCount = async () => {
      try {
        const count = await countResourceFavorites(props.resourceId);
        favoriteCountState.value = count;
      } catch (error) {
        console.error("获取收藏数量失败:", error);
      }
    };

    // 监听props变化
    const watchProps = () => {
      // 如果props中的值发生变化，更新本地状态
      if (props.isFavorite !== isFavoriteState.value) {
        isFavoriteState.value = props.isFavorite;
      }

      if (props.favoriteCount !== favoriteCountState.value) {
        favoriteCountState.value = props.favoriteCount;
      }
    };

    onMounted(() => {
      // 初始化时检查收藏状态和数量
      watchProps();

      // 如果没有提供初始状态，从API获取
      if (!props.isFavorite) {
        checkFavoriteStatus();
      }

      // 如果没有提供初始收藏数量，从API获取
      if (props.favoriteCount === 0) {
        loadFavoriteCount();
      }
    });

    return {
      isLoggedIn,
      isFavorite: isFavoriteState,
      favoriteCount: favoriteCountState,
      loading,
      toggleFavorite,
      goToLogin,
    };
  },
};
</script>

<style scoped>
.favorite-button {
  display: inline-block;
}

.favorite-count {
  margin-left: 4px;
  font-size: 12px;
}
</style>
