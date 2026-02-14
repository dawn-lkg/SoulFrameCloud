<template>
<a-breadcrumb>
    <a-breadcrumb-item v-for="item in breadcrumb" :key="item.path">
      {{ item.name }}
    </a-breadcrumb-item>
  </a-breadcrumb>
</template>

<script setup>
import {useRoute} from 'vue-router';
import {useAuthStore} from '@/stores/auth';

const route = useRoute();
const authStore = useAuthStore();

const breadcrumb = ref([]);

//查找父级菜单Path
const findParentPath = (menuItems,path,parentPath=[]) => {
  for(const item of menuItems){
    if(item.path === path){
      return [...parentPath,{name:item.menuName,path:item.path,icon:item.icon}];
    }
    if(item.children&&item.children.length>0){
      const parentPathList = findParentPath(item.children,path,[...parentPath,{name:item.menuName,path:item.path,icon:item.icon}]);
      if(parentPathList.length>0){
        return parentPathList;
      }
    }
  }
  return [];
}

const updateBreadcrumb = () => {
  breadcrumb.value = findParentPath(authStore.showMenus,route.path);
}

watch(()=>route.path,()=>{
  updateBreadcrumb();
},
{
  immediate: true
}
);
</script>
