<template>
  <a-modal
    :open="open"
    :title="title"
    :confirm-loading="submitLoading"
    @ok="handleSubmit"
    @cancel="handleCancel"
    width="700px"
    :maskClosable="false"
    :destroyOnClose="true"
  >
    <a-form
      :model="form"
      :rules="rules"
      ref="formRef"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 16 }"
    >
      <a-form-item label="配置名称" name="configName">
        <a-input v-model:value="form.configName" placeholder="请输入配置名称" />
      </a-form-item>
      <a-form-item label="配置键名" name="configKey">
        <a-input v-model:value="form.configKey" placeholder="请输入配置键名" />
      </a-form-item>
      <a-form-item label="配置类型" name="configType">
        <a-select v-model:value="form.configType" placeholder="请选择配置类型">
          <a-select-option value="string">字符串</a-select-option>
          <a-select-option value="number">数字</a-select-option>
          <a-select-option value="boolean">布尔值</a-select-option>
          <a-select-option value="json">JSON对象</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="配置值" name="configValue">
        <a-textarea v-model:value="form.configValue" placeholder="请输入配置值" :rows="4" />
      </a-form-item>
      <a-form-item label="配置分组" name="configGroup">
        <a-select v-model:value="form.configGroup" placeholder="请选择配置分组" allow-create filterable default-first-option>
          <a-select-option v-for="group in groupOptions" :key="group.value" :value="group.value">
            {{ group.label }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="是否启用" name="isEnabled">
        <a-radio-group v-model:value="form.isEnabled">
          <a-radio :value="1">启用</a-radio>
          <a-radio :value="0">禁用</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label="是否系统配置" name="isSystem">
        <a-radio-group v-model:value="form.isSystem">
          <a-radio :value="1">是</a-radio>
          <a-radio :value="0">否</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label="排序序号" name="sortOrder">
        <a-input-number v-model:value="form.sortOrder" :min="0" style="width: 100%" />
      </a-form-item>
      <a-form-item label="配置描述" name="configDesc">
        <a-textarea v-model:value="form.configDesc" placeholder="请输入配置描述" />
      </a-form-item>
      <a-form-item label="备注" name="remark">
        <a-textarea v-model:value="form.remark" placeholder="请输入备注" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import {message} from 'ant-design-vue'
import {addConfig, updateConfig} from '@/api/modules/config'

const props = defineProps({
  open: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: ''
  },
  configData: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:open', 'success'])

// 表单引用
const formRef = ref(null)
// 提交状态
const submitLoading = ref(false)

// 配置分组选项
const groupOptions = ref([
  { label: '默认分组', value: 'default' },
  { label: '系统设置', value: 'system' },
  { label: '邮件设置', value: 'mail' },
  { label: '文件存储', value: 'storage' }
])

// 表单参数
const form = reactive({
  id: undefined,
  configName: undefined,
  configKey: undefined,
  configValue: undefined,
  configType: 'string',
  configGroup: 'default',
  configDesc: undefined,
  isEnabled: 1,
  isSystem: 0,
  sortOrder: 0,
  remark: undefined
})

// 表单校验
const rules = {
  configName: [{ required: true, message: '配置名称不能为空', trigger: 'blur' }],
  configKey: [{ required: true, message: '配置键名不能为空', trigger: 'blur' }],
  configValue: [{ required: true, message: '配置值不能为空', trigger: 'blur' }],
  configType: [{ required: true, message: '配置类型不能为空', trigger: 'change' }],
  configGroup: [{ required: true, message: '配置分组不能为空', trigger: 'change' }]
}

// 监听配置数据变化
watch(
  () => props.configData,
  (val) => {
    if (val && Object.keys(val).length > 0) {
      console.log(val);
        
      Object.assign(form, val)
    }
  },
  { deep: true, immediate: true }
)

/** 表单重置 */
function resetForm() {
  form.id = undefined
  form.configName = undefined
  form.configKey = undefined
  form.configValue = undefined
  form.configType = 'string'
  form.configGroup = 'default'
  form.configDesc = undefined
  form.isEnabled = 1
  form.isSystem = 0
  form.sortOrder = 0
  form.remark = undefined
  nextTick(() => {
    if (formRef.value) {
      formRef.value.resetFields()
    }
  })
}

/** 取消按钮 */
function handleCancel() {
  emit('update:open', false)
  resetForm()
}

/** 提交按钮 */
function handleSubmit() {
  submitLoading.value = true
  formRef.value.validate().then(() => {
    if (form.id) {
      updateConfig(form).then(() => {
        message.success('修改成功')
        emit('update:open', false)
        emit('success')
      }).finally(() => {
        submitLoading.value = false
      })
    } else {
      addConfig(form).then(() => {
        message.success('新增成功')
        emit('update:open', false)
        emit('success')
      }).finally(() => {
        submitLoading.value = false
      })
    }
  }).catch(() => {
    submitLoading.value = false
  })
}
</script> 