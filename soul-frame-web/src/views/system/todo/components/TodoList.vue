<template>
  <div class="todo-list-container">
    <a-list
      class="todo-list"
      :data-source="data"
      :loading="loading"
      :pagination="pagination"
      :locale="{ emptyText: '暂无待办事项' }"
    >
      <template #renderItem="{ item }">
        <a-list-item>
          <a-list-item-meta>
            <template #avatar>
              <a-avatar :style="{ backgroundColor: getStatusColor(item.status) }">
                <template #icon>
                  <check-outlined v-if="item.status === 'completed'" />
                  <clock-circle-outlined v-else-if="item.status === 'overdue'" />
                  <exclamation-outlined v-else />
                </template>
              </a-avatar>
            </template>
            <template #title>
              <div class="todo-item-title">
                <span :class="{ 'completed': item.status === 'completed' }">{{ item.title }}</span>
                <a-tag :color="getStatusColor(item.status)" size="small">{{ getStatusText(item.status) }}</a-tag>
                <a-tag :color="getPriorityColor(item.priority)" size="small">{{ getPriorityText(item.priority) }}</a-tag>
              </div>
            </template>
            <template #description>
              <div class="todo-item-content" :class="{ 'completed': item.status === 'completed' }">
                {{ item.content }}
              </div>
              <div class="todo-item-time">
                截止: {{ formatTime(item.deadline) }}
              </div>
            </template>
          </a-list-item-meta>
          <template #actions>
            <a-button 
              type="link" 
              @click="handleComplete(item)" 
              v-if="item.status === 'pending' || item.status === 'overdue'"
            >
              完成
            </a-button>
            <a-button type="link" @click="handleEdit(item)" v-if="item.status !== 'completed'">
              编辑
            </a-button>
            <a-popconfirm
              title="确定要删除这个待办事项吗？"
              @confirm="handleDelete(item)"
              ok-text="确定"
              cancel-text="取消"
            >
              <a-button type="link" danger>删除</a-button>
            </a-popconfirm>
          </template>
        </a-list-item>
      </template>
    </a-list>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits } from 'vue';
import { CheckOutlined, ClockCircleOutlined, ExclamationOutlined } from '@ant-design/icons-vue';

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['complete', 'delete', 'edit']);

// 分页配置
const pagination = {
  pageSize: 10,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50'],
  showTotal: (total) => `共 ${total} 项`
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '无截止时间';
  
  const date = new Date(time);
  return date.toLocaleString();
};

// 获取状态颜色
const getStatusColor = (status) => {
  switch (status) {
    case 'pending':
      return '#1890ff'; // 蓝色
    case 'completed':
      return '#52c41a'; // 绿色
    case 'overdue':
      return '#ff4d4f'; // 红色
    default:
      return '#1890ff'; // 蓝色
  }
};

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'pending':
      return '待办';
    case 'completed':
      return '已完成';
    case 'overdue':
      return '已逾期';
    default:
      return '未知';
  }
};

// 获取优先级颜色
const getPriorityColor = (priority) => {
  switch (priority) {
    case 'high':
      return '#ff4d4f'; // 红色
    case 'medium':
      return '#faad14'; // 黄色
    case 'low':
      return '#52c41a'; // 绿色
    default:
      return '#1890ff'; // 蓝色
  }
};

// 获取优先级文本
const getPriorityText = (priority) => {
  switch (priority) {
    case 'high':
      return '高';
    case 'medium':
      return '中';
    case 'low':
      return '低';
    default:
      return '默认';
  }
};

// 处理完成
const handleComplete = (item) => {
  emit('complete', item.id);
};

// 处理删除
const handleDelete = (item) => {
  emit('delete', item.id);
};

// 处理编辑
const handleEdit = (item) => {
  emit('edit', item);
};
</script>

<style lang="scss" scoped>
.todo-list-container {
  .todo-list {
    .todo-item-title {
      display: flex;
      align-items: center;
      gap: 8px;
      
      .completed {
        text-decoration: line-through;
        color: rgba(0, 0, 0, 0.45);
      }
    }
    
    .todo-item-content {
      color: rgba(0, 0, 0, 0.65);
      margin-bottom: 4px;
      
      &.completed {
        text-decoration: line-through;
        color: rgba(0, 0, 0, 0.45);
      }
    }
    
    .todo-item-time {
      font-size: 12px;
      color: rgba(0, 0, 0, 0.45);
    }
  }
}
</style>
