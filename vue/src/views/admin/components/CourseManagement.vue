<template>
  <div class="course-management">
    <div class="section-header">
      <h2>课程管理</h2>
      <div class="search-bar">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="搜索课程..."
          @input="handleSearch"
        />
        <select v-model="courseTypeFilter">
          <option value="">所有类型</option>
          <option value="必修课">必修课</option>
          <option value="选修课">选修课</option>
          <option value="公共课">公共课</option>
        </select>
      </div>
      <button class="add-btn" @click="showAddCourseModal = true">
        添加课程
      </button>
    </div>

    <div class="table-container">
      <table class="course-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>课程名称</th>
            <th>课程类型</th>
            <th>任课教师</th>
            <th>开课日期</th>
            <th>学时</th>
            <th>学分</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="course in filteredCourses" :key="course.id">
            <td>{{ course.id }}</td>
            <td class="course-title">{{ course.name }}</td>
            <td>
              <span class="type-tag" :class="getTypeClass(course.type)">
                {{ course.type }}
              </span>
            </td>
            <td>{{ course.teacher }}</td>
            <td>{{ course.startDate }}</td>
            <td>{{ course.hours }}</td>
            <td>{{ course.credits }}</td>
            <td>
              <span
                class="status-tag"
                :class="{
                  active: course.status === 'active',
                  pending: course.status === 'pending',
                  finished: course.status === 'finished',
                }"
              >
                {{ getStatusName(course.status) }}
              </span>
            </td>
            <td class="action-buttons">
              <button class="view-btn" @click="viewCourse(course)">查看</button>
              <button class="edit-btn" @click="editCourse(course)">编辑</button>
              <button
                v-if="course.status === 'pending'"
                class="approve-btn"
                @click="activateCourse(course)"
              >
                激活
              </button>
              <button class="delete-btn" @click="confirmDelete(course)">
                删除
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination">
      <button :disabled="currentPage === 1" @click="currentPage--">
        上一页
      </button>
      <span>{{ currentPage }} / {{ totalPages }}</span>
      <button :disabled="currentPage === totalPages" @click="currentPage++">
        下一页
      </button>
    </div>

    <!-- 添加/编辑课程弹窗 -->
    <div v-if="showAddCourseModal || showEditCourseModal" class="modal-overlay">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ showEditCourseModal ? "编辑课程" : "添加课程" }}</h3>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitCourseForm">
            <div class="form-group">
              <label>课程名称</label>
              <input type="text" v-model="courseForm.name" required />
            </div>
            <div class="form-group">
              <label>课程类型</label>
              <select v-model="courseForm.type" required>
                <option value="必修课">必修课</option>
                <option value="选修课">选修课</option>
                <option value="公共课">公共课</option>
              </select>
            </div>
            <div class="form-group">
              <label>任课教师</label>
              <input type="text" v-model="courseForm.teacher" required />
            </div>
            <div class="form-group">
              <label>开课日期</label>
              <input type="date" v-model="courseForm.startDate" required />
            </div>
            <div class="form-group">
              <label>学时</label>
              <input
                type="number"
                v-model="courseForm.hours"
                required
                min="1"
              />
            </div>
            <div class="form-group">
              <label>学分</label>
              <input
                type="number"
                v-model="courseForm.credits"
                required
                min="0.5"
                step="0.5"
              />
            </div>
            <div class="form-group">
              <label>课程简介</label>
              <textarea v-model="courseForm.description" rows="4"></textarea>
            </div>
            <div class="form-buttons">
              <button type="button" class="cancel-btn" @click="closeModal">
                取消
              </button>
              <button type="submit" class="submit-btn">保存</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 课程详情弹窗 -->
    <div v-if="showCourseDetail" class="modal-overlay">
      <div class="modal detail-modal">
        <div class="modal-header">
          <h3>课程详情</h3>
          <button class="close-btn" @click="showCourseDetail = false">×</button>
        </div>
        <div class="modal-body">
          <div class="detail-item">
            <div class="detail-label">课程名称</div>
            <div class="detail-value">{{ selectedCourse.name }}</div>
          </div>
          <div class="detail-item">
            <div class="detail-label">课程类型</div>
            <div class="detail-value">{{ selectedCourse.type }}</div>
          </div>
          <div class="detail-item">
            <div class="detail-label">任课教师</div>
            <div class="detail-value">{{ selectedCourse.teacher }}</div>
          </div>
          <div class="detail-item">
            <div class="detail-label">开课日期</div>
            <div class="detail-value">{{ selectedCourse.startDate }}</div>
          </div>
          <div class="detail-item">
            <div class="detail-label">学时</div>
            <div class="detail-value">{{ selectedCourse.hours }}</div>
          </div>
          <div class="detail-item">
            <div class="detail-label">学分</div>
            <div class="detail-value">{{ selectedCourse.credits }}</div>
          </div>
          <div class="detail-item">
            <div class="detail-label">状态</div>
            <div class="detail-value">
              {{ getStatusName(selectedCourse.status) }}
            </div>
          </div>
          <div class="detail-item">
            <div class="detail-label">课程简介</div>
            <div class="detail-value description">
              {{ selectedCourse.description }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 删除确认弹窗 -->
    <div v-if="showDeleteModal" class="modal-overlay">
      <div class="modal confirmation-modal">
        <div class="modal-header">
          <h3>确认删除</h3>
          <button class="close-btn" @click="showDeleteModal = false">×</button>
        </div>
        <div class="modal-body">
          <p>
            确定要删除课程 "{{ courseToDelete?.name }}" 吗？此操作不可撤销。
          </p>
          <div class="form-buttons">
            <button class="cancel-btn" @click="showDeleteModal = false">
              取消
            </button>
            <button class="delete-btn" @click="deleteCourse">删除</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, reactive } from "vue";

