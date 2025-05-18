<template>
  <div class="system-settings">
    <h2>系统设置</h2>

    <el-tabs type="border-card">
      <!-- 网站基本设置 -->
      <el-tab-pane label="网站设置">
        <el-card class="settings-card">
          <el-form :model="websiteSettings" label-width="120px" size="default">
            <el-form-item label="网站标题">
              <el-input
                v-model="websiteSettings.site_title"
                placeholder="网站标题"
              ></el-input>
            </el-form-item>

            <el-form-item label="网站描述">
              <el-input
                v-model="websiteSettings.site_description"
                type="textarea"
                :rows="2"
                placeholder="网站描述信息"
              ></el-input>
            </el-form-item>

            <el-form-item label="系统公告">
              <el-input
                v-model="websiteSettings.system_announcement"
                type="textarea"
                :rows="3"
                placeholder="系统公告信息"
              ></el-input>
            </el-form-item>

            <el-form-item label="联系邮箱">
              <el-input
                v-model="websiteSettings.contact_email"
                placeholder="管理员联系邮箱"
              ></el-input>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="saveWebsiteSettings"
                >保存设置</el-button
              >
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 功能设置 -->
      <el-tab-pane label="功能设置">
        <el-card class="settings-card">
          <el-form :model="functionSettings" label-width="120px">
            <el-form-item label="开放注册">
              <el-switch
                v-model="functionSettings.register_open"
                active-text="开启"
                inactive-text="关闭"
                :active-value="'true'"
                :inactive-value="'false'"
              />
            </el-form-item>

            <el-form-item label="需要审核资源">
              <el-switch
                v-model="functionSettings.review_required"
                active-text="开启"
                inactive-text="关闭"
                :active-value="'true'"
                :inactive-value="'false'"
              />
            </el-form-item>

            <el-form-item label="启用评论功能">
              <el-switch
                v-model="functionSettings.enable_comments"
                active-text="开启"
                inactive-text="关闭"
                :active-value="'true'"
                :inactive-value="'false'"
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="saveFunctionSettings"
                >保存设置</el-button
              >
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 上传设置 -->
      <el-tab-pane label="上传设置">
        <el-card class="settings-card">
          <el-form :model="uploadSettings" label-width="120px">
            <el-form-item label="上传大小限制">
              <el-input-number
                v-model="uploadSettings.upload_size_limit"
                :min="1"
                :max="100"
                :step="1"
              ></el-input-number>
              <span class="input-suffix">MB</span>
            </el-form-item>

            <el-form-item label="允许的文件类型">
              <el-select
                v-model="uploadFileTypes"
                multiple
                collapse-tags
                collapse-tags-tooltip
                placeholder="请选择允许的文件类型"
                style="width: 400px"
              >
                <el-option-group label="文档类型">
                  <el-option value="pdf" label="PDF文档 (.pdf)" />
                  <el-option value="doc" label="Word文档 (.doc)" />
                  <el-option value="docx" label="Word文档 (.docx)" />
                  <el-option value="ppt" label="PowerPoint (.ppt)" />
                  <el-option value="pptx" label="PowerPoint (.pptx)" />
                  <el-option value="xls" label="Excel表格 (.xls)" />
                  <el-option value="xlsx" label="Excel表格 (.xlsx)" />
                  <el-option value="txt" label="文本文件 (.txt)" />
                </el-option-group>
                <el-option-group label="媒体类型">
                  <el-option value="jpg" label="JPG图片 (.jpg)" />
                  <el-option value="jpeg" label="JPEG图片 (.jpeg)" />
                  <el-option value="png" label="PNG图片 (.png)" />
                  <el-option value="gif" label="GIF图片 (.gif)" />
                  <el-option value="mp4" label="MP4视频 (.mp4)" />
                  <el-option value="mp3" label="MP3音频 (.mp3)" />
                </el-option-group>
                <el-option-group label="压缩类型">
                  <el-option value="zip" label="ZIP压缩包 (.zip)" />
                  <el-option value="rar" label="RAR压缩包 (.rar)" />
                </el-option-group>
              </el-select>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="saveUploadSettings"
                >保存设置</el-button
              >
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 高级设置 -->
      <el-tab-pane label="高级设置">
        <el-card class="settings-card">
          <template #header>
            <div class="card-header">
              <span>所有系统配置</span>
              <el-button
                v-if="hasAdminRole"
                type="primary"
                @click="addConfigDialogVisible = true"
              >
                添加配置
              </el-button>
            </div>
          </template>

          <el-table
            :data="configList"
            stripe
            border
            style="width: 100%"
            row-key="id"
          >
            <el-table-column prop="configKey" label="配置键" width="180" />
            <el-table-column prop="configValue" label="配置值" />
            <el-table-column prop="description" label="描述" />
            <el-table-column label="操作" width="150" v-if="hasAdminRole">
              <template #default="scope">
                <el-button size="small" @click="handleEdit(scope.row)">
                  编辑
                </el-button>
                <el-button
                  size="small"
                  type="danger"
                  @click="handleDelete(scope.row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 编辑配置对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑配置" width="500px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="配置键">
          <el-input v-model="editForm.configKey" disabled />
        </el-form-item>
        <el-form-item label="配置值">
          <component
            :is="getComponentForConfigKey(editForm.configKey)"
            v-model="editForm.configValue"
            :config-key="editForm.configKey"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editForm.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveEdit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加配置对话框 -->
    <el-dialog v-model="addConfigDialogVisible" title="添加配置" width="500px">
      <el-form
        :model="addForm"
        label-width="100px"
        :rules="rules"
        ref="addFormRef"
      >
        <el-form-item label="配置键" prop="configKey">
          <el-select
            v-model="addForm.configKey"
            placeholder="选择预设配置键"
            filterable
            allow-create
          >
            <el-option-group label="网站设置">
              <el-option value="site_title" label="网站标题" />
              <el-option value="site_description" label="网站描述" />
              <el-option value="system_announcement" label="系统公告" />
              <el-option value="contact_email" label="联系邮箱" />
            </el-option-group>
            <el-option-group label="功能设置">
              <el-option value="register_open" label="开放注册" />
              <el-option value="review_required" label="需要审核资源" />
              <el-option value="enable_comments" label="启用评论功能" />
            </el-option-group>
            <el-option-group label="上传设置">
              <el-option value="upload_size_limit" label="上传大小限制(MB)" />
              <el-option value="allowed_file_types" label="允许的文件类型" />
            </el-option-group>
          </el-select>
        </el-form-item>
        <el-form-item label="配置值" prop="configValue">
          <component
            :is="getComponentForConfigKey(addForm.configKey)"
            v-model="addForm.configValue"
            :config-key="addForm.configKey"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="addForm.description"
            placeholder="请输入配置描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addConfigDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveNewConfig">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 删除确认对话框 -->
    <el-dialog v-model="deleteDialogVisible" title="删除确认" width="400px">
      <p>
        确定要删除配置
        <strong>{{ deleteForm.configKey }}</strong> 吗？此操作不可恢复！
      </p>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDelete">确定删除</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useStore } from "vuex";
