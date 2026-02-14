<template>
  <v-chart class="chart" :option="option" autoresize />
</template>

<script setup>
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import {
  GridComponent,
  TooltipComponent,
  LegendComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { ref, watch } from 'vue'

use([
  CanvasRenderer,
  LineChart,
  GridComponent,
  TooltipComponent,
  LegendComponent
])

const props = defineProps({
  timeRange: {
    type: String,
    default: 'week'
  }
})

// 模拟数据
const getChartData = (range) => {
  const data = {
    week: {
      dates: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      values: [820, 932, 901, 934, 1290, 1330, 1320]
    },
    month: {
      dates: Array.from({ length: 30 }, (_, i) => `${i + 1}日`),
      values: Array.from({ length: 30 }, () => Math.floor(Math.random() * 2000) + 500)
    },
    year: {
      dates: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
      values: Array.from({ length: 12 }, () => Math.floor(Math.random() * 5000) + 1000)
    }
  }
  return data[range]
}

const option = ref({
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
    data: []
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '访问量',
      type: 'line',
      smooth: true,
      data: [],
      itemStyle: {
        color: '#1890ff'
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            {
              offset: 0,
              color: 'rgba(24,144,255,0.3)'
            },
            {
              offset: 1,
              color: 'rgba(24,144,255,0.1)'
            }
          ]
        }
      }
    }
  ]
})

watch(() => props.timeRange, (newRange) => {
  const data = getChartData(newRange)
  option.value.xAxis.data = data.dates
  option.value.series[0].data = data.values
}, { immediate: true })
</script>

<style scoped>
.chart {
  width: 100%;
  height: 100%;
}
</style>