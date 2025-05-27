<template>
  <header class="app-header">
    <div class="logo">课程思政资源管理系统</div>
    <div class="search-bar">
      <input
        type="text"
        v-model="searchQuery"
        placeholder="搜索资源..."
        @keyup.enter="handleSearch"
      />
      <button @click="handleSearch">🔍</button>
    </div>
    <div class="nav-links">
      <router-link to="/" class="nav-link">首页</router-link>
      <router-link to="/resources" class="nav-link">资源库</router-link>

      <template v-if="!isLoggedIn">
        <button @click="goToLogin" class="login-btn">登录</button>
      </template>
      <template v-else>
        <div class="user-info">
          <span class="username">{{ username }}</span>
          <template v-if="userRole === 'teacher'">
            <button @click="goToUserCenter" class="icon-btn">
              👤 教师空间
            </button>
          </template>
          <template v-if="userRole === 'student'">
            <button @click="goToUserCenter" class="icon-btn">
              👤 学生空间
            </button>
          </template>
          <template v-if="userRole === 'admin'">
            <button @click="goToAdmin" class="icon-btn">⚙️ 系统管理</button>
          </template>
          <button @click="logout" class="logout-btn">退出</button>
        </div>
      </template>
    </div>
  </header>
</template>

<script>
import { computed, ref } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";

export default {
  name: "AppHeader",
  setup() {
    const store = useStore();
    const router = useRouter();
    const searchQuery = ref("");

    const isLoggedIn = computed(() => store.getters["user/isLoggedIn"]);
    const username = computed(() => store.getters["user/username"]);
    const userRole = computed(() => store.getters["user/userRole"]);

    const goToLogin = () => {
      router.push("/login");
    };

    const goToUserCenter = () => {
      if (userRole.value === "teacher") {
        router.push("/teacher/center");
      } else if (userRole.value === "student") {
        router.push("/student/center");
      }
    };

    const goToAdmin = () => {
      if (userRole.value === "admin") {
        router.push("/admin");
      }
    };

    const logout = () => {
      store.dispatch("user/logout").then(() => {
        router.push("/");
      });
    };

    const handleSearch = () => {
      if (searchQuery.value.trim()) {
        store.dispatch("resource/searchResources", {
          keyword: searchQuery.value.trim(),
        });
        router.push({
          path: "/resources",
          query: { keyword: searchQuery.value.trim() },
        });
      }
    };

    return {
      isLoggedIn,
      username,
      userRole,
      searchQuery,
      goToLogin,
      goToUserCenter,
      goToAdmin,
      logout,
      handleSearch,
    };
  },
};
</script>

<style scoped>
.app-header {
  display: flex;
  align-items: center;
  padding: 1rem 2rem;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.logo {
  font-size: 1.5rem;
  font-weight: bold;
  color: #1890ff;
  margin-right: 2rem;
}

.search-bar {
  display: flex;
  margin-right: auto;
}

.search-bar input {
  width: 300px;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px 0 0 4px;
  outline: none;
}

.search-bar button {
  padding: 0.5rem 1rem;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
}

.nav-links {
  display: flex;
  align-items: center;
}

.nav-link {
  margin: 0 1rem;
  color: #333;
  text-decoration: none;
}

.nav-link.router-link-active {
  color: #1890ff;
  font-weight: bold;
}

.login-btn,
.logout-btn,
.icon-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.login-btn {
  background-color: #1890ff;
  color: white;
}

.logout-btn {
  background-color: #f5f5f5;
  color: #666;
  margin-left: 1rem;
}

.icon-btn {
  background-color: transparent;
  color: #1890ff;
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
}

.username {
  margin-right: 1rem;
  font-weight: bold;
}
</style>
