<template>
  <div class="api-test">
    <h1>API测试页面</h1>

    <div class="test-section">
      <h2>标签API测试</h2>
      <div class="button-group">
        <el-button
          type="primary"
          @click="testGetTagList"
          :loading="loading.getTagList"
        >
          获取标签列表
        </el-button>
        <el-button
          type="success"
          @click="testCreateTag"
          :loading="loading.createTag"
        >
          创建测试标签
        </el-button>
      </div>

      <div v-if="results.tagList" class="result-container">
        <h3>标签列表结果：</h3>
        <pre>{{ JSON.stringify(results.tagList, null, 2) }}</pre>
      </div>

      <div v-if="results.tagCreate" class="result-container">
        <h3>创建标签结果：</h3>
        <pre>{{ JSON.stringify(results.tagCreate, null, 2) }}</pre>
      </div>
    </div>

    <div class="test-section">
      <h2>资源API测试</h2>
      <div class="button-group">
        <el-button
          type="primary"
          @click="testGetResourceList"
          :loading="loading.getResourceList"
        >
          获取资源列表
        </el-button>
        <el-button
          type="success"
          @click="testCreateResource"
          :loading="loading.createResource"
        >
          创建测试资源
        </el-button>
      </div>

      <div v-if="results.resourceList" class="result-container">
        <h3>资源列表结果：</h3>
        <pre>{{ JSON.stringify(results.resourceList, null, 2) }}</pre>
      </div>

      <div v-if="results.resourceCreate" class="result-container">
        <h3>创建资源结果：</h3>
        <pre>{{ JSON.stringify(results.resourceCreate, null, 2) }}</pre>
      </div>
    </div>

    <div class="test-section">
      <h2>基础连接测试</h2>
      <div class="button-group">
        <el-button
          type="warning"
          @click="testDirectRequest"
          :loading="loading.directRequest"
        >
          直接请求测试
        </el-button>
      </div>

      <div v-if="results.directRequest" class="result-container">
        <h3>直接请求结果：</h3>
        <pre>{{ JSON.stringify(results.directRequest, null, 2) }}</pre>
      </div>
    </div>

    <div class="error-log" v-if="errorLog.length > 0">
      <h2>错误日志</h2>
      <div v-for="(error, index) in errorLog" :key="index" class="error-item">
        <p class="error-time">{{ error.time }}</p>
        <p class="error-message">{{ error.message }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from "vue";
import axios from "axios";
import { getTagList, saveTag } from "@/api/tag";
import { getResourceList, uploadResource } from "@/api/resource";
import { ElMessage } from "element-plus";

export default {
  name: "ApiTest",
  setup() {
    const loading = reactive({
      getTagList: false,
      createTag: false,
      directRequest: false,
      getResourceList: false,
      createResource: false,
    });

    const results = reactive({
      tagList: null,
      tagCreate: null,
      directRequest: null,
      resourceList: null,
      resourceCreate: null,
    });

    const errorLog = ref([]);

    const addError = (message) => {
      errorLog.value.unshift({
        time: new Date().toLocaleString(),
        message,
      });
    };

    const testGetTagList = async () => {
      loading.getTagList = true;
      try {
        console.log("开始获取标签列表...");
        const response = await getTagList();
        console.log("标签列表获取成功:", response);
        results.tagList = response;
        ElMessage.success("获取标签列表成功");
      } catch (error) {
        console.error("获取标签列表失败:", error);
        addError(`获取标签列表失败: ${error.message || JSON.stringify(error)}`);
        ElMessage.error(`获取标签列表失败: ${error.message || "未知错误"}`);
      } finally {
        loading.getTagList = false;
      }
    };

    const testCreateTag = async () => {
      loading.createTag = true;
      try {
        const testTag = {
          tagName: `测试标签${Date.now().toString().slice(-4)}`,
          tagType: "theme",
        };
        console.log("开始创建测试标签:", testTag);
        const response = await saveTag(testTag);
        console.log("标签创建成功:", response);
        results.tagCreate = response;
        ElMessage.success("创建测试标签成功");
      } catch (error) {
        console.error("创建测试标签失败:", error);
        addError(`创建测试标签失败: ${error.message || JSON.stringify(error)}`);
        ElMessage.error(`创建测试标签失败: ${error.message || "未知错误"}`);
      } finally {
        loading.createTag = false;
      }
    };

    const testDirectRequest = async () => {
      loading.directRequest = true;
      try {
        // 尝试直接使用axios请求，不经过封装
        console.log("开始直接请求测试...");
        const response = await axios.get("http://localhost:8088/tags");
        console.log("直接请求成功:", response);
        results.directRequest = response.data;
        ElMessage.success("直接请求成功");
      } catch (error) {
        console.error("直接请求失败:", error);
        addError(`直接请求失败: ${error.message || JSON.stringify(error)}`);
        ElMessage.error(`直接请求失败: ${error.message || "未知错误"}`);
      } finally {
        loading.directRequest = false;
      }
    };

    const testGetResourceList = async () => {
      loading.getResourceList = true;
      try {
        console.log("开始获取资源列表...");
        const response = await getResourceList();
        console.log("资源列表获取成功:", response);
        results.resourceList = response;
        ElMessage.success("获取资源列表成功");
      } catch (error) {
        console.error("获取资源列表失败:", error);
        addError(`获取资源列表失败: ${error.message || JSON.stringify(error)}`);
        ElMessage.error(`获取资源列表失败: ${error.message || "未知错误"}`);
      } finally {
        loading.getResourceList = false;
      }
    };

    const testCreateResource = async () => {
      loading.createResource = true;
      try {
        const formData = new FormData();
        formData.append("title", `测试资源${Date.now().toString().slice(-4)}`);
        formData.append("type", "教学资源");
        formData.append("description", "这是一个测试资源，用于测试API功能");
        formData.append("tags", JSON.stringify(["测试", "API"]));

        // 创建测试文件，这里实际上需要上传一个真实文件，但在测试时可以模拟
        const testFile = new File(["测试文件内容"], "test.txt", {
          type: "text/plain",
        });
        formData.append("file", testFile);

        console.log("开始创建测试资源...");
        const response = await uploadResource(formData);
        console.log("资源创建成功:", response);
        results.resourceCreate = response;
        ElMessage.success("创建测试资源成功");
      } catch (error) {
        console.error("创建测试资源失败:", error);
        addError(`创建测试资源失败: ${error.message || JSON.stringify(error)}`);
        ElMessage.error(`创建测试资源失败: ${error.message || "未知错误"}`);
      } finally {
        loading.createResource = false;
      }
    };

    return {
      loading,
      results,
      errorLog,
      testGetTagList,
      testCreateTag,
      testDirectRequest,
      testGetResourceList,
      testCreateResource,
    };
  },
};
</script>

<style scoped>
.api-test {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.test-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.button-group {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.result-container {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  margin-top: 10px;
}

pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  overflow-x: auto;
}

.error-log {
  margin-top: 30px;
  padding: 20px;
  border: 1px solid #f56c6c;
  border-radius: 4px;
}

.error-item {
  padding: 10px;
  border-bottom: 1px solid #ebeef5;
}

.error-time {
  font-size: 12px;
  color: #909399;
  margin-bottom: 5px;
}

.error-message {
  color: #f56c6c;
  margin: 0;
}
</style>
