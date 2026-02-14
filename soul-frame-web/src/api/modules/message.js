import request from "@/utils/request"

// 获取所有消息
export async function getMessages(params) {
    return await request.get('/message/list', { params })
}

// 获取未读消息数量
export async function getUnreadCount() {
    return await request.get('/message/unread/count')
}

// 标记消息为已读
export async function markAsRead(id) {
    return await request.put(`/message/read/${id}`)
}

// 标记所有消息为已读
export async function markAllAsRead() {
    return await request.put('/message/read/all')
}

// 删除消息
export async function deleteMessage(id) {
    return await request.delete(`/message/${id}`)
}

// 发送消息
export async function sendMessage(data) {
    return await request.post('/message/send', data)
}

// 获取消息详情
export async function getMessageDetail(id) {
    return await request.get(`/message/${id}`)
}
