<template>
  <div class="message-container">
    <a-card :bordered="false">
      <template #title>
        <div class="card-title">
          <span>消息中心</span>
          <div>
            <a-button type="primary" @click="showNewMessageModal" v-if="activeTab === 'message'" style="margin-right: 10px;">
              发送消息
            </a-button>
            <a-button type="primary" @click="showSendNotificationModal" v-if="activeTab === 'notification'" style="margin-right: 10px;">
              发送通知
            </a-button>
            <a-button type="primary" @click="markAllAsRead">全部已读</a-button>
          </div>
        </div>
      </template>

      <a-tabs v-model:activeKey="activeTab">
        <a-tab-pane key="notification" tab="通知">
          <a-list
            class="message-list"
            :data-source="notificationStore.notifications"
            :loading="notificationLoading"
            :pagination="pagination"
            :locale="{ emptyText: '暂无通知' }"
          >
            <template #renderItem="{ item }">
              <a-list-item>
                <a-list-item-meta>
                  <template #avatar>
                    <a-avatar :style="{ backgroundColor: item.color || '#1890ff' }">
                      <template #icon><notification-outlined /></template>
                    </a-avatar>
                  </template>
                  <template #title>
                    <div class="message-item-title">
                      {{ item.title }}
                      <a-tag v-if="!item.read" color="blue" size="small">新</a-tag>
                    </div>
                  </template>
                  <template #description>
                    <div class="message-item-content">{{ item.content }}</div>
                    <div class="message-item-time">{{ formatTime(item.time) }}</div>
                  </template>
                </a-list-item-meta>
                <template #actions>
                  <a-button type="link" @click="markAsRead(item)" v-if="!item.read">标为已读</a-button>
                  <a-button type="link" danger @click="deleteItem(item)">删除</a-button>
                </template>
              </a-list-item>
            </template>
          </a-list>
        </a-tab-pane>

        <a-tab-pane key="message" tab="消息">
          <a-list
            class="message-list"
            :data-source="messageStore.messages"
            :loading="messageLoading"
            :pagination="pagination"
            :locale="{ emptyText: '暂无消息' }"
          >
            <template #renderItem="{ item }">
              <a-list-item>
                <a-list-item-meta>
                  <template #avatar>
                    <a-avatar :src="item.avatar || ''" :style="{ backgroundColor: item.color || '#1890ff' }">
                      {{ !item.avatar ? item.sender?.charAt(0)?.toUpperCase() : '' }}
                    </a-avatar>
                  </template>
                  <template #title>
                    <div class="message-item-title">
                      {{ item.sender }}
                      <a-tag v-if="!item.read" color="blue" size="small">新</a-tag>
                    </div>
                  </template>
                  <template #description>
                    <div class="message-item-content">{{ item.content }}</div>
                    <div class="message-item-time">{{ formatTime(item.time) }}</div>
                  </template>
                </a-list-item-meta>
                <template #actions>
                  <a-button type="link" @click="markAsRead(item)" v-if="!item.read">标为已读</a-button>
                  <a-button type="link" @click="viewDetail(item)">查看</a-button>
                  <a-button type="link" danger @click="deleteItem(item)">删除</a-button>
                </template>
              </a-list-item>
            </template>
          </a-list>
        </a-tab-pane>
      </a-tabs>
    </a-card>

    <!-- 消息详情对话框 -->
    <a-modal
      v-model:visible="detailVisible"
      title="消息详情"
      :footer="null"
      @cancel="closeDetail"
    >
      <div v-if="currentDetail">
        <h3>{{ currentDetail.title || currentDetail.sender }}</h3>
        <p>{{ currentDetail.content }}</p>
        <div class="detail-meta">
          <span>{{ formatTime(currentDetail.time) }}</span>
        </div>
      </div>
    </a-modal>

    <!-- 新建消息对话框 -->
    <new-message-modal
      v-model:visible="newMessageVisible"
      @success="handleSendSuccess"
    />

    <!-- 发送通知对话框 -->
    <send-notification-modal
      v-model:visible="notificationModalVisible"
      @success="handleNotificationSuccess"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useNotificationStore, useMessageStore } from '@/stores';
import { NotificationOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import NewMessageModal from './components/NewMessageModal.vue';
import SendNotificationModal from './components/SendNotificationModal.vue';

// 初始化存储
const notificationStore = useNotificationStore();
const messageStore = useMessageStore();

// 标签页状态
const activeTab = ref('notification');
const notificationLoading = ref(false);
const messageLoading = ref(false);

// 对话框状态
const detailVisible = ref(false);
const currentDetail = ref(null);
const newMessageVisible = ref(false);
const notificationModalVisible = ref(false);

// 分页配置
const pagination = {
  pageSize: 10,
  current: 1,
  total: 0,
  onChange: (page) => {
    pagination.current = page;
    fetchMessages();
  },
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  
  const now = new Date();
  const messageTime = new Date(time);
  const diffMs = now - messageTime;
  const diffMins = Math.floor(diffMs / (1000 * 60));
  const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
  
  if (diffMins < 60) {
    return `${diffMins}分钟前`;
  } else if (diffHours < 24) {
    return `${diffHours}小时前`;
  } else if (diffDays < 30) {
    return `${diffDays}天前`;
  } else {
    return messageTime.toLocaleDateString();
  }
};

// 获取消息数据
const fetchMessages = async () => {
  if (activeTab.value === 'notification') {
    notificationLoading.value = true;
    try {
      notificationStore.currentPage = pagination.current;
      notificationStore.pageSize = pagination.pageSize;
      await notificationStore.fetchNotifications();
      pagination.total = notificationStore.total;
    } catch (error) {
      console.error('获取通知失败:', error);
      message.error('获取通知失败');
    } finally {
      notificationLoading.value = false;
    }
  } else if (activeTab.value === 'message') {
    messageLoading.value = true;
    try {
      messageStore.currentPage = pagination.current;
      messageStore.pageSize = pagination.pageSize;
      await messageStore.fetchMessages();
      pagination.total = messageStore.total;
    } catch (error) {
      console.error('获取消息失败:', error);
      message.error('获取消息失败');
    } finally {
      messageLoading.value = false;
    }
  }
};

// 标记为已读
const markAsRead = async (item) => {
  try {
    if (activeTab.value === 'notification') {
      await notificationStore.markAsRead(item.id);
      message.success('已标记为已读');
    } else if (activeTab.value === 'message') {
      await messageStore.markAsRead(item.id);
      message.success('已标记为已读');
    }
  } catch (error) {
    console.error('标记已读失败:', error);
    message.error('标记已读失败');
  }
};

// 标记全部为已读
const markAllAsRead = async () => {
  try {
    if (activeTab.value === 'notification') {
      await notificationStore.markAllAsRead();
    } else if (activeTab.value === 'message') {
      await messageStore.markAllAsRead();
    }
    message.success('已全部标记为已读');
  } catch (error) {
    console.error('标记全部已读失败:', error);
    message.error('标记全部已读失败');
  }
};

// 删除消息
const deleteItem = async (item) => {
  try {
    if (activeTab.value === 'notification') {
      await notificationStore.deleteNotification(item.id);
      message.success('删除成功');
    } else if (activeTab.value === 'message') {
      await messageStore.deleteMessage(item.id);
      message.success('删除成功');
    }
  } catch (error) {
    console.error('删除失败:', error);
    message.error('删除失败');
  }
};

// 查看详情
const viewDetail = async (item) => {
  try {
    if (activeTab.value === 'message') {
      const detail = await messageStore.getMessageDetail(item.id);
      currentDetail.value = detail || item;
      detailVisible.value = true;
      
      // 如果未读，标记为已读
      if (!item.read) {
        await messageStore.markAsRead(item.id);
      }
    }
  } catch (error) {
    console.error('获取详情失败:', error);
    message.error('获取详情失败');
  }
};

// 关闭详情
const closeDetail = () => {
  detailVisible.value = false;
  currentDetail.value = null;
};

// 监听标签页变化
watch(activeTab, () => {
  pagination.current = 1;
  fetchMessages();
});

// 显示新消息对话框
const showNewMessageModal = () => {
  newMessageVisible.value = true;
};

// 显示发送通知对话框
const showSendNotificationModal = () => {
  notificationModalVisible.value = true;
};

// 处理发送成功
const handleSendSuccess = () => {
  message.success('消息发送成功');
  fetchMessages();
};

// 处理通知发送成功
const handleNotificationSuccess = () => {
  message.success('通知发送成功');
  fetchMessages();
};

onMounted(() => {
  fetchMessages();
});
</script>

<style lang="scss" scoped>
.message-container {
  padding: 16px;
  
  .card-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .message-list {
    margin-top: 16px;
    
    :deep(.ant-list-item) {
      padding: 16px;
      transition: background-color 0.3s;
      
      &:hover {
        background-color: rgba(0, 0, 0, 0.02);
      }
    }
    
    .message-item-title {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-weight: 500;
    }
    
    .message-item-content {
      color: rgba(0, 0, 0, 0.65);
      margin-bottom: 4px;
    }
    
    .message-item-time {
      font-size: 12px;
      color: rgba(0, 0, 0, 0.45);
    }
  }
}

.detail-meta {
  margin-top: 16px;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.45);
  text-align: right;
}
</style>
