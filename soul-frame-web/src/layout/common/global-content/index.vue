<template>
  <router-view v-slot="{ Component }">
    <!-- <keep-alive> -->
        <component :is="Component" />
    <!-- </keep-alive> -->
  </router-view>
</template>


<script setup>
</script>

<style scoped>
/* 移除路由过渡动画，提升页面切换性能 */
/* 如需要过渡效果，建议使用更轻量的实现 */
</style>
