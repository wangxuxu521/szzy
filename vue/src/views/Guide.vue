<script>
export default {
  name: 'Guide',
  data() {
    return {
      activeSection: 'student',
      sections: [
        {
          id: 'student',
          title: '学生指南',
          icon: '👨‍🎓',
          content: [
            {
              title: '如何浏览教学案例',
              steps: [
                '登录系统后，点击首页的"教学案例"链接',
                '可以按学科分类浏览不同的教学案例',
                '点击具体案例可以查看详细内容',
                '可以通过搜索功能快速找到感兴趣的案例'
              ]
            },
            {
              title: '查看学习记录',
              steps: [
                '点击导航栏的"学生空间"',
                '在个人中心可以查看历史学习记录',
                '记录包含学习时间、进度等信息'
              ]
            }
          ]
        },
        {
          id: 'teacher',
          title: '教师指南',
          icon: '👨‍🏫',
          content: [
            {
              title: '上传教学资源',
              steps: [
                '登录后进入"教师空间"',
                '点击"上传资源"按钮',
                '填写资源信息并上传文件',
                '等待管理员审核通过'
              ]
            },
            {
              title: '管理教学案例',
              steps: [
                '在教师空间中查看已上传的案例',
                '可以编辑或更新案例内容',
                '查看案例的访问统计数据'
              ]
            }
          ]
        },
        {
          id: 'adminCenter',
          title: '管理员指南',
          icon: '👨‍💼',
          content: [
            {
              title: '资源审核',
              steps: [
                '登录管理员界面',
                '在"待审核"列表中查看新上传的资源',
                '审核内容是否符合要求',
                '通过或拒绝上传申请'
              ]
            },
            {
              title: '用户管理',
              steps: [
                '管理用户账号和权限',
                '处理用户反馈和问题',
                '查看系统使用统计数据'
              ]
            }
          ]
        }
      ]
    }
  },
  methods: {
    setSection(sectionId) {
      this.activeSection = sectionId
    }
  }
}
</script>

<template>
  <div class="guide">
    <div class="guide-header">
      <h1>使用指南</h1>
      <p>欢迎使用课程思政资源管理系统，请选择您的身份查看相应的使用指南。</p>
    </div>

    <div class="guide-content">
      <!-- 左侧导航 -->
      <div class="guide-nav">
        <button
          v-for="section in sections"
          :key="section.id"
          :class="['nav-btn', { active: activeSection === section.id }]"
          @click="setSection(section.id)"
        >
          <span class="icon">{{ section.icon }}</span>
          {{ section.title }}
        </button>
      </div>

      <!-- 右侧内容 -->
      <div class="guide-details">
        <template v-for="section in sections" :key="section.id">
          <div 
            v-if="activeSection === section.id"
            class="section-content"
          >
            <div class="section-header">
              <span class="icon">{{ section.icon }}</span>
              <h2>{{ section.title }}</h2>
            </div>

            <div class="guide-items">
              <div 
                v-for="(item, index) in section.content"
                :key="index"
                class="guide-item"
              >
                <h3>{{ item.title }}</h3>
                <ol class="steps">
                  <li 
                    v-for="(step, stepIndex) in item.steps"
                    :key="stepIndex"
                  >
                    {{ step }}
                  </li>
                </ol>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped>
.guide {
  padding: 2rem;
  min-height: 100vh;
  background-color: #f0f2f5;
}

.guide-header {
  text-align: center;
  margin-bottom: 3rem;
}

.guide-header h1 {
  color: #2c3e50;
  margin-bottom: 1rem;
}

.guide-header p {
  color: #666;
  font-size: 1.1rem;
}

.guide-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: 2rem;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.guide-nav {
  padding: 1.5rem;
  border-right: 1px solid #f0f0f0;
  background-color: #fafafa;
}

.nav-btn {
  width: 100%;
  padding: 1rem;
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: none;
  border: 1px solid transparent;
  border-radius: 4px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  text-align: left;
}

.nav-btn:hover {
  color: #1890ff;
  background-color: #e6f7ff;
}

.nav-btn.active {
  color: #1890ff;
  background-color: #e6f7ff;
  border-color: #1890ff;
}

.icon {
  font-size: 1.2rem;
}

.guide-details {
  padding: 2rem;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #f0f0f0;
}

.section-header h2 {
  margin: 0;
  color: #2c3e50;
}

.guide-items {
  display: grid;
  gap: 2rem;
}

.guide-item {
  background-color: #fafafa;
  padding: 1.5rem;
  border-radius: 8px;
}

.guide-item h3 {
  color: #2c3e50;
  margin: 0 0 1rem 0;
}

.steps {
  margin: 0;
  padding-left: 1.5rem;
}

.steps li {
  color: #666;
  line-height: 1.6;
  margin-bottom: 0.5rem;
}

.steps li:last-child {
  margin-bottom: 0;
}

@media (max-width: 768px) {
  .guide-content {
    grid-template-columns: 1fr;
  }

  .guide-nav {
    border-right: none;
    border-bottom: 1px solid #f0f0f0;
  }

  .nav-btn {
    padding: 0.8rem;
  }
}
</style> 