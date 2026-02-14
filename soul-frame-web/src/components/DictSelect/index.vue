<template>
  <a-select
      v-model:value="selectValue"
      :allow-clear="allowClear"
      :disabled="disabled"
      :placeholder="placeholder"
      :style="style"
      @change="handleChange"
  >
    <a-select-option v-for="dict in dictData" :key="dict.value" :value="dict.value">
      {{ dict.label }}
    </a-select-option>
  </a-select>
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
  // 绑定值
  value: {
    type: [String, Number, Array],
    default: undefined
  },
  // 占位符
  placeholder: {
    type: String,
    default: '请选择'
  },
  // 是否允许清空
  allowClear: {
    type: Boolean,
    default: true
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  },
  // 样式
  style: {
    type: Object,
    default: () => ({width: '100%'})
  }
});

const emit = defineEmits(['update:value', 'change']);

const dictStore = useDictStore();
const dictData = ref([]);

// 计算属性：选中值
const selectValue = computed({
  get: () => props.value,
  set: (val) => {
    emit('update:value', val);
  }
});

// 获取字典数据
const loadDict = async () => {
  if (props.dictType) {
    dictData.value = await dictStore.getDictData(props.dictType);
  }
};

// 选择值变化事件
const handleChange = (value) => {
  emit('change', value);
};

// 只在组件挂载时加载一次字典数据
onMounted(loadDict);

// 监听字典类型变化，重新加载字典数据
watch(() => props.dictType, (newVal, oldVal) => {
  if (newVal !== oldVal) {
    loadDict();
  }
}, {immediate: false});
</script> 