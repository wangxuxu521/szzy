import { createRouter, createWebHistory } from "vue-router";
import commonRoutes from "./modules/common";
import adminRoutes from "./modules/admin";
import userRoutes from "./modules/user";
import store from "../store";

// 合并所有路由
const routes = [
  ...commonRoutes,
  ...adminRoutes,
  ...userRoutes,
  // 404路由
  {
    path: "/:pathMatch(.*)*",
    name: "NotFound",
    component: () => import("@/views/home/404.vue"),
    meta: {
      title: "页面不存在",
      requiresAuth: false,
    },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title || "课程思政资源管理系统";

  // 获取用户信息
  const isLoggedIn = store.getters["user/isLoggedIn"];
  const userRole = store.getters["user/userRole"];

  // 登录页面逻辑
  if (to.path === "/login") {
    if (isLoggedIn) {
      next("/");
      return;
    }
    next();
    return;
  }

  // 检查页面是否需要认证
  if (to.meta.requiresAuth) {
    if (!isLoggedIn) {
      next("/login");
      return;
    }

    // 检查角色权限
    if (to.meta.roles && to.meta.roles.length > 0) {
      if (!to.meta.roles.includes(userRole)) {
        next("/");
        return;
      }
    }
  }

  next();
});

export default router;
