import request from "@/utils/request"

// 获取所有待办事项
export async function getTodoList(params) {
    return await request.get('/todo/list', { params })
}

// 获取待办事项详情
export async function getTodoDetail(id) {
    return await request.get(`/todo/${id}`)
}

// 创建待办事项
export async function createTodo(data) {
    return await request.post('/todo', data)
}

// 更新待办事项
export async function updateTodo(id, data) {
    return await request.put(`/todo/${id}`, data)
}

// 删除待办事项
export async function deleteTodo(id) {
    return await request.delete(`/todo/${id}`)
}

// 标记待办事项为已完成
export async function completeTodo(id) {
    return await request.put(`/todo/complete/${id}`)
}

// 标记待办事项为未完成
export async function uncompleteTodo(id) {
    return await request.put(`/todo/incomplete/${id}`)
}

// 获取待处理的待办事项数量
export async function getPendingCount() {
    return await request.get('/todo/pending/count')
}

// 更新待办事项状态
export async function updateTodoStatus(id, data) {
    return await request.put(`/todo/status/${id}`, data)
}
