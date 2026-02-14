<template>
  <div class="analysis-container">
    <div class="analysis-background"></div>
    <!-- 顶部卡片 -->
    <a-row :gutter="[24, 24]" class="top-section">
      <!-- 系统状态卡片 -->
      <a-col :lg="12" :md="12" :sm="24" :xs="24">
        <a-card :bordered="false" class="analysis-card status-card" title="系统状态">
          <template #extra>
            <a-button class="refresh-btn" title="刷新" type="text" @click="fetchSystemStatus">
              <ReloadOutlined :spin="systemStatusLoading"/>
            </a-button>
          </template>
          <a-row :gutter="[0, 16]">
            <a-col :span="12" class="progress-col">
              <div class="progress-container">
                <a-progress :format="percent => `${percent}%`" :percent="systemStatus.cpu" :width="80" type="dashboard" :stroke-color="getProgressColor(systemStatus.cpu)"/>
                <div class="status-label">CPU使用率</div>
              </div>
            </a-col>
            <a-col :span="12" class="progress-col">
              <div class="progress-container">
                <a-progress :format="percent => `${percent}%`" :percent="systemStatus.memory" :width="80"
                          type="dashboard" :stroke-color="getProgressColor(systemStatus.memory)"/>
                <div class="status-label">内存使用率</div>
              </div>
            </a-col>
          </a-row>
          <a-divider style="margin: 16px 0"/>
          <a-row>
            <a-col :span="12" class="status-item">
              <div class="status-value">{{ systemStatus.onlineUsers }}</div>
              <div class="status-label">在线用户</div>
            </a-col>
            <a-col :span="12" class="status-item">
              <div class="status-value">{{ systemStatus.uptime }}</div>
              <div class="status-label">运行时间(小时)</div>
            </a-col>
          </a-row>
        </a-card>
      </a-col>

      <!-- 访问统计卡片 -->
      <a-col :lg="12" :md="12" :sm="24" :xs="24">
        <a-card :bordered="false" class="analysis-card visits-card" title="访问统计">
          <template #extra>
            <a-button class="refresh-btn" title="刷新" type="text" @click="getVisitingStatistic">
              <ReloadOutlined :spin="visitDataLoading"/>
            </a-button>
          </template>
          <div class="data-overview">
            <div class="data-item">
              <div class="data-value">{{ visitData.visitsTodayCount }}</div>
              <div class="data-label">今日访问</div>
              <div :class="visitData.todayIncrease>=0?'data-trend up':'data-trend down'">
                <span>{{ visitData.todayIncrease }}%</span>
                <RiseOutlined v-if="visitData.todayIncrease>=0" />
                <FallOutlined v-else />
              </div>
            </div>
            <div class="data-item">
              <div class="data-value">{{ visitData.visitsLastWeekCount }}</div>
              <div class="data-label">本周访问</div>
              <div :class="visitData.weekIncrease>=0?'data-trend up':'data-trend down'">
                <span>{{ visitData.weekIncrease }}%</span>
                <RiseOutlined v-if="visitData.weekIncrease>=0" />
                <FallOutlined v-else />
              </div>
            </div>
            <div class="data-item">
              <div class="data-value">{{ visitData.visitsThisMonthCount }}</div>
              <div class="data-label">本月访问</div>
              <div :class="visitData.monthIncrease>=0?'data-trend up':'data-trend down'">
                <span>{{ visitData.monthIncrease }}%</span>
                <RiseOutlined v-if="visitData.monthIncrease>=0" />
                <FallOutlined v-else />
              </div>
            </div>
          </div>
          <div ref="visitChartRef" class="visit-chart"></div>
        </a-card>
      </a-col>

      <!-- 性能监控卡片 -->
      <a-col :lg="24" :md="24" :sm="24" :xs="24">
        <a-card :bordered="false" class="analysis-card performance-card" title="性能监控">
          <template #extra>
            <a-button class="refresh-btn" title="刷新" type="text" @click="getPerformanceMetricsByTab">
              <ReloadOutlined :spin="performanceMetricsLoading"/>
            </a-button>
          </template>
          <div class="performance-tabs">
            <a-radio-group v-model:value="performanceTab" button-style="solid">
              <a-radio-button value="realtime">实时</a-radio-button>
              <a-radio-button value="day">今日</a-radio-button>
              <a-radio-button value="week">本周</a-radio-button>
            </a-radio-group>
          </div>
          <div ref="performanceChartRef" class="performance-chart"></div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue';
