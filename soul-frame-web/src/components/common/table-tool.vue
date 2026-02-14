<template>
  <div class="table-tool">
    <a-space>
      <a-tooltip title="刷新">
        <Icon v-if="showButtons.includes('refresh')" class="table-tool-icon" name="ReloadOutlined" size="18"
              @click="emitRefresh"/>
      </a-tooltip>
      <a-dropdown>
        <a-tooltip title="密度">
          <Icon v-if="showButtons.includes('density')" class="table-tool-icon" name="ColumnHeightOutlined" size="18"/>
        </a-tooltip>
        <template #overlay>
          <a-menu :selectedKeys="[tableSize]" @click="$emit('update:tableSize', $event.key)">
            <a-menu-item key="default" class="table-tool-menu-item">默认</a-menu-item>
            <a-menu-item key="middle" class="table-tool-menu-item">中等</a-menu-item>
            <a-menu-item key="small" class="table-tool-menu-item">小</a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>
      <a-popover title="列设置" placement="bottom" trigger="click">
        <template v-if="showButtons.includes('column')" #content>
            <div v-for="column in tableColumns" :key="column.dataIndex" >
            <a-checkbox :checked="column.visible" @change="handleColumnVisible(column)">
                {{ column.title }}
            </a-checkbox>
        </div>
        </template>
        <a-tooltip title="设置">
          <Icon v-if="showButtons.includes('column')" class="table-tool-icon" name="SettingOutlined" size="18"/>
      </a-tooltip>
      </a-popover>
      
      <a-tooltip :title="isFull ? '退出全屏' : '全屏'">
        <Icon v-if="showButtons.includes('fullscreen')" :name="isFull ? 'FullscreenExitOutlined' : 'FullscreenOutlined'" class="table-tool-icon"
              size="18" @click="handleFullScreen"/>
      </a-tooltip>
    </a-space>
  </div>
</template>

<script setup>

// 定义事件
const emit = defineEmits(['refresh','update:tableSize','update:tableColumns'])
// 定义属性
const props = defineProps({
  //表格大小
  tableSize: {
    type: String,
  },
  //表格列设置
  tableColumns: {
    type: Array,
  },
  //需要全屏的元素
  fullScreenElement: {
    type: Object,
  },
  //展示的按钮
  showButtons: {
    type: Array,
    default: () => ['refresh', 'density', 'column', 'fullscreen']
  }
})

// 全屏
const isFull = ref(false)

// 全屏
const handleFullScreen = () => {
  if(isFull.value){
    isFull.value = false
    props.fullScreenElement.classList.remove('fullscreen-active')
  }else{
    isFull.value = true
    props.fullScreenElement.classList.add('fullscreen-active')
  }
}

// 刷新 
const emitRefresh = () => {
  emit('refresh')
}

// 列设置
const handleColumnVisible = (column) => {
    column.visible = !column.visible
    emit('update:tableColumns',[...props.tableColumns])
}
</script>

<style lang="scss" scoped>
.table-tool {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
}

// 增加菜单项宽度
:deep(.table-tool-menu-item) {
  min-width: 80px;
}

// 图标悬浮变色效果
.table-tool-icon {
  transition: color 0.3s;
  
  &:hover {
    color: #1890ff; 
  }
}
</style>
