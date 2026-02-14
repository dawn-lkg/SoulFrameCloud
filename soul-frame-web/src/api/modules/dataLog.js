import request from '@/utils/request'

// 分页获取数据日志列表
export async function getDataLogPage(params) {
  return request.get('/system/operLog/page', { params })
}

// 删除数据日志
export async function deleteDataLog(id) {
  return request.delete(`/system/operLog/${id}`)
}

// 批量删除数据日志
export async function batchDeleteDataLog(ids) {
  return request.batchDel('/system/operLog/batch', ids)
}

// 获取数据日志详情
export async function getDataLogDetail(id) {
  return request.get(`/system/operLog/${id}`)
}


// 清空数据日志
export async function clearDataLog() {
  return request.delete('/system/operLog/clear')
}

// 导出数据日志
export async function exportDataLog(params) {
  return request.download('/system/operLog/export', '数据日志导出.xlsx',{},params)
}

// 获取访问统计
export async function getVisitingStatistic() {
  return request.get('/system/operLog/visitingStatistic')
}


