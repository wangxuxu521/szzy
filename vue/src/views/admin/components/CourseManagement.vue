<template>
  <div class="course-management">
    <div class="header">
      <h2>课程管理</h2>
      <el-button type="primary" @click="openDialog()">添加课程</el-button>
    </div>

    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索课程名称"
        clearable
        @keyup.enter="handleSearch"
        style="width: 300px; margin-right: 10px"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-select
        v-model="searchTypeId"
        placeholder="选择类型"
        clearable
        style="width: 200px; margin-right: 10px"
      >
        <el-option
          v-for="item in typeOptions"
          :key="item.typeId"
          :label="item.typeName"
          :value="item.typeId"
        />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <el-table :data="courses" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="courseId" label="ID" width="80" />
      <el-table-column label="封面" width="120">
        <template #default="scope">
          <el-image
            style="width: 80px; height: 60px"
            :src="scope.row.coverUrl || '/default-cover.jpg'"
            fit="cover"
            :preview-src-list="[scope.row.coverUrl]"
            :initial-index="0"
            preview-teleported
          />
        </template>
      </el-table-column>
      <el-table-column
        prop="courseName"
        label="课程名称"
        show-overflow-tooltip
      />
      <el-table-column prop="typeName" label="类型" width="120" />
      <el-table-column prop="teacherName" label="教师" width="120" />
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" @click="openDialog(scope.row)"
            >编辑</el-button
          >
          <el-popconfirm
            title="确定删除该课程吗？"
            @confirm="handleDelete(scope.row.courseId)"
          >
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 添加/编辑课程弹窗 -->
    <el-dialog
      :title="form.courseId ? '编辑课程' : '添加课程'"
      v-model="dialogVisible"
      width="700px"
    >
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程类型" prop="typeId">
          <el-select
            v-model="form.typeId"
            placeholder="请选择课程类型"
            style="width: 100%"
          >
            <el-option
              v-for="item in typeOptions"
              :key="item.typeId"
              :label="item.typeName"
              :value="item.typeId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="教师" prop="teacherId">
          <el-select
            v-model="form.teacherId"
            placeholder="请选择教师"
            style="width: 100%"
          >
            <el-option
              v-for="item in teacherOptions"
              :key="item.userId"
              :label="item.username"
              :value="item.userId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="课程简介" prop="introduction">
          <el-input
            v-model="form.introduction"
            type="textarea"
            placeholder="请输入课程简介"
            :rows="4"
          />
        </el-form-item>
        <el-form-item label="封面图片">
          <el-upload
            class="cover-uploader"
            action="#"
            :http-request="uploadCover"
            :show-file-list="false"
            :before-upload="beforeUpload"
          >
            <img v-if="form.coverUrl" :src="form.coverUrl" class="cover" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">点击上传封面图片（建议尺寸：16:9）</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave" :loading="saveLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from "vue";
import { ElMessage } from "element-plus";
import { Search, Plus } from "@element-plus/icons-vue";
import { apiGet, apiPost, apiDelete } from "@/api/request";
import { formatDate } from "@/utils/format";
import { useRouter } from "vue-router";

const router = useRouter();

// 数据
const courses = ref([]);
const typeOptions = ref([]);
const teacherOptions = ref([]);
const loading = ref(false);
const dialogVisible = ref(false);
const saveLoading = ref(false);
const formRef = ref(null);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const searchKeyword = ref("");
const searchTypeId = ref("");

// 表单数据
const form = reactive({
  courseId: null,
  courseName: "",
  typeId: "",
  teacherId: "",
  price: 0,
  introduction: "",
  coverUrl: "",
  coverFile: null,
});

// 表单验证规则
const rules = {
  courseName: [
    { required: true, message: "请输入课程名称", trigger: "blur" },
    { min: 2, max: 100, message: "长度在 2 到 100 个字符", trigger: "blur" },
  ],
  typeId: [{ required: true, message: "请选择课程类型", trigger: "change" }],
  teacherId: [{ required: true, message: "请选择教师", trigger: "change" }],
  introduction: [{ max: 2000, message: "最多 2000 个字符", trigger: "blur" }],
};

// 生命周期
onMounted(() => {
  fetchCourseData();
  fetchTypeOptions();
  fetchTeacherOptions();
});

