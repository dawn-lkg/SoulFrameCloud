<template>
  <a-dropdown
    :trigger="['click']"
    :destroyPopupOnHide="false"
    :open="dropdownVisible"
    @openChange="handleVisibleChange"
  >
    <hover-container class="message-center">
      <a-badge :count="unreadCount">
        <BellOutlined class="message-icon" />
      </a-badge>
    </hover-container>
    <template #overlay>
      <a-card class="message-dropdown" :bordered="false">
        <a-tabs v-model:activeKey="activeTab">
          <a-tab-pane key="notification" tab="通知">
            <a-list
              class="message-list"
              :data-source="notificationMessages"
              :loading="loading"
              :locale="{ emptyText: '暂无通知' }"
            >
              <template #renderItem="{ item }">
                <a-list-item @click="handleItemClick(item)">
                  <a-list-item-meta>
                    <template #avatar>
                      <a-avatar
                        :style="{ backgroundColor: item.color || '#1890ff' }"
                      >
                        <template #icon><NotificationOutlined /></template>
                      </a-avatar>
                    </template>
                    <template #title>
                      <div class="message-item-title">
                        {{ item.title }}
                        <a-tag v-if="!item.read" color="blue" size="small"
                          >新</a-tag
                        >
                      </div>
                    </template>
                    <template #description>
                      <div class="message-item-content">{{ item.content }}</div>
                      <div class="message-item-time">
                        {{ formatTime(item.time) }}
                      </div>
                    </template>
                  </a-list-item-meta>
                </a-list-item>
              </template>
            </a-list>
          </a-tab-pane>
          <a-tab-pane key="message" tab="消息">
            <a-list
              class="message-list"
              :data-source="personalMessages"
              :loading="loading"
              :locale="{ emptyText: '暂无消息' }"
            >
              <template #renderItem="{ item }">
                <a-list-item @click="handleItemClick(item)">
                  <a-list-item-meta>
                    <template #avatar>
                      <a-avatar
                        :src="item.avatar || ''"
                        :style="{ backgroundColor: item.color || '#1890ff' }"
                      >
                        {{
                          !item.avatar
                            ? item.sender?.charAt(0)?.toUpperCase()
                            : ""
                        }}
                      </a-avatar>
                    </template>
                    <template #title>
                      <div class="message-item-title">
                        {{ item.sender }}
                        <a-tag v-if="!item.read" color="blue" size="small"
                          >新</a-tag
                        >
                      </div>
                    </template>
                    <template #description>
                      <div class="message-item-content">{{ item.content }}</div>
                      <div class="message-item-time">
                        {{ formatTime(item.time) }}
                      </div>
                    </template>
                  </a-list-item-meta>
                </a-list-item>
              </template>
            </a-list>
          </a-tab-pane>
          <a-tab-pane key="todo" tab="待办">
            <a-list
              class="message-list"
              :data-source="todoItems"
              :loading="loading"
              :locale="{ emptyText: '暂无待办' }"
            >
              <template #renderItem="{ item }">
                <a-list-item @click="handleItemClick(item)">
                  <a-list-item-meta>
                    <template #avatar>
                      <a-avatar
                        :style="{
                          backgroundColor: getStatusColor(item.status),
                        }"
                      >
                        <template #icon
                          ><CheckOutlined
                            v-if="
                              item.status === 'completed'
                            " /><ClockCircleOutlined v-else
                        /></template>
                      </a-avatar>
                    </template>
                    <template #title>
                      <div class="message-item-title">
                        {{ item.title }}
                        <a-tag
                          :color="getStatusColor(item.status)"
                          size="small"
                          >{{ getStatusText(item.status) }}</a-tag
                        >
                      </div>
                    </template>
                    <template #description>
                      <div class="message-item-content">{{ item.content }}</div>
                      <div class="message-item-time">
                        截止: {{ formatTime(item.deadline) }}
                      </div>
                    </template>
                  </a-list-item-meta>
                </a-list-item>
              </template>
            </a-list>
          </a-tab-pane>
        </a-tabs>
        <template #actions>
          <div class="message-footer-actions">
            <a-button type="link" @click="markAllAsRead">全部已读</a-button>
            <a-button type="link" @click="handleTabAction">{{
              getTabActionText()
            }}</a-button>
          </div>
        </template>
      </a-card>
    </template>
  </a-dropdown>

  <TodoDetail v-model:visible="todoDetailVisible" :data="currentTodoDetail" />
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import {
  BellOutlined,
  NotificationOutlined,
  CheckOutlined,
  ClockCircleOutlined,
} from "@ant-design/icons-vue";
import { message, notification } from "ant-design-vue";
import { useNotificationStore, useMessageStore, useTodoStore } from "@/stores";
import TodoDetail from "@/views/system/todo/components/TodoDetail.vue";
import { useSSE } from "@/utils/useSSE";

// 初始化存储
const notificationStore = useNotificationStore();
const messageStore = useMessageStore();
const todoStore = useTodoStore();
const router = useRouter();

// 控制下拉菜单的显示状态
const dropdownVisible = ref(false);
const loading = ref(false);
const activeTab = ref("notification");
const todoDetailVisible = ref(false);
const currentTodoDetail = ref(null);

const init = () => {
  notificationStore.getUnreadCount();
  messageStore.getUnreadCount();
  todoStore.getPendingCount();
};

init();

