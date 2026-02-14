import SockJS from 'sockjs-client'
import Stomp from 'stompjs'

class WebSocketManager {
  constructor() {
    this.stompClient = null
    this.isConnected = false
    this.subscriptions = new Map()
  }

  /**
   * 连接WebSocket
   * @param {string} token - 认证token
   * @param {function} onConnect - 连接成功回调
   * @param {function} onError - 连接错误回调
   */
  connect(token, onConnect, onError) {
    if (this.isConnected) {
      console.warn('WebSocket已经连接')
      return
    }

    try {
      // 使用主应用的WebSocket端点
      const socket = new SockJS('/ws')
      this.stompClient = Stomp.over(socket)
      
      // 禁用调试信息
      this.stompClient.debug = null

      this.stompClient.connect({
        Authorization: `Bearer ${token}`,
        userId: '1' // 这里可以从token中解析用户ID
      }, (frame) => {
        console.log('WebSocket连接成功:', frame)
        this.isConnected = true
        onConnect && onConnect(frame)
      }, (error) => {
        console.error('WebSocket连接失败:', error)
        this.isConnected = false
        onError && onError(error)
      })
    } catch (error) {
      console.error('WebSocket连接异常:', error)
      onError && onError(error)
    }
  }

  /**
   * 订阅消息
   * @param {string} destination - 订阅目标
   * @param {function} callback - 消息回调
   */
  subscribe(destination, callback) {
    if (!this.isConnected || !this.stompClient) {
      console.error('WebSocket未连接，无法订阅消息')
      return
    }

    const subscription = this.stompClient.subscribe(destination, (message) => {
      callback && callback(message)
    })

    this.subscriptions.set(destination, subscription)
    console.log(`已订阅: ${destination}`)
  }

  /**
   * 取消订阅
   * @param {string} destination - 订阅目标
   */
  unsubscribe(destination) {
    const subscription = this.subscriptions.get(destination)
    if (subscription) {
      subscription.unsubscribe()
      this.subscriptions.delete(destination)
      console.log(`已取消订阅: ${destination}`)
    }
  }

  /**
   * 发送消息
   * @param {string} destination - 发送目标
   * @param {object} message - 消息内容
   */
  send(destination, message) {
    if (!this.isConnected || !this.stompClient) {
      console.error('WebSocket未连接，无法发送消息')
      return
    }

    this.stompClient.send(destination, {}, JSON.stringify(message))
    console.log(`消息已发送到: ${destination}`, message)
  }

  /**
   * 断开连接
   */
  disconnect() {
    if (this.stompClient && this.isConnected) {
      // 取消所有订阅
      this.subscriptions.forEach((subscription, destination) => {
        subscription.unsubscribe()
      })
      this.subscriptions.clear()

      // 断开连接
      this.stompClient.disconnect(() => {
        console.log('WebSocket连接已断开')
      })
      
      this.isConnected = false
      this.stompClient = null
    }
  }

  /**
   * 获取连接状态
   */
  getConnectionStatus() {
    return this.isConnected
  }
}

// 创建单例实例
const websocketManager = new WebSocketManager()

export default websocketManager
