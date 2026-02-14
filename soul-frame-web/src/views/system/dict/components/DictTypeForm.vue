<template>
  <a-modal
      :confirm-loading="submitLoading"
      :destroyOnClose="true"
      :maskClosable="false"
      :open="open"
      :title="title"
      width="550px"
      @cancel="handleCancel"
      @ok="handleSubmit"
  >
    <a-form
        ref="formRef"
        :label-col="{ span: 6 }"
        :model="form"
        :rules="rules"
        :wrapper-col="{ span: 16 }"
    >
      <a-form-item label="字典名称" name="dictName">
        <a-input v-model:value="form.dictName" placeholder="请输入字典名称"/>
      </a-form-item>
      <a-form-item label="字典类型" name="dictType">
        <a-input v-model:value="form.dictType" :disabled="isEdit" placeholder="请输入字典类型"/>
      </a-form-item>
      <a-form-item label="状态" name="status">
        <a-radio-group v-model:value="form.status">
          <a-radio value="0">正常</a-radio>
          <a-radio value="1">停用</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label="备注" name="remark">
        <a-textarea v-model:value="form.remark" :rows="4" placeholder="请输入备注"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import {computed, reactive, ref, watch} from 'vue';
import {message} from 'ant-design-vue';
import {addDictType, updateDictType} from '@/api/modules/dict';

const props = defineProps({
  open: {
    type: Boolean,
    default: false
  },
  dictTypeData: {
    type: Object,
    default: () => ({})
  },
  isEdit: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:open', 'success']);

// 表单相关
const submitLoading = ref(false);
const formRef = ref(null);
const form = reactive({
  dictId: undefined,
  dictName: '',
  dictType: '',
  status: '0',
  remark: ''
});

// 计算弹窗标题
const title = computed(() => {
  return props.isEdit ? '修改字典类型' : '新增字典类型';
});

// 计算是否为编辑模式
const isEdit = computed(() => {
  return props.isEdit;
});

// 表单验证规则
const rules = {
  dictName: [{required: true, message: '请输入字典名称', trigger: 'blur'}],
  dictType: [{required: true, message: '请输入字典类型', trigger: 'blur'}],
  status: [{required: true, message: '请选择状态', trigger: 'change'}]
};

// 监听dictTypeData变化，填充表单
watch(
    () => props.dictTypeData,
    (val) => {
      if (val && Object.keys(val).length > 0) {
        Object.assign(form, val);
      }
    },
    {deep: true, immediate: true}
);

// 重置表单
const resetForm = () => {
  form.dictId = undefined;
  form.dictName = '';
  form.dictType = '';
  form.status = '0';
  form.remark = '';

  // 延迟清除验证，确保在表单可见后进行
  setTimeout(() => {
    formRef.value?.resetFields();
  }, 0);
};

// 提交表单
const handleSubmit = () => {
  formRef.value?.validate().then(async () => {
    submitLoading.value = true;
    try {
      if (isEdit.value) {
        await updateDictType(form);
        message.success('修改成功');
      } else {
        await addDictType(form);
        message.success('新增成功');
      }
      emit('success');
      handleCancel();
    } catch (error) {
      message.error(isEdit.value ? '修改失败' : '新增失败');
    } finally {
      submitLoading.value = false;
    }
  });
};

// 取消操作
const handleCancel = () => {
  resetForm();
  emit('update:open', false);
};
</script> 