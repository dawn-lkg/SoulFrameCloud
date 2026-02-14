import { defineStore } from 'pinia'
import { todo as todoApi } from '@/api'

export const useTodoStore = defineStore('todo', {
  state: () => ({
    todoList: [],
    pendingCount: 0,
    loading: false,
    currentPage: 1,
    pageSize: 10,
    total: 0
  }),
  getters: {
    hasPending: (state) => state.pendingCount > 0
  },
  actions: {
    // 初始化
    init() {
      this.fetchTodoList()
      this.getPendingCount()
    },
    // 获取待办事项列表
    async fetchTodoList() {
      this.loading = true
      try {
        const data = await todoApi.getTodoList({pageNum: this.currentPage,pageSize: this.pageSize})
        this.todoList = data.records || []
        this.total = data.total || 0
        return data
      } catch (error) {
        console.error('获取待办事项列表失败:', error)
        return Promise.reject(error)
      } finally {
        this.loading = false
      }
    },

    // 获取待办事项详情
    async getTodoDetail(id) {
      try {
        return await todoApi.getTodoDetail(id)
      } catch (error) {
        console.error('获取待办事项详情失败:', error)
        return Promise.reject(error)
      }
    },

    // 创建待办事项
    async createTodo(data) {
      try {
        const res = await todoApi.createTodo(data)
        // 更新本地状态
        if (res) {
          this.todoList.unshift(res)
          this.pendingCount++
        }
        return res
      } catch (error) {
        console.error('创建待办事项失败:', error)
        return Promise.reject(error)
      }
    },

    // 更新待办事项
    async updateTodo(id, data) {
      try {
        const res = await todoApi.updateTodo(id, data)
        // 更新本地状态
        const index = this.todoList.findIndex(item => item.id === id)
        if (index !== -1) {
          this.todoList[index] = { ...this.todoList[index], ...data }
        }
        return res
      } catch (error) {
        console.error('更新待办事项失败:', error)
        return Promise.reject(error)
      }
    },

    // 删除待办事项
    async deleteTodo(id) {
      try {
        await todoApi.deleteTodo(id)
        // 更新本地状态
        const index = this.todoList.findIndex(item => item.id === id)
        if (index !== -1) {
          const isPending = this.todoList[index].status === 'pending'
          this.todoList.splice(index, 1)
          if (isPending && this.pendingCount > 0) this.pendingCount--
        }
        return true
      } catch (error) {
        console.error('删除待办事项失败:', error)
        return Promise.reject(error)
      }
    },

    // 标记待办事项为已完成
    async completeTodo(id) {
      try {
        await todoApi.completeTodo(id)
        // 更新本地状态
        const index = this.todoList.findIndex(item => item.id === id)
        if (index !== -1) {
          const wasPending = this.todoList[index].status === 'pending'
          this.todoList[index].status = 'completed'
          if (wasPending && this.pendingCount > 0) this.pendingCount--
        }
        return true
      } catch (error) {
        console.error('标记待办事项为已完成失败:', error)
        return Promise.reject(error)
      }
    },

    // 获取待处理的待办事项数量
    async getPendingCount() {
      try {
        const count = await todoApi.getPendingCount()
        this.pendingCount = count
        return count
      } catch (error) {
        console.error('获取待处理的待办事项数量失败:', error)
        return Promise.reject(error)
      }
    },

    // 添加新待办事项（用于WebSocket推送）
    addTodo(todo) {
      this.todoList.unshift(todo)
      if (todo.status === 'pending') {
        this.pendingCount++
      }
    }
  }
})
