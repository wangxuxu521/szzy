<template>
  <div class="course-management">
    <div class="page-header">
      <h1>课程管理</h1>
      <el-button type="primary" @click="openCourseDialog()">添加课程</el-button>
    </div>

    <!-- 课程列表 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>
    <div v-else-if="courses.length === 0" class="empty-container">
      <el-empty description="暂无课程" />
      <el-button type="primary" @click="openCourseDialog()"
        >添加第一个课程</el-button
      >
    </div>
    <div v-else class="course-list">
      <el-card
        v-for="course in courses"
        :key="course.courseId"
        class="course-card"
      >
        <div class="course-header">
          <div class="course-title">
            <h3>{{ course.courseName }}</h3>
            <el-tag v-if="course.typeName">{{ course.typeName }}</el-tag>
          </div>
          <div class="course-actions">
            <el-button
              type="primary"
              size="small"
              @click="openCourseDialog(course)"
              >编辑</el-button
            >
            <el-button type="danger" size="small" @click="confirmDelete(course)"
              >删除</el-button
            >
          </div>
        </div>
        <div class="course-content">
          <p v-if="course.description">{{ course.description }}</p>
          <p v-else class="empty-desc">暂无课程描述</p>
        </div>
      </el-card>
    </div>

    <!-- 课程表单对话框 -->
    <el-dialog
      v-model="courseDialogVisible"
      :title="isEdit ? '编辑课程' : '添加课程'"
      width="600px"
    >
      <el-form
        ref="courseFormRef"
        :model="courseForm"
        :rules="courseRules"
        label-width="100px"
      >
        <el-form-item label="课程名称" prop="courseName">
          <el-input
            v-model="courseForm.courseName"
            placeholder="请输入课程名称"
          />
        </el-form-item>
        <el-form-item label="课程类型" prop="typeId">
          <el-select
            v-model="courseForm.typeId"
            placeholder="请选择课程类型"
            style="width: 100%"
          >
            <el-option
              v-for="type in courseTypes"
              :key="type.typeId"
              :label="type.typeName"
              :value="type.typeId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="courseForm.description"
            type="textarea"
            rows="5"
            placeholder="请输入课程描述"
          />
        </el-form-item>
        <el-form-item label="封面图片">
          <el-upload
            class="cover-uploader"
            action="#"
            :auto-upload="false"
            :on-change="handleCoverChange"
            :limit="1"
            :file-list="coverList"
            :show-file-list="true"
            list-type="picture-card"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">支持jpg、png格式，大小不超过2MB</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="courseDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveCourse" :loading="saving"
            >保存</el-button
          >
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import {
  getTeacherCourses,
  createCourse,
  updateCourse,
  deleteCourse,
} from "@/api/teacher";

