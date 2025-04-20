<template>
  <footer class="app-footer">
    <div class="footer-content">
      <div class="footer-section">
        <h3>关于我们</h3>
        <p>
          课程思政资源管理系统致力于整合和共享高质量思政教学资源，促进课程思政建设与发展。
        </p>
      </div>
      <div class="footer-section">
        <h3>快速链接</h3>
        <router-link to="/" class="footer-link">首页</router-link>
        <router-link to="/resources" class="footer-link">资源库</router-link>
        <router-link to="/cases" class="footer-link">教学案例</router-link>
        <router-link to="/research" class="footer-link">教学研究</router-link>
        <router-link to="/guide" class="footer-link">使用指南</router-link>
      </div>
      <div class="footer-section">
        <h3>联系我们</h3>
        <p>电子邮件: contact@example.com</p>
        <p>电话: 123-456-7890</p>
      </div>
    </div>
    <div class="footer-bottom">
      <p>&copy; {{ currentYear }} 课程思政资源管理系统 版权所有</p>
    </div>
  </footer>
</template>

<script>
import { computed } from "vue";

export default {
  name: "AppFooter",
  setup() {
    const currentYear = computed(() => new Date().getFullYear());

    return {
      currentYear,
    };
  },
};
</script>

<style scoped>
.app-footer {
  background-color: #24292e;
  color: #fff;
  padding: 2rem 0;
  margin-top: 2rem;
}

.footer-content {
  display: flex;
  justify-content: space-around;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

.footer-section {
  flex: 1;
  margin: 0 1rem;
}

.footer-section h3 {
  margin-bottom: 1rem;
  border-bottom: 1px solid #444;
  padding-bottom: 0.5rem;
}

.footer-link {
  display: block;
  color: #ddd;
  text-decoration: none;
  margin-bottom: 0.5rem;
}

.footer-link:hover {
  color: #1890ff;
}

.footer-bottom {
  text-align: center;
  padding-top: 2rem;
  border-top: 1px solid #444;
  margin-top: 2rem;
}
</style>