// 计算未读消息总数量
const unreadCount = computed(() => {
  return (
    notificationStore.unreadCount +
    messageStore.unreadCount +
    todoStore.pendingCount
  );
});

// 获取通知列表
const notificationMessages = computed(() => notificationStore.notifications);

// 获取消息列表
const personalMessages = computed(() => messageStore.messages);

// 获取待办列表
const todoItems = computed(() => todoStore.todoList);

// 格式化时间
const formatTime = (time) => {
  if (!time) return "";

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

// 获取状态颜色
const getStatusColor = (status) => {
  switch (status) {
    case "pending":
      return "#faad14"; // 黄色
    case "completed":
      return "#52c41a"; // 绿色
    case "overdue":
      return "#ff4d4f"; // 红色
    default:
      return "#1890ff"; // 蓝色
  }
};

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case "pending":
      return "待办";
    case "completed":
      return "已完成";
    case "overdue":
      return "已逾期";
    default:
      return "未知";
  }
};

// 标记所有为已读
const markAllAsRead = async () => {
  loading.value = true;
  try {
    if (activeTab.value === "notification") {
      await notificationStore.markAllAsRead();
    } else if (activeTab.value === "message") {
      await messageStore.markAllAsRead();
    }
    message.success("已将所有消息标记为已读");
  } catch (error) {
    message.error("标记已读失败: " + error.message);
  } finally {
    loading.value = false;
  }
};

// 获取当前标签页对应的操作文本
const getTabActionText = () => {
  switch (activeTab.value) {
    case "notification":
      return "清空通知";
    case "message":
      return "查看更多";
    case "todo":
      return "添加待办";
    default:
      return "更多操作";
  }
};

// 处理标签页对应的操作
const handleTabAction = async () => {
  loading.value = true;
  try {
    switch (activeTab.value) {
      case "notification":
        await notificationStore.clearAllNotifications();
        message.success("已清空所有通知");
        break;
      case "message":
        router.push("/system/message");
        dropdownVisible.value = false;
        break;
      case "todo":
        router.push("/system/todo");
        dropdownVisible.value = false;
        break;
      default:
        break;
    }
  } catch (error) {
    message.error("操作失败: " + error.message);
  } finally {
    loading.value = false;
  }
};

// 获取消息数据
const fetchMessages = async () => {
  loading.value = true;
  try {
    // 根据当前标签页加载不同数据
    if (activeTab.value === "notification") {
      await notificationStore.fetchNotifications();
    } else if (activeTab.value === "message") {
      await messageStore.fetchMessages();
    } else if (activeTab.value === "todo") {
      await todoStore.fetchTodoList();
    }
  } catch (error) {
    console.error("获取消息数据失败:", error);
  } finally {
    loading.value = false;
  }
};

// 处理下拉菜单可见性变化
const handleVisibleChange = (visible) => {
  if (visible !== undefined) {
    dropdownVisible.value = visible;
    if (visible) {
      // 当下拉菜单打开时，获取最新数据
      fetchMessages();
    }
  }
};

// 处理不同类型的事件
const handleEventType = (data, e) => {
  if (e === "message-event") {
    messageStore.getUnreadCount();
    if (data) {
      notification.open({
        message: `${data?.sender}消息`,
        description: `${data?.content}`,
        key: "message-event",
        onClose: close,
      });
    }
  } else if (e === "notification-event") {
    notificationStore.getUnreadCount();
  } else if (e === "todo-event") {
    todoStore.getPendingCount();
  }
};

// 点击消息列表项
const handleItemClick = async (item) => {
  try {
    if (activeTab.value === "notification" && !item.read) {
      await notificationStore.markAsRead(item.id);
    } else if (activeTab.value === "message" && !item.read) {
      await messageStore.markAsRead(item.id);
    } else if (activeTab.value === "todo") {
      // 跳转到待办详情页
      // router.push(`/system/todo/${item.id}`);
      // dropdownVisible.value = false;
      todoDetailVisible.value = true;
      currentTodoDetail.value = item;
    }
  } catch (error) {
    console.error("处理消息点击失败:", error);
  }
  return false; // 不关闭下拉菜单
};

// 监听标签页变化，加载对应数据
watch(activeTab, (newTab) => {
  fetchMessages();
});

onMounted(() => {
  // 初始化加载数据
  fetchMessages();
  // useSSE("/notification/sse", {
  //   keepAlive: false,
  //   onMessage: (v, e) => {
  //     handleEventType(v, e);
  //   },
  // });
});
</script>

<style lang="scss" scoped>
.message-center {
  padding: 0 12px;
  display: flex;
  align-items: center;
  cursor: pointer;
  height: 100%;

  .message-icon {
    font-size: 18px;
  }
}

.message-dropdown {
  width: 350px;
  max-height: 500px;

  .message-list {
    max-height: 300px;
    overflow-y: auto;

    :deep(.ant-list-item) {
      cursor: pointer;
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

:deep(.ant-tabs-nav) {
  margin-bottom: 8px;
  margin-top: 0;
}

:deep(.ant-dropdown-menu-body) {
  padding: 0 24px;
}

:deep(.ant-list-item) {
  padding: 8px 0;
}

.message-footer-actions {
  width: 100%;
  display: flex;
  justify-content: space-between;
}

:deep(.ant-card-actions) {
  border-top: 1px solid #f0f0f0;
}
</style>
