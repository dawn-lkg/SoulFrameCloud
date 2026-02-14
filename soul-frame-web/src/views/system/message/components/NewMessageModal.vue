<template>
  <a-modal
    :visible="visible"
    title="发送消息"
    @ok="handleOk"
    @cancel="handleCancel"
    :confirmLoading="loading"
  >
    <a-form
      ref="formRef"
      :model="formState"
      :rules="formRules"
      :label-col="{ span: 4 }"
      :wrapper-col="{ span: 20 }"
    >
      <a-form-item label="收件人" name="receiverId">
        <user-select v-model:value="formState.receiverId" placeholder="请选择收件人" />
      </a-form-item>
      <a-form-item label="消息内容" name="content">
        <a-textarea v-model:value="formState.content" placeholder="请输入消息内容" :rows="4" />
      </a-form-item>
      <a-form-item label="消息类型" name="type">
        <a-radio-group v-model:value="formState.type">
          <a-radio :value="1">文本</a-radio>
          <a-radio :value="2">图片</a-radio>
          <a-radio :value="3">文件</a-radio>
        </a-radio-group>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { message } from 'ant-design-vue';
import { useMessageStore } from '@/stores';
import UserSelect from '@/components/common/user-select.vue';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:visible', 'success']);

// 表单
const formRef = ref();
const loading = ref(false);
const formState = reactive({
  receiverId: undefined,
  content: '',
  type: 1
});

// 表单验证规则
const formRules = {
  receiverId: [
    { required: true, message: '请选择收件人', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入消息内容', trigger: 'blur' },
    { max: 500, message: '消息内容不能超过500个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择消息类型', trigger: 'change' }
  ]
};

// 消息存储
const messageStore = useMessageStore();

// 处理确认
const handleOk = async () => {
  try {
    await formRef.value.validate();
    
    loading.value = true;
    
    // 发送消息
    await messageStore.sendMessage(formState);
    
    message.success('发送成功');
    emit('success');
    formRef.value.resetFields();
    emit('update:visible', false);
  } catch (error) {
    console.error('发送消息失败:', error);
  } finally {
    loading.value = false;
  }
};

// 处理取消
const handleCancel = () => {
  formRef.value.resetFields();
  emit('update:visible', false);
};
</script>
