<template>
  <div class="upload-form-container">
    <el-form
      ref="uploadFormRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="loading"
    >
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="formData.title"
          placeholder="请输入资源标题"
        ></el-input>
      </el-form-item>

      <el-form-item label="资源类型" prop="typeId">
        <el-select
          v-model="formData.typeId"
          placeholder="请选择资源类型"
          style="width: 100%"
          filterable
        >
          <el-option-group label="类型列表">
            <el-option
              v-for="type in resourceTypes"
              :key="type.typeId"
              :label="type.typeName"
              :value="type.typeId"
            ></el-option>
          </el-option-group>
        </el-select>
      </el-form-item>

      <el-form-item label="描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          rows="3"
          placeholder="请输入资源描述"
        ></el-input>
      </el-form-item>

      <el-form-item label="标签" prop="tags">
        <el-select
          v-model="formData.tags"
          multiple
          filterable
          allow-create
          default-first-option
          placeholder="请选择或输入标签"
          style="width: 100%"
        >
          <el-option-group label="主题标签">
            <el-option
              v-for="tag in themeTagOptions"
              :key="tag.value"
              :label="tag.label"
              :value="tag.value"
            >
              <span>{{ tag.label }}</span>
              <el-tag size="small" type="success">主题</el-tag>
            </el-option>
          </el-option-group>
          <el-option-group label="学科标签">
            <el-option
              v-for="tag in subjectTagOptions"
              :key="tag.value"
              :label="tag.label"
              :value="tag.value"
            >
              <span>{{ tag.label }}</span>
              <el-tag size="small" type="primary">学科</el-tag>
            </el-option>
          </el-option-group>
          <el-option-group label="格式标签">
            <el-option
              v-for="tag in formatTagOptions"
              :key="tag.value"
              :label="tag.label"
              :value="tag.value"
            >
              <span>{{ tag.label }}</span>
              <el-tag size="small" type="info">格式</el-tag>
            </el-option>
          </el-option-group>
        </el-select>
        <div class="form-tip">多个标签可以更好地帮助其他用户找到您的资源</div>
      </el-form-item>

      <el-form-item label="资源文件" prop="file">
        <el-upload
          class="upload-file"
          :auto-upload="false"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          :before-remove="confirmRemoveFile"
          :limit="1"
          :file-list="fileList"
          :on-exceed="handleExceed"
        >
          <template #trigger>
            <el-button type="primary">选择文件</el-button>
          </template>
          <template #tip>
            <div class="el-upload__tip">
              支持PDF、Word、PPT、Excel等格式，文件大小不超过{{ maxFileSize }}MB
              <div v-if="fileInfo.size" class="file-info">
                <span class="file-size"
                  >文件大小: {{ formatFileSize(fileInfo.size) }}</span
                >
                <span class="file-format">格式: {{ fileInfo.format }}</span>
              </div>
            </div>
          </template>
        </el-upload>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitForm" :loading="submitting">
          {{ submitButtonText }}
        </el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getTagList } from "@/api/tag";
import { getAllTypes } from "@/api/type";
import { getSystemConfig } from "@/api/system";