export default {
  name: "CourseManagement",
  setup() {
    // 搜索和筛选
    const searchQuery = ref("");
    const courseTypeFilter = ref("");

    // 分页
    const currentPage = ref(1);
    const pageSize = ref(10);

    // 模态框状态
    const showAddCourseModal = ref(false);
    const showEditCourseModal = ref(false);
    const showCourseDetail = ref(false);
    const showDeleteModal = ref(false);

    // 当前选中/编辑的课程
    const selectedCourse = ref({});
    const courseToDelete = ref(null);

    // 课程表单
    const courseForm = reactive({
      id: null,
      name: "",
      type: "必修课",
      teacher: "",
      startDate: "",
      hours: 32,
      credits: 2,
      description: "",
      status: "pending",
    });

    // 模拟课程数据
    const courses = ref([
      {
        id: 1,
        name: "计算机网络",
        type: "必修课",
        teacher: "张教授",
        startDate: "2024-09-01",
        hours: 48,
        credits: 3,
        description:
          "本课程主要学习计算机网络的基本概念、网络协议和网络应用开发，培养学生的网络意识和网络安全意识。",
        status: "active",
      },
      {
        id: 2,
        name: "数据结构",
        type: "必修课",
        teacher: "李教授",
        startDate: "2024-09-05",
        hours: 64,
        credits: 4,
        description:
          "本课程系统讲授数据结构的基本概念、基本原理和基本方法，培养学生的抽象思维能力和编程能力。",
        status: "active",
      },
      {
        id: 3,
        name: "人工智能导论",
        type: "选修课",
        teacher: "王教授",
        startDate: "2024-09-10",
        hours: 32,
        credits: 2,
        description:
          "本课程介绍人工智能的基本概念、技术和应用，引导学生了解人工智能的发展历程和未来趋势。",
        status: "pending",
      },
      {
        id: 4,
        name: "中国传统文化",
        type: "公共课",
        teacher: "赵教授",
        startDate: "2024-09-03",
        hours: 32,
        credits: 2,
        description:
          "本课程介绍中国传统文化的基本内容和精神内涵，培养学生的文化自信和文化认同。",
        status: "finished",
      },
    ]);

    // 过滤课程列表
    const filteredCourses = computed(() => {
      let result = [...courses.value];

      // 类型筛选
      if (courseTypeFilter.value) {
        result = result.filter((c) => c.type === courseTypeFilter.value);
      }

      // 搜索筛选
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase();
        result = result.filter(
          (c) =>
            c.name.toLowerCase().includes(query) ||
            c.teacher.toLowerCase().includes(query) ||
            c.description.toLowerCase().includes(query)
        );
      }

      // 分页
      const startIndex = (currentPage.value - 1) * pageSize.value;
      return result.slice(startIndex, startIndex + pageSize.value);
    });

    // 计算总页数
    const totalPages = computed(() => {
      const filteredTotal = courses.value.filter((c) => {
        if (courseTypeFilter.value && c.type !== courseTypeFilter.value) {
          return false;
        }

        if (searchQuery.value) {
          const query = searchQuery.value.toLowerCase();
          return (
            c.name.toLowerCase().includes(query) ||
            c.teacher.toLowerCase().includes(query) ||
            c.description.toLowerCase().includes(query)
          );
        }

        return true;
      }).length;

      return Math.ceil(filteredTotal / pageSize.value) || 1;
    });

    // 获取课程类型样式
    const getTypeClass = (type) => {
      switch (type) {
        case "必修课":
          return "required";
        case "选修课":
          return "elective";
        case "公共课":
          return "public";
        default:
          return "";
      }
    };

    // 获取状态显示名称
    const getStatusName = (status) => {
      switch (status) {
        case "active":
          return "进行中";
        case "pending":
          return "待开课";
        case "finished":
          return "已结束";
        default:
          return "未知";
      }
    };

    // 处理搜索
    const handleSearch = () => {
      currentPage.value = 1;
    };

    // 查看课程详情
    const viewCourse = (course) => {
      selectedCourse.value = { ...course };
      showCourseDetail.value = true;
    };

    // 编辑课程
    const editCourse = (course) => {
      Object.assign(courseForm, { ...course });
      showEditCourseModal.value = true;
    };

    // 激活课程
    const activateCourse = (course) => {
      const index = courses.value.findIndex((c) => c.id === course.id);
      if (index !== -1) {
        courses.value[index].status = "active";
      }
    };

    // 确认删除
    const confirmDelete = (course) => {
      courseToDelete.value = course;
      showDeleteModal.value = true;
    };

    // 删除课程
    const deleteCourse = () => {
      const index = courses.value.findIndex(
        (c) => c.id === courseToDelete.value.id
      );
      if (index !== -1) {
        courses.value.splice(index, 1);
      }
      showDeleteModal.value = false;
      courseToDelete.value = null;
    };

    // 关闭模态框
    const closeModal = () => {
      showAddCourseModal.value = false;
      showEditCourseModal.value = false;
      Object.assign(courseForm, {
        id: null,
        name: "",
        type: "必修课",
        teacher: "",
        startDate: "",
        hours: 32,
        credits: 2,
        description: "",
        status: "pending",
      });
    };

    // 提交课程表单
    const submitCourseForm = () => {
      if (showEditCourseModal.value) {
        // 编辑现有课程
        const index = courses.value.findIndex((c) => c.id === courseForm.id);
        if (index !== -1) {
          courses.value[index] = { ...courseForm };
        }
      } else {
        // 添加新课程
        const newId = Math.max(0, ...courses.value.map((c) => c.id)) + 1;
        courses.value.push({
          ...courseForm,
          id: newId,
          status: "pending",
        });
      }
      closeModal();
    };

    return {
      searchQuery,
      courseTypeFilter,
      currentPage,
      totalPages,
      courses,
      filteredCourses,
      showAddCourseModal,
      showEditCourseModal,
      showCourseDetail,
      showDeleteModal,
      selectedCourse,
      courseToDelete,
      courseForm,
      getTypeClass,
      getStatusName,
      handleSearch,
      viewCourse,
      editCourse,
      activateCourse,
      confirmDelete,
      deleteCourse,
      closeModal,
      submitCourseForm,
    };
  },
};
</script>

