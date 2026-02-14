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
      <a-form-item label="字典类型" name="dictType">
        <a-input v-model:value="form.dictType" disabled/>
      </a-form-item>
      <a-form-item label="数据标签" name="dictLabel">
        <a-input v-model:value="form.dictLabel" placeholder="请输入数据标签"/>
      </a-form-item>
      <a-form-item label="数据键值" name="dictValue">
        <a-input v-model:value="form.dictValue" placeholder="请输入数据键值"/>
      </a-form-item>
      <a-form-item label="显示排序" name="dictSort">
        <a-input-number v-model:value="form.dictSort" :min="0" style="width: 100%"/>
      </a-form-item>
      <a-form-item label="样式属性" name="cssClass">
        <a-input v-model:value="form.cssClass" placeholder="请输入样式属性"/>
      </a-form-item>
      <a-form-item label="回显样式" name="listClass">
        <a-select v-model:value="form.listClass" allow-clear placeholder="请选择回显样式">
          <a-select-option value="default">默认</a-select-option>
          <a-select-option value="primary">主要</a-select-option>
          <a-select-option value="success">成功</a-select-option>
          <a-select-option value="info">信息</a-select-option>
          <a-select-option value="warning">警告</a-select-option>
          <a-select-option value="danger">危险</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="是否默认" name="isDefault">
        <a-radio-group v-model:value="form.isDefault">
          <a-radio value="Y">是</a-radio>
          <a-radio value="N">否</a-radio>
        </a-radio-group>
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
import {addDictData, updateDictData} from '@/api/modules/dict';

const props = defineProps({
  open: {
    type: Boolean,
    default: false
  },
  dictData: {
    type: Object,
    default: () => ({})
  },
  dictType: {
    type: String,
    default: ''
  },
  isEdit: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:open', 'success']);

// 计算弹窗标题
const title = computed(() => {
  return props.isEdit ? '修改字典数据' : '新增字典数据';
});

// 表单相关
const submitLoading = ref(false);
const formRef = ref(null);
const form = reactive({
  dictCode: undefined,
  dictSort: 0,
  dictLabel: '',
  dictValue: '',
  dictType: '',
  cssClass: '',
  listClass: '',
  isDefault: 'N',
  status: '0',
  remark: ''
});

// 表单验证规则
const rules = {
  dictLabel: [{required: true, message: '请输入数据标签', trigger: 'blur'}],
  dictValue: [{required: true, message: '请输入数据键值', trigger: 'blur'}],
  dictSort: [{required: true, message: '请输入显示排序', trigger: 'blur'}],
  status: [{required: true, message: '请选择状态', trigger: 'change'}]
};

// 监听dictData变化，填充表单
watch(
    () => props.dictData,
    (val) => {
      if (val && Object.keys(val).length > 0) {
        Object.assign(form, val);
      }
    },
    {deep: true, immediate: true}
);

// 监听dictType变化
watch(
    () => props.dictType,
    (val) => {
      if (val) {
        form.dictType = val;
      }
    },
    {immediate: true}
);

// 重置表单
const resetForm = () => {
  form.dictCode = undefined;
  form.dictSort = 0;
  form.dictLabel = '';
  form.dictValue = '';
  form.cssClass = '';
  form.listClass = '';
  form.isDefault = 'N';
  form.status = '0';
  form.remark = '';

  // 保留字典类型
  form.dictType = props.dictType;

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
      if (form.dictCode) {
        await updateDictData(form);
        message.success('修改成功');
      } else {
        await addDictData(form);
        message.success('新增成功');
      }
      emit('success');
      handleCancel();
    } catch (error) {
      message.error(form.dictCode ? '修改失败' : '新增失败');
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