import { message } from 'ant-design-vue';
import { getCpuMonitorInfo as getCpuMonitorInfoApi,getMemoryMonitorInfo as getMemoryMonitorInfoApi,getServerRunTime as getServerRunTimeApi} from '@/api/modules/monitor-server';
import {
  RiseOutlined,
  FallOutlined,
  ReloadOutlined
} from '@ant-design/icons-vue';
import * as echarts from 'echarts/core';
import { 
  LineChart, 
  BarChart, 
  PieChart 
} from 'echarts/charts';
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components';
import dayjs from 'dayjs';
import { CanvasRenderer } from 'echarts/renderers';
import { getVisitingStatistic as getVisitingStatisticApi } from '@/api/modules/dataLog';
import { getOnlineUserCount as getOnlineUserCountApi } from '@/api/modules/onlineUser';
import { realtime as getRealtimePerformanceApi,getPerformanceMetricsByHour as getPerformanceMetricsByHourApi,getPerformanceMetricsByWeek as getPerformanceMetricsByWeekApi } from '@/api/modules/performance-metrics';

// 注册必须的组件
echarts.use([
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  LineChart,
  BarChart,
  PieChart,
  CanvasRenderer
]);

// 系统状态
const systemStatus = ref({
  cpu: 0,
  memory: 0,
  onlineUsers: 0,
  uptime: 0
});

// 系统状态加载状态
const systemStatusLoading = ref(false);

// 访问数据
const visitData = ref({
  today: 0,
  todayIncrease:0,
  week: 0,
  weekIncrease:0,
  month: 0,
  monthIncrease:0,
  chartData: [],
  xData:[]
});

// 访问数据加载状态
const visitDataLoading = ref(false);

// 性能指标加载状态
const performanceMetricsLoading = ref(false)

// 实时性能指标
const performanceData = ref({
  cpuUsageList: [],
  memoryUsageList: [],
  timeList: []
});

// 性能监控选项
const performanceTab = ref('realtime');
const performanceChartRef = ref(null);
let performanceChart = null;

// 获取访问统计
const getVisitingStatistic = async () => {
  visitDataLoading.value = true;
  const data = await getVisitingStatisticApi();
  visitData.value = data;
  visitData.value.todayIncrease = ((visitData.value.visitsTodayCount-visitData.value.visitsYesterdayCount)/visitData.value.visitsYesterdayCount*100).toFixed(2);
  visitData.value.weekIncrease = ((visitData.value.visitsThisWeekCount-visitData.value.visitsLastWeekCount)/visitData.value.visitsLastWeekCount*100).toFixed(2);
  visitData.value.monthIncrease = ((visitData.value.visitsThisMonthCount-visitData.value.visitsLastMonthCount)/visitData.value.visitsLastMonthCount*100).toFixed(2);
  visitData.value.chartData=data.list.map(item=>item.count);
  visitData.value.xData=data.list.map(item=>item.time);
  visitDataLoading.value = false;
}

// 获取cpu监控信息
const getCpuMonitorInfo = async () => {
  const data = await getCpuMonitorInfoApi();
  systemStatus.value.cpu = data.used;
}

// 获取内存监控信息
const getMemoryMonitorInfo = async () => {
  const data = await getMemoryMonitorInfoApi();
  systemStatus.value.memory = data.usage;
}

// 获取运行时长
const getServerRunTime = async () => {
  const data = await getServerRunTimeApi();
  systemStatus.value.uptime = data;
}

