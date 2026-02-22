<template>
  <a-modal :open="visible" title="数据日志详情" width="800px" :footer="null" :destroyOnClose="true"
    :maskClosable="false" ok-text="确定" cancel-text="取消" @cancel="emit('update:visible', false)">
    <a-descriptions bordered :column="2">
      <a-descriptions-item label="日志标题" span="2">{{ detailInfo.title || '-' }}</a-descriptions-item>
      <a-descriptions-item label="请求方式">{{ detailInfo.requestMethod || '-' }}</a-descriptions-item>
      <a-descriptions-item label="操作状态">
        <dict-tag
            :value="detailInfo.status"
            dict-type="sys_operate_status"
        />
      </a-descriptions-item>
      <a-descriptions-item label="操作类型">
        <dict-tag
            :value="detailInfo.businessType"
            dict-type="sys_operate_type"
        />
      </a-descriptions-item>
      <a-descriptions-item label="耗费时间">{{ detailInfo.costTimeDesc || '-' }}</a-descriptions-item>
      <a-descriptions-item label="操作人员">{{ detailInfo.operName || '-' }}</a-descriptions-item>
      <a-descriptions-item label="操作IP">{{ detailInfo.operIp || '-' }}</a-descriptions-item>
      <a-descriptions-item label="操作时间">{{ detailInfo.operTime || '-' }}</a-descriptions-item>
      <a-descriptions-item label="操作地点">{{ detailInfo.operLocation || '-' }}</a-descriptions-item>
      <a-descriptions-item label="操作系统">{{ detailInfo.os || '-' }}</a-descriptions-item>
      <a-descriptions-item label="浏览器">{{ detailInfo.browser || '-' }}</a-descriptions-item>
      <a-descriptions-item label="请求URL" span="2">{{ detailInfo.operUrl || '-' }}</a-descriptions-item>
      <a-descriptions-item label="请求方法" span="2">{{ detailInfo.method || '-' }}</a-descriptions-item>
      <a-descriptions-item label="请求参数" span="2">
        <div class="json-container">
          <pre v-if="detailInfo.operParam" v-html="formatJsonWithHighlight(detailInfo.operParam)"></pre>
          <span v-else>-</span>
        </div>
      </a-descriptions-item>
      <a-descriptions-item label="返回参数" span="2">
        <div class="json-container">
          <pre v-if="detailInfo.jsonResult" v-html="formatJsonWithHighlight(detailInfo.jsonResult)"></pre>
          <span v-else>-</span>
        </div>
      </a-descriptions-item>
      <a-descriptions-item label="错误信息" span="2" v-if="detailInfo.errorMsg">
        <pre style="color: #ff4d4f;">{{ detailInfo.errorMsg }}</pre>
      </a-descriptions-item>
    </a-descriptions>
  </a-modal>
</template>

<script setup>
import {OPERATE_STATUS, OPERATE_TYPE} from '@/config'
import { getDataLogDetail } from '@/api/modules/dataLog'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  operId: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(['update:visible'])

// 详情信息
const detailInfo = ref(null)


// 获取数据日志详情
const getDetail = async () => {
  const res = await getDataLogDetail(props.operId)
  detailInfo.value = res.data
  console.log(detailInfo.value);
  
}

// 监听visible变化
watch(() => props.visible, (newVal) => {
  if (!newVal) {
    emit('update:visible', false)
  } else {
    getDetail()
  }
})

// 获取操作类型标签文字
const getOperateTypeLabel = (type) => {
  switch (type) {
    case OPERATE_TYPE.INSERT:
      return '新增'
    case OPERATE_TYPE.UPDATE:
      return '修改'
    case OPERATE_TYPE.DELETE:
      return '删除'
    case OPERATE_TYPE.QUERY:
      return '查询'
    default:
      return '未知'
  }
}

// 获取操作类型标签颜色
const getOperateTypeColor = (type) => {
  switch (type) {
    case OPERATE_TYPE.INSERT:
      return 'success'
    case OPERATE_TYPE.UPDATE:
      return 'processing'
    case OPERATE_TYPE.DELETE:
      return 'error'
    case OPERATE_TYPE.QUERY:
      return 'warning'
    default:
      return 'default'
  }
}

//获取操作状态标签文字
const getOperateStatusLabel = (status) => {
  switch (status) {
    case OPERATE_STATUS.SUCCESS:
      return '成功'
    case OPERATE_STATUS.FAIL:
      return '失败'
    default:
      return '未知'
  }
}

// 获取操作状态标签颜色
const getOperateStatusColor = (status) => {
  switch (status) {
    case OPERATE_STATUS.SUCCESS:
      return 'success'
    case OPERATE_STATUS.FAIL:
      return 'error'
    default:
      return 'default'
  }
}

// 格式化JSON并高亮显示
const formatJsonWithHighlight = (jsonStr) => {
  if (!jsonStr) return ''
  try {
    // 尝试解析JSON
    const jsonObj = typeof jsonStr === 'string' ? JSON.parse(jsonStr) : jsonStr
    const formatted = JSON.stringify(jsonObj, null, 2)
    
    // 修复正则表达式，避免样式问题
    return formatted
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"([^"]+)":/g, '<span class="json-key">"$1"</span>:') // 键名
      .replace(/"([^"]+)"(?=[,\n]|\s*\})/g, '<span class="json-string">"$1"</span>') // 字符串值
      .replace(/\b(true|false)\b/g, '<span class="json-boolean">$1</span>') // 布尔值
      .replace(/\b(\d+)\b/g, '<span class="json-number">$1</span>') // 数字
      .replace(/\b(null)\b/g, '<span class="json-null">$1</span>') // null值
  } catch (e) {
    // 如果解析失败，返回原始字符串，但确保HTML安全
    return String(jsonStr)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
  }
}
</script>

<style scoped>
:deep(.ant-descriptions-item-label) {
  width: 120px;
  font-weight: bold;
  background-color: #fafafa;
}

.json-container {
  width: 100%;
  position: relative;
}

:deep(pre) {
  margin: 0;
  padding: 8px;
  background-color: #f5f5f5;
  border: 1px solid #e8e8e8;
  border-radius: 2px;
  max-height: 300px;
  overflow: auto;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-word;
}

/* JSON高亮样式 */
:deep(.json-key) {
  color: #92278f;
}

:deep(.json-string) {
  color: #3ab54a;
}

:deep(.json-boolean),
:deep(.json-number) {
  color: #0033cc;
}

:deep(.json-null) {
  color: #ff0000;
}
</style> 