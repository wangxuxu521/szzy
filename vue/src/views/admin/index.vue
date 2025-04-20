<template>
  <admin-layout>
    <router-view></router-view>
  </admin-layout>
</template>

<script>
import AdminLayout from "@/layout/AdminLayout.vue";

export default {
  name: "AdminView",
  components: {
    AdminLayout,
  },
};
</script>