// 获取在线用户数量
const getOnlineUserCount = async () => {
  const data = await getOnlineUserCountApi();
  systemStatus.value.onlineUsers = data;
}

// 获取实时性能指标
const getRealtimePerformance = async () => {
  performanceMetricsLoading.value=true;
  const data = await getRealtimePerformanceApi();
  performanceData.value.cpuUsageList = data.cpuUsageList;
  performanceData.value.memoryUsageList = data.memUsageList;
  const size = data.cpuTimeList.length;
  const timeList = [];
  for(let i=0;i<size;i++){
    const time= i*5;
    if(time==0){
      timeList.unshift('现在');
    }else if(time%60==0){
      timeList.unshift(time/60+'分钟前');
    }else{
      timeList.unshift(time);
    }
  }
  performanceData.value.timeList = timeList;
  updatePerformanceChart();
  performanceMetricsLoading.value=false;
}

// 获取按小时性能指标
const getPerformanceMetricsByHour = async () => {
  performanceMetricsLoading.value=true;
  const cpuData = await getPerformanceMetricsByHourApi({metricType:'CPU_USAGE'});
  const memData = await getPerformanceMetricsByHourApi({metricType:'MEM_USAGE'})
  const timeList=cpuData.map(d=>Number(dayjs(d.hour).format('HH')));
  const cpuUsage=cpuData.map(d=>d.avgValue);
  const memUsage=memData.map(d=>d.avgValue);
  performanceData.value.cpuUsageList = cpuUsage;
  performanceData.value.memoryUsageList = memUsage;
  performanceData.value.timeList = timeList;
  updatePerformanceChart();
  performanceMetricsLoading.value=false;
}

// 获取按周性能指标
const getPerformanceMetricsByWeek = async () => {
  performanceMetricsLoading.value=true;
  const cpuData = await getPerformanceMetricsByWeekApi({metricType:'CPU_USAGE'});
  const memData = await getPerformanceMetricsByWeekApi({metricType:'CPU_USAGE'});
  const timeList=cpuData.map(d=>d.date);
  const cpuUsage=cpuData.map(d=>d.avgValue);
  const memUsage=memData.map(d=>d.avgValue);
  performanceData.value.cpuUsageList = cpuUsage;
  performanceData.value.memoryUsageList = memUsage;
  performanceData.value.timeList = timeList;
  console.log(timeList);
  updatePerformanceChart();
  performanceMetricsLoading.value=false;
}

// 获取性能指标
const getPerformanceMetricsByTab = async ()=>{
  const tab=performanceTab.value;
  if(tab=='realtime'){
    getRealtimePerformance();
  }else if(tab=='day'){
    getPerformanceMetricsByHour();
  }else if(tab=='week'){
    getPerformanceMetricsByWeek();
  }
}

// 访问图表
const visitChartRef = ref(null);
let visitChart = null;

// 获取进度条颜色
const getProgressColor = (percent) => {
  if (percent < 50) {
    return '#52c41a'; // 绿色
  } else if (percent < 80) {
    return '#faad14'; // 黄色
  } else {
    return '#f5222d'; // 红色
  }
};

// 初始化访问图表
const initVisitChart = () => {
  if (!visitChartRef.value) return;
  
  visitChart = echarts.init(visitChartRef.value);
  
  const xData = visitData.value.xData;
  const data = visitData.value.chartData;
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: xData
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: data,
      type: 'line',
      smooth: true,
      areaStyle: {
        opacity: 0.3
      },
      itemStyle: {
        color: '#1890ff'
      },
      lineStyle: {
        width: 2
      }
    }]
  };
  
  visitChart.setOption(option);
};

