<template>
  <div class="resource-card" @click="viewResource">
    <div class="resource-type">{{ resource.type }}</div>
    <div class="resource-content">
      <h3 class="resource-title">{{ resource.title }}</h3>
      <div class="resource-info">
        <span class="resource-author">{{ resource.author }}</span>
        <span class="resource-views">👁 {{ resource.views }}</span>
      </div>
      <p v-if="resource.description" class="resource-description">
        {{ truncateDescription(resource.description) }}
      </p>
      <div class="resource-tags" v-if="resource.tags && resource.tags.length">
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
    </div>
  </div>
</template>

<script>
import { useRouter } from "vue-router";
import { computed } from "vue";

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
        return props.resource.tags
          .split(",")
          .map((tag) => tag.replace(/["'\[\]]/g, "").trim())
          .filter((tag) => tag);
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
        path: `/resources/${props.resource.id}`,
      });
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
      getTagClass,
      getTagIcon,
      parsedTags,
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
}

.resource-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
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

.resource-content {
  padding: 1.5rem;
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

.tag-default {
  background-color: #f5f5f5;
  color: #666;
  border-color: #d9d9d9;
}
</style>
