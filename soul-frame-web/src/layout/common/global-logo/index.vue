<template>
  <div class="global-logo">
    <span class="logo-icon" ref="logoIcon">B</span>
    <h1 v-if="!app.siderCollapse" ref="logoText">{{ configStore?.systemConfig?.name }}</h1>
  </div>
</template>

<script setup>
import {useAppStore} from "@/stores";
import {useConfigStore} from "@/stores/config";
import {onMounted, ref, watch} from 'vue';
import gsap from 'gsap';

const app = useAppStore();
const logoIcon = ref(null);
const logoText = ref(null);
const configStore = useConfigStore()

// 初始动画
onMounted(() => {
  // Logo 图标动画
  gsap.from(logoIcon.value, {
    duration: 1,
    scale: 0,
    rotation: 360,
    ease: "elastic.out(1, 0.3)"
  });

  // Logo 文字动画
  if (!app.siderCollapse) {
    gsap.from(logoText.value, {
      duration: 0.8,
      x: -50,
      opacity: 0,
      delay: 0.3,
      ease: "power2.out"
    });
  }
});

// 监听侧边栏折叠状态
watch(() => app.siderCollapse, (newValue) => {
  if (newValue) {
    // 折叠时的动画
    gsap.to(logoIcon.value, {
      duration: 0.3,
      scale: 0.8,
      ease: "power2.inOut"
    });
  } else {
    // 展开时的动画
    gsap.to(logoIcon.value, {
      duration: 0.3,
      scale: 1,
      ease: "power2.inOut"
    });
    gsap.from(logoText.value, {
      duration: 0.3,
      x: -20,
      opacity: 0,
      ease: "power2.out"
    });
  }
});
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;
.global-logo {
  height: $header-height;
  padding: 16px;
  text-align: center;
  margin-bottom: 4px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  // overflow: hidden;

  h1 {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    color: inherit;
  }

  .logo-icon {
    height: 32px;
    width: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: var(--primary-color, #1890ff);
    color: #fff;
    font-size: 20px;
    font-weight: bold;
    border-radius: 4px;
    flex-shrink: 0;
  }
}
</style>
