// 用户(学生和教师)路由模块

const routes = [
  {
    path: "/teacher/center",
    name: "TeacherCenter",
    component: () => import("@/views/teacher/index.vue"),
    meta: {
      title: "教师空间",
      requiresAuth: true,
      roles: ["teacher"],
    },
  },
  {
    path: "/teacher/resources",
    name: "TeacherResourceManagement",
    component: () => import("@/views/teacher/ResourceManagement.vue"),
    meta: {
      title: "资源管理",
      requiresAuth: true,
      roles: ["teacher"],
    },
  },
  {
    path: "/teacher/courses",
    name: "TeacherCourseManagement",
    component: () => import("@/views/teacher/CourseManagement.vue"),
    meta: {
      title: "课程管理",
      requiresAuth: true,
      roles: ["teacher"],
    },
  },
  {
    path: "/teacher/profile",
    name: "TeacherProfileSettings",
    component: () => import("@/views/teacher/ProfileSettings.vue"),
    meta: {
      title: "个人设置",
      requiresAuth: true,
      roles: ["teacher"],
    },
  },
  {
    path: "/student/center",
    name: "StudentCenter",
    component: () => import("@/views/student/index.vue"),
    meta: {
      title: "学生空间",
      requiresAuth: true,
      roles: ["student"],
    },
  },
  {
    path: "/user",
    name: "UserCenter",
    component: () => import("@/views/user/UserCenter.vue"),
    meta: {
      title: "个人中心",
      requiresAuth: true,
      icon: "el-icon-user",
    },
    redirect: { name: "UserProfile" },
    children: [
      {
        path: "profile",
        name: "UserProfile",
        component: () => import("@/views/user/UserProfile.vue"),
        meta: {
          title: "个人资料",
          requiresAuth: true,
          icon: "el-icon-user",
        },
      },
      {
        path: "resources",
        name: "UserResources",
        component: () => import("@/views/user/UserResources.vue"),
        meta: {
          title: "我的资源",
          requiresAuth: true,
          icon: "el-icon-folder-opened",
        },
      },
      {
        path: "favorites",
        name: "UserFavorites",
        component: () => import("@/views/user/UserFavorites.vue"),
        meta: {
          title: "我的收藏",
          requiresAuth: true,
          icon: "el-icon-star-on",
        },
      },
      {
        path: "comments",
        name: "UserComments",
        component: () => import("@/views/user/UserComments.vue"),
        meta: {
          title: "我的评论",
          requiresAuth: true,
          icon: "el-icon-chat-line-square",
        },
      },
      {
        path: "settings",
        name: "UserSettings",
        component: () => import("@/views/user/UserSettings.vue"),
        meta: {
          title: "账号设置",
          requiresAuth: true,
          icon: "el-icon-setting",
        },
      },
    ],
  },
];

export default routes;
