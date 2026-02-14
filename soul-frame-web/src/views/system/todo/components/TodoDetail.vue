<template>
<a-modal
      :visible="visible"
      title="待办详情"
      :footer="null"
      width="500px"
      @cancel="emit('update:visible', false)"
    >
      <div v-if="data" class="todo-detail">
        <div class="todo-detail-header">
          <h3>{{ data.content }}</h3>
          <a-tag :color="getPriorityColor(data.priority)">{{ getPriorityText(data.priority) }}</a-tag>
        </div>
        
        <div class="todo-detail-info">
          <p v-if="data.description"><strong>描述：</strong>{{ data.description || '无' }}</p>
          <p><strong>截止时间：</strong>{{ data.deadline || '无' }}</p>
          <p><strong>状态：</strong>{{ data.status == 'completed' ? '已完成' : '未完成' }}</p>
        </div>
        
        <div class="todo-detail-actions">
          <a-space>
            <a-button 
              :loading="loading"
              :type="data.status == 'completed' ? 'default' : 'primary'"
              @click="handleTodoStatusChange(data)"
            >
              {{ data.status == 'completed' ? '标为未完成' : '标为已完成' }}
            </a-button>
            <a-button type="primary" :loading="loading" @click="handleTodoEdit(data)">编辑</a-button>
            <a-button danger :loading="loading" @click="handleTodoDelete(data)">删除</a-button>
          </a-space>
        </div>
      </div>
    </a-modal>
</template>

<script setup>
import { completeTodo, uncompleteTodo } from '@/api/modules/todo';


const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  data: {
    type: Object,
    default: null
  }
});
const emit = defineEmits(['edit', 'delete', 'update:visible','success']);
const loading = ref(false);
const handleTodoStatusChange =async (todo) => {
  loading.value = true;
  if(todo.status === 'completed'){
    await uncompleteTodo(todo.id);
    emit('success', todo);
    emit('update:visible', false);
  }else{
    await completeTodo(todo.id);
    emit('success', todo);
    emit('update:visible', false);
  }
  loading.value = false;
};
const handleTodoEdit = (todo) => {
  loading.value = true;
  emit('edit', todo);
  emit('success', todo);
  emit('update:visible', false);
  loading.value = false;
};
const handleTodoDelete = (todo) => {
  emit('delete', todo);
  emit('success', todo);
  emit('update:visible', false);
};



const getPriorityColor = (priority) => {
  switch (priority) {
    case 'high':
      return 'red';
    case 'medium':
      return 'orange';
    case 'low':
      return 'blue';
    default:
      return 'blue';
  }
};

const getPriorityText = (priority) => {
  switch (priority) {
    case 'high':
      return '高';
    case 'medium':
      return '中';
    case 'low':
      return '低';
  }
};


</script>

<style lang="scss" scoped>
.todo-detail .todo-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.todo-detail .todo-detail-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.todo-detail .todo-detail-info {
  margin-bottom: 24px;
  padding: 12px;
  background: rgba(0, 0, 0, 0.02);
  border-radius: 8px;
}

.todo-detail .todo-detail-info p {
  margin-bottom: 8px;
}

.todo-detail .todo-detail-info p:last-child {
  margin-bottom: 0;
}

.todo-detail .todo-detail-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>