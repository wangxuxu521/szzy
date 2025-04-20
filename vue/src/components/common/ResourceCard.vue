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
          v-for="(tag, index) in resource.tags"
          :key="index"
          class="resource-tag"
        >
          {{ tag }}
        </span>
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter } from "vue-router";

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

    const truncateDescription = (desc) => {
      if (!desc) return "";
      return desc.length > 100 ? desc.substring(0, 100) + "..." : desc;
    };

    const viewResource = () => {
      router.push({
        path: `/resources/${props.resource.id}`,
      });
    };

    return {
      truncateDescription,
      viewResource,
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
}

.resource-tag {
  background-color: #f5f5f5;
  color: #666;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  margin-right: 0.5rem;
  margin-bottom: 0.5rem;
  font-size: 0.8rem;
}
</style>