// 获取课程数据
const fetchCourseData = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value,
      typeId: searchTypeId.value || null,
    };

    const res = await apiGet("/courses/search", params);
    if (res.code === 200) {
      courses.value = res.data.list || [];
      total.value = res.data.total || 0;
    } else {
      ElMessage.error(res.msg || "获取课程列表失败");
    }
  } catch (error) {
    console.error("获取课程列表出错:", error);
    ElMessage.error("获取课程列表失败");
  } finally {
    loading.value = false;
  }
};

// 获取类型选项
const fetchTypeOptions = async () => {
  try {
    const res = await apiGet("/types");
    if (res.code === 200) {
      typeOptions.value = res.data || [];
    }
  } catch (error) {
    console.error("获取类型选项出错:", error);
  }
};

// 获取教师选项
const fetchTeacherOptions = async () => {
  try {
    const res = await apiGet("/user/teachers");
    if (res.code === 200) {
      teacherOptions.value = res.data || [];
    }
  } catch (error) {
    console.error("获取教师选项出错:", error);
  }
};

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
  fetchCourseData();
};

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = "";
  searchTypeId.value = "";
  currentPage.value = 1;
  fetchCourseData();
};

// 分页
const handleSizeChange = (size) => {
  pageSize.value = size;
  fetchCourseData();
};

const handleCurrentChange = (page) => {
  currentPage.value = page;
  fetchCourseData();
};

// 打开弹窗
const openDialog = (row) => {
  formRef.value?.resetFields();

  if (row) {
    // 编辑模式，填充表单
    Object.keys(form).forEach((key) => {
      if (key !== "coverFile") {
        form[key] = row[key];
      }
    });
  } else {
    // 添加模式，重置表单
    Object.keys(form).forEach((key) => {
      form[key] = key === "courseId" ? null : key === "price" ? 0 : "";
    });
  }

  dialogVisible.value = true;
};

// 文件上传前验证
const beforeUpload = (file) => {
  const isImage = file.type.indexOf("image/") === 0;
  const isLt2M = file.size / 1024 / 1024 < 5;

  if (!isImage) {
    ElMessage.error("只能上传图片文件!");
    return false;
  }
  if (!isLt2M) {
    ElMessage.error("图片大小不能超过 5MB!");
    return false;
  }
  return true;
};

// 上传封面
const uploadCover = async (params) => {
  form.coverFile = params.file;

  // 预览
  const reader = new FileReader();
  reader.onload = (e) => {
    form.coverUrl = e.target.result;
  };
  reader.readAsDataURL(params.file);
};

// 保存课程
const handleSave = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (!valid) return;

    saveLoading.value = true;
    try {
      // 创建FormData对象
      const formData = new FormData();
      Object.keys(form).forEach((key) => {
        if (key === "coverFile" && form[key]) {
          formData.append("file", form[key]);
        } else if (key !== "coverFile" && key !== "coverUrl") {
          if (form[key] !== null && form[key] !== undefined) {
            formData.append(key, form[key]);
          }
        }
      });

      const res = await apiPost("/courses", formData);
      if (res.code === 200) {
        ElMessage.success(form.courseId ? "更新成功" : "添加成功");
        dialogVisible.value = false;
        fetchCourseData();
      } else {
        ElMessage.error(res.msg || (form.courseId ? "更新失败" : "添加失败"));
      }
    } catch (error) {
      console.error("保存课程出错:", error);
      ElMessage.error(form.courseId ? "更新失败" : "添加失败");
    } finally {
      saveLoading.value = false;
    }
  });
};

// 删除课程
const handleDelete = async (id) => {
  try {
    const res = await apiDelete(`/courses/${id}`);
    if (res.code === 200) {
      ElMessage.success("删除成功");
      // 如果当前页没有数据了，且不是第一页，则返回上一页
      if (courses.value.length === 1 && currentPage.value > 1) {
        currentPage.value--;
      }
      fetchCourseData();
    } else {
      ElMessage.error(res.msg || "删除失败");
    }
  } catch (error) {
    console.error("删除课程出错:", error);
    ElMessage.error("删除失败");
  }
};
</script>

<style scoped>
.course-management {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-bar {
  display: flex;
  margin-bottom: 20px;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.cover-uploader {
  width: 178px;
  height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.cover-uploader:hover {
  border-color: #409eff;
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.cover {
  width: 178px;
  height: 100px;
  display: block;
  object-fit: cover;
}

.upload-tip {
  font-size: 12px;
  color: #606266;
  margin-top: 5px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
