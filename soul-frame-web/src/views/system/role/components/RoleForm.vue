<template>
  <a-modal :open="open" :title="title" :confirm-loading="submitLoading" @ok="handleSubmit" @cancel="handleCancel"
    width="500px" :maskClosable="false" :destroyOnClose="true">
    <a-form :model="form" :rules="rules" ref="formRef" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="角色名称" name="roleName">
        <a-input v-model:value="form.roleName" placeholder="请输入角色名称" />
      </a-form-item>
      <a-form-item label="角色编码" name="roleKey">
        <a-input v-model:value="form.roleKey" placeholder="请输入角色编码" />
      </a-form-item>
      <a-form-item label="排序" name="roleSort">
        <a-input-number v-model:value="form.roleSort" :min="0" style="width: 100%" />
      </a-form-item>
      <a-form-item label="状态" name="status">
        <dict-radio
            v-model:value="form.status"
            dict-type="sys_role_status"
        />
      </a-form-item>
      <a-form-item label="备注" name="remark">
        <a-textarea v-model:value="form.remark" placeholder="请输入备注" :rows="4" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import {message} from 'ant-design-vue'
import {addRole, updateRole} from '@/api/modules/role'

const props = defineProps({
  open: {
    type: Boolean,
    default: false
  },
  roleData: {
    type: Object,
    default: () => ({})
  },
  isEdit: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:open', 'success'])

// 计算弹窗标题
const title = computed(() => {
  return props.isEdit ? '修改角色' : '新增角色';
})

// 表单相关
const submitLoading = ref(false)
const formRef = ref(null)
const form = reactive({
  roleId: undefined,
  roleName: '',
  roleKey: '',
  roleSort: 0,
  status: '0',
  remark: ''
})

// 计算是否为编辑模式
const isEdit = computed(() => {
  return props.isEdit
})

// 表单验证规则
const rules = computed(() => ({
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    ...(isEdit.value ? [] : [{ validator: validateRoleName, trigger: 'blur' }])
  ],
  roleKey: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    ...(isEdit.value ? [] : [{ validator: validateRoleCode, trigger: 'blur' }])
  ],
  roleSort: [{ required: true, message: '请输入排序', trigger: 'blur' }]
}))

// 角色名称唯一性验证
const validateRoleName = (rule, value) => {
  if (!value) {
    return Promise.resolve()
  }
  // 模拟API调用验证角色名称唯一性
  if (value === '管理员') {
    return Promise.reject('该角色名称已存在')
  }
  return Promise.resolve()
}

// 角色编码唯一性验证
const validateRoleCode = (rule, value) => {
  if (!value) {
    return Promise.resolve()
  }
  // 模拟API调用验证角色编码唯一性
  if (value === 'admin') {
    return Promise.reject('该角色编码已存在')
  }
  return Promise.resolve()
}

// 重置表单
const resetForm = () => {
  form.roleId = undefined
  form.roleName = ''
  form.roleKey = ''
  form.roleSort = 0
  form.status = '0'
  form.remark = ''

  // 延迟清除验证，确保在表单可见后进行
  setTimeout(() => {
    formRef.value?.resetFields()
  }, 0)
}

// 监听roleData变化，填充表单
watch(
  () => props.roleData,
  (roleData) => {
    if (roleData && Object.keys(roleData).length > 0) {
      resetForm()
      Object.assign(form, roleData)
      console.log('表单数据已更新:', form)
    } else {
      resetForm()
    }
  },
  { immediate: true, deep: true }
)

// 监听visible变化，当打开时填充表单，关闭时重置表单
watch(
  () => props.open,
  (val) => {
    if (!val) {
      resetForm()
    }
  }
)

// 处理取消
const handleCancel = () => {
  emit('update:open', false)
}

// 提交表单
const handleSubmit = () => {
  formRef.value
    .validate()
    .then(async () => {
      try {
        submitLoading.value = true

        // 调用实际API
        if (isEdit.value) {
          // 编辑角色
          const res = await updateRole(form)
          message.success('修改成功')

          // 构造返回数据，包含是否为编辑模式的标志
          const returnData = {
            ...form,
            isEdit: isEdit.value
          }

          emit('success', returnData)
          emit('update:open', false)
        } else {
          // 新增角色
          const res = await addRole(form)
          message.success('新增成功')

          // 构造返回数据，包含是否为编辑模式的标志
          const returnData = {
            ...form,
            roleId: res.data?.roleId,
            isEdit: false
          }

          emit('success', returnData)
          emit('update:open', false)
        }
      } catch (error) {
        console.error('提交表单失败:', error)
        message.error('操作失败，请重试')
      } finally {
        submitLoading.value = false
      }
    })
    .catch(error => {
      console.log('验证失败:', error)
    })
}
</script>

<style lang="scss" scoped>
:deep(.ant-modal-body) {
  padding: 24px 24px 10px;
}

:deep(.ant-form-item) {
  margin-bottom: 20px;
}

:deep(.ant-form-item-label) {
  text-align: right;

  label {
    color: rgba(0, 0, 0, 0.85);
  }
}
</style>