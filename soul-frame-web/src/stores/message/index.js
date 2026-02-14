import { defineStore } from 'pinia'
import { message as messageApi } from '@/api'

export const useMessageStore = defineStore('message', {
  state: () => ({
    messages: [],
    unreadCount: 0,
    loading: false,
    currentPage: 1,
    pageSize: 10,
    total: 0
  }),
  getters: {
    hasUnread: (state) => state.unreadCount > 0
  },
  actions: {
    // 初始化
    async init() {
      await this.fetchMessages()
      await this.getUnreadCount()
    },
    // 获取消息列表
    async fetchMessages() {
      this.loading = true
      try {
        const data = await messageApi.getMessages({pageNum: this.currentPage,pageSize: this.pageSize})        
        this.messages = data.records || []
        this.total = data.total || 0
        return data
      } catch (error) {
        console.error('获取消息列表失败:', error)
        return Promise.reject(error)
      } finally {
        this.loading = false
      }
    },

    // 获取未读消息数量
    async getUnreadCount() {
      try {
        const count = await messageApi.getUnreadCount()
        this.unreadCount = count
        return count
      } catch (error) {
        console.error('获取未读消息数量失败:', error)
        return Promise.reject(error)
      }
    },

    // 标记消息为已读
    async markAsRead(id) {
      try {
        await messageApi.markAsRead(id)
        // 更新本地状态
        const index = this.messages.findIndex(item => item.id === id)
        if (index !== -1) {
          this.messages[index].read = true
          if (this.unreadCount > 0) this.unreadCount--
        }
        return true
      } catch (error) {
        console.error('标记消息为已读失败:', error)
        return Promise.reject(error)
      }
    },

    // 标记所有消息为已读
    async markAllAsRead() {
      try {
        await messageApi.markAllAsRead()
        // 更新本地状态
        this.messages = this.messages.map(item => ({
          ...item,
          read: true
        }))
        this.unreadCount = 0
        return true
      } catch (error) {
        console.error('标记所有消息为已读失败:', error)
        return Promise.reject(error)
      }
    },

    // 删除消息
    async deleteMessage(id) {
      try {
        await messageApi.deleteMessage(id)
        // 更新本地状态
        const index = this.messages.findIndex(item => item.id === id)
        if (index !== -1) {
          const isUnread = !this.messages[index].read
          this.messages.splice(index, 1)
          if (isUnread && this.unreadCount > 0) this.unreadCount--
        }
        return true
      } catch (error) {
        console.error('删除消息失败:', error)
        return Promise.reject(error)
      }
    },

    // 发送消息
    async sendMessage(data) {
      try {
        return await messageApi.sendMessage(data)
      } catch (error) {
        console.error('发送消息失败:', error)
        return Promise.reject(error)
      }
    },

    // 获取消息详情
    async getMessageDetail(id) {
      try {
        return await messageApi.getMessageDetail(id)
      } catch (error) {
        console.error('获取消息详情失败:', error)
        return Promise.reject(error)
      }
    },

    // 添加新消息（用于WebSocket推送）
    addMessage(message) {
      this.messages.unshift(message)
      if (!message.read) {
        this.unreadCount++
      }
    }
  }
})
