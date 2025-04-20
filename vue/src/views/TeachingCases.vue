<script>
export default {
  name: 'TeachingCases',
  data() {
    return {
      activeSubject: '全部',
      subjects: ['全部', '计算机网络', '数据结构', '软件工程', '人工智能'],
      cases: [
        {
          id: 1,
          title: '网络协议中的中国标准',
          subject: '计算机网络',
          teacher: '张教授',
          department: '计算机科学系',
          summary: '通过分析中国自主研发的网络协议标准，培养学生的民族自信心和创新精神。',
          views: 1580,
          likes: 245,
          date: '2024-03-20'
        },
        {
          id: 2,
          title: '数据结构中的辩证思维',
          subject: '数据结构',
          teacher: '李教授',
          department: '软件工程系',
          summary: '结合中国传统哲学思想，深入浅出地讲解数据结构的设计原理。',
          views: 1280,
          likes: 198,
          date: '2024-03-19'
        },
        {
          id: 3,
          title: 'AI伦理与社会责任',
          subject: '人工智能',
          teacher: '王教授',
          department: '人工智能学院',
          summary: '探讨人工智能发展中的伦理问题，培养学生的社会责任感。',
          views: 960,
          likes: 167,
          date: '2024-03-18'
        }
      ],
      searchQuery: ''
    }
  },
  computed: {
    filteredCases() {
      let result = this.cases
      if (this.activeSubject !== '全部') {
        result = result.filter(item => item.subject === this.activeSubject)
      }
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase()
        result = result.filter(item => 
          item.title.toLowerCase().includes(query) ||
          item.teacher.toLowerCase().includes(query) ||
          item.summary.toLowerCase().includes(query)
        )
      }
      return result
    }
  },
  methods: {
    setSubject(subject) {
      this.activeSubject = subject
    },
    handleSearch() {
      console.log('搜索:', this.searchQuery)
    },
    viewCase(teachingCase) {
      console.log('查看案例:', teachingCase)
    }
  }
}
</script>

<template>
  <div class="teaching-cases">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>优秀教学案例</h1>
      <div class="search-bar">
        <input 
          type="text" 
          v-model="searchQuery"
          placeholder="搜索案例..."
          @keyup.enter="handleSearch"
        >
        <button @click="handleSearch">搜索</button>
      </div>
    </div>

    <!-- 学科导航 -->
    <div class="subject-nav">
      <button 
        v-for="subject in subjects"
        :key="subject"
        :class="['subject-btn', { active: activeSubject === subject }]"
        @click="setSubject(subject)"
      >
        {{ subject }}
      </button>
    </div>

    <!-- 案例列表 -->
    <div class="case-list">
      <div 
        v-for="teachingCase in filteredCases"
        :key="teachingCase.id"
        class="case-card"
        @click="viewCase(teachingCase)"
      >
        <div class="case-header">
          <h3>{{ teachingCase.title }}</h3>
          <span class="subject-tag">{{ teachingCase.subject }}</span>
        </div>
        <div class="case-content">
          <p class="summary">{{ teachingCase.summary }}</p>
          <div class="teacher-info">
            <span class="teacher">👤 {{ teachingCase.teacher }}</span>
            <span class="department">{{ teachingCase.department }}</span>
          </div>
        </div>
        <div class="case-footer">
          <div class="stats">
            <span>👁️ {{ teachingCase.views }}</span>
            <span>❤️ {{ teachingCase.likes }}</span>
          </div>
          <span class="date">{{ teachingCase.date }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.teaching-cases {
  padding: 2rem;
  min-height: 100vh;
  background-color: #f0f2f5;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-header h1 {
  color: #2c3e50;
  margin: 0;
}

.search-bar {
  display: flex;
  gap: 1rem;
  max-width: 400px;
}

.search-bar input {
  flex: 1;
  padding: 0.5rem 1rem;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  font-size: 1rem;
}

.search-bar button {
  padding: 0.5rem 1.5rem;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.search-bar button:hover {
  background-color: #40a9ff;
}

.subject-nav {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.subject-btn {
  padding: 0.5rem 1.5rem;
  background-color: white;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  color: #666;
}

.subject-btn:hover {
  color: #1890ff;
  border-color: #1890ff;
}

.subject-btn.active {
  background-color: #1890ff;
  color: white;
  border-color: #1890ff;
}

.case-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 2rem;
}

.case-card {
  background-color: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: transform 0.3s;
}

.case-card:hover {
  transform: translateY(-5px);
}

.case-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.case-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.2rem;
  flex: 1;
  margin-right: 1rem;
}

.subject-tag {
  padding: 0.2rem 0.5rem;
  background-color: #e6f7ff;
  color: #1890ff;
  border-radius: 4px;
  font-size: 0.9rem;
  white-space: nowrap;
}

.case-content {
  margin-bottom: 1.5rem;
}

.summary {
  color: #666;
  margin: 0 0 1rem 0;
  line-height: 1.6;
}

.teacher-info {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 0.9rem;
}

.case-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #999;
  font-size: 0.9rem;
}

.stats {
  display: flex;
  gap: 1rem;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }

  .search-bar {
    max-width: none;
  }

  .case-list {
    grid-template-columns: 1fr;
  }

  .case-header {
    flex-direction: column;
    gap: 0.5rem;
  }

  .teacher-info {
    flex-direction: column;
    gap: 0.5rem;
  }
}
</style> 