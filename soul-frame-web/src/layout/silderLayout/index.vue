<template>
  <a-layout :class="layoutClass">
    <!-- 左侧菜单 -->
    <a-layout-sider v-model:collapsed="app.siderCollapse" :theme="themeStore.menuTheme"
                    v-if="!app.hideMenu" :collapsedWidth="app.collapsedWidth" :style="siderStyle" class="layout-sider"
                    collapsible>
      <global-logo />
      <global-sider />
    </a-layout-sider>
    <!-- 右侧内容 -->
    <a-layout :style="layoutStyle">
      <!-- 头部 -->
      <a-layout-header :class="{ 'fixed-header': themeStore.layoutConfig.fixedHeader, 'hide-menu': app.hideMenu }"
                       class="layout-header">
        <global-header />
      </a-layout-header>
      <!-- 头部占位符 -->
      <div v-if="themeStore.layoutConfig.fixedHeader" class="header-placeholder"></div>
      <!-- 头部tab-->
      <global-tab v-if="themeStore.layoutConfig.showTagsView"
                  :class="{ 'fixed-tab': themeStore.layoutConfig.fixedHeader,'hide-menu': app.hideMenu}"/>
      <!-- 标签页占位符 -->
      <div v-if="themeStore.layoutConfig.fixedHeader && themeStore.layoutConfig.showTagsView" class="tab-placeholder"></div>
      <!-- 内容 -->
      <a-layout-content class="layout-content">
        <global-content />
        <!-- 内容占位 -->
        <div class="content-placeholder"></div>
      </a-layout-content>
    </a-layout>
    <setting-drawer />
    <menu-float-button @click="handleMenuDrawerOpen(true)"/>
    <menu-drawer v-model:open="menuDrawerOpen" @update:open="handleMenuDrawerOpen"/>
  </a-layout>

</template>

<script setup>
import {computed, onUnmounted} from "vue";
import {useAppStore, useThemeStore} from "@/stores";
import globalSider from "../common/global-sider/index.vue";
import globalLogo from "../common/global-logo/index.vue";
import GlobalHeader from "../common/global-header/index.vue";
import SettingDrawer from "../common/setting-drawer/index.vue";
import globalTab from "../common/global-tab/index.vue"
import globalContent from "../common/global-content/index.vue";
import menuFloatButton from "../common/global-floatButton/menu_float_button.vue"

const themeStore = useThemeStore();
const app = useAppStore()

const menuDrawerOpen = ref(false)


// 计算布局样式
const layoutClass = computed(() => ({
  'layout-container': true,
  'dark-mode': themeStore.isDarkMode,
  'fixed-header-layout': themeStore.layoutConfig.fixedHeader
}));

// 侧边栏样式
const siderStyle = computed(() => ({
  backgroundColor: themeStore.menuTheme === 'dark' ? '#001529' : '#fff',
  borderRight: `1px solid ${themeStore.menuTheme === 'dark' ? '#1e1e1e' : '#f0f0f0'}`,
  color: themeStore.menuTheme === 'dark' ? '#fff' : '#000',
  position: themeStore.layoutConfig.fixedSidebar ? 'fixed' : 'static',
  left: themeStore.layoutConfig.fixedSidebar ? 0 : 'auto',
  width: app.siderCollapse ? `${app.collapsedWidth}px` : `${app.siderWidth}px`,
  zIndex: 101
}));
// 布局样式
const layoutStyle = computed(() => ({
  marginLeft: themeStore.layoutConfig.fixedSidebar && !app.hideMenu ? `${app.siderCollapse ? app.collapsedWidth : app.siderWidth}px` : '0',
}));

const handleMenuDrawerOpen = (value) => {
  menuDrawerOpen.value = value
}

const handleResize = () => {
  if (window.innerWidth < 1200) {
    app.hideMenu = true
  } else {
    app.hideMenu = false
  }
}

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
window.addEventListener('resize', handleResize)
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.layout-container {
  .layout-sider {
    height: 100vh;
    z-index: 101;
    transition: all $animation-duration-base;

    &:deep(.ant-layout-sider-children) {
      display: flex;
      flex-direction: column;
      height: 100%;
      background: inherit;
    }

    &:deep(.ant-menu) {
      flex: 1;
      overflow-x: hidden;
      overflow-y: auto;
      background: inherit;

      &::-webkit-scrollbar {
        width: 6px;
        height: 6px;
      }

      &::-webkit-scrollbar-thumb {
        background: rgba(0, 0, 0, 0.12);
        border-radius: 3px;

        .dark-mode & {
          background: rgba(255, 255, 255, 0.08);
        }
      }

      &::-webkit-scrollbar-track {
        background: transparent;
      }
    }

    &:deep(.ant-layout-sider-trigger) {
      border-right: 1px solid;
      border-color: inherit;
      background: inherit;
    }
  }

  .layout-header {
    background: $component-bg;
    padding: 0;
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: $header-height;
    line-height: $header-height;
    z-index: 100;
    transition: all $animation-duration-base;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);

    &.fixed-header {
      position: fixed;
      top: 0;
      right: 0;
      width: calc(100% - var(--sider-width, #{$sidebar-width}));
      z-index: 999;
      transition: width $animation-duration-base;
    }

    // 隐藏菜单时，头部占位符
    &.hide-menu {
      width: 100% !important;
    }


    .header-left {
      .ant-breadcrumb {
        line-height: $header-height;
      }
    }

    .header-right {
      display: flex;
      align-items: center;
    }
  }

  // 头部占位符
  .header-placeholder {
    height: $header-height;
    width: 100%;
  }

  // 标签页占位符
  .tab-placeholder {
    height: $tab-height;
    width: 100%;
  }

  // 固定标签页
  :deep(.global-tab) {
    &.fixed-tab {
      position: fixed;
      top: $header-height;
      right: 0;
      width: calc(100% - var(--sider-width, #{$sidebar-width}));
      z-index: 998;
      transition: width $animation-duration-base;
      box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
      background-color: $component-bg;
    }

    &.hide-menu {
      width: 100% !important;
    }
  }

  .layout-content {
    margin: $content-padding $content-padding 0;
    min-height: calc($content-height - 24px);
    // padding-bottom: 24px;
    // background: $component-bg;
    border-radius: $border-radius-base;
    transition: all $animation-duration-base;

    .content-placeholder {
      height: 40px;
    }
  }

  :deep(.ant-layout) {
    // margin-left: $sidebar-width;
    transition: margin-left $animation-duration-base;

    &.ant-layout-has-sider {
      margin-left: $sidebar-collapsed-width;
    }
  }
}

// 设置侧边栏宽度变量
:root {
  --sider-width: #{$sidebar-width};
}

// 当侧边栏收起时
.layout-container:has(.ant-layout-sider-collapsed) {
  --sider-width: #{$sidebar-collapsed-width};
}

// 暗黑模式特定样式
:deep(.dark-mode) {
  .layout-sider {
    box-shadow: 1px 0 8px rgba(0, 0, 0, 0.15);

    .ant-menu {

      &-item,
      &-submenu-title {
        &:hover {
          background-color: rgba(255, 255, 255, 0.08) !important;
        }

        &.ant-menu-item-selected {
          background-color: #177ddc !important;
        }
      }

      &-submenu-popup {
        background-color: #001529;
      }

      &-inline {
        background: inherit;
      }
    }
  }

  .layout-header {
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.15);
    
    &.fixed-header {
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
    }
  }
  
  .global-tab.fixed-tab {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  }
}
</style>
