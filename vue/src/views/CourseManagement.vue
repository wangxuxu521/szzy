<template>
  <div class="course-management">
    <h2>课程管理</h2>
    <!-- 课程列表和操作 -->
    <div class="course-list">
      <!-- TODO: 实现课程列表 -->
    </div>
  </div>
</template>

<script>
export default {
  name: "CourseManagement",
  data() {
    return {
      courses: [], // 课程数据
    };
  },
  methods: {
    fetchCourses() {
      // TODO: 从API获取课程数据
    },
  },
  mounted() {
    this.fetchCourses();
  },
};
</script>

<style scoped>
.course-management {
  padding: 1rem;
}
</style>
