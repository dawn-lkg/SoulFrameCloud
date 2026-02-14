<template>
  <div>
    <!-- 添加全局加载状态 -->
    <a-spin :spinning="loading" tip="数据加载中...">
      <!-- 基本信息 -->
      <a-card title="基本信息" class="card-box">
        <a-descriptions :column="2" bordered>
          <a-descriptions-item label="服务器名称">
            {{ serverInfo?.sys?.computerName }}
          </a-descriptions-item>
          <a-descriptions-item label="操作系统">
            {{ serverInfo?.sys?.osName }}
          </a-descriptions-item>
          <a-descriptions-item label="IP地址">
            {{ serverInfo?.sys?.computerIp }}
          </a-descriptions-item>
          <a-descriptions-item label="系统架构">
            {{ serverInfo?.sys?.osArch }}
          </a-descriptions-item>
        </a-descriptions>
      </a-card>

      <!-- CPU信息 -->
      <a-card title="CPU信息" class="card-box">
        <a-row :gutter="24">
          <a-col :span="12">
            <a-descriptions :column="1" bordered>
              <a-descriptions-item label="核心数">
                {{ serverInfo?.cpu?.cpuNum }}核{{ serverInfo?.cpu?.threadNum }}线程
              </a-descriptions-item>
              <a-descriptions-item label="型号">
                {{ serverInfo?.cpu?.cpuModel }}
              </a-descriptions-item>
              <a-descriptions-item label="cpu架构">
                {{ serverInfo?.cpu?.cpuArch }}位
              </a-descriptions-item>
            </a-descriptions>
          </a-col>
          <a-col :span="12">
            <div class="chart-wrapper">
              <div class="chart-title">CPU使用率</div>
              <a-progress
                type="dashboard"
                :percent="serverInfo?.cpu?.used"
                :format="percent => `${percent}%`"
                :stroke-color="getStatusColor(serverInfo?.cpu?.used)"
              />
            </div>
          </a-col>
        </a-row>
      </a-card>

      <!-- JVM信息 -->
      <a-card title="JVM信息" class="card-box">
        <a-row :gutter="24">
          <a-col :span="12">
            <a-descriptions :column="1" bordered>
              <a-descriptions-item label="JVM名称">
                {{ serverInfo?.jvm?.name }}
              </a-descriptions-item>
              <a-descriptions-item label="Java版本">
                {{ serverInfo?.jvm?.version }}
              </a-descriptions-item>
              <a-descriptions-item label="启动时间">
                {{ serverInfo?.jvm?.startTime }}
              </a-descriptions-item>
              <a-descriptions-item label="运行时长">
                {{ serverInfo?.jvm?.runTime }}
              </a-descriptions-item>
              <a-descriptions-item label="安装路径">
                {{ serverInfo?.jvm?.home }}
              </a-descriptions-item>
              <a-descriptions-item label="项目路径">
                {{ serverInfo?.sys?.userDir }}
              </a-descriptions-item>
            </a-descriptions>
          </a-col>
          <a-col :span="12">
            <div class="chart-wrapper">
              <div class="chart-title">JVM内存使用</div>
              <a-progress
                type="dashboard"
                :percent="serverInfo?.jvm?.used"
                :format="percent => `${percent}%`"
                :stroke-color="getStatusColor(serverInfo?.jvm?.used)"
              />
              <div style="margin-top: 10px">
                <span>已用内存: {{ serverInfo?.jvm?.used }}</span>
                <a-divider type="vertical" />
                <span>总内存: {{ serverInfo?.jvm?.total }}</span>
                <a-divider type="vertical" />
                <span>最大可用: {{ serverInfo?.jvm?.max }}</span>
              </div>
            </div>
          </a-col>
        </a-row>
      </a-card>

      <!-- 内存信息 -->
      <a-card title="内存信息" class="card-box">
        <a-row :gutter="24">
          <a-col :span="12">
            <a-descriptions :column="1" bordered>
              <a-descriptions-item label="总内存">
                {{ serverInfo?.mem?.total }}G
              </a-descriptions-item>
              <a-descriptions-item label="已用内存">
                {{ serverInfo?.mem?.used }}G
              </a-descriptions-item>
              <a-descriptions-item label="剩余内存">
                {{ serverInfo?.mem?.free }}G
              </a-descriptions-item>
            </a-descriptions>
          </a-col>
          <a-col :span="12">
            <div class="chart-wrapper">
              <div class="chart-title">内存使用率</div>
              <a-progress
                type="dashboard"
                :percent="serverInfo?.mem?.usage"
                :format="percent => `${percent}%`"
                :stroke-color="getStatusColor(serverInfo?.mem?.usage)"
              />
            </div>
          </a-col>
        </a-row>
      </a-card>

      <!-- 磁盘信息 -->
      <a-card title="磁盘信息" class="card-box">
        <a-table :columns="diskColumns" :data-source="serverInfo?.sysFiles" :pagination="false">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'usage'">
              <a-progress
                :percent="record.usage"
                :stroke-color="getStatusColor(record.usage)"
                size="small"
              />
            </template>
          </template>
        </a-table>
      </a-card>
    </a-spin>
  </div>
</template>

<script setup>
import {getServerInfo} from '@/api/modules/monitor-server'


const serverInfo = ref({})
const loading = ref(false)
// 磁盘表格列定义
const diskColumns = [
  {
    title: '挂载点',
    dataIndex: 'dirName',
    key: 'dirName'
  },
  {
    title: '文件系统',
    dataIndex: 'sysTypeName',
    key: 'sysTypeName'
  },
  {
    title: '总容量',
    dataIndex: 'total',
    key: 'total'
  },
  {
    title: '已用',
    dataIndex: 'used',
    key: 'used'
  },
  {
    title: '可用',
    dataIndex: 'free',
    key: 'free'
  },
  {
    title: '使用率',
    dataIndex: 'usage',
    key: 'usage'
  }
]

// 获取服务器信息
const getServerInfoData = async () => {
  loading.value = true
  try {
    const data = await getServerInfo()
    serverInfo.value = data
  } catch (error) {
    console.error('获取服务器信息失败:', error)
  } finally {
    loading.value = false
  }
}

getServerInfoData();

// 根据使用率返回不同的颜色
const getStatusColor = (percent) => {
  if (percent < 60) {
    return '#52c41a' // 绿色
  } else if (percent < 80) {
    return '#faad14' // 黄色
  } else {
    return '#f5222d' // 红色
  }
}
</script>

<style scoped>
.card-box {
  margin-bottom: 20px;
}

.chart-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.chart-title {
  margin-bottom: 16px;
  font-size: 16px;
  font-weight: 500;
}
</style>
