// 公共路由模块

const routes = [
  {
    path: "/",
    name: "Home",
    component: () => import("@/views/home/index.vue"),
    meta: {
      title: "首页",
      requiresAuth: false,
    },
  },
  {
    path: "/about",
    name: "About",
    component: () => import("@/views/About.vue"),
    meta: {
      title: "关于我们",
      requiresAuth: false,
    },
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/Login.vue"),
    meta: {
      title: "登录",
      requiresAuth: false,
    },
  },
  {
    path: "/guide",
    name: "Guide",
    component: () => import("@/views/Guide.vue"),
    meta: {
      title: "使用指南",
      requiresAuth: false,
    },
  },
  {
    path: "/resources",
    name: "ResourceLibrary",
    component: () => import("@/views/ResourceLibrary.vue"),
    meta: {
      title: "思政资源库",
      requiresAuth: true,
    },
  },
  {
    path: "/resource-management",
    name: "ResourceManagement",
    component: () => import("@/views/ResourceManagement.vue"),
    meta: {
      title: "思政资源管理",
      requiresAuth: true,
    },
  },
  {
    path: "/cases",
    name: "TeachingCases",
    component: () => import("@/views/TeachingCases.vue"),
    meta: {
      title: "教学案例",
      requiresAuth: true,
    },
  },
  {
    path: "/research",
    name: "Research",
    component: () => import("@/views/Research.vue"),
    meta: {
      title: "教学研究",
      requiresAuth: true,
    },
  },
  // 404路由放在最后
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

export default routes;
