<template>
  <a-modal
    title="选择Bean和方法"
    :open="visible"
    @ok="handleSelect"
    @cancel="handleCancel"
    width="500px"
  >
    <p style="margin-bottom: 8px; color: #999;">可以通过搜索快速查找Bean或方法</p>
    <a-tree-select
      v-model:value="selectedMethod"
      style="width: 100%"
      :tree-data="beanTreeData"
      :field-names="{ children: 'children', label: 'title', value: 'value' }"
      placeholder="请选择Bean和方法"
      tree-default-expand-all
      show-search
      :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
      :tree-line="false"
      :show-icon="true"
      allow-clear
    >
      <template #title="{ title, isLeaf }">
        <span class="tree-node-title">
          <span class="tree-icon" :class="{ 'bean-icon': !isLeaf, 'method-icon': isLeaf }">
            <api-outlined v-if="!isLeaf" />
            <code-outlined v-else />
          </span>
          <span class="tree-title">{{ title }}</span>
        </span>
      </template>
      <template #suffixIcon><span></span></template>
      <template #tagRender="{ value }">
        <span class="custom-tag">
          <span class="tree-icon method-icon">
            <code-outlined />
          </span>
          <span class="tree-title">{{ getMethodName(value) }}</span>
        </span>
      </template>
    </a-tree-select>
  </a-modal>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { message } from 'ant-design-vue'
import { ApiOutlined, CodeOutlined } from '@ant-design/icons-vue'
import { getBeanList } from '@/api/modules/bean'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  value: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:visible', 'select', 'cancel'])

// 选中的方法
const selectedMethod = ref('')
const beanTreeData = ref([])

// 监听value变化
watch(() => props.value, (newVal) => {
  selectedMethod.value = newVal
}, { immediate: true })

// 监听visible变化
watch(() => props.visible, (newVal) => {
  if (newVal) {
    selectedMethod.value = props.value
    getMethodList()
  }
}, { immediate: true })

// 获取Bean列表
const getMethodList = async () => {
  try {
    const beanData = await getBeanList();
    
    // 转换为树形结构
    beanTreeData.value = beanData.map(beanObj => {
      const beanName = Object.keys(beanObj)[0]
      const methods = beanObj[beanName]
      
      return {
        title: beanName,
        value: beanName,
        key: beanName,
        selectable: false,
        isLeaf: false,
        children: methods.map(method => ({
          title: method.label,
          value: method.value,
          key: method.value,
          isLeaf: true,
          selectable: true
        }))
      }
    })
  } catch (error) {
    console.error('获取Bean列表失败', error)
    message.error('获取Bean列表失败')
  }
}

// 获取方法名称
const getMethodName = (value) => {
  if (!value) return '';
  
  // 从所有方法中查找匹配的方法
  for (const bean of beanTreeData.value) {
    if (bean.children) {
      for (const method of bean.children) {
        if (method.value === value) {
          return method.title;
        }
      }
    }
  }
  
  // 如果找不到匹配的方法，返回原始值
  return value;
}

// 处理选择
const handleSelect = () => {
  if (selectedMethod.value) {
    emit('select', selectedMethod.value)
    message.success('已选择目标方法')
  }
  emit('update:visible', false)
}

// 处理取消
const handleCancel = () => {
  emit('update:visible', false)
  emit('cancel')
}
</script>

<style scoped>
/* 树形选择器样式 */
:deep(.ant-select-tree-node-content-wrapper) {
  display: flex;
  align-items: center;
  padding: 4px 8px;
}

:deep(.ant-select-tree-node-content-wrapper:hover) {
  background-color: #f5f5f5;
}

:deep(.ant-select-tree-node-content-wrapper.ant-select-tree-node-selected) {
  background-color: #e6f7ff;
}

:deep(.ant-select-tree-switcher) {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 选择框样式 */
:deep(.ant-select-selection-item) {
  display: flex !important;
  align-items: center !important;
  padding: 0 !important;
}

/* 自定义选择框中的内容 */
:deep(.ant-tree-select-selection-item) {
  display: flex;
  align-items: center;
}

/* 自定义标签样式 */
.custom-tag {
  display: flex;
  align-items: center;
  padding: 2px 0;
  max-width: 100%;
}

/* 隐藏默认后缀图标 */
:deep(.ant-select-arrow) {
  display: none;
}

/* 清除按钮样式调整 */
:deep(.ant-select-clear) {
  right: 8px;
  z-index: 2;
}

/* 树节点标题样式 */
.tree-node-title {
  display: flex;
  align-items: center;
  transition: all 0.3s;
}

.tree-node-title:hover .tree-icon {
  transform: scale(1.1);
}

.tree-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 6px;
  font-size: 16px;
  width: 20px;
  height: 20px;
  border-radius: 4px;
  transition: all 0.3s;
  flex-shrink: 0;
}

.bean-icon {
  color: #fff;
  background-color: #1890ff;
  box-shadow: 0 2px 4px rgba(24, 144, 255, 0.3);
}

.method-icon {
  color: #fff;
  background-color: #52c41a;
  box-shadow: 0 2px 4px rgba(82, 196, 26, 0.3);
}

.tree-title {
  font-size: 14px;
  transition: all 0.3s;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

:deep(.ant-select-tree-node-selected) .tree-title {
  font-weight: bold;
}
</style> 