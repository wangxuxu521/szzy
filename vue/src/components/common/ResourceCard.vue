<template>
  <div
    class="resource-card"
    @click="viewResource"
    tabindex="0"
    role="article"
    aria-labelledby="resource-title"
    @keyup.enter="viewResource"
  >
    <div class="resource-type" :class="getTypeClass(resource.type)">
      {{ resource.type }}
    </div>
    <div class="resource-content">
      <h3 class="resource-title" id="resource-title">{{ resource.title }}</h3>
      <div class="resource-info">
        <span class="resource-author">
          <i class="el-icon-user"></i>
          {{ resource.author || resource.uploaderName || "未知用户" }}
        </span>
        <span class="resource-views">
          <i class="el-icon-view"></i>
          {{ formatNumber(resource.viewCount || 0) }}
        </span>
      </div>
      <p v-if="resource.description" class="resource-description">
        {{ truncateDescription(resource.description) }}
      </p>
      <div class="resource-tags" v-if="parsedTags.length">
        <span
          v-for="(tag, index) in parsedTags"
          :key="index"
          class="resource-tag"
          :class="getTagClass(tag)"
        >
          <span class="tag-icon">{{ getTagIcon(tag) }}</span>
          {{ tag }}
        </span>
      </div>
      <div class="resource-actions">
        <button
          class="action-btn view-btn"
          @click.stop="viewResource"
          aria-label="查看资源详情"
        >
          <i class="el-icon-view"></i> 查看
        </button>
        <button
          class="action-btn download-btn"
          @click.stop="downloadResource"
          aria-label="下载资源"
        >
          <i class="el-icon-download"></i> 下载
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter } from "vue-router";
import { computed } from "vue";
import { ElMessage } from "element-plus";
import { downloadResource } from "@/api/resource";

