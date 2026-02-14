import request from '@/utils/request'

// 获取实时性能指标
export async function realtime() {
    return request.get('/performanceMetrics/realtime')
}

// 按小时获取性能指标
export async function getPerformanceMetricsByHour(params) {
    return request.get('/performanceMetrics/hour',{params})
}

// 按周获取性能指标
export async function getPerformanceMetricsByWeek(params) {
    return request.get('/performanceMetrics/week',{params})
}
