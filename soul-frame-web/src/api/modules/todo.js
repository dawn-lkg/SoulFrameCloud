import request from "@/utils/request"

// 获取所有待办事项
export async function getTodoList(params) {
    return await request.get('/notice/todo/list', { params })
}

// 获取待办事项详情
export async function getTodoDetail(id) {
    return await request.get(`/notice/todo/${id}`)
}

// 创建待办事项
export async function createTodo(data) {
    return await request.post('/notice/todo', data)
}

// 更新待办事项
export async function updateTodo(id, data) {
    return await request.put(`/notice/todo/${id}`, data)
}

// 删除待办事项
export async function deleteTodo(id) {
    return await request.delete(`/notice/todo/${id}`)
}

// 标记待办事项为已完成
export async function completeTodo(id) {
    return await request.put(`/notice/todo/complete/${id}`)
}

// 标记待办事项为未完成
export async function uncompleteTodo(id) {
    return await request.put(`/notice/todo/incomplete/${id}`)
}

// 获取待处理的待办事项数量
export async function getPendingCount() {
    return await request.get('/notice/todo/pending/count')
}

// 更新待办事项状态
export async function updateTodoStatus(id, data) {
    return await request.put(`/notice/todo/status/${id}`, data)
}