<style scoped>
.course-management {
  padding: 1rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.search-bar {
  display: flex;
  gap: 0.5rem;
}

.search-bar input {
  width: 300px;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.search-bar select {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.add-btn {
  background-color: #1890ff;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
}

.table-container {
  overflow-x: auto;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.course-table {
  width: 100%;
  border-collapse: collapse;
}

.course-table th,
.course-table td {
  padding: 0.75rem 1rem;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.course-table th {
  background-color: #f5f5f5;
  font-weight: 600;
}

.course-title {
  max-width: 200px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.type-tag {
  display: inline-block;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
}

.type-tag.required {
  background-color: #e6f7ff;
  color: #1890ff;
}

.type-tag.elective {
  background-color: #f6ffed;
  color: #52c41a;
}

.type-tag.public {
  background-color: #fff7e6;
  color: #fa8c16;
}

.status-tag {
  display: inline-block;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
}

.status-tag.active {
  background-color: #f6ffed;
  color: #52c41a;
}

.status-tag.pending {
  background-color: #fff7e6;
  color: #fa8c16;
}

.status-tag.finished {
  background-color: #f5f5f5;
  color: #666;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
}

.action-buttons button {
  padding: 0.25rem 0.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
}

.view-btn {
  background-color: #f0f0f0;
  color: #333;
}

.edit-btn {
  background-color: #e6f7ff;
  color: #1890ff;
}

.delete-btn {
  background-color: #fff1f0;
  color: #f5222d;
}

.approve-btn {
  background-color: #f6ffed;
  color: #52c41a;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 1.5rem;
  gap: 1rem;
}

.pagination button {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  background-color: white;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  color: #ccc;
  cursor: not-allowed;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background-color: white;
  border-radius: 4px;
  width: 600px;
  max-width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.detail-modal {
  width: 700px;
}

.confirmation-modal {
  width: 400px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}

.modal-body {
  padding: 1rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.form-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  margin-top: 1rem;
}

.cancel-btn {
  padding: 0.5rem 1rem;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
}

.submit-btn {
  padding: 0.5rem 1rem;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

/* 详情样式 */
.detail-item {
  display: flex;
  margin-bottom: 1rem;
}

.detail-label {
  font-weight: 500;
  width: 100px;
  flex-shrink: 0;
}

.detail-value {
  flex-grow: 1;
}

.detail-value.description {
  white-space: pre-line;
}

.tag {
  display: inline-block;
  padding: 0.25rem 0.5rem;
  background-color: #f5f5f5;
  color: #666;
  border-radius: 4px;
  margin-right: 0.5rem;
  margin-bottom: 0.5rem;
}

.download-link {
  color: #1890ff;
  text-decoration: none;
}

.download-link:hover {
  text-decoration: underline;
}
</style>
