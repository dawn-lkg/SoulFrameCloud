<template>
  <div>
    <a-button type="primary" class="chat-float-btn" @click="visible = true" shape="circle">
      <template #icon>
        <MessageOutlined :style="{ fontSize: '24px' }" />
      </template>
    </a-button>

    <a-drawer
      v-model:open="visible"
      :width="drawerWidth"
      placement="right"
      :mask="true"
      :closable="true"
      :destroy-on-close="false"
      title="即时沟通"
    >
      <div class="chat-container">
        <div class="chat-sider">
          <a-input-search v-model:value="keyword" placeholder="搜索联系人" allow-clear @search="onSearch" />
          <a-list :data-source="filteredUsers" class="user-list" :split="false">
            <template #renderItem="{ item }">
              <a-list-item :class="['user-item', { active: item.id === selectedUserId }]" @click="selectUser(item.id)">
                <a-badge :count="unreadMap[item.id] || 0" :overflow-count="99">
                  <a-avatar :size="32">{{ item.name.slice(0,1) }}</a-avatar>
                </a-badge>
                <div class="user-meta">
                  <div class="user-name-row">
                    <div class="user-name">{{ item.name }}</div>
                    <div class="user-time">{{ lastMsgTime(item.id) }}</div>
                  </div>
                  <div class="user-desc" :title="lastMsgPreview(item.id)">{{ lastMsgPreview(item.id) }}</div>
                </div>
              </a-list-item>
            </template>
          </a-list>
        </div>

        <div class="chat-main">
          <div class="chat-header">
            <div class="chat-title">
              <a-avatar :size="32" v-if="currentUser">{{ currentUserInitial }}</a-avatar>
              <span class="chat-title-text">{{ currentUser?.name || '未选择联系人' }}</span>
              <span class="status-dot" v-if="currentUser"></span>
            </div>
            <div class="chat-header-actions">
              <a-button size="small" type="text" @click="clearUnread(selectedUserId)" :disabled="!selectedUserId">清除未读</a-button>
            </div>
          </div>

          <div v-if="!selectedUserId" class="chat-empty">
            <div class="empty-tip">请选择左侧联系人开始聊天</div>
          </div>

          <div v-else class="chat-messages" ref="messagesRef" @scroll="handleMessageScroll">
            <div v-for="(msg, idx) in currentMessages" :key="idx" :class="['msg-row', msg.from === 'me' ? 'me' : 'other']" :data-message-id="msg.messageId">
              <a-avatar :size="32">{{ msg.from === 'me' ? '我' : currentUserInitial }}</a-avatar>
              <div class="bubble">
                <div class="content">{{ msg.content }}</div>
                <div class="time">{{ msg.time }}</div>
              </div>
            </div>
          </div>

          <div class="chat-input">
            <div class="toolbar">
              <a-button type="text" size="small" @click="insertEmoji('😊')">😊</a-button>
              <a-button type="text" size="small" @click="attach">📎</a-button>
              <span class="hint">回车发送，Shift+回车换行</span>
            </div>
            <a-input-text-area
              v-model:value="draft"
              :rows="3"
              placeholder="输入消息..."
              @keydown.enter.exact.prevent="send"
            />
            <div class="input-actions">
              <a-button type="primary" :disabled="!draft.trim() || !selectedUserId" @click="send">发送</a-button>
            </div>
          </div>
        </div>
      </div>
    </a-drawer>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onUnmounted } from 'vue'
import { MessageOutlined } from '@ant-design/icons-vue'

const visible = ref(false)
const drawerWidth = ref(getDrawerWidth())

function getDrawerWidth() {
  const vw = window.innerWidth
  if (vw >= 1280) return 900
  if (vw >= 992) return 780
  if (vw >= 768) return 640
  return Math.min(560, Math.floor(vw * 0.95))
}

const handleResize = () => {
  drawerWidth.value = getDrawerWidth()
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})

const keyword = ref('')
const users = ref([
  { id: 'u1', name: '张三', desc: '后端开发' },
  { id: 'u2', name: '李四', desc: '前端开发' },
  { id: 'u3', name: '王五', desc: '测试工程师' }
])
const filteredUsers = computed(() =>
  users.value.filter(u => !keyword.value || u.name.includes(keyword.value) || u.desc.includes(keyword.value))
)
const onSearch = () => {}

const selectedUserId = ref('')
const selectUser = async (id) => {
  selectedUserId.value = id
  clearUnread(id)
  
  // 标记会话已读
  if (id) {
    await markConversationAsRead(id)
  }
  
  nextTick(scrollToBottom)
}

const conversations = ref({
  u1: [
    { from: 'u1', content: '你好，我是张三', time: '09:30' },
    { from: 'me', content: '你好，请问有什么可以帮你？', time: '09:31' }
  ],
  u2: [
    { from: 'u2', content: '构建脚本报错了', time: '10:12' }
  ],
  u3: []
})

const unreadMap = ref({ u1: 1, u2: 2, u3: 0 })
const clearUnread = (id) => { if (!id) return; unreadMap.value[id] = 0 }

const lastMsg = (id) => {
  const list = conversations.value[id] || []
  return list.length ? list[list.length - 1] : null
}
const lastMsgPreview = (id) => {
  const lm = lastMsg(id)
  return lm ? (lm.from === 'me' ? `我: ${lm.content}` : lm.content) : '暂无消息'
}
const lastMsgTime = (id) => {
  const lm = lastMsg(id)
  return lm ? lm.time : ''
}

const currentUser = computed(() => users.value.find(u => u.id === selectedUserId.value))
const currentUserInitial = computed(() => currentUser.value ? currentUser.value.name.slice(0,1) : '对')
const currentMessages = computed(() => conversations.value[selectedUserId.value] || [])

