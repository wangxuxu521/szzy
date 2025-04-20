<template>
  <div class="base-layout">
    <app-header />
    <main class="main-content">
      <slot></slot>
    </main>
    <app-footer />
  </div>
</template>

<script>
import AppHeader from "@/components/common/AppHeader.vue";
import AppFooter from "@/components/common/AppFooter.vue";

export default {
  name: "BaseLayout",
  components: {
    AppHeader,
    AppFooter,
  },
};
</script>

<style scoped>
.base-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-content {
  flex: 1;
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}
</style>
