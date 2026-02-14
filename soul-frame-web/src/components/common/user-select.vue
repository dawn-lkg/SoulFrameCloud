<template>
  <a-select v-model:value="value" :placeholder="placeholder" :allow-clear="allowClear" @change="handleChange">
    <a-select-option v-for="item in options" :key="item.userId">{{ item.userName }}</a-select-option>
  </a-select>
</template>

<script setup>
import {userSelect} from '@/api/modules/user'
import {onMounted, ref} from 'vue'

const props = defineProps({
  value: {
    default: undefined
  },
  placeholder: {
    type: String,
    default: '请选择用户'
  },
  allowClear: {
    type: Boolean,
    default: true
  },
  email:{
    type: String,
    default: ''
  },
  phone:{
    type: String,
    default: ''
  },
  username:{
    type: String,
    default: ''
  },

});

const options = ref([]);
const value = ref(props.value||undefined);

const emit = defineEmits(['change', 'update:value','update:email','update:phone','update:username']);

onMounted(async () => {
  const data = await userSelect({});
  options.value = data;
  console.log(options.value);
  
});

const handleChange = (value) => {
  emit('change', value);
  emit('update:value', value);
  emit('update:email', options.value.find(item => item.userId === value)?.email);
  emit('update:phone', options.value.find(item => item.userId === value)?.phone);
  emit('update:username', options.value.find(item => item.userId === value)?.userName);
};

</script>

<style>

</style>