export default {
  name: "ResourceCard",
  props: {
    resource: {
      type: Object,
      required: true,
    },
  },
  setup(props) {
    const router = useRouter();

    // 确保标签是数组格式
    const parsedTags = computed(() => {
      if (!props.resource.tags) return [];

      // 如果已经是数组格式，处理每个标签
      if (Array.isArray(props.resource.tags)) {
        return props.resource.tags
          .map((tag) => {
            // 去除引号、方括号等标点符号
            return tag.replace(/["'\[\]]/g, "").trim();
          })
          .filter((tag) => tag);
      }

      // 如果是逗号分隔的字符串，转换为数组
      if (typeof props.resource.tags === "string") {
        // 尝试解析JSON字符串
        try {
          const parsedJson = JSON.parse(props.resource.tags);
          if (Array.isArray(parsedJson)) {
            return parsedJson
              .map((tag) => tag.toString().trim())
              .filter((tag) => tag);
          }
        } catch (e) {
          // 如果解析JSON失败，当作普通逗号分隔字符串处理
          return props.resource.tags
            .split(",")
            .map((tag) => tag.replace(/["'\[\]]/g, "").trim())
            .filter((tag) => tag);
        }
      }

      // 其他情况返回空数组
      return [];
    });

    const truncateDescription = (desc) => {
      if (!desc) return "";
      return desc.length > 100 ? desc.substring(0, 100) + "..." : desc;
    };

    const viewResource = () => {
      router.push({
        path: `/resources/${props.resource.resourceId}`,
      });
    };

    // 下载资源
    const downloadResource = async () => {
      try {
        ElMessage.info("正在准备下载...");
        await downloadResource(props.resource.resourceId);
        ElMessage.success("下载成功");
      } catch (error) {
        console.error("下载失败:", error);
        ElMessage.error("下载失败，请稍后再试");
      }
    };

    // 格式化数字
    const formatNumber = (num) => {
      if (num < 1000) return num;
      if (num < 10000) return (num / 1000).toFixed(1) + "K";
      return (num / 10000).toFixed(1) + "W";
    };

    // 获取资源类型样式类
    const getTypeClass = (type) => {
      const typeMap = {
        计算机: "type-computer",
        通信: "type-communication",
        人工智能: "type-ai",
      };
      return typeMap[type] || "type-default";
    };

    // 根据标签内容确定标签类型和样式
    const getTagClass = (tag) => {
      // 主题相关标签
      if (
        tag.includes("主义") ||
        tag.includes("精神") ||
        tag.includes("价值观") ||
        tag.includes("伦理")
      ) {
        return "tag-theme";
      }
      // 学科相关标签
      else if (
        tag.includes("计算机") ||
        tag.includes("网络") ||
        tag.includes("人工智能") ||
        tag.includes("数据") ||
        tag.includes("结构") ||
        tag.includes("工程")
      ) {
        return "tag-subject";
      }
      // 格式相关标签
      else if (
        tag.includes("PDF") ||
        tag.includes("PPT") ||
        tag.includes("Word") ||
        tag.includes("Excel") ||
        tag.includes("视频")
      ) {
        return "tag-format";
      }
      // 默认样式
      return "tag-default";
    };

    // 为不同类型的标签提供图标
    const getTagIcon = (tag) => {
      // 主题相关标签
      if (
        tag.includes("主义") ||
        tag.includes("精神") ||
        tag.includes("价值观") ||
        tag.includes("伦理")
      ) {
        return "🔮";
      }
      // 学科相关标签
      else if (
        tag.includes("计算机") ||
        tag.includes("网络") ||
        tag.includes("人工智能") ||
        tag.includes("数据") ||
        tag.includes("结构") ||
        tag.includes("工程")
      ) {
        return "📚";
      }
      // 格式相关标签
      else if (
        tag.includes("PDF") ||
        tag.includes("PPT") ||
        tag.includes("Word") ||
        tag.includes("Excel") ||
        tag.includes("视频")
      ) {
        return "📄";
      }
      // 默认图标
      return "🏷️";
    };

    return {
      truncateDescription,
      viewResource,
      downloadResource,
      getTagClass,
      getTagIcon,
      parsedTags,
      formatNumber,
      getTypeClass,
    };
  },
};
</script>

<style scoped>
.resource-card {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  position: relative;
  cursor: pointer;
  margin-bottom: 1.5rem;
  outline: none; /* 移除默认的focus轮廓 */
}

.resource-card:hover,
.resource-card:focus {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.resource-card:focus {
  outline: 2px solid #1890ff; /* 自定义focus轮廓 */
}

.resource-type {
  position: absolute;
  top: 0;
  right: 0;
  background-color: #1890ff;
  color: white;
  padding: 0.3rem 0.8rem;
  border-radius: 0 0 0 8px;
  font-size: 0.8rem;
}

.type-computer {
  background-color: #1890ff;
}
.type-communication {
  background-color: #52c41a;
}
.type-ai {
  background-color: #722ed1;
}
.type-default {
  background-color: #faad14;
}

.resource-content {
  padding: 1.5rem;
  padding-top: 2rem;
}

.resource-title {
  margin-top: 0;
  margin-bottom: 0.5rem;
  color: #333;
}

.resource-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
  font-size: 0.9rem;
  color: #666;
}

.resource-description {
  color: #666;
  margin-bottom: 1rem;
  line-height: 1.5;
}

.resource-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 1rem;
}

.resource-tag {
  display: inline-flex;
  align-items: center;
  background-color: #f5f5f5;
  color: #666;
  padding: 4px 8px;
  border-radius: 16px;
  font-size: 0.8rem;
  transition: all 0.2s ease;
  border: 1px solid transparent;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.resource-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 3px 5px rgba(0, 0, 0, 0.1);
}

.tag-icon {
  margin-right: 4px;
  font-size: 0.9rem;
}

.tag-theme {
  background-color: #f6ffed;
  color: #52c41a;
  border-color: #b7eb8f;
}

.tag-subject {
  background-color: #e6f7ff;
  color: #1890ff;
  border-color: #91d5ff;
}

.tag-format {
  background-color: #fff7e6;
  color: #fa8c16;
  border-color: #ffd591;
}

.resource-actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.action-btn {
  background: none;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  padding: 6px 12px;
  font-size: 13px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.3s;
}

.view-btn {
  color: #1890ff;
  border-color: #1890ff;
}

.view-btn:hover {
  background-color: #e6f7ff;
}

.download-btn {
  color: #52c41a;
  border-color: #52c41a;
}

.download-btn:hover {
  background-color: #f6ffed;
}
</style>
