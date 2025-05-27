<template>
  <div class="admin-layout">
    <app-header />
    <div class="admin-container">
      <aside class="admin-sidebar">
        <div class="sidebar-header">
          <h3>系统管理</h3>
        </div>
        <nav class="sidebar-menu">
          <router-link
            v-for="(item, index) in menuItems"
            :key="index"
            :to="item.path"
            class="menu-item"
          >
            <span class="menu-icon">{{ item.icon }}</span>
            <span class="menu-text">{{ item.title }}</span>
          </router-link>
        </nav>
      </aside>
      <main class="admin-content">
        <div class="content-header">
          <h2>{{ currentPageTitle }}</h2>
        </div>
        <div class="content-body">
          <slot></slot>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import { computed } from "vue";
import { useRoute } from "vue-router";
import AppHeader from "@/components/common/AppHeader.vue";

export default {
  name: "AdminLayout",
  components: {
    AppHeader,
  },
  setup() {
    const route = useRoute();

    const menuItems = [
      {
        title: "控制台",
        path: "/admin/dashboard",
        icon: "📊",
      },
      {
        title: "用户管理",
        path: "/admin/user-management",
        icon: "👥",
      },
      {
        title: "公告管理",
        path: "/admin/announcement-management",
        icon: "📢",
      },
      {
        title: "资源管理",
        path: "/admin/resource-management",
        icon: "📚",
      },
      {
        title: "标签管理",
        path: "/admin/tag-management",
        icon: "🏷️",
      },
      {
        title: "课程管理",
        path: "/admin/course-management",
        icon: "🎓",
      },
      {
        title: "类型管理",
        path: "/admin/type-management",
        icon: "🔍",
      },
      {
        title: "统计分析",
        path: "/admin/statistics",
        icon: "📈",
      },
      {
        title: "系统设置",
        path: "/admin/system-settings",
        icon: "⚙️",
      },
    ];

    const currentPageTitle = computed(() => {
      const currentMenuItem = menuItems.find(
        (item) => item.path === route.path
      );
      return currentMenuItem ? currentMenuItem.title : "系统管理";
    });

    return {
      menuItems,
      currentPageTitle,
    };
  },
};
</script>

<style scoped>
.admin-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.admin-container {
  display: flex;
  flex: 1;
}

.admin-sidebar {
  width: 250px;
  background-color: #001529;
  color: #fff;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 1.5rem;
  border-bottom: 1px solid #003a70;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 1.2rem;
}

.sidebar-menu {
  padding: 1rem 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 0.8rem 1.5rem;
  color: #ffffffcc;
  text-decoration: none;
  transition: background-color 0.3s;
}

.menu-item:hover {
  background-color: #1890ff;
  color: #fff;
}

.menu-item.router-link-active {
  background-color: #1890ff;
  color: #fff;
  border-right: 3px solid #fff;
}

.menu-icon {
  margin-right: 0.8rem;
}

.admin-content {
  flex: 1;
  padding: 1.5rem;
  background-color: #f0f2f5;
  overflow-y: auto;
}

.content-header {
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #ddd;
}

.content-header h2 {
  margin: 0;
  font-size: 1.5rem;
  color: #333;
}

.content-body {
  background-color: #fff;
  padding: 1.5rem;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>
