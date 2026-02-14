<template>
  <div class="todo-container">
    <a-card :bordered="false">
      <template #title>
        <div class="card-title">
          <span>待办事项</span>
          <a-button type="primary" @click="showCreateModal">新建待办</a-button>
        </div>
      </template>

      <a-tabs v-model:activeKey="activeStatus">
        <a-tab-pane key="all" tab="全部">
          <todo-list 
            :data="filteredTodos" 
            :loading="loading" 
            @complete="completeTodo"
            @delete="deleteTodo"
            @edit="editTodo"
          />
        </a-tab-pane>
        <a-tab-pane key="pending" tab="待办">
          <todo-list 
            :data="filteredTodos" 
            :loading="loading" 
            @complete="completeTodo"
            @delete="deleteTodo"
            @edit="editTodo"
          />
        </a-tab-pane>
        <a-tab-pane key="completed" tab="已完成">
          <todo-list 
            :data="filteredTodos" 
            :loading="loading" 
            @complete="completeTodo"
            @delete="deleteTodo"
            @edit="editTodo"
          />
        </a-tab-pane>
        <a-tab-pane key="overdue" tab="已逾期">
          <todo-list 
            :data="filteredTodos" 
            :loading="loading" 
            @complete="completeTodo"
            @delete="deleteTodo"
            @edit="editTodo"
          />
        </a-tab-pane>
      </a-tabs>
    </a-card>

    <!-- 新建/编辑待办对话框 -->
    <!-- <a-modal
      v-model:visible="modalVisible"
      :title="isEdit ? '编辑待办' : '新建待办'"
      @ok="handleModalOk"
      @cancel="handleModalCancel"
      :confirmLoading="modalLoading"
    >
      <a-form
        ref="formRef"
        :model="formState"
        :rules="formRules"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 20 }"
      >
        <a-form-item label="标题" name="title">
          <a-input v-model:value="formState.title" placeholder="请输入待办标题" />
        </a-form-item>
        <a-form-item label="内容" name="content">
          <a-textarea v-model:value="formState.content" placeholder="请输入待办内容" :rows="4" />
        </a-form-item>
        <a-form-item label="截止时间" name="deadline">
          <a-date-picker 
            v-model:value="formState.deadline" 
            show-time 
            format="YYYY-MM-DD HH:mm:ss" 
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
            placeholder="请选择截止时间"
          />
        </a-form-item>
        <a-form-item label="优先级" name="priority">
          <a-radio-group v-model:value="formState.priority">
            <a-radio value="high">高</a-radio>
            <a-radio value="medium">中</a-radio>
            <a-radio value="low">低</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal> -->
    <TodoForm v-model:visible="modalVisible" :data="formState" :isEdit="isEdit" @success="fetchTodos" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue';
import { useTodoStore } from '@/stores';
import { message } from 'ant-design-vue';
import TodoList from './components/TodoList.vue';
import TodoForm from './components/TodoForm.vue';

// 初始化存储
const todoStore = useTodoStore();

// 状态
const loading = ref(false);
const activeStatus = ref('all');
const modalVisible = ref(false);
const isEdit = ref(false);
const currentId = ref(null);
const formState = ref({});




// 根据当前标签页过滤待办事项
const filteredTodos = computed(() => {
  if (activeStatus.value === 'all') {
    return todoStore.todoList;
  }
  return todoStore.todoList.filter(item => item.status === activeStatus.value);
});

// 获取待办列表
const fetchTodos = async () => {
  loading.value = true;
  try {
    await todoStore.fetchTodoList();
  } catch (error) {
    console.error('获取待办列表失败:', error);
    message.error('获取待办列表失败');
  } finally {
    loading.value = false;
  }
};

// 完成待办
const completeTodo = async (id) => {
  try {
    await todoStore.completeTodo(id);
    message.success('已标记为完成');
  } catch (error) {
    console.error('标记完成失败:', error);
    message.error('标记完成失败');
  }
};

// 删除待办
const deleteTodo = async (id) => {
  try {
    await todoStore.deleteTodo(id);
    message.success('删除成功');
  } catch (error) {
    console.error('删除失败:', error);
    message.error('删除失败');
  }
};

// 编辑待办
const editTodo = async (todo) => {
  isEdit.value = true;
  currentId.value = todo.id;
  formState.value = todo;
  modalVisible.value = true;
};

// 显示创建对话框
const showCreateModal = () => {
  isEdit.value = false;
  currentId.value = null;
  formState.value = {};
  
  modalVisible.value = true;
};


onMounted(() => {
  fetchTodos();
});
</script>

<style lang="scss" scoped>
.todo-container {
  padding: 16px;
  
  .card-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
