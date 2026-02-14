<template>
  <div class="global-sider">
    <transition name="fade-slide">
      <a-menu v-model:openKeys="state.openKeys" v-model:selectedKeys="state.selectedKeys" :inline-collapsed="state.collapsed"
              :class="['custom-menu', 'custom-scrollbar', themeStore.menuTheme === 'dark' ? 'menu-dark dark' : '']"
              :theme="themeStore.menuTheme"
              mode="inline"
              @openChange="handleOpenChange">
        <sub-menu :menuList="menuList"/>
      </a-menu>
    </transition>
  </div>
</template>
<script setup>
import {onMounted, reactive, watch} from 'vue';
import {useThemeStore} from '@/stores/theme';
import {useAuthStore} from '@/stores/auth';
import SubMenu from './components/sub-menu.vue';
import {useRoute} from 'vue-router';

const themeStore = useThemeStore();
const authStore = useAuthStore();
const route = useRoute();
const state = reactive({
  collapsed: false,
  selectedKeys: [],
  openKeys: [],
  preOpenKeys: [],
});
const menuList = reactive(authStore.showMenus);

// 监听打开的菜单
watch(
  () => state.openKeys,
  (_val, oldVal) => {
    state.preOpenKeys = oldVal;
  },
);

//查找父级菜单Path
const findParentPath = (menuItems,path,parentPath=[]) => {
  for(const item of menuItems){
    if(item.path === path){
      return [...parentPath];
    }
    if(item.children&&item.children.length>0){
      const parentPathList = findParentPath(item.children,path,[...parentPath,item.path]);
      if(parentPathList.length>0){
        return parentPathList;
      }
    }
  }
  return [];
}
// 监听路由
watch(()=>route.path,(val,oldVal)=>{
  state.selectedKeys = [val];
  state.openKeys = [...new Set([...findParentPath(menuList,val),...state.openKeys])];
},
{
  immediate: true
}
)
// 监听菜单
onMounted(()=>{
  state.selectedKeys = [route.path];
  state.openKeys = [...new Set([...findParentPath(menuList,route.path),...state.openKeys])];
})
// 监听菜单收缩
const toggleCollapsed = () => {
  state.collapsed = !state.collapsed;
  state.openKeys = state.collapsed ? [] : state.preOpenKeys;
};
// 监听菜单展开
const handleOpenChange = (openKeys) => {
  state.openKeys = openKeys;
};
</script>

<style lang="scss" scoped>
// 导入全局菜单样式
@use '@/styles/theme/menu';

.global-sider {
  height: 100%;
  display: flex;
  flex-direction: column;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  position: relative;
  z-index: 10;

  // 菜单过渡动画
  .fade-slide-enter-active,
  .fade-slide-leave-active {
    transition: all 0.3s ease;
  }

  .fade-slide-enter-from,
  .fade-slide-leave-to {
    opacity: 0;
    transform: translateX(-10px);
  }
  
  :deep(.custom-menu) {
    height: 100%;
    border-right: none;
    overflow-y: auto;
    overflow-x: hidden;
    flex: 1;
    padding: 8px 0;

    // 使用直接类名而不是@extend
    .ant-menu-item, .ant-menu-submenu-title {
      border-radius: 8px;
      height: 40px;
      line-height: 40px;
      position: relative;
      overflow: hidden;
      transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
      margin: 4px 8px;

      &:active {
        transform: scale(0.98);
      }

      // 左侧指示条
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 0;
        border-radius: 0 2px 2px 0;
        background-color: var(--ant-primary-color, #1890ff);
        transition: height 0.3s;
      }

      // 悬浮背景
      &::after {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background-color: var(--ant-primary-1, rgba(24, 144, 255, 0.1));
        opacity: 0;
        transition: opacity 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
        z-index: -1;
      }
      
      &:hover {
        background-color: transparent;

        &::after {
          opacity: 1;
        }
      }
    }
    
    .ant-menu-item-selected {
      background-color: var(--ant-primary-1, rgba(24, 144, 255, 0.08)) !important;
      
      &::before {
        height: 16px;
      }

      .menu-text, .menu-name {
        color: var(--ant-primary-color, #1890ff);
        font-weight: 400;
      }

      .menu-icon, .anticon {
        color: var(--ant-primary-color, #1890ff);
      }
    }

    // 子菜单展开样式
    .ant-menu-submenu-open > .ant-menu-submenu-title {
      .anticon-down {
        transform: rotate(180deg);
      }
    }

    // 子菜单内容样式
    .ant-menu-sub.ant-menu-inline {
      background: transparent;
      padding-left: 8px;
    }

    // 暗色主题样式
    &.ant-menu-dark {
      background: linear-gradient(to bottom, #1e2033, #13151f);
      border-right: 1px solid rgba(255, 255, 255, 0.05);
      
      .ant-menu-item, .ant-menu-submenu-title {
        &:hover {
          background-color: rgba(0, 0, 0, 0.2);
        }
        
        &::before {
          background-color: rgba(64, 158, 255, 0.15);
        }
      }

      .menu-text, .menu-name {
        &:hover {
          color: #409eff;
          opacity: 0.9;
        }
      }
      
      .ant-menu-item-selected {
        background-color: rgba(64, 158, 255, 0.1) !important;
        
        &::before {
          background-color: #409eff;
        }

        .menu-text, .menu-name {
          color: #409eff;
        }

        .menu-icon, .anticon {
          color: #409eff;
        }

        box-shadow: 0 0 10px 1px rgba(64, 158, 255, 0.15);
      }

      .ant-menu-sub.ant-menu-inline {
        background: rgba(0, 0, 0, 0.15);
      }
    }
  }
}
</style>
