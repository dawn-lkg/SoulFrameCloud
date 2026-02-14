<template>
  <a-drawer :body-style="{ padding: 0}" :closable="false" :header-style="{ padding: 0
  }" :open="open"
            :width="200" placement="left" @close="handleClose">
    <template #title>
      <div :style="menuTheme" class="menu-drawer-title">
        <global-logo/>
      </div>
    </template>
    <div :style="menuTheme" class="menu-drawer-container">
      <global-sider/>
    </div>
  </a-drawer>
</template>

<script setup>
import globalSider from "@/layout/common/global-sider/index.vue";
import globalLogo from "@/layout/common/global-logo/index.vue";
import {defineEmits, defineProps} from "vue";
import {useThemeStore} from "@/stores/theme";

const themeStore = useThemeStore();

const props = defineProps({
  open: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["update:open"]);

const menuTheme = computed(() => ({
  backgroundColor: themeStore.menuTheme === 'dark' ? '#001529' : '#fff',
  color: themeStore.menuTheme === 'dark' ? '#fff' : '#000',
}));

const handleClose = () => {
  emit("update:open", false);
};
</script>

<style lang="scss" scoped>
.menu-drawer-title {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.menu-drawer-container {
  height: 100%;

  :deep(.ant-layout-sider) {
    height: 100%;
    overflow: hidden;
  }

  :deep(.ant-menu) {
    height: 100%;
  }
}
</style>