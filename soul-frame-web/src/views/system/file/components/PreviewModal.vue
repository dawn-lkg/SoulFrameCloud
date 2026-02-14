<template>
  <a-modal
      :footer="null"
      :open="visible"
      :title="file?.originalName"
      width="800px"
      @cancel="handleCancel"
  >
    <file-preview :file="file" :visible="visible"/>
  </a-modal>
</template>

<script setup>
import FilePreview from './FilePreview.vue';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  file: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['update:visible', 'cancel']);

// 取消预览
const handleCancel = () => {
  emit('update:visible', false);
  emit('cancel');
};
</script> 