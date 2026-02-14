<template>
  <a-radio-group
      v-model:value="radioValue"
      :disabled="disabled"
      @change="handleChange"
  >
    <a-radio v-for="dict in dictData" :key="dict.value" :value="dict.value">
      {{ dict.label }}
    </a-radio>
  </a-radio-group>
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
    type: [String, Number],
    default: undefined
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  },
});

const emit = defineEmits(['update:value', 'change']);

const dictStore = useDictStore();
const dictData = ref([]);

// 计算属性：选中值
const radioValue = computed({
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
const handleChange = (e) => {
  emit('change', e.target.value);
};


onMounted(loadDict);

watch(() => props.dictType, (newVal, oldVal) => {
  if (newVal !== oldVal) {
    loadDict();
  }
}, {immediate: false});
</script> 