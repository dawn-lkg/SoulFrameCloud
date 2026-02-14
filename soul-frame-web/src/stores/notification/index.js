import { defineStore } from 'pinia'
import { notification as notificationApi } from '@/api'

export const useNotificationStore = defineStore('notification', {
  state: () => ({
    notifications: [],
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
      await this.fetchNotifications()
      await this.getUnreadCount()
    },
    
    // 获取通知列表
    async fetchNotifications() {     
      this.loading = true
      try {
        const data = await notificationApi.getNotifications({pageNum: this.currentPage,pageSize: this.pageSize})
        this.notifications = data.records || []
        this.total = data.total || 0
        return data
      } catch (error) {
        console.error('获取通知列表失败:', error)
        return Promise.reject(error)
      } finally {
        this.loading = false
      }
    },

    // 获取未读通知数量
    async getUnreadCount() {
      try {
        const count = await notificationApi.getUnreadCount()
        this.unreadCount = count
        return count
      } catch (error) {
        console.error('获取未读通知数量失败:', error)
        return Promise.reject(error)
      }
    },

    // 标记通知为已读
    async markAsRead(id) {
      try {
        await notificationApi.markAsRead(id)
        // 更新本地状态
        const index = this.notifications.findIndex(item => item.id === id)
        if (index !== -1) {
          this.notifications[index].read = true
          if (this.unreadCount > 0) this.unreadCount--
        }
        return true
      } catch (error) {
        console.error('标记通知为已读失败:', error)
        return Promise.reject(error)
      }
    },

    // 标记所有通知为已读
    async markAllAsRead() {
      try {
        await notificationApi.markAllAsRead()
        // 更新本地状态
        this.notifications = this.notifications.map(item => ({
          ...item,
          read: true
        }))
        this.unreadCount = 0
        return true
      } catch (error) {
        console.error('标记所有通知为已读失败:', error)
        return Promise.reject(error)
      }
    },

    // 删除通知
    async deleteNotification(id) {
      try {
        await notificationApi.deleteNotification(id)
        // 更新本地状态
        const index = this.notifications.findIndex(item => item.id === id)
        if (index !== -1) {
          const isUnread = !this.notifications[index].read
          this.notifications.splice(index, 1)
          if (isUnread && this.unreadCount > 0) this.unreadCount--
        }
        return true
      } catch (error) {
        console.error('删除通知失败:', error)
        return Promise.reject(error)
      }
    },

    // 清空所有通知
    async clearAllNotifications() {
      try {
        await notificationApi.clearAllNotifications()
        // 更新本地状态
        this.notifications = []
        this.unreadCount = 0
        return true
      } catch (error) {
        console.error('清空所有通知失败:', error)
        return Promise.reject(error)
      }
    },

    // 添加新通知
    addNotification(notification) {
      this.notifications.unshift(notification)
      if (!notification.read) {
        this.unreadCount++
      }
    },
    
    // 发送手动通知
    async sendNotification(data) {
      try {
        return await notificationApi.sendNotification(data)
      } catch (error) {
        console.error('发送通知失败:', error)
        return Promise.reject(error)
      }
    }
  }
})
