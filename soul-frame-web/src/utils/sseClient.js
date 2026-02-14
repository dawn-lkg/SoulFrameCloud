import {fetchEventSource} from '@microsoft/fetch-event-source'

class SSEClient {
    constructor(config = {}) {
        this.baseURL = import.meta.env.VITE_API_BASE_URL || ''
        this.defaultHeaders = {...config.headers}
        this.defaultRetryDelay = config.retryDelay || 5000
        this.maxRetries = config.maxRetries || 5
        this.connections = new Map()
    }

    /**
     * 创建SSE连接
     * @param {string} id - 连接唯一标识
     * @param {string} url - 连接地址
     * @param {object} options - 配置选项
     */
    connect = async (id, url, options = {}) => {
        if (this.connections.has(id)) {
            this.disconnect(id)
        }

        const controller = new AbortController()
        const connection = {
            id,
            url: this.baseURL + url,
            controller,
            status: 'connecting',
            retryCount: 0,
            startTime: Date.now()
        }

        this.connections.set(id, connection)

        const {
            method = 'GET',
            headers = {},
            body,
            onMessage,
            onError,
            onOpen,
            onClose,
            transform,
            retryDelay = this.defaultRetryDelay,
            maxRetries = this.maxRetries,
            retry = true
        } = options

        try {
            await fetchEventSource(connection.url, {
                method,
                headers: {
                    ...this.defaultHeaders,
                    ...headers
                },
                body: body ? JSON.stringify(body) : undefined,
                signal: controller.signal,

                async onopen(response) {
                    connection.status = 'connected'
                    connection.retryCount = 0
                    if (response.ok) {
                        onOpen?.(response)
                    } else if (response.status >= 400 && response.status < 500) {
                        // 客户端错误，不重试
                        throw new FetchEventSourceError(
                            `Client error: ${response.status}`,
                            response.status
                        )
                    } else {
                        // 服务器错误，可能重试
                        throw new FetchEventSourceError(
                            `Server error: ${response.status}`,
                            response.status
                        )
                    }
                },

                onmessage: (event) => {
                    try {
                        const data = transform ? transform(event) : this.parseMessage(event)
                        onMessage?.(data, event)
                    } catch (error) {
                        console.error('Message parsing error:', error)
                        onError?.(error)
                    }
                },

                onerror(err) {
                    connection.status = 'error'
                    onError?.(err)

                    // 用户主动取消
                    if (err.name === 'AbortError') {
                        return undefined
                    }

                    // 不需要重试
                    if (!retry) {
                        return undefined
                    }

                    // 达到最大重试次数
                    if (connection.retryCount >= maxRetries) {
                        connection.status = 'failed'
                        onError?.(new Error('Max retries reached'))
                        return undefined
                    }

                    connection.retryCount++

                    // 计算重试延迟（指数退避）
                    const delay = Math.min(
                        retryDelay * Math.pow(2, connection.retryCount - 1),
                        30000
                    )

                    console.log(`Retry ${connection.retryCount}/${maxRetries} after ${delay}ms`)
                    return delay
                },

                onclose() {
                    connection.status = 'closed'
                    onClose?.()
                }
            })
        } catch (error) {
            connection.status = 'failed'
            this.connections.delete(id)
            throw error
        }
    }

    /**
     * 断开指定连接
     */
    disconnect(id) {
        const connection = this.connections.get(id)
        if (connection) {
            connection.controller.abort()
            this.connections.delete(id)
        }
    }

    /**
     * 断开所有连接
     */
    disconnectAll() {
        for (const [id] of this.connections) {
            this.disconnect(id)
        }
    }

    /**
     * 获取连接状态
     */
    getConnectionStatus(id) {
        return this.connections.get(id)?.status || 'disconnected'
    }

    /**
     * 获取所有连接
     */
    getAllConnections() {
        return Array.from(this.connections.entries()).map(([id, conn]) => ({
            id,
            url: conn.url,
            status: conn.status,
            retryCount: conn.retryCount,
            duration: Date.now() - conn.startTime
        }))
    }

    /**
     * 默认消息解析
     */
    parseMessage(event) {
        try {
            return JSON.parse(event.data)
        } catch {
            return event.data
        }
    }
}

// 自定义错误类
class FetchEventSourceError extends Error {
    constructor(message, status) {
        super(message)
        this.name = 'FetchEventSourceError'
        this.status = status
    }
}

// 单例模式
let instance = null

export const createSSEClient = (config) => {
    if (!instance) {
        instance = new SSEClient(config)
    }
    return instance
}

export const getSSEClient = () => {
    if (!instance) {
        instance = new SSEClient()
    }
    return instance
}

export default SSEClient