export default {
  name: "ResourceUploadForm",
  props: {
    initialData: {
      type: Object,
      default: () => ({}),
    },
    isEdit: {
      type: Boolean,
      default: false,
    },
    loading: {
      type: Boolean,
      default: false,
    },
  },
  emits: ["submit", "cancel"],
  setup(props, { emit }) {
    // 表单数据
    const formData = reactive({
      title: props.initialData.title || "",
      typeId: props.initialData.typeId || null,
      type: props.initialData.type || "",
      description: props.initialData.description || "",
      tags: Array.isArray(props.initialData.tags)
        ? props.initialData.tags
        : parseTagsString(props.initialData.tags),
      file: null,
    });

    // 文件列表
    const fileList = ref([]);

    // 文件信息
    const fileInfo = reactive({
      size: null,
      format: null,
    });

    // 最大文件大小(MB)
    const maxFileSize = ref(20);

    // 允许的文件格式
    const allowedFileTypes = ref([]);

    // 初始化文件列表（如果是编辑模式）
    if (props.isEdit && props.initialData.fileName) {
      fileList.value.push({
        name: props.initialData.fileName,
        url: props.initialData.filePath,
      });

      fileInfo.size = props.initialData.fileSize;
      fileInfo.format =
        props.initialData.format ||
        (props.initialData.fileName
          ? props.initialData.fileName.split(".").pop()
          : "");
    }

    // 表单验证规则
    const formRules = {
      title: [
        { required: true, message: "请输入资源标题", trigger: "blur" },
        {
          min: 2,
          max: 100,
          message: "标题长度在2-100个字符之间",
          trigger: "blur",
        },
      ],
      typeId: [
        { required: true, message: "请选择资源类型", trigger: "change" },
      ],
      file: [
        {
          required: !props.isEdit,
          message: "请上传资源文件",
          trigger: "change",
          validator: (rule, value, callback) => {
            if (props.isEdit && !formData.file && fileList.value.length > 0) {
              // 编辑模式下，如果没有选择新文件但有原始文件，则通过验证
              callback();
            } else if (!formData.file) {
              callback(new Error("请上传资源文件"));
            } else {
              // 验证文件大小
              const maxSize = maxFileSize.value * 1024 * 1024; // 转换为字节
              if (formData.file.size > maxSize) {
                callback(new Error(`文件大小不能超过${maxFileSize.value}MB`));
              } else if (!validateFileType(formData.file.name)) {
                callback(new Error("不支持的文件格式"));
              } else {
                callback();
              }
            }
          },
        },
      ],
    };

    // 提交按钮文字
    const submitButtonText = computed(() => {
      return props.isEdit ? "保存修改" : "上传资源";
    });

    // 资源类型列表
    const resourceTypes = ref([]);

    // 可用标签列表
    const availableTags = ref([]);

    // 按标签类型分组
    const themeTagOptions = computed(() => {
      return availableTags.value
        .filter((tag) => tag.tagType === "theme")
        .map((tag) => ({
          label: tag.tagName,
          value: tag.tagName,
        }));
    });

    const subjectTagOptions = computed(() => {
      return availableTags.value
        .filter((tag) => tag.tagType === "subject")
        .map((tag) => ({
          label: tag.tagName,
          value: tag.tagName,
        }));
    });

    const formatTagOptions = computed(() => {
      return availableTags.value
        .filter((tag) => tag.tagType === "format")
        .map((tag) => ({
          label: tag.tagName,
          value: tag.tagName,
        }));
    });

    // 提交状态
    const submitting = ref(false);

    // 表单引用
    const uploadFormRef = ref(null);

    // 解析标签字符串
    function parseTagsString(tagsStr) {
      if (!tagsStr) return [];

      try {
        if (typeof tagsStr === "string") {
          // 尝试解析JSON字符串
          if (tagsStr.startsWith("[") && tagsStr.endsWith("]")) {
            return JSON.parse(tagsStr);
          }
          // 如果不是JSON格式，按逗号分隔
          return tagsStr
            .split(",")
            .map((tag) => tag.trim())
            .filter(Boolean);
        }
      } catch (e) {
        console.error("解析标签失败:", e);
      }

      return [];
    }

    // 加载系统配置
    const loadSystemConfig = async () => {
      try {
        const response = await getSystemConfig();
        if (response && response.code === 200 && response.data) {
          const configs = response.data;

          // 获取文件大小限制
          const sizeLimit = configs.find(
            (c) => c.configKey === "upload_size_limit"
          );
          if (sizeLimit && sizeLimit.configValue) {
            maxFileSize.value = parseInt(sizeLimit.configValue) || 20;
          }

          // 获取允许的文件类型
          const fileTypes = configs.find(
            (c) => c.configKey === "allowed_file_types"
          );
          if (fileTypes && fileTypes.configValue) {
            allowedFileTypes.value = fileTypes.configValue
              .split(",")
              .map((t) => t.trim());
          } else {
            allowedFileTypes.value = [
              "pdf",
              "doc",
              "docx",
              "ppt",
              "pptx",
              "xls",
              "xlsx",
              "txt",
              "jpg",
              "jpeg",
              "png",
              "gif",
              "mp4",
              "mp3",
              "zip",
              "rar",
            ];
          }
        }
      } catch (error) {
        console.error("获取系统配置失败:", error);
      }
    };

    // 验证文件类型
    const validateFileType = (fileName) => {
      if (!fileName) return false;

      const extension = fileName.split(".").pop().toLowerCase();
      if (!extension) return false;

      return allowedFileTypes.value.includes(extension);
    };

    // 格式化文件大小
    const formatFileSize = (size) => {
      if (!size) return "未知大小";

      if (size < 1024) {
        return size + " B";
      } else if (size < 1024 * 1024) {
        return (size / 1024).toFixed(2) + " KB";
      } else if (size < 1024 * 1024 * 1024) {
        return (size / (1024 * 1024)).toFixed(2) + " MB";
      } else {
        return (size / (1024 * 1024 * 1024)).toFixed(2) + " GB";
      }
    };

    // 加载资源类型
    const loadResourceTypes = async () => {
      try {
        const response = await getAllTypes();

        // 处理响应
        if (response && typeof response === "object") {
          if (Array.isArray(response)) {
            resourceTypes.value = response;
          } else if (response.data && Array.isArray(response.data)) {
            resourceTypes.value = response.data;
          }
        }

        // 如果没有获取到类型数据或数据为空，显示错误
        if (!resourceTypes.value || resourceTypes.value.length === 0) {
          console.error("未获取到类型数据");
          ElMessage.warning("获取资源类型失败，请刷新页面重试");
        }
      } catch (error) {
        console.error("获取资源类型失败:", error);
        ElMessage.warning("获取资源类型失败，请刷新页面重试");
      }
    };

    // 加载标签数据
    const loadTags = async () => {
      try {
        const response = await getTagList();

        if (response && typeof response === "object") {
          if (Array.isArray(response)) {
            availableTags.value = response;
          } else if (response.data && Array.isArray(response.data)) {
            availableTags.value = response.data;
          }
        }
      } catch (error) {
        console.error("获取标签列表失败:", error);
        // 默认标签
        availableTags.value = [
          { tagId: 1, tagName: "爱国主义", tagType: "theme" },
          { tagId: 2, tagName: "工科", tagType: "subject" },
          { tagId: 3, tagName: "PDF", tagType: "format" },
          { tagId: 4, tagName: "爱国教育", tagType: "subject" },
        ];
      }
    };

    // 处理文件超出限制
    const handleExceed = () => {
      ElMessage.warning("只能上传一个文件");
    };

    // 确认移除文件
    const confirmRemoveFile = (file) => {
      return ElMessageBox.confirm(`确认移除 ${file.name}？`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => true)
        .catch(() => false);
    };

    // 处理文件改变
    const handleFileChange = (file) => {
      formData.file = file.raw;
      fileInfo.size = file.raw.size;
      fileInfo.format = file.raw.name.split(".").pop().toLowerCase();

      // 验证文件大小
      const maxSize = maxFileSize.value * 1024 * 1024; // 转换为字节
      if (file.raw.size > maxSize) {
        ElMessage.error(`文件大小不能超过${maxFileSize.value}MB`);
        handleFileRemove();
        return false;
      }

      // 验证文件类型
      if (!validateFileType(file.raw.name)) {
        ElMessage.error(`不支持的文件格式: ${fileInfo.format}`);
        handleFileRemove();
        return false;
      }

      return true;
    };

    // 处理文件移除
    const handleFileRemove = () => {
      formData.file = null;
      fileInfo.size = null;
      fileInfo.format = null;
      fileList.value = [];
    };

    // 提交表单
    const submitForm = async () => {
      if (!uploadFormRef.value) return;

      uploadFormRef.value.validate(async (valid) => {
        if (!valid) {
          ElMessage.warning("请填写完整表单信息");
          return;
        }

        submitting.value = true;

        try {
          const formDataToSubmit = new FormData();
          formDataToSubmit.append("title", formData.title);

          // 添加typeId字段
          if (formData.typeId) {
            formDataToSubmit.append("typeId", formData.typeId);
          }

          // 同时保留type字段向后兼容
          const selectedType = resourceTypes.value.find(
            (t) => t.typeId === formData.typeId
          );
          if (selectedType) {
            formDataToSubmit.append("type", selectedType.typeName);
          }

          if (formData.description) {
            formDataToSubmit.append("description", formData.description);
          }

          // 处理标签 - 转换为JSON字符串
          if (formData.tags && formData.tags.length > 0) {
            formDataToSubmit.append("tags", JSON.stringify(formData.tags));
          }

          // 只有在选择了文件时才添加文件
          if (formData.file) {
            formDataToSubmit.append("file", formData.file);
          }

          // 添加资源ID（如果是编辑模式）
          if (props.isEdit && props.initialData.resourceId) {
            formDataToSubmit.append("resourceId", props.initialData.resourceId);
          }

          // 发送表单数据给父组件处理
          emit("submit", formDataToSubmit);
        } catch (error) {
          console.error("准备表单数据失败:", error);
          ElMessage.error("提交表单失败，请重试");
        } finally {
          submitting.value = false;
        }
      });
    };

    // 重置表单
    const resetForm = () => {
      if (uploadFormRef.value) {
        uploadFormRef.value.resetFields();
      }

      formData.title = "";
      formData.typeId = null;
      formData.type = "";
      formData.description = "";
      formData.tags = [];
      formData.file = null;

      fileList.value = [];
      fileInfo.size = null;
      fileInfo.format = null;
    };

    // 组件挂载时加载数据
    onMounted(() => {
      loadSystemConfig();
      loadResourceTypes();
      loadTags();
    });

    return {
      formData,
      formRules,
      fileList,
      fileInfo,
      resourceTypes,
      availableTags,
      themeTagOptions,
      subjectTagOptions,
      formatTagOptions,
      maxFileSize,
      uploadFormRef,
      submitting,
      submitButtonText,
      handleFileChange,
      handleFileRemove,
      handleExceed,
      confirmRemoveFile,
      submitForm,
      resetForm,
      formatFileSize,
      validateFileType,
    };
  },
};
</script>

<style scoped>
.upload-form-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.upload-file {
  width: 100%;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.el-upload__tip {
  line-height: 1.4;
  margin-top: 8px;
}

.file-info {
  margin-top: 5px;
  font-size: 12px;
  display: flex;
  gap: 10px;
}

.file-size,
.file-format {
  background-color: #f0f9eb;
  color: #67c23a;
  padding: 2px 5px;
  border-radius: 3px;
}

.file-format {
  background-color: #f4f4f5;
  color: #909399;
}
</style>
