<template>
  <template v-for="item in menuList" :key="item.menuId">
    <a-sub-menu v-if="item.menuType==='M'&&item.visible==='0'" :key="item.path" class="custom-submenu">
      <template #icon>
        <Icon :name="item.icon" class="menu-icon"/>
      </template>
      <template #title>
        <span class="menu-text">{{ item.menuName }}</span>
      </template>
      <subMenu :menuList="item.children" />
    </a-sub-menu>
    <a-menu-item v-if="item.menuType==='C'&&item.visible==='0'" :key="item.path" class="custom-menu-item"
                 @click="handleClickMenu(item)">
      <template #icon>
        <Icon :name="item.icon" class="menu-icon"/>
      </template>
      <span class="menu-text">{{ item.menuName }}</span>
    </a-menu-item>
  </template>
</template>

<script setup>
import Icon from "@/components/common/icon.vue";
import {useRouter} from 'vue-router'

const router = useRouter()
const props = defineProps({
  menuList: {
    type: Array,
    required: true,
  },
});
const handleClickMenu = (item) => {
  if (router.currentRoute.value.path === item.path) return;

  router.push(item.path).catch(err => {
    if (err.name !== 'NavigationDuplicated') {
      console.error(err);
    }
  });
}
</script>

<style lang="scss" scoped>
@use '@/styles/theme/menu';
:deep(.custom-menu-item),
:deep(.custom-submenu .ant-menu-submenu-title) {
  padding-left: 12px !important;
  margin: 4px 8px;
  border-radius: 8px;
  height: 40px;
  line-height: 40px;
  position: relative;
  overflow: hidden;
}
</style>
