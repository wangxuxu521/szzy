<script>
import { ref } from "vue";
import BaseLayout from "@/layout/BaseLayout.vue";

export default {
  name: "About",
  components: {
    BaseLayout,
  },
  setup() {
    const features = ref([
      {
        title: "资源共享",
        description: "提供丰富的课程思政教学资源，支持多种格式的文件上传和下载",
        icon: "📚",
      },
      {
        title: "分类管理",
        description: "按照专业、学科等多维度对资源进行分类，便于查找和管理",
        icon: "🗂️",
      },
      {
        title: "在线预览",
        description:
          "支持常见文档、图片、视频等资源的在线预览，无需下载即可查看",
        icon: "👁️",
      },
      {
        title: "用户互动",
        description:
          "用户可以对资源进行评论、点赞、收藏，促进教学资源的交流与共享",
        icon: "💬",
      },
      {
        title: "数据统计",
        description: "提供资源使用情况的统计分析，帮助管理者了解资源利用效率",
        icon: "📊",
      },
    ]);

    const team = ref([
      {
        name: "张三",
        role: "项目负责人",
        avatar:
          "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png",
      },
      {
        name: "李四",
        role: "前端开发",
        avatar:
          "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png",
      },
      {
        name: "王五",
        role: "后端开发",
        avatar:
          "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png",
      },
      {
        name: "赵六",
        role: "UI设计",
        avatar:
          "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png",
      },
    ]);

    return {
      features,
      team,
    };
  },
};
</script>

<template>
  <base-layout>
    <div class="about-page">
      <section class="hero">
        <h1>关于课程思政资源管理系统</h1>
        <p class="subtitle">整合优质思政教学资源，助力课程思政建设</p>
      </section>

      <section class="introduction">
        <h2>系统介绍</h2>
        <div class="content">
          <p>
            课程思政资源管理系统是一个专为高校教师和学生设计的教学资源共享平台。本系统旨在促进思政元素与专业课程的有机融合，为教师提供丰富的课程思政教学资源，助力实现"立德树人"的教育目标。
          </p>
          <p>
            通过本系统，教师可以便捷地上传、分享和管理课程思政教学资源，学生可以方便地查找和学习相关资料，管理员可以高效地维护和审核系统内容，共同构建优质的课程思政教学资源库。
          </p>
        </div>
      </section>

      <section class="features">
        <h2>功能特点</h2>
        <div class="feature-grid">
          <div
            v-for="(feature, index) in features"
            :key="index"
            class="feature-card"
          >
            <div class="feature-icon">{{ feature.icon }}</div>
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.description }}</p>
          </div>
        </div>
      </section>

      <section class="team">
        <h2>开发团队</h2>
        <div class="team-grid">
          <div v-for="(member, index) in team" :key="index" class="team-card">
            <img :src="member.avatar" :alt="member.name" class="team-avatar" />
            <h3>{{ member.name }}</h3>
            <p>{{ member.role }}</p>
          </div>
        </div>
      </section>

      <section class="contact">
        <h2>联系我们</h2>
        <div class="contact-info">
          <p><strong>邮箱：</strong> contact@example.com</p>
          <p><strong>地址：</strong> 中国·某省·某市·某大学</p>
          <p><strong>电话：</strong> 123-4567-8910</p>
        </div>
      </section>
    </div>
  </base-layout>
</template>

<style scoped>
.about-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 0;
}

.hero {
  text-align: center;
  margin-bottom: 3rem;
}

.hero h1 {
  font-size: 2.5rem;
  color: #2c3e50;
  margin-bottom: 1rem;
}

.subtitle {
  font-size: 1.2rem;
  color: #666;
}

section {
  margin-bottom: 4rem;
}

h2 {
  font-size: 1.8rem;
  color: #2c3e50;
  margin-bottom: 1.5rem;
  position: relative;
  padding-bottom: 0.5rem;
}

h2::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 0;
  width: 50px;
  height: 3px;
  background-color: #1890ff;
}

.content {
  line-height: 1.8;
  color: #666;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 2rem;
}

.feature-card {
  background-color: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.feature-card h3 {
  font-size: 1.2rem;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.feature-card p {
  color: #666;
  line-height: 1.6;
}

.team-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 2rem;
}

.team-card {
  background-color: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  text-align: center;
  transition: transform 0.3s;
}

.team-card:hover {
  transform: translateY(-5px);
}

.team-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 1rem;
}

.team-card h3 {
  font-size: 1.2rem;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.team-card p {
  color: #666;
}

.contact-info {
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.contact-info p {
  margin-bottom: 1rem;
  color: #666;
}

@media (max-width: 768px) {
  .feature-grid,
  .team-grid {
    grid-template-columns: 1fr;
  }

  .hero h1 {
    font-size: 2rem;
  }
}
</style>
