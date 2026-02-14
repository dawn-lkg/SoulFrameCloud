<template>
  <a-modal
    :visible="localVisible"
    title="发送通知"
    :width="500"
    :confirm-loading="loading"
    @ok="handleOk"
    @cancel="handleCancel"
    @update:visible="(val) => localVisible = val"
  >
    <a-form
      ref="formRef"
      :model="formState"
      :rules="rules"
      :label-col="{ span: 4 }"
      :wrapper-col="{ span: 20 }"
    >
      <a-form-item label="接收人" name="userId">
        <user-select v-model:value="formState.userId" placeholder="请选择接收人" />
      </a-form-item>
      <a-form-item label="标题" name="title">
        <a-input v-model:value="formState.title" placeholder="请输入通知标题" />
      </a-form-item>
      <a-form-item label="内容" name="content">
        <a-textarea
          v-model:value="formState.content"
          placeholder="请输入通知内容"
          :rows="4"
        />
      </a-form-item>
      <a-form-item label="类型" name="type">
        <a-select v-model:value="formState.type" placeholder="请选择通知类型">
          <a-select-option value="1">系统通知</a-select-option>
          <a-select-option value="2">安全提醒</a-select-option>
          <a-select-option value="3">活动通知</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="颜色" name="color">
        <a-input
          v-model:value="formState.color"
          placeholder="请输入颜色代码 (例如: #1890ff)"
          :disabled="!showColorInput"
        >
          <template #addonAfter>
            <a-checkbox v-model:checked="showColorInput">自定义</a-checkbox>
          </template>
        </a-input>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, reactive, watch } from 'vue';
import { message } from 'ant-design-vue';
import { useNotificationStore } from '@/stores/notification';
import UserSelect from '@/components/common/user-select.vue';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:visible', 'success']);

const notificationStore = useNotificationStore();
const formRef = ref();
const loading = ref(false);
const showColorInput = ref(false);

// 创建本地状态来跟踪visible属性
const localVisible = ref(false);

// 监听props.visible的变化，更新本地状态
watch(() => props.visible, (newVal) => {
  localVisible.value = newVal;
});

// 监听本地状态的变化，通过emit更新父组件的状态
watch(localVisible, (newVal) => {
  if (newVal !== props.visible) {
    emit('update:visible', newVal);
  }
});

// 表单数据
const formState = reactive({
  userId: undefined,
  title: '',
  content: '',
  type: '1',
  color: '#1890ff'
});

// 表单校验规则
const rules = {
  userId: [
    { required: true, message: '请选择接收人', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请输入通知标题', trigger: 'blur' },
    { max: 50, message: '标题长度不能超过50个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入通知内容', trigger: 'blur' },
    { max: 500, message: '内容长度不能超过500个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择通知类型', trigger: 'change' }
  ]
};

// 根据类型设置默认颜色
watch(() => formState.type, (newType) => {
  if (!showColorInput.value) {
    switch (newType) {
      case '1':
        formState.color = '#1890ff';
        break;
      case '2':
        formState.color = '#faad14';
        break;
      case '3':
        formState.color = '#ff4d4f';
        break;
      case '4':
        formState.color = '#52c41a';
        break;
      default:
        formState.color = '#1890ff';
    }
  }
});

// 提交表单
const handleOk = () => {
  formRef.value.validate().then(() => {
    loading.value = true;
    
    // 构建通知数据
    const notificationData = {
      userId: formState.userId,
      title: formState.title,
      content: formState.content,
      type: formState.type,
      color: showColorInput.value ? formState.color : getColorByType(formState.type)
    };
    
    // 发送通知
    notificationStore.sendNotification(notificationData)
      .then(() => {
        message.success('通知发送成功');
        resetForm();
        localVisible.value = false;
        emit('success');
      })
      .catch((error) => {
        console.error('发送通知失败:', error);
        message.error('发送通知失败: ' + error.message);
      })
      .finally(() => {
        loading.value = false;
      });
  }).catch(error => {
    console.log('表单验证失败:', error);
  });
};

// 根据类型获取颜色
const getColorByType = (type) => {
  switch (type) {
    case 'info':
      return '#1890ff';
    case 'warning':
      return '#faad14';
    case 'error':
      return '#ff4d4f';
    case 'success':
      return '#52c41a';
    default:
      return '#1890ff';
  }
};

// 取消
const handleCancel = () => {
  resetForm();
  localVisible.value = false;
};

// 重置表单
const resetForm = () => {
  formRef.value.resetFields();
  showColorInput.value = false;
};
</script>

<style scoped>
</style>
