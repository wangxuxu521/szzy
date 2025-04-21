// 管理员路由模块

export default [
  {
    path: "/admin",
    name: "Admin",
    component: () => import("@/views/admin/index.vue"),
    redirect: "/admin/dashboard",
    meta: {
      title: "系统管理",
      requiresAuth: true,
      roles: ["admin"],
    },
    children: [
      {
        path: "dashboard",
        name: "AdminDashboard",
        component: () => import("@/views/admin/components/Dashboard.vue"),
        meta: {
          title: "管理控制台",
          requiresAuth: true,
          roles: ["admin"],
        },
      },
      {
        path: "user-management",
        name: "UserManagement",
        component: () => import("@/views/admin/components/UserManagement.vue"),
        meta: {
          title: "用户管理",
          requiresAuth: true,
          roles: ["admin"],
        },
      },
      {
        path: "resource-management",
        name: "ResourceManagement",
        component: () =>
          import("@/views/admin/components/ResourceManagement.vue"),
        meta: {
          title: "资源管理",
          requiresAuth: true,
          roles: ["admin"],
        },
      },
      {
        path: "tag-management",
        name: "TagManagement",
        component: () => import("@/views/admin/components/TagManagement.vue"),
        meta: {
          title: "标签管理",
          requiresAuth: true,
          roles: ["admin"],
        },
      },
      {
        path: "course-management",
        name: "CourseManagement",
        component: () =>
          import("@/views/admin/components/CourseManagement.vue"),
        meta: {
          title: "课程管理",
          requiresAuth: true,
          roles: ["admin"],
        },
      },
      {
        path: "type-management",
        name: "TypeManagement",
        component: () => import("@/views/admin/components/TypeManagement.vue"),
        meta: {
          title: "类型管理",
          requiresAuth: true,
          roles: ["admin"],
        },
      },
      {
        path: "statistics",
        name: "StatisticsAnalysis",
        component: () =>
          import("@/views/admin/components/StatisticsAnalysis.vue"),
        meta: {
          title: "统计分析",
          requiresAuth: true,
          roles: ["admin"],
        },
      },
      {
        path: "system-settings",
        name: "SystemSettings",
        component: () => import("@/views/admin/components/SystemSettings.vue"),
        meta: {
          title: "系统设置",
          requiresAuth: true,
          roles: ["admin"],
        },
      },
      {
        path: "api-test",
        name: "ApiTest",
        component: () => import("@/views/ApiTest.vue"),
        meta: {
          title: "API测试",
          requiresAuth: true,
          roles: ["admin"],
        },
      },
    ],
  },
];
