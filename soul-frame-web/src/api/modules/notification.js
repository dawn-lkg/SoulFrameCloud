import request from "@/utils/request"

// 获取所有通知
export async function getNotifications(params) {
    return await request.get('/notification/list', { params })
}

// 获取未读通知数量
export async function getUnreadCount() {
    return await request.get('/notification/unread/count')
}

// 标记通知为已读
export async function markAsRead(id) {
    return await request.put(`/notification/read/${id}`)
}

// 标记所有通知为已读
export async function markAllAsRead() {
    return await request.put('/notification/read/all')
}

// 删除通知
export async function deleteNotification(id) {
    return await request.delete(`/notification/${id}`)
}

// 清空所有通知
export async function clearAllNotifications() {
    return await request.delete('/notification/clear')
}

// 发送手动通知
export async function sendNotification(data) {
    return await request.post('/notification/send', data)
}