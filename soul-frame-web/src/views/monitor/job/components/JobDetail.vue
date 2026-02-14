<template>
  <a-modal
    title="任务详情"
    :visible="visible"
    width="700px"
    @cancel="handleCancel"
  >
    <a-descriptions bordered :column="2">
      <a-descriptions-item label="任务编号">{{ jobInfo.jobId }}</a-descriptions-item>
      <a-descriptions-item label="任务名称">{{ jobInfo.jobName }}</a-descriptions-item>
      <a-descriptions-item label="任务组名">{{ jobGroupFormat(jobInfo) }}</a-descriptions-item>
      <a-descriptions-item label="创建时间">{{ jobInfo.createTime }}</a-descriptions-item>
      <a-descriptions-item label="cron表达式">{{ jobInfo.cronExpression }}</a-descriptions-item>
      <a-descriptions-item label="下次执行时间">{{ nextExecution }}</a-descriptions-item>
      <a-descriptions-item label="调用目标字符串" :span="2">{{ jobInfo.invokeTarget }}</a-descriptions-item>
      <a-descriptions-item label="执行策略">
        <span v-if="jobInfo.misfirePolicy === '1'">立即执行</span>
        <span v-else-if="jobInfo.misfirePolicy === '2'">执行一次</span>
        <span v-else-if="jobInfo.misfirePolicy === '3'">放弃执行</span>
      </a-descriptions-item>
      <a-descriptions-item label="是否并发">
        <span v-if="jobInfo.concurrent === '0'">允许</span>
        <span v-else-if="jobInfo.concurrent === '1'">禁止</span>
      </a-descriptions-item>
      <a-descriptions-item label="状态">
        <a-badge :status="jobInfo.status === '0' ? 'success' : 'error'" :text="jobInfo.status === '0' ? '正常' : '暂停'" />
      </a-descriptions-item>
      <a-descriptions-item label="备注" :span="2">{{ jobInfo.remark }}</a-descriptions-item>
    </a-descriptions>
    <template #footer>
      <a-button @click="handleCancel">关 闭</a-button>
    </template>
  </a-modal>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  data: {
    type: Object,
    default: {}
  }
})

const emit = defineEmits(['update:visible', 'cancel'])

// 任务信息
const jobInfo = reactive({
  jobId: undefined,
  jobName: undefined,
  jobGroup: undefined,
  invokeTarget: undefined,
  cronExpression: undefined,
  misfirePolicy: undefined,
  concurrent: undefined,
  status: undefined,
  createTime: undefined,
  remark: undefined
})

// 下次执行时间
const nextExecution = ref('')

// 任务组选项
const jobGroupOptions = ref([
  { value: 'DEFAULT', label: '默认' },
  { value: 'SYSTEM', label: '系统' }
])

// 格式化任务组名
const jobGroupFormat = (row) => {
  return jobGroupOptions.value.find(item => item.value === row.jobGroup)?.label || ''
}

// 监听data变化，加载任务数据
watch(() => props.data, (newVal) => {
  Object.assign(jobInfo, newVal)
}, { immediate: true })

// 取消
const handleCancel = () => {
  emit('cancel')
  emit('update:visible', false)
}
</script>

<style scoped>
/* 可以根据需要添加样式 */
</style> 