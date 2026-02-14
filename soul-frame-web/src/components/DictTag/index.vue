<template>
  <a-tag v-if="tag" :color="tag.color">{{ tag.label }}</a-tag>
  <span v-else>{{ value }}</span>
</template>

<script setup>
import {computed, onMounted, ref, watch} from 'vue';
import {useDictStore} from '@/stores/dict';

const props = defineProps({
  // 字典类型
  dictType: {
    type: String,
    required: true
  },
  // 字典值
  value: {
    type: [String, Number],
    required: true
  }
});

const dictStore = useDictStore();
const dictData = ref([]);

// 获取字典数据
const loadDict = async () => {
  if (props.dictType) {
    dictData.value = await dictStore.getDictData(props.dictType)
  }
};

// 计算标签
const tag = computed(() => {
  if (!dictData.value.length || props.value === undefined) return null;

  const dict = dictData.value.find(item => item.value == props.value);
  if (!dict) return null;

  // 根据listClass设置颜色
  let color = '';
  if (dict.listClass) {
    switch (dict.listClass) {
      case 'default':
        color = '';
        break;
      case 'primary':
        color = 'blue';
        break;
      case 'success':
        color = 'green';
        break;
      case 'info':
        color = 'cyan';
        break;
      case 'warning':
        color = 'orange';
        break;
      case 'danger':
        color = 'red';
        break;
      default:
        color = dict.listClass;
    }
  }

  return {
    label: dict.label,
    color: color
  };
});

// 只在组件挂载时加载一次字典数据
onMounted(loadDict);

// 监听dictType变化，重新加载字典数据
watch(() => props.dictType, (newVal, oldVal) => {
  if (newVal !== oldVal) {
    loadDict();
  }
}, {immediate: false});
</script> 