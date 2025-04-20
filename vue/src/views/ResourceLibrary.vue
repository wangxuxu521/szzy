<script>
export default {
  name: 'ResourceLibrary',
  data() {
    return {
      activeCategory: '全部',
      categories: ['全部', '思政理论', '专业课程', '通识教育', '实践教学'],
      resources: [
        {
          id: 1,
          title: '计算机网络中的国产自主创新案例',
          category: '专业课程',
          type: '教学资源',
          author: '张教授',
          views: 1234,
          date: '2024-03-20'
        },
        {
          id: 2,
          title: '数据结构中的民族精神教育',
          category: '专业课程',
          type: '教学资源',
          author: '李教授',
          views: 890,
          date: '2024-03-19'
        },
        {
          id: 3,
          title: '中国传统文化与软件工程',
          category: '思政理论',
          type: '教学资源',
          author: '王教授',
          views: 756,
          date: '2024-03-18'
        }
      ],
      searchQuery: ''
    }
  },
  computed: {
    filteredResources() {
      let result = this.resources
      if (this.activeCategory !== '全部') {
        result = result.filter(item => item.category === this.activeCategory)
      }
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase()
        result = result.filter(item => 
          item.title.toLowerCase().includes(query) ||
          item.author.toLowerCase().includes(query)
        )
      }
      return result
    }
  },
  methods: {
    setCategory(category) {
      this.activeCategory = category
    },
    handleSearch() {
      console.log('搜索:', this.searchQuery)
    },
    viewResource(resource) {
      console.log('查看资源:', resource)
    }
  }
}
</script>

<template>
  <div class="resource-library">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>思政教学资源库</h1>
      <div class="search-bar">
        <input 
          type="text" 
          v-model="searchQuery"
          placeholder="搜索资源..."
          @keyup.enter="handleSearch"
        >
        <button @click="handleSearch">搜索</button>
      </div>
    </div>

    <!-- 分类导航 -->
    <div class="category-nav">
      <button 
        v-for="category in categories"
        :key="category"
        :class="['category-btn', { active: activeCategory === category }]"
        @click="setCategory(category)"
      >
        {{ category }}
      </button>
    </div>

    <!-- 资源列表 -->
    <div class="resource-grid">
      <div 
        v-for="resource in filteredResources"
        :key="resource.id"
        class="resource-card"
        @click="viewResource(resource)"
      >
        <div class="resource-type">{{ resource.type }}</div>
        <h3>{{ resource.title }}</h3>
        <div class="resource-meta">
          <span class="category">{{ resource.category }}</span>
          <span class="author">{{ resource.author }}</span>
        </div>
        <div class="resource-footer">
          <span class="views">👁️ {{ resource.views }}</span>
          <span class="date">{{ resource.date }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.resource-library {
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

.category-nav {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.category-btn {
  padding: 0.5rem 1.5rem;
  background-color: white;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  color: #666;
}

.category-btn:hover {
  color: #1890ff;
  border-color: #1890ff;
}

.category-btn.active {
  background-color: #1890ff;
  color: white;
  border-color: #1890ff;
}

.resource-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 2rem;
}

.resource-card {
  background-color: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: transform 0.3s;
}

.resource-card:hover {
  transform: translateY(-5px);
}

.resource-type {
  display: inline-block;
  padding: 0.2rem 0.5rem;
  background-color: #e6f7ff;
  color: #1890ff;
  border-radius: 4px;
  font-size: 0.9rem;
  margin-bottom: 1rem;
}

.resource-card h3 {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 1.1rem;
}

.resource-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
  color: #666;
  font-size: 0.9rem;
}

.resource-footer {
  display: flex;
  justify-content: space-between;
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

  .resource-grid {
    grid-template-columns: 1fr;
  }
}
</style> 