export default {
  name: "CourseManagement",
  components: {
    Plus,
  },
  setup() {
    // 数据
    const courses = ref([]);
    const loading = ref(false);
    const courseTypes = ref([]);

    // 获取课程类型列表
    const fetchCourseTypes = async () => {
      try {
        // 使用类型控制器获取类型列表
        const response = await fetch("/api/types");
        const data = await response.json();

        if (data.code === 200 && data.data) {
          courseTypes.value = data.data;
        } else {
          // 使用默认值
          courseTypes.value = [
            { typeId: 1, typeName: "计算机" },
            { typeId: 2, typeName: "通信" },
            { typeId: 3, typeName: "人工智能" },
          ];
        }
      } catch (error) {
        console.error("获取课程类型失败:", error);
        // 使用默认值
        courseTypes.value = [
          { typeId: 1, typeName: "计算机" },
          { typeId: 2, typeName: "通信" },
          { typeId: 3, typeName: "人工智能" },
        ];
      }
    };

    // 课程表单
    const courseDialogVisible = ref(false);
    const isEdit = ref(false);
    const courseForm = ref({
      courseId: null,
      courseName: "",
      typeId: "",
      description: "",
      coverImage: "",
    });
    const courseRules = {
      courseName: [
        { required: true, message: "请输入课程名称", trigger: "blur" },
      ],
      typeId: [
        { required: true, message: "请选择课程类型", trigger: "change" },
      ],
    };
    const courseFormRef = ref(null);
    const coverList = ref([]);
    const saving = ref(false);

    // 获取课程列表
    const fetchCourses = async () => {
      loading.value = true;
      try {
        const res = await getTeacherCourses();
        if (res.code === 200) {
          courses.value = res.data || [];
        } else {
          ElMessage.error(res.message || "获取课程列表失败");
        }
      } catch (error) {
        console.error("获取课程失败:", error);
        ElMessage.error("获取课程列表失败，请稍后重试");
      } finally {
        loading.value = false;
      }
    };

    // 打开课程表单对话框
    const openCourseDialog = (course = null) => {
      isEdit.value = !!course;
      if (course) {
        courseForm.value = {
          courseId: course.courseId,
          courseName: course.courseName,
          typeId: course.typeId,
          description: course.description || "",
          coverImage: course.coverImage || "",
        };

        // 如果有封面图片，显示预览
        if (course.coverImage) {
          coverList.value = [
            {
              name: "封面图片",
              url: course.coverImage,
            },
          ];
        } else {
          coverList.value = [];
        }
      } else {
        courseForm.value = {
          courseId: null,
          courseName: "",
          typeId: "",
          description: "",
          coverImage: "",
        };
        coverList.value = [];
      }
      courseDialogVisible.value = true;
    };

    // 处理封面图片变更
    const handleCoverChange = (file) => {
      const isImage =
        file.raw.type === "image/jpeg" || file.raw.type === "image/png";
      const isLt2M = file.raw.size / 1024 / 1024 < 2;

      if (!isImage) {
        ElMessage.error("封面图片只能是 JPG 或 PNG 格式!");
        coverList.value = [];
        return false;
      }

      if (!isLt2M) {
        ElMessage.error("封面图片大小不能超过 2MB!");
        coverList.value = [];
        return false;
      }

      // 暂存文件
      courseForm.value.coverFile = file.raw;
      return true;
    };

    // 保存课程
    const saveCourse = async () => {
      // 表单验证
      if (!courseFormRef.value) return;

      await courseFormRef.value.validate(async (valid) => {
        if (!valid) return;

        saving.value = true;

        try {
          const formData = new FormData();
          formData.append("courseName", courseForm.value.courseName);
          formData.append("typeId", courseForm.value.typeId);
          formData.append("description", courseForm.value.description || "");

          if (courseForm.value.coverFile) {
            formData.append("coverImage", courseForm.value.coverFile);
          }

          let res;
          if (isEdit.value) {
            formData.append("courseId", courseForm.value.courseId);
            res = await updateCourse(courseForm.value.courseId, formData);
          } else {
            res = await createCourse(formData);
          }

          if (res.code === 200) {
            ElMessage.success(isEdit.value ? "课程更新成功" : "课程添加成功");
            courseDialogVisible.value = false;
            fetchCourses();
          } else {
            ElMessage.error(
              res.message || (isEdit.value ? "更新失败" : "添加失败")
            );
          }
        } catch (error) {
          console.error("保存课程失败:", error);
          ElMessage.error("操作失败，请稍后重试");
        } finally {
          saving.value = false;
        }
      });
    };

    // 确认删除
    const confirmDelete = (course) => {
      ElMessageBox.confirm(
        `确定要删除课程"${course.courseName}"吗？`,
        "删除确认",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(async () => {
          try {
            const res = await deleteCourse(course.courseId);
            if (res.code === 200) {
              ElMessage.success("课程删除成功");
              fetchCourses();
            } else {
              ElMessage.error(res.message || "删除失败");
            }
          } catch (error) {
            console.error("删除课程失败:", error);
            ElMessage.error("删除失败，请稍后重试");
          }
        })
        .catch(() => {
          ElMessage.info("已取消删除");
        });
    };

    onMounted(() => {
      fetchCourseTypes();
      fetchCourses();
    });

    return {
      courses,
      loading,
      courseTypes,
      courseDialogVisible,
      isEdit,
      courseForm,
      courseRules,
      courseFormRef,
      coverList,
      saving,
      openCourseDialog,
      handleCoverChange,
      saveCourse,
      confirmDelete,
    };
  },
};
</script>

<style scoped>
.course-management {
  padding: 1rem 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-header h1 {
  margin: 0;
  font-size: 1.8rem;
  color: #333;
}

.loading-container {
  padding: 2rem;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.empty-container {
  text-align: center;
  padding: 3rem;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.empty-container .el-button {
  margin-top: 1rem;
}

.course-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  grid-gap: 1.5rem;
}

.course-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.course-title {
  flex: 1;
}

.course-title h3 {
  margin: 0 0 0.5rem;
  font-size: 1.2rem;
}

.course-actions {
  display: flex;
  gap: 0.5rem;
}

.course-content {
  flex: 1;
}

.empty-desc {
  color: #999;
  font-style: italic;
}

.cover-uploader {
  display: flex;
  justify-content: center;
}

.cover-uploader .el-upload {
  width: 100%;
  max-width: 200px;
}
</style>
