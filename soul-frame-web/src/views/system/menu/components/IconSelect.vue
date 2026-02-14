<template>
  <div class="icon-select">
    <a-input-search v-model:value="searchValue" placeholder="搜索图标" style="margin-bottom: 10px" @change="handleSearch" />
    <div class="icon-list">
      <div v-for="icon in filteredIcons" :key="icon" class="icon-item" :class="{ active: modelValue === icon }"
        @click="handleSelect(icon)">
        <component :is="getIconComponent(icon)" />
        <span class="icon-name">{{ icon }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import * as Icons from '@ant-design/icons-vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue'])

// 搜索值
const searchValue = ref('')

// 获取所有Outlined图标
const allIcons = Object.keys(Icons);

// 过滤后的图标列表
const filteredIcons = computed(() => {
  if (!searchValue.value) {
    return allIcons
  }
  return allIcons.filter(icon =>
    icon.toLowerCase().includes(searchValue.value.toLowerCase())
  )
})

// 获取图标组件
const getIconComponent = (iconName) => {
  const componentName = iconName;
  return Icons[componentName]
}

// 处理图标选择
const handleSelect = (icon) => {
  emit('update:modelValue', icon)
}

// 处理搜索
const handleSearch = () => {
  // 搜索逻辑已通过计算属性实现
}
</script>

<style lang="scss" scoped>
.icon-select {
  .icon-list {
    height: 300px;
    overflow-y: auto;
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
    gap: 8px;
    padding: 8px;
  }

  .icon-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 8px;
    cursor: pointer;
    border: 1px solid #f0f0f0;
    border-radius: 4px;
    transition: all 0.3s;

    &:hover {
      color: #1890ff;
      border-color: #1890ff;
      background-color: #e6f7ff;
    }

    &.active {
      color: #1890ff;
      border-color: #1890ff;
      background-color: #e6f7ff;
    }

    .anticon {
      font-size: 20px;
      margin-bottom: 4px;
    }

    .icon-name {
      font-size: 12px;
      text-align: center;
      word-break: break-all;
    }
  }
}
</style>