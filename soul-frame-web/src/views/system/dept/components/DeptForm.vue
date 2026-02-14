<template>
  <a-modal 
    :open="open" 
    :title="title" 
    :confirm-loading="submitLoading" 
    @ok="handleSubmit" 
    @cancel="handleCancel"
    width="550px" 
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
      <a-form-item label="上级部门" name="parentId">
        <a-tree-select 
          v-model:value="form.parentId" 
          :tree-data="deptOptions" 
          :fieldNames="{ label: 'deptName', value: 'deptId', key: 'deptId' }"
          placeholder="请选择上级部门"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }" 
          allow-clear 
          tree-default-expand-all 
        />
      </a-form-item>
      <a-form-item label="部门名称" name="deptName">
        <a-input v-model:value="form.deptName" placeholder="请输入部门名称" />
      </a-form-item>
      <a-form-item label="显示排序" name="orderNum">
        <a-input-number v-model:value="form.orderNum" :min="0" style="width: 100%" />
      </a-form-item>
      <a-form-item label="负责人" name="leader">
        <UserSelect v-model:value="form.leader" v-model:email="form.email" v-model:phone="form.phone" />
        <!-- <a-input v-model:value="form.leader" placeholder="请输入负责人" /> -->
      </a-form-item>
      <a-form-item label="联系电话" name="phone">
        <a-input v-model:value="form.phone" placeholder="请输入联系电话" />
      </a-form-item>
      <a-form-item label="邮箱" name="email">
        <a-input v-model:value="form.email" placeholder="请输入邮箱" />
      </a-form-item>
      <a-form-item label="部门状态" name="status">
        <a-radio-group v-model:value="form.status">
          <a-radio value="0">正常</a-radio>
          <a-radio value="1">停用</a-radio>
        </a-radio-group>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { message } from 'ant-design-vue'
import { addDept, updateDept, checkDeptNameUnique } from '@/api/modules/dept'

const props = defineProps({
  open: {
    type: Boolean,
    default: false
  },
  deptData: {
    type: Object,
    default: () => ({})
  },
  deptOptions: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:open', 'refresh'])

// 表单引用
const formRef = ref(null)

// 表单数据
const form = reactive({
  deptId: undefined,
  parentId: 0,
  ancestors: '',
  deptName: '',
  orderNum: 0,
  leader: undefined,
  phone: '',
  email: '',
  status: '0'
})

// 表单校验规则
const rules = {
  parentId: [{ required: true, message: '请选择上级部门', trigger: 'change' }],
  deptName: [
    { required: true, message: '请输入部门名称', trigger: 'blur' },
    { max: 30, message: '部门名称长度不能超过30个字符', trigger: 'blur' },
    { validator: validateDeptName, trigger: 'blur' }
  ],
  orderNum: [{ required: true, message: '请输入显示排序', trigger: 'blur' }],
  // leader: [{ required: false, message: '请选择负责人', trigger: 'change' }],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' },
    { max: 11, message: '联系电话长度不能超过11个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' },
    { max: 50, message: '邮箱长度不能超过50个字符', trigger: 'blur' }
  ]
}

// 提交状态
const submitLoading = ref(false)

// 弹窗标题
const title = ref('添加部门')

// 监听打开状态变化
watch(
  () => props.open,
  (val) => {
    if (val) {
      if (props.deptData.deptId) {
        title.value = '修改部门'
        Object.assign(form, props.deptData)
      } else {
        title.value = '添加部门'
        resetForm()
        if (props.deptData.parentId) {
          form.parentId = props.deptData.parentId
        }
      }
    }
  }
)

// 校验部门名称是否唯一
async function validateDeptName(rule, value) {
  if (!value) {
    return Promise.resolve()
  }
  try {
    const data = await checkDeptNameUnique(value, form.parentId, form.deptId)    
    if (data) {
      return Promise.reject('部门名称已存在')
    } else {
      return Promise.resolve()
    }
  } catch (error) {
    return Promise.reject('部门名称校验失败')
  }
}

// 重置表单
function resetForm() {
  form.deptId = undefined
  form.parentId = 0
  form.ancestors = ''
  form.deptName = ''
  form.orderNum = 0
  form.leader = undefined
  form.phone = ''
  form.email = ''
  form.status = '0'
}

// 提交表单
function handleSubmit() {
  formRef.value.validate().then(async () => {
    submitLoading.value = true
    try {
      if (form.deptId) {
        // 修改部门
        await updateDept(form)
        message.success('修改成功')
      } else {
        // 新增部门
        await addDept(form)
        message.success('新增成功')
      }
      emit('update:open', false)
      emit('refresh')
    } catch (error) {
      console.error('提交失败', error)
    } finally {
      submitLoading.value = false
    }
  }).catch(error => {
    console.log('校验失败', error)
  })
}

// 取消操作
function handleCancel() {
  emit('update:open', false)
  resetForm()
}
</script>

<style scoped>
</style>
