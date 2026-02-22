import request from '@/utils/request'

// 获取服务器信息
export const getServerInfo = () => {
  return request.get('/system/monitor/server')
}

// 获取cpu监控信息
export const getCpuMonitorInfo = () => {
  return request.get('/system/monitor/server/cpu')
}

// 获取内存监控信息
export const getMemoryMonitorInfo = () => {
    return request.get('/system/monitor/server/mem')
}

// 获取运行时长
export const getServerRunTime = () => {
    return request.get('/system/monitor/server/runTime')
}