const draft = ref('')
const messagesRef = ref(null)

const scrollToBottom = () => {
  const el = messagesRef.value
  if (!el) return
  el.scrollTop = el.scrollHeight
}

const nowTime = () => new Date().toLocaleTimeString('zh-CN', { hour12: false, hour: '2-digit', minute: '2-digit' })

// 在 script setup 中添加已读状态管理
const messageStatus = ref({}) // 存储消息状态

// 获取消息状态
const getMessageStatus = (messageId) => {
  return messageStatus.value[messageId] || 'unread'
}

// 标记消息已读
const markMessageAsRead = async (messageId) => {
  try {
    // 调用API标记消息已读
    await markMessageReadApi(messageId)
    messageStatus.value[messageId] = 'read'
  } catch (error) {
    console.error('标记消息已读失败:', error)
  }
}

// 标记会话所有消息已读
const markConversationAsRead = async (conversationId) => {
  try {
    await markConversationReadApi(conversationId)
    // 更新当前会话所有消息状态
    currentMessages.value.forEach(msg => {
      messageStatus.value[msg.messageId] = 'read'
    })
  } catch (error) {
    console.error('标记会话已读失败:', error)
  }
}

// 发送消息时创建未读状态
const send = async () => {
  if (!draft.value.trim() || !selectedUserId.value) return
  
  const messageId = generateMessageId() // 生成唯一消息ID
  const msg = { 
    messageId,
    from: 'me', 
    content: draft.value.trim(), 
    time: nowTime(),
    status: 'sent'
  }
  
  // 添加到本地消息列表
  const list = conversations.value[selectedUserId.value] || []
  list.push(msg)
  conversations.value[selectedUserId.value] = list
  
  // 发送到服务器
  try {
    await sendMessageApi({
      conversationId: selectedUserId.value,
      content: msg.content,
      type: 'text'
    })
    msg.status = 'delivered'
  } catch (error) {
    msg.status = 'failed'
    console.error('发送消息失败:', error)
  }
  
  draft.value = ''
  nextTick(scrollToBottom)
}

const insertEmoji = (e) => { draft.value += e }
const attach = () => { /* 这里可接入上传 */ }

// 监听消息滚动，标记可见消息为已读
const handleMessageScroll = () => {
  const el = messagesRef.value
  if (!el || !selectedUserId.value) return
  
  // 检查哪些消息在可视区域内
  const messages = el.querySelectorAll('.msg-row')
  messages.forEach(msgEl => {
    const rect = msgEl.getBoundingClientRect()
    const isVisible = rect.top >= 0 && rect.bottom <= window.innerHeight
    
    if (isVisible) {
      const messageId = msgEl.dataset.messageId
      if (messageId && getMessageStatus(messageId) === 'unread') {
        markMessageAsRead(messageId)
      }
    }
  })
}

// 生成消息ID
const generateMessageId = () => {
  return `msg_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
}
</script>

<style scoped>
.chat-float-btn {
  position: fixed;
  right: 24px;
  bottom: 24px;
  z-index: 1000;
  box-shadow: 0 6px 16px rgba(0,0,0,0.15);
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-container {
  display: flex;
  height: calc(100vh - 140px);
}

.chat-sider {
  width: 280px;
  padding-right: 12px;
  border-right: 1px solid rgba(0,0,0,0.06);
  display: flex;
  flex-direction: column;
}

.user-list {
  margin-top: 12px;
  overflow: auto;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 8px;
  cursor: pointer;
  border-radius: 8px;
}
.user-item:hover { background: rgba(0,0,0,0.03); }
.user-item.active { background: rgba(24,144,255,0.08); }

.user-meta { display: flex; flex-direction: column; width: 100%; }
.user-name-row { display: flex; justify-content: space-between; align-items: center; }
.user-name { font-weight: 600; }
.user-time { color: rgba(0,0,0,0.45); font-size: 12px; margin-left: 8px; }
.user-desc { color: rgba(0,0,0,0.45); font-size: 12px; margin-top: 2px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.chat-main { flex: 1; display: flex; flex-direction: column; padding-left: 12px; }
.chat-header { padding: 8px 0 12px; border-bottom: 1px solid rgba(0,0,0,0.06); display: flex; justify-content: space-between; align-items: center; }
.chat-title { display: flex; align-items: center; gap: 8px; font-size: 16px; font-weight: 600; }
.chat-title-text { vertical-align: middle; }
.status-dot { display: inline-block; width: 8px; height: 8px; background: #52c41a; border-radius: 50%; }

.chat-empty { flex: 1; display: flex; align-items: center; justify-content: center; color: rgba(0,0,0,0.45); }
.empty-tip { font-size: 14px; }

.chat-messages { flex: 1; overflow: auto; padding: 12px 8px; background: linear-gradient(180deg, rgba(0,0,0,0.015), transparent); }
.msg-row { display: flex; align-items: flex-end; gap: 8px; margin-bottom: 12px; }
.msg-row.me { flex-direction: row-reverse; }
.bubble { max-width: 70%; background: #fff; border-radius: 10px; padding: 8px 10px; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
.msg-row.me .bubble { background: #e6f4ff; }
.content { white-space: pre-wrap; word-break: break-word; }
.time { margin-top: 4px; font-size: 12px; color: rgba(0,0,0,0.45); text-align: right; }

.chat-input { border-top: 1px solid rgba(0,0,0,0.06); padding-top: 10px; }
.toolbar { display: flex; align-items: center; gap: 4px; margin-bottom: 6px; }
.hint { color: rgba(0,0,0,0.35); font-size: 12px; margin-left: 6px; }
.input-actions { text-align: right; margin-top: 8px; }
</style> 