<template>
  <hover-container class="theme-mode" @click="toggleDarkMode" :tooltipContent="tooltipContent">
    <transition name="slide-fade" mode="out-in">
      <div :key="themeStore.isDarkMode">
        <svg-icon name="yueliang" size="30" type="svg" v-if="!themeStore.isDarkMode" class="icon-rotate" />
        <svg-icon name="taiyang" size="30" type="svg" v-else />
      </div>
    </transition>
  </hover-container>
</template>

<script setup>
import { useThemeStore } from "@/stores/theme";
const themeStore = useThemeStore();
const toggleDarkMode = () => {
  setTimeout(() => {
    themeStore.toggleDarkMode();
  }, 100);
};
const tooltipContent = computed(() => {
  return themeStore.isDarkMode ? "浅色模式" : "深色模式";
});
</script>

<style scoped>
.theme-mode {
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 0 10px;
}

.icon-rotate {
  transform: scaleX(-1);
}

/* .icon-rotate:hover {
  transform: scaleX(-1);
} */

.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.3s ease-in;
}

.slide-fade-enter-from {
  transform: translateX(-20px);
  opacity: 0;
}

.slide-fade-leave-to {
  transform: translateX(20px);
  opacity: 0;
}
</style>