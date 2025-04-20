// 用户(学生和教师)路由模块

export default [
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
    path: "/student/center",
    name: "StudentCenter",
    component: () => import("@/views/student/index.vue"),
    meta: {
      title: "学生空间",
      requiresAuth: true,
      roles: ["student"],
    },
  },
];
