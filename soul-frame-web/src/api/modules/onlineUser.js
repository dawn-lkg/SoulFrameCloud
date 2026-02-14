import request from '@/utils/request'

// 获取在线用户列表
export async function getOnlineUserList(params) {
  return request.get('/system/online/list', { params })
}

// 强退用户
export async function forceLogout(userId) {
    return request.delete(`/system/online/${userId}`)
}

// 批量强退
export async function batchForceLogout(userIds) {
    return request.batchDel('/system/online/batch', userIds)
}

// 获取在线用户数量
export async function getOnlineUserCount() {
    return request.get('/system/online/count')
}

