<template>
  <base-layout>
    <div class="not-found">
      <div class="not-found-content">
        <h1>404</h1>
        <h2>页面不存在</h2>
        <p>抱歉，您访问的页面不存在或已被移除</p>
        <router-link to="/" class="back-home">返回首页</router-link>
      </div>
    </div>
  </base-layout>
</template>

<script>
import BaseLayout from "@/layout/BaseLayout.vue";

export default {
  name: "NotFound",
  components: {
    BaseLayout,
  },
};
</script>

<style scoped>
.not-found {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 500px;
  text-align: center;
}

.not-found-content {
  max-width: 400px;
}

h1 {
  font-size: 8rem;
  margin: 0;
  color: #1890ff;
}

h2 {
  font-size: 2rem;
  margin: 0 0 1rem;
  color: #333;
}

p {
  color: #666;
  margin-bottom: 2rem;
}

.back-home {
  display: inline-block;
  padding: 0.8rem 2rem;
  background-color: #1890ff;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.back-home:hover {
  background-color: #40a9ff;
}
</style>