// 初始化性能图表
const initPerformanceChart = () => {
  if (!performanceChartRef.value) return;
  
  performanceChart = echarts.init(performanceChartRef.value);
  const xData = performanceData.value.timeList;
  const cpuData = performanceData.value.cpuUsageList;
  const memoryData = performanceData.value.memoryUsageList;
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['CPU', '内存']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: xData,
      axisLabel:{
        rotate: 45,
        
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [
      {
        name: 'CPU',
        type: 'line',
        data: cpuData,
        smooth: true,
        itemStyle: {
          color: '#1890ff'
        }
      },
      {
        name: '内存',
        type: 'line',
        data: memoryData,
        smooth: true,
        itemStyle: {
          color: '#52c41a'
        }
      }
    ]
  };
  
  performanceChart.setOption(option);
};

// 更新图表的数据
const updatePerformanceChart = (forceReplace = false) => {
  if (!performanceChart) return;
  performanceChart.setOption({
    xAxis: { data: performanceData.value.timeList },
    series: [
      { name: 'CPU', data: performanceData.value.cpuUsageList },
      { name: '内存', data: performanceData.value.memoryUsageList }
    ]
  }, forceReplace);
};

// 监听性能选项变化
watch(performanceTab, (newVal) => {
  getPerformanceMetricsByTab(newVal)
  updatePerformanceChart();
});

// 处理窗口大小变化
const handleResize = () => {
  visitChart?.resize();
  performanceChart?.resize();
};

// 获取系统状态
const fetchSystemStatus = () => {
  systemStatusLoading.value = true;
  Promise.all([
    getCpuMonitorInfo(),
    getMemoryMonitorInfo(),
    getOnlineUserCount(),
    getServerRunTime()
  ]).then(() => {
    systemStatusLoading.value = false;
  });
};

// 生命周期钩子
onMounted(() => {
  getVisitingStatistic().then(()=>{
    initVisitChart();
  });
  fetchSystemStatus();
  getPerformanceMetricsByTab().then(()=>{
    initPerformanceChart();
  })
  
  // 添加窗口大小变化监听
  window.addEventListener('resize', handleResize);
  
  // 定时更新系统状态
  const statusInterval = setInterval(fetchSystemStatus, 30000);

  // 定时更新实时性能指标
  const performanceInterval = setInterval(()=>{
    if(performanceTab.value=='realtime'){
      getPerformanceMetricsByTab();
    }
  }, 5000);
  
  // 清理函数
  onUnmounted(() => {
    window.removeEventListener('resize', handleResize);
    clearInterval(statusInterval);
    clearInterval(performanceInterval);
    // 销毁图表实例
    visitChart?.dispose();
    performanceChart?.dispose();
  });
});
</script>

<style scoped>
.analysis-container {
  padding: 24px;
  background-color: var(--bg-color, #f0f2f5);
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

.analysis-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(circle at 10% 20%, rgba(0, 0, 0, 0.02) 0%, transparent 8%),
    radial-gradient(circle at 90% 30%, rgba(0, 0, 0, 0.02) 0%, transparent 8%),
    radial-gradient(circle at 30% 70%, rgba(0, 0, 0, 0.02) 0%, transparent 8%),
    radial-gradient(circle at 80% 90%, rgba(0, 0, 0, 0.02) 0%, transparent 8%);
  z-index: 0;
  pointer-events: none;
  opacity: 0.7;
}

.top-section {
  position: relative;
  z-index: 1;
}

/* 卡片通用样式 */
.analysis-card {
  height: 100%;
  transition: all 0.3s ease;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  position: relative;
  backdrop-filter: blur(10px);
}

.analysis-card::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 5px;
  background: linear-gradient(90deg, var(--primary-color, #1890ff), #7546c9);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.analysis-card:hover::after {
  opacity: 1;
}

.analysis-card:hover {
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  transform: translateY(-5px);
}

.analysis-card :deep(.ant-card-head) {
  padding: 0 20px;
  min-height: 50px;
  font-weight: 500;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  position: relative;
  overflow: hidden;
}

.analysis-card :deep(.ant-card-head)::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, rgba(0, 0, 0, 0.01), transparent);
  pointer-events: none;
}

