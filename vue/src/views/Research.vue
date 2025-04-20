<script>
export default {
  name: 'Research',
  data() {
    return {
      activeType: '全部',
      types: ['全部', '研究论文', '教改项目', '教学成果', '研讨会议'],
      researches: [
        {
          id: 1,
          title: '课程思政融入计算机专业课程的探索与实践',
          type: '研究论文',
          author: '张教授',
          department: '计算机科学系',
          keywords: ['课程思政', '计算机专业', '教学改革'],
          abstract: '本研究探讨了在计算机专业课程中融入思政元素的有效方法，提出了"三位一体"的教学模式。',
          publication: '高等教育研究',
          date: '2024-03-20',
          citations: 12
        },
        {
          id: 2,
          title: '基于大数据的课程思政教学效果评价体系构建',
          type: '教改项目',
          author: '李教授',
          department: '软件工程系',
          keywords: ['教学评价', '大数据分析', '课程思政'],
          abstract: '运用大数据技术构建科学的课程思政教学效果评价体系，实现教学质量的精准评估。',
          funding: '省级教改重点项目',
          date: '2024-03-19',
          status: '在研'
        },
        {
          id: 3,
          title: '新工科背景下的课程思政教学模式创新',
          type: '教学成果',
          author: '王教授',
          department: '人工智能学院',
          keywords: ['新工科', '课程思政', '教学创新'],
          abstract: '探索新工科背景下课程思政与专业教育深度融合的创新模式，形成可推广的教学经验。',
          award: '省级教学成果一等奖',
          date: '2024-03-18',
          implementation: '已在5所高校推广'
        }
      ],
      searchQuery: ''
    }
  },
  computed: {
    filteredResearches() {
      let result = this.researches
      if (this.activeType !== '全部') {
        result = result.filter(item => item.type === this.activeType)
      }
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase()
        result = result.filter(item => 
          item.title.toLowerCase().includes(query) ||
          item.author.toLowerCase().includes(query) ||
          item.abstract.toLowerCase().includes(query) ||
          item.keywords.some(keyword => keyword.toLowerCase().includes(query))
        )
      }
      return result
    }
  },
  methods: {
    setType(type) {
      this.activeType = type
    },
    handleSearch() {
      console.log('搜索:', this.searchQuery)
    },
    viewResearch(research) {
      console.log('查看研究:', research)
    }
  }
}
</script>

<template>
  <div class="research">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>教学研究成果</h1>
      <div class="search-bar">
        <input 
          type="text" 
          v-model="searchQuery"
          placeholder="搜索研究成果..."
          @keyup.enter="handleSearch"
        >
        <button @click="handleSearch">搜索</button>
      </div>
    </div>

    <!-- 类型导航 -->
    <div class="type-nav">
      <button 
        v-for="type in types"
        :key="type"
        :class="['type-btn', { active: activeType === type }]"
        @click="setType(type)"
      >
        {{ type }}
      </button>
    </div>

    <!-- 研究列表 -->
    <div class="research-list">
      <div 
        v-for="research in filteredResearches"
        :key="research.id"
        class="research-card"
        @click="viewResearch(research)"
      >
        <div class="research-header">
          <span class="type-tag">{{ research.type }}</span>
          <h3>{{ research.title }}</h3>
          <div class="author-info">
            <span class="author">👤 {{ research.author }}</span>
            <span class="department">{{ research.department }}</span>
          </div>
        </div>
        <div class="research-content">
          <div class="keywords">
            <span 
              v-for="(keyword, index) in research.keywords" 
              :key="index"
              class="keyword"
            >
              {{ keyword }}
            </span>
          </div>
          <p class="abstract">{{ research.abstract }}</p>
        </div>
        <div class="research-footer">
          <div class="achievement">
            <span v-if="research.publication">发表于：{{ research.publication }}</span>
            <span v-if="research.funding">项目类型：{{ research.funding }}</span>
            <span v-if="research.award">获得奖项：{{ research.award }}</span>
          </div>
          <div class="meta">
            <span v-if="research.citations">被引：{{ research.citations }}</span>
            <span v-if="research.status">状态：{{ research.status }}</span>
            <span v-if="research.implementation">{{ research.implementation }}</span>
            <span class="date">{{ research.date }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.research {
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

.type-nav {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.type-btn {
  padding: 0.5rem 1.5rem;
  background-color: white;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  color: #666;
}

.type-btn:hover {
  color: #1890ff;
  border-color: #1890ff;
}

.type-btn.active {
  background-color: #1890ff;
  color: white;
  border-color: #1890ff;
}

.research-list {
  display: grid;
  grid-template-columns: 1fr;
  gap: 2rem;
}

.research-card {
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: transform 0.3s;
}

.research-card:hover {
  transform: translateY(-5px);
}

.research-header {
  margin-bottom: 1.5rem;
}

.type-tag {
  display: inline-block;
  padding: 0.2rem 0.5rem;
  background-color: #e6f7ff;
  color: #1890ff;
  border-radius: 4px;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}

.research-header h3 {
  margin: 0.5rem 0;
  color: #2c3e50;
  font-size: 1.3rem;
}

.author-info {
  display: flex;
  gap: 1rem;
  color: #666;
  font-size: 0.9rem;
}

.keywords {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin-bottom: 1rem;
}

.keyword {
  padding: 0.2rem 0.5rem;
  background-color: #f5f5f5;
  border-radius: 4px;
  font-size: 0.9rem;
  color: #666;
}

.abstract {
  color: #666;
  line-height: 1.6;
  margin: 0;
}

.research-footer {
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid #f0f0f0;
}

.achievement {
  margin-bottom: 1rem;
  color: #2c3e50;
}

.meta {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  color: #999;
  font-size: 0.9rem;
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

  .author-info {
    flex-direction: column;
    gap: 0.5rem;
  }

  .meta {
    flex-direction: column;
    gap: 0.5rem;
  }
}
</style> 