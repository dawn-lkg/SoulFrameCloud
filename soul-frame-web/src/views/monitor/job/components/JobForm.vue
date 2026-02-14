<template>
  <a-modal
    :title="title"
    :open="visible"
    width="700px"
    @ok="submitForm"
    @cancel="handleCancel"
    :confirm-loading="loading"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-row>
        <a-col :span="24">
          <a-form-item label="任务名称" name="jobName">
            <a-input v-model:value="form.jobName" placeholder="请输入任务名称" />
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label="任务组名" name="jobGroup">
            <DictSelect v-model:value="form.jobGroup" dictType="sys_job_group" placeholder="请选择任务组名" />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="24">
          <a-form-item label="调用目标字符串" name="invokeTarget">
            <a-input-group compact>
              <a-input v-model:value="form.invokeTarget" placeholder="请输入调用目标字符串" style="width: calc(100% - 40px)" />
              <a-button type="primary" @click="openBeanSelector">
                <template #icon><select-outlined /></template>
              </a-button>
            </a-input-group>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="24">
          <a-form-item label="cron表达式" name="cronExpression">
            <a-input-group compact>
              <a-input v-model:value="form.cronExpression" placeholder="请输入cron执行表达式" style="width: calc(100% - 40px)" />
              <a-button type="primary" @click="openCronGenerator">
                <template #icon><schedule-outlined /></template>
              </a-button>
            </a-input-group>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="24">
          <a-form-item label="执行策略" name="misfirePolicy">
            <a-radio-group v-model:value="form.misfirePolicy">
              <a-radio value="1">立即执行</a-radio>
              <a-radio value="2">执行一次</a-radio>
              <a-radio value="3">放弃执行</a-radio>
            </a-radio-group>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="24">
          <a-form-item label="是否并发" name="concurrent">
            <a-radio-group v-model:value="form.concurrent">
              <a-radio value="0">允许</a-radio>
              <a-radio value="1">禁止</a-radio>
            </a-radio-group>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="24">
          <a-form-item label="状态">
            <a-radio-group v-model:value="form.status">
              <a-radio v-for="dict in statusOptions" :key="dict.value" :value="dict.value">{{ dict.label }}</a-radio>
            </a-radio-group>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="24">
          <a-form-item label="备注" name="remark">
            <a-textarea v-model:value="form.remark" placeholder="请输入备注" />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-modal>
  
  <!-- Bean选择器弹窗 -->
  <bean-selector
    v-model:visible="showBeanSelector"
    :value="form.invokeTarget"
    @select="handleBeanSelected"
    @cancel="showBeanSelector = false"
  />
  
  <!-- Cron表达式生成器弹窗 -->
  <cron-generator
    v-model:visible="showCronGenerator"
    :value="form.cronExpression"
    @confirm="handleCronSelected"
    @cancel="showCronGenerator = false"
  />
</template>

<script setup>
import { message } from 'ant-design-vue'
import { addJob, updateJob, getJob } from '@/api/modules/job'
import { SelectOutlined, ScheduleOutlined } from '@ant-design/icons-vue'
import BeanSelector from './BeanSelector.vue'
import CronGenerator from './CronGenerator.vue'
import { nextTick, watch } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  jobData: {
    type: Object,
    default: () => ({})
  },
  isEdit: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:visible', 'success', 'cancel'])

// 表单引用
const formRef = ref(null)

// 加载状态
const loading = ref(false)

// 标题
const title = computed(() => {
  return props.isEdit ? '修改任务' : '添加任务'
})

// 表单数据
const form = reactive({
  jobId: undefined,
  jobName: undefined,
  jobGroup: undefined,
  invokeTarget: undefined,
  cronExpression: undefined,
  misfirePolicy: '3',
  concurrent: '1',
  status: '0',
  remark: undefined
})

// 表单校验规则
const rules = {
  jobName: [{ required: true, message: '任务名称不能为空', trigger: 'blur' }],
  jobGroup: [{ required: true, message: '任务组名不能为空', trigger: 'blur' }],
  invokeTarget: [{ required: true, message: '调用目标字符串不能为空', trigger: 'blur' }],
  cronExpression: [{ required: true, message: 'cron执行表达式不能为空', trigger: 'blur' }]
}

// 状态选项
const statusOptions = ref([
  { value: '0', label: '正常' },
  { value: '1', label: '暂停' }
])

// Bean选择器相关
const showBeanSelector = ref(false)

// Cron表达式生成器相关
const showCronGenerator = ref(false)

// 重置表单
const resetForm = () => {
  form.jobId = undefined
  form.jobName = undefined
  form.jobGroup = undefined
  form.invokeTarget = undefined
  form.cronExpression = undefined
  form.misfirePolicy = '3'
  form.concurrent = '1'
  form.status = '0'
  form.remark = undefined
}

watch(() => props.jobData, (newVal) => {
  if(newVal&&Object.keys(newVal).length>0){
    resetForm()
    // setTimeout(() => {
      Object.assign(form, newVal)
    // }, 0)
  }else{
    resetForm();
  }
  
}, { immediate: true, deep: true })

// 监听visible变化，当打开弹窗时进行处理
watch(() => props.visible, (newVal) => {
  if(!newVal){
    resetForm()
  }
})

// watch(() => props.isEdit, (newVal) => {
//   if(!newVal){
//     resetForm()
//   }
// })



// 处理Bean选择
const handleBeanSelected = (value) => {
  form.invokeTarget = value
  // 手动触发表单验证
  formRef.value?.validateFields(['invokeTarget'])
}

// 打开Bean选择器
const openBeanSelector = () => {
  showBeanSelector.value = true
}

// 处理Cron表达式选择
const handleCronSelected = (value) => {
  form.cronExpression = value
  // 手动触发表单验证
  formRef.value?.validateFields(['cronExpression'])
}

// 打开Cron表达式生成器
const openCronGenerator = () => {
  showCronGenerator.value = true
}

// 提交表单
const submitForm = () => {
  loading.value = true
  formRef.value.validate().then(async () => {
    try {
      if (props.isEdit) {
        await updateJob(form)
        message.success('修改成功')
      } else {
        await addJob(form)
        message.success('新增成功')
      }
      loading.value = false
      emit('success')
      emit('update:visible', false)
    } catch (error) {
      loading.value = false
      message.error(error.message || '操作失败')
    }
  }).catch(error => {
    loading.value = false
    console.log('表单校验失败：', error)
  })
}

// 取消
const handleCancel = () => {
  emit('cancel')
  emit('update:visible', false)
}
</script>

<style scoped>
.ant-input-group {
  display: flex;
}

.ant-input-group .ant-input {
  flex: 1;
}
</style> 