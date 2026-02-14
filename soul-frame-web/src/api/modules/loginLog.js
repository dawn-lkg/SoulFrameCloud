import request from '@/utils/request'

// 分页获取登录日志列表
export async function getLoginLogPage(params) {
  return request.get('/system/loginLog/page', { params })
}

// 清空登录日志
export async function clearLoginLog() {
  return request.delete('/system/loginLog/clear')
}

// 删除登录日志
export async function deleteLoginLog(id) {
  return request.delete(`/system/loginLog/${id}`)
}

// 批量删除登录日志
export async function batchDeleteLoginLog(ids) {
  return request.batchDel('/system/loginLog/batch', ids)
}

// 导出登录日志
export async function exportLoginLog(params) {
  return request.download('/system/loginLog/export', '登录日志导出.xlsx', {}, params)
}

// 获取当前用户的登录日志
export async function getCurrentUserLoginLog(params) {
    return request.get('/system/loginLog/current', {params})
}