<template>
  <a-drawer v-model:open="app.settingDrawerVisible" title="主题配置" placement="right">
    <a-divider class="divider">主题模式</a-divider>
    <a-form :labelCol="{ span: 20 }" :wrapperCol="{ span: 4 }" labelAlign="left">
      <a-form-item label="暗黑模式">
        <a-switch v-model:checked="themeStore.isDarkMode"  :checkedValue="true" :unCheckedValue="false">
          <template #checkedChildren>
            <svgIcon name="yueliang" />
          </template>
          <template #unCheckedChildren>
            <svgIcon name="taiyang" />
          </template>
        </a-switch>
      </a-form-item>
      <a-form-item label="侧边栏深色">
        <a-switch v-model:checked="themeStore.menuTheme" checkedValue="dark" unCheckedValue="light"></a-switch>
      </a-form-item>
    </a-form>

    <a-divider class="divider">侧边栏设置</a-divider>
    <a-form :labelCol="{ span: 8 }" :wrapperCol="{ span: 16 }" labelAlign="left">
      <a-form-item label="展开宽度">
        <a-input-number v-model:value="app.siderWidth" :min="180" :max="300" :step="10" addon-after="px" />
      </a-form-item>
      <a-form-item label="收起宽度">
        <a-input-number v-model:value="app.collapsedWidth" :min="48" :max="120" :step="8" addon-after="px" />
      </a-form-item>
      <a-form-item label="折叠侧边栏">
        <a-switch v-model:checked="app.siderCollapse"></a-switch>
      </a-form-item>
      <a-form-item label="隐藏菜单">
        <a-switch v-model:checked="app.hideMenu"></a-switch>
      </a-form-item>
    </a-form>

    <a-divider class="divider">导航设置</a-divider>
    <a-form :labelCol="{ span: 20 }" :wrapperCol="{ span: 4 }" labelAlign="left">
      <a-form-item label="显示面包屑">
        <a-switch v-model:checked="themeStore.layoutConfig.showBreadcrumb"></a-switch>
      </a-form-item>
      <a-form-item label="固定头部">
        <a-switch v-model:checked="themeStore.layoutConfig.fixedHeader"></a-switch>
      </a-form-item>
      <a-form-item label="固定侧边栏">
        <a-switch v-model:checked="themeStore.layoutConfig.fixedSidebar"></a-switch>
      </a-form-item>
      <a-form-item label="显示标签页">
        <a-switch v-model:checked="themeStore.layoutConfig.showTagsView"></a-switch>
      </a-form-item>
    </a-form>

    <a-divider class="divider">组件设置</a-divider>
    <a-form :labelCol="{ span: 16 }" :wrapperCol="{ span: 8 }" labelAlign="left">
      <a-form-item label="组件尺寸">
        <a-select v-model:value="app.componentSize" style="width: 100%">
          <a-select-option value="default">默认</a-select-option>
          <a-select-option value="middle">中等</a-select-option>
          <a-select-option value="small">紧凑</a-select-option>
        </a-select>
      </a-form-item>
    </a-form>
  </a-drawer>
</template>

<script setup>
import {useAppStore} from '@/stores/app';
import {useThemeStore} from '@/stores/theme';

const app = useAppStore();
const themeStore = useThemeStore();

</script>

<style lang="scss" scoped>
.divider {
  margin: 16px 0;
  color: rgba(0, 0, 0, 0.85);
  font-weight: 500;
  font-size: 16px;

  :deep(.ant-divider-inner-text) {
    font-size: 14px;
  }
}

:deep(.ant-input-number) {
  width: 100%;
}
</style>
