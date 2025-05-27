<template>
  <div class="course-management">
    <h2>课程管理</h2>

    <!-- 搜索和操作栏 -->
    <div class="action-bar">
      <el-input
        v-model="searchQuery"
        placeholder="搜索课程"
        clearable
        @clear="fetchCourses"
        @input="handleSearch"
        style="width: 300px; margin-right: 15px"
      >
        <template #append>
          <el-button :icon="Search" @click="handleSearch"></el-button>
        </template>
      </el-input>

      <el-button type="primary" @click="handleAdd">新增课程</el-button>
    </div>

    <!-- 课程列表 -->
    <el-table
      v-loading="loading"
      :data="courses"
      border
      style="width: 100%; margin-top: 20px"
    >
      <el-table-column label="ID" prop="courseId" width="80" />
      <el-table-column label="课程名称" prop="courseName" />
      <el-table-column label="课程类型" prop="typeName" />
      <el-table-column label="授课教师" prop="teacherName" />
      <el-table-column label="创建时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? "正常" : "禁用" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)"
            >编辑</el-button
          >
          <el-button size="small" type="danger" @click="handleDelete(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页控件 -->
    <div class="pagination-container">
      <el-pagination
        v-if="total > 0"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        :current-page="currentPage"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 课程表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑课程' : '新增课程'"
      width="600px"
    >
      <el-form
        ref="courseFormRef"
        :model="courseForm"
        :rules="courseRules"
        label-width="100px"
      >
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="courseForm.courseName" />
        </el-form-item>

        <el-form-item label="课程类型" prop="typeId">
          <el-select v-model="courseForm.typeId" placeholder="请选择课程类型">
            <el-option
              v-for="item in courseTypes"
              :key="item.typeId"
              :label="item.typeName"
              :value="item.typeId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="授课教师" prop="teacherId">
          <el-select
            v-model="courseForm.teacherId"
            placeholder="请选择授课教师"
          >
            <el-option
              v-for="item in teachers"
              :key="item.userId"
              :label="item.name || item.username"
              :value="item.userId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="courseForm.description"
            type="textarea"
            :rows="3"
          />
        </el-form-item>

        <el-form-item label="课程封面">
          <el-upload
            class="cover-upload"
            action="/api/upload"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :on-success="handleUploadSuccess"
          >
            <img
              v-if="courseForm.coverImage"
              :src="courseForm.coverImage"
              class="cover-image"
            />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="courseForm.status"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, Plus } from "@element-plus/icons-vue";
import {
  getCourseList,
  getCourseById,
  addCourse,
  updateCourse,
  deleteCourse,
} from "@/api/course";
import { getResourceTypes } from "@/api/resource";
import { findByRole } from "@/api/user";

