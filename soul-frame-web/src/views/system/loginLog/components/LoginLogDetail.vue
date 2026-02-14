<template>
  <a-modal :open="visible" title="登录日志详情" width="700px" :footer="null" :destroyOnClose="true"
    :maskClosable="false" ok-text="确定" cancel-text="取消" @cancel="emit('update:visible', false)">
    <a-descriptions bordered :column="2">
      <a-descriptions-item label="访问编号" span="1">{{ detailInfo.infoId || '-' }}</a-descriptions-item>
      <a-descriptions-item label="用户账号" span="1">{{ detailInfo.userName || '-' }}</a-descriptions-item>
      <a-descriptions-item label="用户ID" span="1">{{ detailInfo.userId || '-' }}</a-descriptions-item>
      <a-descriptions-item label="登录状态" span="1">
        <dict-tag
            :value="detailInfo.status"
            dict-type="sys_loginLog_status"
        />
      </a-descriptions-item>
      <a-descriptions-item label="登录IP" span="1">{{ detailInfo.ipaddr || '-' }}</a-descriptions-item>
      <a-descriptions-item label="登录地点" span="1">{{ detailInfo.loginLocation || '-' }}</a-descriptions-item>
      <a-descriptions-item label="浏览器" span="1">{{ detailInfo.browser || '-' }}</a-descriptions-item>
      <a-descriptions-item label="操作系统" span="1">{{ detailInfo.os || '-' }}</a-descriptions-item>
      <a-descriptions-item label="登录时间" span="2">{{ detailInfo.loginTime || '-' }}</a-descriptions-item>
      <a-descriptions-item label="提示消息" span="2">{{ detailInfo.msg || '-' }}</a-descriptions-item>
    </a-descriptions>
  </a-modal>
</template>

<script setup>
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  detailData: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:visible'])

// 详情信息
const detailInfo = ref({})

// 监听detailData变化
watch(() => props.detailData, (newVal) => {
  if (newVal) {
    detailInfo.value = newVal
  }
}, { deep: true, immediate: true })

// 监听visible变化
watch(() => props.visible, (newVal) => {
  if (!newVal) {
    emit('update:visible', false)
  }
})
</script>

<style scoped>
:deep(.ant-descriptions-item-label) {
  width: 100px;
  font-weight: bold;
  background-color: #fafafa;
}

:deep(.ant-descriptions-item-content) {
  word-break: break-all;
}
</style> 