.analysis-card :deep(.ant-card-head-title) {
  padding: 12px 0;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.analysis-card :deep(.ant-card-body) {
  padding: 20px;
  position: relative;
}

/* 状态卡片样式 */
.status-card::after {
  background: linear-gradient(90deg, #1890ff, #36cfc9);
}

.status-card :deep(.ant-progress-text) {
  font-weight: 500;
  color: var(--primary-color, #1890ff);
}

.status-card :deep(.ant-progress-inner) {
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
}

.status-card :deep(.ant-progress-circle-trail) {
  stroke: rgba(0, 0, 0, 0.04);
}

.status-item {
  text-align: center;
  padding: 10px;
  border-radius: 8px;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.status-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at center, rgba(0, 0, 0, 0.01), transparent);
  opacity: 0;
  transition: opacity 0.3s;
}

.status-item:hover {
  background-color: rgba(0, 0, 0, 0.02);
}

.status-item:hover::before {
  opacity: 1;
}

.status-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--primary-color, #1890ff);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.status-label {
  font-size: 14px;
  color: rgba(0, 0, 0, 0.45);
  text-align: center;
  margin-top: 8px;
}

/* 访问统计卡片 */
.visits-card::after {
  background: linear-gradient(90deg, #722ed1, #eb2f96);
}

.data-overview {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.data-item {
  text-align: center;
  flex: 1;
  padding: 10px;
  position: relative;
}

.data-value {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.data-label {
  font-size: 14px;
  color: rgba(0, 0, 0, 0.45);
  margin: 5px 0;
}

.data-trend {
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.data-trend.up {
  color: #52c41a;
}

.data-trend.down {
  color: #f5222d;
}

.data-trend :deep(.anticon) {
  margin-left: 4px;
}

.visit-chart {
  height: 200px;
}

/* 性能监控卡片 */
.performance-card::after {
  background: linear-gradient(90deg, #fa8c16, #fa541c);
}

.performance-tabs {
  margin-bottom: 20px;
  text-align: center;
}

.performance-chart {
  height: 250px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .analysis-container {
    padding: 16px;
  }
  
  .analysis-card {
    border-radius: 12px;
  }
  
  .analysis-card :deep(.ant-card-body) {
    padding: 16px;
  }
  
  .analysis-card :deep(.ant-card-head-title) {
    font-size: 15px;
  }
  
  .status-value {
    font-size: 20px;
  }
  
  .data-value {
    font-size: 20px;
  }
  
  .visit-chart,
  .performance-chart {
    height: 200px;
  }
  
  .data-overview {
    flex-direction: column;
    align-items: center;
  }
  
  .data-item {
    width: 100%;
    max-width: 150px;
    margin-bottom: 10px;
  }
}

/* 深色模式适配 */
.dark-mode .analysis-background {
  background-image: 
    radial-gradient(circle at 10% 20%, rgba(255, 255, 255, 0.02) 0%, transparent 8%),
    radial-gradient(circle at 90% 30%, rgba(255, 255, 255, 0.02) 0%, transparent 8%),
    radial-gradient(circle at 30% 70%, rgba(255, 255, 255, 0.02) 0%, transparent 8%),
    radial-gradient(circle at 80% 90%, rgba(255, 255, 255, 0.02) 0%, transparent 8%);
}

.dark-mode .analysis-card {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  background-color: rgba(30, 30, 30, 0.8);
}

.dark-mode .data-value {
  color: rgba(255, 255, 255, 0.85);
}

.dark-mode .status-item:hover,
.dark-mode .data-item:hover {
  background-color: rgba(255, 255, 255, 0.02);
}

.dark-mode :deep(.ant-card-head) {
  border-bottom-color: rgba(255, 255, 255, 0.08);
}

.dark-mode :deep(.ant-card-head)::before {
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.01), transparent);
}

/* 添加进度条居中样式 */
.progress-col {
  display: flex;
  justify-content: center;
  align-items: center;
}

.progress-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}
</style> 