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
    path: "/register",
    name: "Register",
    component: () => import("@/views/Register.vue"),
    meta: {
      title: "注册",
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
    path: "/announcements",
    name: "AnnouncementList",
    component: () => import("@/views/AnnouncementList.vue"),
    meta: {
      title: "通知公告",
      requiresAuth: false,
    },
  },
  {
    path: "/resources",
    name: "ResourceLibrary",
    component: () => import("@/views/ResourceLibrary.vue"),
    meta: {
      title: "资源库",
      icon: "el-icon-folder-opened",
      requiresAuth: true,
    },
  },
  {
    path: "/resources/:id",
    name: "ResourceDetail",
    component: () => import("@/views/ResourceDetail.vue"),
    meta: {
      title: "资源详情",
      requiresAuth: true,
    },
    props: true,
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
    path: "/upload-resource",
    name: "UploadResource",
    component: () => import("@/views/UploadResource.vue"),
    meta: {
      title: "上传资源",
      requiresAuth: true,
      roles: ["admin", "teacher", "student"],
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