export default {
  name: "CourseManagement",
  setup() {
    // 数据列表
    const courses = ref([]);
    const courseTypes = ref([]);
    const teachers = ref([]);

    // 加载状态
    const loading = ref(false);

    // 分页数据
    const currentPage = ref(1);
    const pageSize = ref(10);
    const total = ref(0);

    // 搜索查询
    const searchQuery = ref("");

    // 表单数据
    const courseFormRef = ref(null);
    const courseForm = reactive({
      courseId: null,
      courseName: "",
      typeId: null,
      teacherId: null,
      description: "",
      coverImage: "",
      status: 1,
    });

    // 表单验证规则
    const courseRules = {
      courseName: [
        { required: true, message: "请输入课程名称", trigger: "blur" },
        { min: 2, max: 50, message: "长度在2到50个字符", trigger: "blur" },
      ],
      typeId: [
        { required: true, message: "请选择课程类型", trigger: "change" },
      ],
      teacherId: [
        { required: true, message: "请选择授课教师", trigger: "change" },
      ],
    };

    // 对话框状态
    const dialogVisible = ref(false);
    const isEdit = ref(false);

    // 获取课程列表
    const fetchCourses = async () => {
      loading.value = true;
      try {
        const res = await getCourseList({
          page: currentPage.value,
          size: pageSize.value,
          query: searchQuery.value,
        });

        if (res && res.data) {
          courses.value = res.data.list || res.data;
          total.value = res.data.total || courses.value.length;
        } else {
          courses.value = [];
          total.value = 0;
        }
      } catch (error) {
        console.error("获取课程列表失败:", error);
        ElMessage.error("获取课程列表失败");
      } finally {
        loading.value = false;
      }
    };

    // 获取课程类型
    const fetchCourseTypes = async () => {
      try {
        const res = await getResourceTypes();
        courseTypes.value = Array.isArray(res) ? res : res.data || [];
      } catch (error) {
        console.error("获取课程类型失败:", error);
      }
    };

    // 获取教师列表
    const fetchTeachers = async () => {
      try {
        const res = await findByRole("teacher");
        teachers.value = Array.isArray(res) ? res : res.data || [];
      } catch (error) {
        console.error("获取教师列表失败:", error);
      }
    };

    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return "";
      const date = new Date(dateString);
      return date.toLocaleString();
    };

    // 处理搜索
    const handleSearch = () => {
      currentPage.value = 1;
      fetchCourses();
    };

    // 处理分页大小变化
    const handleSizeChange = (size) => {
      pageSize.value = size;
      fetchCourses();
    };

    // 处理页码变化
    const handleCurrentChange = (page) => {
      currentPage.value = page;
      fetchCourses();
    };

    // 新增课程
    const handleAdd = () => {
      isEdit.value = false;
      resetForm();
      dialogVisible.value = true;
    };

    // 编辑课程
    const handleEdit = async (row) => {
      isEdit.value = true;
      resetForm();

      try {
        loading.value = true;
        const res = await getCourseById(row.courseId);
        const courseData = res.data || res;

        // 填充表单数据
        Object.keys(courseForm).forEach((key) => {
          if (courseData[key] !== undefined) {
            courseForm[key] = courseData[key];
          }
        });

        dialogVisible.value = true;
      } catch (error) {
        console.error("获取课程详情失败:", error);
        ElMessage.error("获取课程详情失败");
      } finally {
        loading.value = false;
      }
    };

    // 删除课程
    const handleDelete = (row) => {
      ElMessageBox.confirm(
        `确定要删除课程"${row.courseName}"吗？`,
        "删除确认",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(async () => {
          try {
            loading.value = true;
            await deleteCourse(row.courseId);
            ElMessage.success("删除成功");
            fetchCourses();
          } catch (error) {
            console.error("删除课程失败:", error);
            ElMessage.error("删除课程失败");
          } finally {
            loading.value = false;
          }
        })
        .catch(() => {});
    };

    // 重置表单
    const resetForm = () => {
      if (courseFormRef.value) {
        courseFormRef.value.resetFields();
      }

      Object.keys(courseForm).forEach((key) => {
        if (key === "status") {
          courseForm[key] = 1;
        } else {
          courseForm[key] = null;
        }
      });

      courseForm.courseName = "";
      courseForm.description = "";
      courseForm.coverImage = "";
    };

    // 表单提交
    const submitForm = async () => {
      if (!courseFormRef.value) return;

      await courseFormRef.value.validate(async (valid) => {
        if (!valid) return;

        try {
          loading.value = true;

          if (isEdit.value) {
            // 更新课程
            await updateCourse(courseForm.courseId, courseForm);
            ElMessage.success("更新成功");
          } else {
            // 新增课程
            await addCourse(courseForm);
            ElMessage.success("添加成功");
          }

          dialogVisible.value = false;
          fetchCourses();
        } catch (error) {
          console.error(
            isEdit.value ? "更新课程失败:" : "添加课程失败:",
            error
          );
          ElMessage.error(isEdit.value ? "更新课程失败" : "添加课程失败");
        } finally {
          loading.value = false;
        }
      });
    };

    // 上传前检查
    const beforeUpload = (file) => {
      const isImage = file.type.startsWith("image/");
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isImage) {
        ElMessage.error("上传封面只能是图片格式!");
        return false;
      }

      if (!isLt2M) {
        ElMessage.error("上传封面图片大小不能超过2MB!");
        return false;
      }

      return true;
    };

    // 上传成功回调
    const handleUploadSuccess = (response) => {
      if (response && (response.code === 200 || !response.code)) {
        const fileUrl = response.data || response;
        courseForm.coverImage = fileUrl;
        ElMessage.success("上传成功");
      } else {
        ElMessage.error("上传失败");
      }
    };

    // 生命周期钩子
    onMounted(() => {
      fetchCourses();
      fetchCourseTypes();
      fetchTeachers();
    });

    return {
      courses,
      courseTypes,
      teachers,
      loading,
      currentPage,
      pageSize,
      total,
      searchQuery,
      courseFormRef,
      courseForm,
      courseRules,
      dialogVisible,
      isEdit,
      Search,
      Plus,
      formatDate,
      fetchCourses,
      handleSearch,
      handleSizeChange,
      handleCurrentChange,
      handleAdd,
      handleEdit,
      handleDelete,
      resetForm,
      submitForm,
      beforeUpload,
      handleUploadSuccess,
    };
  },
};
</script>

<style scoped>
.course-management {
  padding: 1rem;
}

.action-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.cover-upload {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 178px;
  height: 178px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  overflow: hidden;
}

.cover-upload:hover {
  border-color: #409eff;
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.cover-image {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}
</style>
