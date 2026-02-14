<template>
  <hover-container @click="toggleCollapse" class="">
    <div class="menu-collapse" ref="menuIcon">
      <MenuFoldOutlined v-if="app.siderCollapse" />
      <MenuUnfoldOutlined v-else />
    </div>
  </hover-container>
</template>

<script setup>
import { MenuFoldOutlined, MenuUnfoldOutlined } from '@ant-design/icons-vue';
import { ref, onMounted } from 'vue';
import { useAppStore } from '@/stores';
import gsap from 'gsap';

const app = useAppStore();
const menuIcon = ref(null);

// 初始动画
onMounted(() => {
  gsap.from(menuIcon.value, {
    duration: 0.5,
    x: -20,
    opacity: 0,
    ease: "power2.out"
  });
});

// 点击切换动画
const toggleCollapse = () => {
  // 先执行旋转动画
  gsap.to(menuIcon.value, {
    duration: 0.3,
    rotate: app.siderCollapse ? -180 : 180,
    scale: 0.8,
    ease: "power2.inOut",
    onComplete: () => {
      // 动画完成后恢复状态并触发折叠
      gsap.to(menuIcon.value, {
        duration: 0.2,
        rotate: 0,
        scale: 1,
        ease: "power2.out"
      });
      app.toggleSiderCollapse();
    }
  });
};
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;
.menu-collapse {
  font-size: 20px;
  width: 40px;
  height: $header-height;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.3s;

  &:hover {
    color: var(--primary-color, #1890ff);
  }
}
</style>