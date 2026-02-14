import {computed, onUnmounted, ref, shallowRef} from 'vue'
import {getSSEClient} from './sseClient'
import {useAuthStore} from '@/stores/auth'

export function useSSE(url, options = {}) {
    const authStore = useAuthStore()
    const {
        immediate = true,
        transform,
        headers,
        method = 'GET',
        body,
        retry = true,
        retryDelay = 5000,
        maxRetries = 5,
        keepAlive = true
    } = options

    // 响应式状态
    const data = ref([])
    const lastMessage = shallowRef(null)
    const error = shallowRef(null)
    const status = ref('disconnected')
    const retryCount = ref(0)

    // 计算属性
    const isConnected = computed(() => status.value === 'connected')
    const isConnecting = computed(() => status.value === 'connecting')
    const hasError = computed(() => status.value === 'error')

    // SSE客户端
    const client = getSSEClient()
    const connectionId = `sse_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`

    // 连接方法
    const connect = async () => {
        try {
            error.value = null
            status.value = 'connecting'

            await client.connect(connectionId, url, {
                method,
                headers: typeof headers === 'function' ? {
                        ...headers(),
                        [authStore.tokenName]: `Bearer ${authStore.token}`
                    } :
                    {...headers, [authStore.tokenName]: `Bearer ${authStore.token}`},
                body: typeof body === 'function' ? body() : body,
                retry,
                retryDelay,
                maxRetries,
                transform,

                onOpen: (response) => {
                    status.value = 'connected'
                    retryCount.value = 0
                    options.onOpen?.(response)
                },

                onMessage: (message, event) => {
                    if (event?.event === 'heartbeat') {
                        return
                    }

                    lastMessage.value = message

                    if (keepAlive) {
                        data.value = [...data.value, message]
                    } else {
                        data.value = [message]
                    }

                    options.onMessage?.(message, event?.event)
                },

                onError: (err) => {
                    error.value = err
                    status.value = 'error'
                    retryCount.value++
                    options.onError?.(err)
                },

                onClose: () => {
                    status.value = 'closed'
                    options.onClose?.()
                }
            })
        } catch (err) {
            error.value = err
            status.value = 'failed'
            throw err
        }
    }

    // 断开连接
    const disconnect = () => {
        console.log("disconnect", connectionId)
        client.disconnect(connectionId)
        status.value = 'disconnected'
    }

    // 清空数据
    const clearData = () => {
        data.value = []
        lastMessage.value = null
    }

    // 重新连接
    const reconnect = async () => {
        disconnect()
        await new Promise(resolve => setTimeout(resolve, 100))
        return connect()
    }

    // 发送消息（如果支持）
    const send = async (message) => {
        if (!isConnected.value) {
            throw new Error('Not connected')
        }

        // 这里可以通过其他HTTP请求发送消息
        // 或者使用WebSocket等双向通信
        console.warn('SSE is read-only, use WebSocket for bidirectional communication')
    }

    // 立即连接
    if (immediate) {
        connect()
    }

    // 组件卸载时断开连接
    onUnmounted(() => {
        disconnect()
    })

    return {
        // 状态
        data,
        lastMessage,
        error,
        status,
        retryCount,

        // 计算属性
        isConnected,
        isConnecting,
        hasError,

        // 方法
        connect,
        disconnect,
        reconnect,
        clearData,
        send
    }
}