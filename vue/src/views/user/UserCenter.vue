<template>
  <div class="user-center">
    <el-container>
      <el-aside width="250px">
        <div class="user-panel">
          <div class="user-info">
            <el-avatar :size="80" :src="userAvatar" class="user-avatar">
              {{ avatarText }}
            </el-avatar>
            <h3 class="user-name">{{ userName }}</h3>
            <div class="user-role">{{ userRoleText }}</div>
          </div>

          <el-menu :default-active="activeMenu" class="user-menu" router>
            <el-menu-item index="/user/profile">
              <i class="el-icon-user"></i>
              <span>个人资料</span>
            </el-menu-item>

            <el-menu-item index="/user/resources">
              <i class="el-icon-folder-opened"></i>
              <span>我的资源</span>
            </el-menu-item>

            <el-menu-item index="/user/favorites">
              <i class="el-icon-star-on"></i>
              <span>我的收藏</span>
              <el-badge
                v-if="favoritesCount > 0"
                :value="favoritesCount"
                class="menu-badge"
              />
            </el-menu-item>

            <el-menu-item index="/user/comments">
              <i class="el-icon-chat-line-square"></i>
              <span>我的评论</span>
              <el-badge
                v-if="commentsCount > 0"
                :value="commentsCount"
                class="menu-badge"
              />
            </el-menu-item>

            <el-menu-item index="/user/settings">
              <i class="el-icon-setting"></i>
              <span>账号设置</span>
            </el-menu-item>
          </el-menu>
        </div>
      </el-aside>

      <el-container>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { computed, ref, onMounted } from "vue";
import { useStore } from "vuex";
import { useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import { countUserFavorites } from "@/api/favorite";

export default {
  name: "UserCenter",
  setup() {
    const store = useStore();
    const route = useRoute();

    // 用户信息
    const userName = computed(() => store.getters["user/userName"] || "用户");
    const userRole = computed(() => store.getters["user/userRole"] || "user");
    const userAvatar = computed(() => store.getters["user/userAvatar"] || "");
    const userId = computed(() => store.getters["user/userId"]);

    // 菜单相关
    const activeMenu = computed(() => route.path);

    // 统计数据
    const favoritesCount = ref(0);
    const commentsCount = ref(0);

    // 头像文本
    const avatarText = computed(() => {
      if (!userName.value) return "";
      return userName.value.charAt(0).toUpperCase();
    });

    // 角色文本
    const userRoleText = computed(() => {
      const roleMap = {
        admin: "管理员",
        teacher: "教师",
        student: "学生",
        user: "普通用户",
      };
      return roleMap[userRole.value] || "用户";
    });

    // 加载统计数据
    const loadStatistics = async () => {
      if (!userId.value) return;

      try {
        // 加载收藏数量
        const favorites = await countUserFavorites(userId.value);
        favoritesCount.value = favorites;

        // 加载评论数量
        // 这里可以添加获取评论数量的API调用
        // const comments = await countUserComments(userId.value);
        // commentsCount.value = comments;
      } catch (error) {
        console.error("加载统计数据失败:", error);
      }
    };

    onMounted(() => {
      if (userId.value) {
        loadStatistics();
      }
    });

    return {
      userName,
      userRole,
      userAvatar,
      activeMenu,
      favoritesCount,
      commentsCount,
      avatarText,
      userRoleText,
    };
  },
};
</script>

<style scoped>
.user-center {
  height: calc(100vh - 60px);
}

.el-container {
  height: 100%;
}

.el-aside {
  background-color: #f8f8f8;
  border-right: 1px solid #e6e6e6;
}

.user-panel {
  padding: 20px 0;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.user-info {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #e6e6e6;
  margin-bottom: 20px;
}

.user-avatar {
  margin-bottom: 15px;
}

.user-name {
  margin: 0 0 5px;
  font-size: 18px;
  color: #333;
}

.user-role {
  color: #999;
  font-size: 14px;
}

.user-menu {
  border-right: none;
  flex: 1;
}

.el-main {
  padding: 20px;
  background-color: #fff;
}

.menu-badge {
  margin-top: 2px;
  margin-right: 15px;
}
</style>
