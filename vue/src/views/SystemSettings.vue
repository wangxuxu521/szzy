<template>
  <div class="system-settings">
    <h2>系统设置</h2>

    <el-tabs type="border-card">
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
    </el-tabs>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { systemApi } from "@/api";

export default {
  name: "SystemSettings",
  setup() {
    const configList = ref([]);

    // 功能设置
    const functionSettings = reactive({
      register_open: "true",
      review_required: "true",
      enable_comments: "true",
    });

    // 上传设置
    const uploadSettings = reactive({
      upload_size_limit: 20,
    });

    const uploadFileTypes = ref([]);

    // 获取系统设置
    const fetchSettings = async () => {
      try {
        const res = await systemApi.getSystemConfigs();
        if (res.code === 200) {
          configList.value = res.data || [];

          // 填充设置
          configList.value.forEach((config) => {
            if (config.configKey in functionSettings) {
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

    onMounted(() => {
      fetchSettings();
    });

    return {
      functionSettings,
      uploadSettings,
      uploadFileTypes,
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

.input-suffix {
  margin-left: 10px;
  color: #606266;
}

.el-form {
  max-width: 800px;
}

:deep(.el-tabs__content) {
  padding: 20px;
}

:deep(.el-card) {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

:deep(.el-form-item) {
  margin-bottom: 22px;
}
</style>