import { systemApi } from "@/api";

export default {
  name: "SystemSettings",
  setup() {
    const store = useStore();
    const configList = ref([]);
    const editDialogVisible = ref(false);
    const addConfigDialogVisible = ref(false);
    const deleteDialogVisible = ref(false);
    const addFormRef = ref(null);

    // 按类别分组的设置
    const websiteSettings = reactive({
      site_title: "",
      site_description: "",
      system_announcement: "",
      contact_email: "",
    });

    const functionSettings = reactive({
      register_open: "true",
      review_required: "true",
      enable_comments: "true",
    });

    const uploadSettings = reactive({
      upload_size_limit: 20,
    });

    const uploadFileTypes = ref([]);

    const editForm = reactive({
      id: null,
      configKey: "",
      configValue: "",
      description: "",
    });

    const addForm = reactive({
      configKey: "",
      configValue: "",
      description: "",
    });

    const deleteForm = reactive({
      id: null,
      configKey: "",
    });

    const rules = {
      configKey: [
        { required: true, message: "请输入配置键", trigger: "blur" },
        { min: 2, max: 50, message: "长度在 2 到 50 个字符", trigger: "blur" },
      ],
      configValue: [
        { required: true, message: "请输入配置值", trigger: "blur" },
      ],
    };

    const hasAdminRole = computed(() => {
      return store.getters["user/userRole"] === "admin";
    });

    // 根据配置键返回合适的配置组件
    const getComponentForConfigKey = (configKey) => {
      const booleanConfigs = [
        "register_open",
        "review_required",
        "enable_comments",
      ];
      const numberConfigs = ["upload_size_limit"];
      const multilineConfigs = ["site_description", "system_announcement"];

      if (booleanConfigs.includes(configKey)) {
        return "el-switch";
      } else if (numberConfigs.includes(configKey)) {
        return "el-input-number";
      } else if (multilineConfigs.includes(configKey)) {
        return "el-input-textarea";
      } else {
        return "el-input";
      }
    };

    const fetchSettings = async () => {
      try {
        const res = await systemApi.getSystemConfigs();
        if (res.code === 200) {
          configList.value = res.data || [];

          // 填充分组设置
          configList.value.forEach((config) => {
            if (config.configKey in websiteSettings) {
              websiteSettings[config.configKey] = config.configValue;
            } else if (config.configKey in functionSettings) {
              functionSettings[config.configKey] = config.configValue;
            } else if (config.configKey === "upload_size_limit") {
              uploadSettings.upload_size_limit =
                parseInt(config.configValue) || 20;
            } else if (config.configKey === "allowed_file_types") {
              uploadFileTypes.value = config.configValue
                ? config.configValue.split(",")
                : [];
            }
          });
        }
      } catch (error) {
        console.error("获取系统设置失败", error);
        ElMessage.error("获取系统设置失败");
      }
    };

    const handleEdit = (row) => {
      editForm.id = row.id;
      editForm.configKey = row.configKey;
      editForm.configValue = row.configValue;
      editForm.description = row.description;
      editDialogVisible.value = true;
    };

    const saveEdit = async () => {
      try {
        const res = await systemApi.updateSystemConfig(editForm);
        if (res.code === 200) {
          ElMessage.success("更新成功");
          editDialogVisible.value = false;
          fetchSettings();
        } else {
          ElMessage.error(res.message || "更新失败");
        }
      } catch (error) {
        console.error("更新配置失败", error);
        ElMessage.error("更新配置失败");
      }
    };

    const saveNewConfig = async () => {
      try {
        const res = await systemApi.saveSystemConfig(addForm);
        if (res.code === 200) {
          ElMessage.success("添加成功");
          addConfigDialogVisible.value = false;
          // 重置表单
          addForm.configKey = "";
          addForm.configValue = "";
          addForm.description = "";
          fetchSettings();
        } else {
          ElMessage.error(res.message || "添加失败");
        }
      } catch (error) {
        console.error("添加配置失败", error);
        ElMessage.error("添加配置失败");
      }
    };

    const handleDelete = (row) => {
      deleteForm.id = row.id;
      deleteForm.configKey = row.configKey;
      deleteDialogVisible.value = true;
    };

    const confirmDelete = async () => {
      try {
        const res = await systemApi.deleteSystemConfigByKey(
          deleteForm.configKey
        );
        if (res.code === 200) {
          ElMessage.success("删除成功");
          deleteDialogVisible.value = false;
          fetchSettings();
        } else {
          ElMessage.error(res.message || "删除失败");
        }
      } catch (error) {
        console.error("删除配置失败", error);
        ElMessage.error("删除配置失败");
      }
    };

    // 保存网站设置
    const saveWebsiteSettings = async () => {
      try {
        let success = true;
        for (const [key, value] of Object.entries(websiteSettings)) {
          const res = await systemApi.updateSystemConfigValue(key, value);
          if (res.code !== 200) {
            success = false;
          }
        }

        if (success) {
          ElMessage.success("网站设置保存成功");
          fetchSettings();
        } else {
          ElMessage.warning("部分设置保存失败");
        }
      } catch (error) {
        console.error("保存网站设置失败", error);
        ElMessage.error("保存网站设置失败");
      }
    };

    // 保存功能设置
    const saveFunctionSettings = async () => {
      try {
        let success = true;
        for (const [key, value] of Object.entries(functionSettings)) {
          const res = await systemApi.updateSystemConfigValue(key, value);
          if (res.code !== 200) {
            success = false;
          }
        }

        if (success) {
          ElMessage.success("功能设置保存成功");
          fetchSettings();
        } else {
          ElMessage.warning("部分设置保存失败");
        }
      } catch (error) {
        console.error("保存功能设置失败", error);
        ElMessage.error("保存功能设置失败");
      }
    };

    // 保存上传设置
    const saveUploadSettings = async () => {
      try {
        // 保存上传大小限制
        const sizeRes = await systemApi.updateSystemConfigValue(
          "upload_size_limit",
          uploadSettings.upload_size_limit.toString()
        );

        // 保存允许的文件类型
        const typesRes = await systemApi.updateSystemConfigValue(
          "allowed_file_types",
          uploadFileTypes.value.join(",")
        );

        if (sizeRes.code === 200 && typesRes.code === 200) {
          ElMessage.success("上传设置保存成功");
          fetchSettings();
        } else {
          ElMessage.warning("部分设置保存失败");
        }
      } catch (error) {
        console.error("保存上传设置失败", error);
        ElMessage.error("保存上传设置失败");
      }
    };

    // 监听添加配置的键，自动填充描述
    watch(
      () => addForm.configKey,
      (newKey) => {
        const descriptionMap = {
          site_title: "网站标题",
          site_description: "网站描述",
          system_announcement: "系统公告",
          contact_email: "联系邮箱",
          register_open: "是否开放注册功能",
          review_required: "上传资源是否需要审核",
          enable_comments: "是否启用评论功能",
          upload_size_limit: "上传文件大小限制(MB)",
          allowed_file_types: "允许上传的文件类型，逗号分隔",
        };

        if (descriptionMap[newKey]) {
          addForm.description = descriptionMap[newKey];
        }
      }
    );

    onMounted(() => {
      fetchSettings();
    });

    return {
      configList,
      editDialogVisible,
      addConfigDialogVisible,
      deleteDialogVisible,
      editForm,
      addForm,
      deleteForm,
      rules,
      addFormRef,
      hasAdminRole,
      websiteSettings,
      functionSettings,
      uploadSettings,
      uploadFileTypes,
      getComponentForConfigKey,
      handleEdit,
      saveEdit,
      saveNewConfig,
      handleDelete,
      confirmDelete,
      saveWebsiteSettings,
      saveFunctionSettings,
      saveUploadSettings,
    };
  },
};
</script>

<style scoped>
.system-settings {
  padding: 1rem;
}

.settings-card {
  margin-bottom: 1.5rem;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.input-suffix {
  margin-left: 10px;
  color: #606266;
}

.el-form {
  max-width: 800px;
}
</style>
