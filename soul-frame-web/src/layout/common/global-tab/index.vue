<template>
  <div class="global-tab">
    <a-tabs v-model:activeKey="activeKey" type="editable-card" size="small" :hideAdd="true" @edit="handleTabEdit" @change="handleTabChange">
      <a-tab-pane :key="HOME_PATH" tab="首页" :closable="false"></a-tab-pane>
      <a-tab-pane v-for="item in tabList" :key="item.key" :closable="item.closable">
        <template #tab>
          <div class="tab-content">
            <Icon :name="item.icon" size="12" />
            <span class="tab-text">{{ item.tab }}</span>
          </div>
        </template>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script setup>
import {ref, watch} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {useAuthStore} from '@/stores/auth'
import Icon from '@/components/common/icon.vue'
import {HOME_PATH} from '@/config'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const tabList = ref([])
const activeKey = ref('/home')

// 查找菜单项
const findMenuItem = (menus, path) => {
  for (const item of menus) {
    if (item.path === path) {
      return item
    }
    if (item.children && item.children.length > 0) {
      const found = findMenuItem(item.children, path)
      if (found) {
        return found
      }
    }
  }
  return null
}

// 处理标签页编辑
const handleTabEdit = (targetKey, action) => {
  console.log(targetKey, action);
  if (action === 'remove') {
    handleTabClose(targetKey)
  }
}

// 处理标签页关闭
const handleTabClose = (key) => {
  const targetIndex = tabList.value.findIndex(item => item.key === key)
  if(targetIndex === -1){
    return
  }
  tabList.value.splice(targetIndex, 1)

  if(activeKey.value === key){
    if(tabList.value.length){
      tabList.value.length
      activeKey.value = tabList.value[tabList.value.length - 1].key
      router.push(activeKey.value)
    }else{
      activeKey.value = HOME_PATH
      router.push(HOME_PATH)
    }
  }
}

const handleTabChange = (key) => {
  router.push(key)
}

watch(()=>route.path,(newPath)=>{
  activeKey.value = newPath
  const menuItem = findMenuItem(authStore.showMenus, newPath)
  if(!menuItem){
    return
  }

  if(tabList.value.find(item=>item.key===newPath)||newPath===HOME_PATH){
    return
  }
  tabList.value.push({
    key: newPath,
    tab: menuItem.menuName,
    icon: menuItem.icon,
    closable: true
  })
},
{
  immediate: true
}
)
</script>

<style lang="scss" scoped>
// 定义变量
$tab-bg: #fff;
$tab-active-color: #1890ff;
$tab-hover-color: #e6f7ff;
$tab-text-color: #595959;
$tab-border-radius: 4px;
$tab-height: 36px;
$transition-duration: 0.3s;

.global-tab {
  background-color: $tab-bg;
  padding: 0 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  position: relative;
  z-index: 10;
  height: $tab-height;
  transition: all $transition-duration;

  &.fixed-tab {
    transition: all $transition-duration;
  }

  :deep(.ant-tabs-nav) {
    margin: 0;
    
    &::before {
      display: none; // 移除底部边框
    }
    
    .ant-tabs-nav-wrap {
      .ant-tabs-nav-list {
        transition: transform $transition-duration;
        
        .ant-tabs-tab {
          padding: 4px 16px;
          margin: 0 4px 0 0;
          background: rgba(0, 0, 0, 0.02);
          border: none !important;
          border-radius: $tab-border-radius $tab-border-radius 0 0;
          transition: all $transition-duration ease;
          height: $tab-height;
          line-height: $tab-height - 8px;
          opacity: 0.85;
          position: relative;
          overflow: hidden;
          
          &::after {
            content: '';
            position: absolute;
            bottom: 0;
            left: 0;
            width: 100%;
            height: 2px;
            background-color: $tab-active-color;
            transform: scaleX(0);
            transition: transform $transition-duration ease;
            transform-origin: center;
          }
          
          &:hover {
            background: $tab-hover-color;
            opacity: 1;
          }
          
          .ant-tabs-tab-remove {
            margin-left: 6px;
            color: #999;
            
            &:hover {
              color: #f5222d;
            }
          }
        }
        
        .ant-tabs-tab-active {
          background: $tab-bg;
          box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
          opacity: 1;
          font-weight: 500;
          
          &::after {
            transform: scaleX(1);
          }
          
          .ant-tabs-tab-btn {
            color: $tab-active-color;
          }
        }
      }
    }
  }
  
  .tab-content {
    display: flex;
    align-items: center;
    color: $tab-text-color;
    
    .tab-text {
      margin-left: 6px;
      transition: transform $transition-duration ease;
      white-space: nowrap;
      max-width: 120px;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

:deep(.ant-tabs-tab) {
  animation: fadeIn 0.3s ease-out;
}
</style>