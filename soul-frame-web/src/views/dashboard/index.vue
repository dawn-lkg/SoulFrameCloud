<template>
  <div class="dashboard-container">
    <div class="dashboard-background"></div>

    <!-- 新建/编辑待办对话框 -->
    <a-modal
        v-model:visible="todoModalVisible"
        :confirmLoading="todoModalLoading"
        :title="'新建待办'"
        @cancel="handleTodoModalCancel"
        @ok="handleTodoModalOk"
    >
      <a-form
          ref="todoFormRef"
          :label-col="{ span: 4 }"
          :model="todoFormState"
          :rules="todoFormRules"
          :wrapper-col="{ span: 20 }"
      >
        <a-form-item label="标题" name="title">
          <a-input v-model:value="todoFormState.title" placeholder="请输入待办标题"/>
        </a-form-item>
        <a-form-item label="内容" name="content">
          <a-textarea v-model:value="todoFormState.content" :rows="4" placeholder="请输入待办内容"/>
        </a-form-item>
        <a-form-item label="截止时间" name="deadline">
          <a-date-picker
              v-model:value="todoFormState.deadline"
              format="YYYY-MM-DD HH:mm:ss"
              placeholder="请选择截止时间"
              show-time
              style="width: 100%"
              value-format="YYYY-MM-DD HH:mm:ss"
          />
        </a-form-item>
        <a-form-item label="优先级" name="priority">
          <a-radio-group v-model:value="todoFormState.priority">
            <a-radio value="high">高</a-radio>
            <a-radio value="medium">中</a-radio>
            <a-radio value="low">低</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 待办详情预览对话框 -->
    <!-- <a-modal
      v-model:visible="todoDetailVisible"
      title="待办详情"
      :footer="null"
      width="500px"
    >
      <div v-if="currentTodoDetail" class="todo-detail">
        <div class="todo-detail-header">
          <h3>{{ currentTodoDetail.content }}</h3>
          <a-tag :color="getPriorityColor(currentTodoDetail.priority)">{{ currentTodoDetail.priority }}</a-tag>
        </div>
        
        <div class="todo-detail-info">
          <p v-if="currentTodoDetail.description"><strong>描述：</strong>{{ currentTodoDetail.description || '无' }}</p>
          <p><strong>截止时间：</strong>{{ currentTodoDetail.deadline || '无' }}</p>
          <p><strong>状态：</strong>{{ currentTodoDetail.completed ? '已完成' : '未完成' }}</p>
        </div>
        
        <div class="todo-detail-actions">
          <a-space>
            <a-button 
              :type="currentTodoDetail.completed ? 'default' : 'primary'"
              @click="handleTodoStatusChange(currentTodoDetail)"
            >
              {{ currentTodoDetail.completed ? '标为未完成' : '标为已完成' }}
            </a-button>
            <a-button type="primary" @click="handleTodoEdit(currentTodoDetail)">编辑</a-button>
            <a-button danger @click="handleTodoDelete(currentTodoDetail)">删除</a-button>
          </a-space>
        </div>
      </div>
    </a-modal> -->

    

    
    <a-row :gutter="[24, 24]" class="top-section">
      <a-col :lg="8" :md="12" :sm="24" :xs="24">
        <a-card class="welcome-card dashboard-card">
          <div class="card-decoration"></div>
          <div class="weather-time-wrapper">
            <div class="welcome-info">
              <h2>{{ greeting }}，{{ userInfo.name }}</h2>
              <p class="date-info">{{ currentDate }}</p>
            </div>
            <div class="weather-info">
              <CloudOutlined v-if="weather.type === 'cloudy'" class="weather-icon"/>
              <FireOutlined v-else-if="weather.type === 'sunny'" class="weather-icon"/>
              <ThunderboltOutlined v-else-if="weather.type === 'storm'" class="weather-icon"/>
              <CloudDownloadOutlined v-else class="weather-icon"/>
              <div class="weather-detail">
                <div class="temperature">{{ weather.temperature }}°C</div>
                <div class="location">{{ weather.location }}</div>
              </div>
            </div>
          </div>
          <div class="time-display">{{ currentTime }}</div>
        </a-card>
      </a-col>

      <a-col :lg="8" :md="12" :sm="24" :xs="24">
        <a-card :bordered="false" class="dashboard-card daily-quote-card" title="每日一言">
          <template #extra>
            <a-button class="refresh-btn" title="换一句" type="text" @click="refreshDailyQuote">
              <ReloadOutlined :spin="isQuoteLoading"/>
            </a-button>
          </template>
          <div class="quote-container">
            <div v-if="isQuoteLoading" class="quote-loading">
              <a-spin/>
            </div>
            <template v-else>
              <div class="quote-category">
                <a-tag :color="getCategoryColor(dailyQuote.category)">{{ dailyQuote.category }}</a-tag>
              </div>
              <div class="quote-content-wrapper">
                <span class="quote-mark quote-mark-left">"</span>
                <p class="quote-content">{{ dailyQuote.content }}</p>
                <span class="quote-mark quote-mark-right">"</span>
              </div>
              <div class="quote-author">
                <span>—— {{ dailyQuote.author }}</span>
              </div>
            </template>
          </div>
        </a-card>
      </a-col>

      <!-- 快捷操作卡片 -->
      <a-col :lg="8" :md="24" :sm="24" :xs="24">
        <a-card :bordered="false" class="dashboard-card action-card" title="快捷操作">
          <div class="quick-actions">
            <a-button class="action-button" type="primary" @click="handleAction('create')">
              <PlusOutlined/>
              创建任务
            </a-button>
            <a-button class="action-button" type="default" @click="handleAction('message')">
              <MessageOutlined/>
              发送消息
            </a-button>
            <a-button class="action-button" type="default" @click="handleAction('report')">
              <FileTextOutlined/>
              生成报表
            </a-button>
            <a-button class="action-button" type="default" @click="handleAction('analysis')">
              <AreaChartOutlined/>
              数据分析
            </a-button>
          </div>
        </a-card>
      </a-col>

      <!-- 待办事项 -->
      <a-col :lg="8" :md="12" :xs="24">
        <a-card :bordered="false" class="dashboard-card todo-card" title="待办事项">
          <template #extra>
            <a-button size="small" type="link" @click="handleAddTodo">
              <PlusOutlined/>
              添加
            </a-button>
          </template>
          <div class="todo-summary">
            <div class="todo-stat">
              <div class="todo-stat-number">{{ todoList.filter(item => !item.completed).length }}</div>
              <div class="todo-stat-label">待处理</div>
            </div>
            <div class="todo-stat">
              <div class="todo-stat-number">{{ todoList.filter(item => item.completed).length }}</div>
              <div class="todo-stat-label">已完成</div>
            </div>
            <div class="todo-stat">
              <div class="todo-stat-number">{{ todoList.length }}</div>
              <div class="todo-stat-label">总计</div>
            </div>
          </div>
          <a-list :data-source="todoList" :loading="todoStore.loading" class="todo-list" size="small">
            <template #renderItem="{ item }">
              <a-list-item class="todo-item" @click="viewTodoDetail(item)">
                  <span :class="{ 'completed': item.completed }">{{ item.content }}</span>
                <template #actions>
                  <a-tag :color="getPriorityColor(item.priority)">{{ item.priority }}</a-tag>
                  <a-button size="small" type="text" @click.stop="deleteTodo(item.id)">
                    <DeleteOutlined/>
                  </a-button>
                </template>
              </a-list-item>
            </template>
            <template #empty>
              <div class="empty-todo">
                <p>暂无待办事项</p>
                <a-button type="link" @click="handleAddTodo">添加待办</a-button>
              </div>
            </template>
          </a-list>
        </a-card>
      </a-col>

      <!-- 通知公告 -->
      <a-col :lg="8" :md="12" :xs="24">
        <a-card :bordered="false" class="dashboard-card announcement-card" title="通知">
          <template #extra>
            <a-dropdown>
              <template #overlay>
                <a-menu>
                  <a-menu-item key="1">系统通知</a-menu-item>
                  <a-menu-item key="2">安全通知</a-menu-item>
                  <a-menu-item key="3">重要通知</a-menu-item>
                  <a-menu-item key="4">其他通知</a-menu-item>
                </a-menu>
              </template>
              <a-button size="small" type="link">
                筛选
                <DownOutlined/>
              </a-button>
            </a-dropdown>
          </template>
          <a-list :data-source="announcements" class="announcement-list" size="small">
            <template #renderItem="{ item }">
              <a-list-item class="announcement-item">
                <a-list-item-meta>
                  <template #title>
                    <div class="announcement-title">
                      <a-tag v-if="item.important" color="red">重要</a-tag>
                      {{ item.title }}
                    </div>
                  </template>
                  <template #description>
                    <div class="announcement-content">{{ item.content }}</div>
                    <div class="announcement-footer">
                      <span><CalendarOutlined/> {{ item.date }}</span>
                      <span><UserOutlined/> {{ item.publisher }}</span>
                    </div>
                  </template>
                </a-list-item-meta>
              </a-list-item>
            </template>
          </a-list>
        </a-card>
      </a-col>

      <!-- 消息中心 -->
      <a-col :lg="8" :md="24" :xs="24">
        <a-card :bordered="false" class="dashboard-card message-card" title="消息中心">
          <template #extra>
            <a-button size="small" type="link" @click="markAllRead">全部已读</a-button>
          </template>
          <a-tabs default-active-key="unread">
            <a-tab-pane key="unread" tab="未读消息">
              <a-list :data-source="unreadMessages" class="message-list" size="small">
                <template #renderItem="{ item }">
                  <a-list-item class="message-item">
                    <a-list-item-meta>
                      <template #avatar>
                        <a-badge v-if="!item.read" dot>
                          <a-avatar
                              :src="item.avatar"
                                    :style="{ backgroundColor: getMessageColor(item.type) }"/>
                        </a-badge>
                        <a-avatar v-else :src="item.avatar"
                                  :style="{ backgroundColor: getMessageColor(item.type) }"/>
                      </template>
                      <template #title>{{ item.title }}</template>
                      <template #description>
                        <div class="message-content">{{ item.content }}</div>
                        <div class="message-time">
                          <ClockCircleOutlined/>
                          {{ item.time }}
                        </div>
                      </template>
                    </a-list-item-meta>
                    <template #actions>
                      <a-button size="small" type="link" @click="markAsRead(item.id)">标为已读</a-button>
                    </template>
                  </a-list-item>
                </template>
              </a-list>
            </a-tab-pane>
            <a-tab-pane key="all" tab="全部消息">
              <a-list :data-source="allMessages" class="message-list" size="small">
                <template #renderItem="{ item }">
                  <a-list-item class="message-item">
                    <a-list-item-meta>
                      <template #avatar>
                        <a-badge v-if="!item.read" dot>
                          <a-avatar
                              :src="item.avatar"
                                    :style="{ backgroundColor: getMessageColor(item.type) }"/>
                        </a-badge>
                        <a-avatar v-else
                                  :src="item.avatar"
                                  :style="{ backgroundColor: getMessageColor(item.type) }"/>
                      </template>
                      <template #title>{{ item.title }}</template>
                      <template #description>
                        <div class="message-content">{{ item.content }}</div>
                        <div class="message-time">
                          <ClockCircleOutlined/>
                          {{ item.time }}
                        </div>
                      </template>
                    </a-list-item-meta>
                  </a-list-item>
                </template>
              </a-list>
            </a-tab-pane>
          </a-tabs>
        </a-card>
      </a-col>
    </a-row>

    <TodoDetail v-model:visible="todoDetailVisible" :data="currentTodoDetail" @delete="handleTodoDelete"
                @edit="handleTodoEdit" @success="todoStore.fetchTodoList()"/>
  </div>
</template>

<script setup>
import {computed, onMounted, onUnmounted, ref} from 'vue';
import {useRouter} from 'vue-router';
import {message, Modal} from 'ant-design-vue';
import {useTodoStore, useNotificationStore, useMessageStore} from '@/stores';
import {
  AreaChartOutlined,
  BellOutlined,
  CalendarOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  CloudDownloadOutlined,
  CloudOutlined,
  DeleteOutlined,
  DownOutlined,
  FileTextOutlined,
  FireOutlined,
  InfoCircleOutlined,
  MessageOutlined,
  PlusOutlined,
  ReloadOutlined,
  ThunderboltOutlined,
  UserOutlined,
  WarningOutlined
} from '@ant-design/icons-vue';
import TodoDetail from "../system/todo/components/TodoDetail.vue";

const router = useRouter();
const notificationStore = useNotificationStore();
const messageStore = useMessageStore();

// 用户信息
const userInfo = ref({
  name: '管理员',
  lastLogin: '2024-01-15 09:30:00'
});

// 问候语
const greeting = computed(() => {
  const hour = new Date().getHours();
  if (hour < 6) return '凌晨好';
  if (hour < 9) return '早上好';
  if (hour < 12) return '上午好';
  if (hour < 14) return '中午好';
  if (hour < 17) return '下午好';
  if (hour < 19) return '傍晚好';
  return '晚上好';
});

// 当前时间和日期
const currentTime = ref('');
const currentDate = ref('');
let timeInterval = null;

// 更新时间
const updateTime = () => {
  const now = new Date();
  const pad = (n) => n.toString().padStart(2, '0');

  // 更新时间
  currentTime.value = `${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`;

  // 更新日期
  const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
  currentDate.value = `${now.getFullYear()}年${pad(now.getMonth() + 1)}月${pad(now.getDate())}日 ${weekdays[now.getDay()]}`;
};

// 天气信息
const weather = ref({
  type: 'sunny',
  temperature: 26,
  location: '北京市'
});

// 系统状态
const systemStatus = ref({
  cpu: 32.5,
  memory: 45.8,
  onlineUsers: 128,
  uptime: 72
});

// 初始化待办存储
const todoStore = useTodoStore();

// 待办事项
const todoList = computed(() => {
  return todoStore.todoList.map(item => ({
    id: item.id,
    content: item.title,
    completed: item.status === 'completed',
    priority: getPriorityText(item.priority)
  }));
});

// 待办表单相关
const todoModalVisible = ref(false);
const todoModalLoading = ref(false);
const todoFormRef = ref();
const todoFormState = reactive({
  title: '',
  content: '',
  deadline: null,
  priority: 'medium'
});

// 待办详情相关
const todoDetailVisible = ref(false);
const currentTodoDetail = ref(null);

// 表单验证规则
const todoFormRules = {
  title: [
    {required: true, message: '请输入待办标题', trigger: 'blur'},
    {max: 50, message: '标题不能超过50个字符', trigger: 'blur'}
  ],
  content: [
    {required: true, message: '请输入待办内容', trigger: 'blur'},
    {max: 500, message: '内容不能超过500个字符', trigger: 'blur'}
  ],
  deadline: [
    {required: true, message: '请选择截止时间', trigger: 'change'}
  ],
  priority: [
    {required: true, message: '请选择优先级', trigger: 'change'}
  ]
};

// 格式化时间函数
const formatTimeAgo = (time) => {
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

// 系统公告 - 使用通知功能
const announcements = computed(() => {
  // 将通知数据转换为公告格式
  return notificationStore.notifications.map(item => ({
    id: item.id,
    title: item.title,
    content: item.content,
    date: formatTimeAgo(item.time),
    publisher: item.sender || '系统',
    important: item.type === '2', // 安全提醒类型标记为重要
    color: item.color
  }));
});

// 系统消息 - 使用消息功能
const allMessages = computed(() => {
  // 将消息数据转换为系统消息格式
  return messageStore.messages.map(item => ({
    id: item.id,
    title: item.title || item.sender,
    content: item.content,
    time: formatTimeAgo(item.time),
    read: item.read,
    type: getMessageTypeFromContent(item.content),
    avatar: item.avatar,
    sender: item.sender
  }));
});

// 根据消息内容判断消息类型
const getMessageTypeFromContent = (content) => {
  if (content.includes('警告') || content.includes('异常') || content.includes('错误')) {
    return 'warning';
  } else if (content.includes('完成') || content.includes('成功')) {
    return 'success';
  } else {
    return 'info';
  }
};

// 未读消息
const unreadMessages = computed(() => {
  return allMessages.value.filter(msg => !msg.read);
});

// 每日一言
const isQuoteLoading = ref(false);
const dailyQuote = ref({
  content: '生活中最重要的不是你所处的位置，而是你所朝的方向。',
  author: '霍姆斯',
  category: '励志'
});

// 刷新每日一言
const refreshDailyQuote = async () => {
  isQuoteLoading.value = true;

  try {
    // 这里可以替换为实际的API调用
    // 模拟API请求延迟
    await new Promise(resolve => setTimeout(resolve, 800));

    // 模拟数据
    const quotes = [
      {
        content: '人生就像一场旅行，不必在乎目的地，在乎的是沿途的风景以及看风景的心情。',
        author: '列夫·托尔斯泰',
        category: '人生'
      },
      {content: '生活中最重要的不是你所处的位置，而是你所朝的方向。', author: '霍姆斯', category: '励志'},
      {content: '不要等待机会，而要创造机会。', author: '林肯', category: '励志'},
      {content: '人的一生可能燃烧也可能腐朽，我不能腐朽，我愿意燃烧起来。', author: '奥斯特洛夫斯基', category: '励志'},
      {content: '生命如同寓言，其价值不在于长短，而在于内容。', author: '塞涅卡', category: '人生'},
      {
        content: '世界上最宽阔的是海洋，比海洋更宽阔的是天空，比天空更宽阔的是人的心灵。',
        author: '雨果',
        category: '哲理'
      },
      {content: '真正的才智是刚毅的志向。', author: '拿破仑', category: '智慧'},
      {content: '人生的价值，并不是用时间，而是用深度去衡量的。', author: '列夫·托尔斯泰', category: '人生'},
      {content: '如果你想要拥有你从未拥有过的东西，那么你必须做你从未做过的事情。', author: '香奈儿', category: '励志'},
      {content: '成功不是将来才有的，而是从决定去做的那一刻起，持续累积而成。', author: '佚名', category: '成功'},
      {content: '千里之行，始于足下。', author: '老子', category: '古诗词'},
      {content: '知之者不如好之者，好之者不如乐之者。', author: '孔子', category: '学习'},
      {content: '学而不思则罔，思而不学则殆。', author: '孔子', category: '学习'},
      {content: '三人行，必有我师焉。择其善者而从之，其不善者而改之。', author: '孔子', category: '智慧'},
      {content: '工欲善其事，必先利其器。', author: '孔子', category: '智慧'},
      {content: '不积跬步，无以至千里；不积小流，无以成江海。', author: '荀子', category: '古诗词'},
      {content: '天行健，君子以自强不息。', author: '周易', category: '古诗词'},
      {content: '业精于勤，荒于嬉；行成于思，毁于随。', author: '韩愈', category: '学习'},
      {content: '读书破万卷，下笔如有神。', author: '杜甫', category: '学习'},
      {content: '书山有路勤为径，学海无涯苦作舟。', author: '韩愈', category: '学习'},
      {content: '纸上得来终觉浅，绝知此事要躬行。', author: '陆游', category: '学习'},
      {content: '人生自古谁无死，留取丹心照汗青。', author: '文天祥', category: '古诗词'},
      {content: '路漫漫其修远兮，吾将上下而求索。', author: '屈原', category: '古诗词'},
      {content: '人生如逆旅，我亦是行人。', author: '苏轼', category: '人生'},
      {content: '宝剑锋从磨砺出，梅花香自苦寒来。', author: '佚名', category: '励志'},
      {content: '不以物喜，不以己悲。', author: '范仲淹', category: '哲理'},
      {content: '长风破浪会有时，直挂云帆济沧海。', author: '李白', category: '古诗词'},
      {content: '会当凌绝顶，一览众山小。', author: '杜甫', category: '古诗词'},
      {content: '海纳百川，有容乃大；壁立千仞，无欲则刚。', author: '林则徐', category: '哲理'},
      {content: '天生我材必有用，千金散尽还复来。', author: '李白', category: '古诗词'},
      {
        content: '冬天会周而复始，该相逢的人会再相逢，所以我们不必总惦记遗憾，而是要学会期待，全世界的水都会重逢，北冰洋和尼罗河会在诗云中交融，这古老美丽的比喻，让此刻变得神圣，即使漫游，每条路也会带我们归家，我的目光仍拥有选择，南方和北方都还属于他。',
        author: '《克林索尔的最后夏天》',
        category: '哲理'
      }
    ];

    // 随机选择一条
    const randomIndex = Math.floor(Math.random() * quotes.length);

    // 添加淡出动画
    if (dailyQuote.value.content) {
      dailyQuote.value = {...dailyQuote.value, fadeOut: true};
      await new Promise(resolve => setTimeout(resolve, 300));
    }

    // 更新内容
    dailyQuote.value = {...quotes[randomIndex], fadeOut: false};

    message.success('刷新成功');
  } catch (error) {
    message.error('刷新失败');
  } finally {
    isQuoteLoading.value = false;
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
      return '中';
  }
};

// 获取优先级颜色
const getPriorityColor = (priority) => {
  switch (priority) {
    case '高':
      return 'red';
    case '中':
      return 'orange';
    case '低':
      return 'blue';
    default:
      return 'blue';
  }
};

// 获取分类颜色
const getCategoryColor = (category) => {
  const colorMap = {
    '励志': 'blue',
    '人生': 'purple',
    '哲理': 'cyan',
    '智慧': 'geekblue',
    '学习': 'green',
    '成功': 'orange',
    '古诗词': 'magenta'
  };
  return colorMap[category] || 'blue';
};

// 获取消息图标
const getMessageIcon = (type) => {
  switch (type) {
    case 'warning':
      return WarningOutlined;
    case 'success':
      return CheckCircleOutlined;
    case 'info':
      return InfoCircleOutlined;
    default:
      return BellOutlined;
  }
};

// 获取消息颜色
const getMessageColor = (type) => {
  switch (type) {
    case 'warning':
      return '#faad14';
    case 'success':
      return '#52c41a';
    case 'info':
      return '#1890ff';
    default:
      return '#1890ff';
  }
};

// 获取进度条颜色
const getProgressColor = (percent) => {
  if (percent < 50) {
    return '#52c41a'; // 绿色
  } else if (percent < 80) {
    return '#faad14'; // 黄色
  } else {
    return '#f5222d'; // 红色
  }
};

// 删除待办事项
const deleteTodo = async (id) => {
  // 获取待办项信息
  const todoItem = todoList.value.find(item => item.id === id);
  const title = todoItem ? todoItem.content : '此待办事项';

  Modal.confirm({
    title: '删除待办',
    content: `确定要删除"${title}"吗？此操作不可恢复。`,
    okText: '确认删除',
    okType: 'danger',
    cancelText: '取消',
    async onOk() {
      try {
        await todoStore.deleteTodo(id);
        message.success('删除成功');
      } catch (error) {
        message.error('删除失败: ' + error.message);
      }
    }
  });
};

// 显示添加待办弹窗
const handleAddTodo = () => {
  // 重置表单
  todoFormState.title = '';
  todoFormState.content = '';
  todoFormState.deadline = null;
  todoFormState.priority = 'medium';

  // 显示弹窗
  todoModalVisible.value = true;
};

// 查看待办详情
const viewTodoDetail = (item) => {
  // 获取完整的待办信息
  todoStore.getTodoDetail(item.id).then(detail => {
    // 如果获取到详情，使用详情，否则使用列表中的简要信息
    currentTodoDetail.value = detail || {
      ...item,
      description: '',
      deadline: '未设置'
    };
    todoDetailVisible.value = true;
  }).catch(error => {
    console.error('获取待办详情失败:', error);
    // 如果获取详情失败，使用列表中的简要信息
    currentTodoDetail.value = {
      ...item,
      description: '',
      deadline: '未设置'
    };
    todoDetailVisible.value = true;
  });
};


// 编辑待办
const handleTodoEdit = (todo) => {
  // 关闭详情弹窗
  todoDetailVisible.value = false;

  // 填充表单数据
  todoFormState.title = todo.content;
  todoFormState.content = todo.description || '';
  todoFormState.deadline = todo.deadline;
  todoFormState.priority = todo.priority === '高' ? 'high' :
      todo.priority === '中' ? 'medium' : 'low';

  // 显示编辑弹窗
  setTimeout(() => {
    todoModalVisible.value = true;
  }, 300);
};

// 删除待办
const handleTodoDelete = (todo) => {
  todoDetailVisible.value = false;
  deleteTodo(todo.id);
};

// 处理待办弹窗确认
const handleTodoModalOk = async () => {
  try {
    // 表单验证
    await todoFormRef.value.validate();

    todoModalLoading.value = true;

    // 检查截止时间是否已过
    const now = new Date();
    const deadline = new Date(todoFormState.deadline);

    if (deadline < now) {
      // 显示警告但允许继续
      await new Promise(resolve => {
        message.warning('您设置的截止时间已过，请确认是否继续？', 3, resolve);
      });
    }

    const data = {
      title: todoFormState.title,
      content: todoFormState.content,
      deadline: todoFormState.deadline,
      priority: todoFormState.priority
    };

    // 创建待办
    await todoStore.createTodo(data);

    // 成功提示
    message.success('待办创建成功');

    todoModalVisible.value = false;
  } catch (error) {
    console.error('表单验证失败或操作失败:', error);
    if (error.message) {
      message.error(`创建失败: ${error.message}`);
    }
  } finally {
    todoModalLoading.value = false;
  }
};

// 处理待办弹窗取消
const handleTodoModalCancel = () => {
  todoModalVisible.value = false;
};

// 标记消息为已读
const markAsRead = async (id) => {
  try {
    await messageStore.markAsRead(id);
    message.success('已标记为已读');
  } catch (error) {
    message.error('标记已读失败: ' + error.message);
  }
};

// 标记所有消息为已读
const markAllRead = async () => {
  try {
    await messageStore.markAllAsRead();
    message.success('全部标记为已读');
  } catch (error) {
    message.error('标记全部已读失败: ' + error.message);
  }
};

// 处理快捷操作
const handleAction = (action) => {
  switch (action) {
    case 'create':
      message.info('创建任务功能开发中...');
      break;
    case 'message':
      message.info('发送消息功能开发中...');
      break;
    case 'report':
      message.info('生成报表功能开发中...');
      break;
    case 'analysis':
      router.push('/analysis');
      break;
    case 'settings':
      router.push('/settings');
      break;
  }
};

// 生命周期钩子
onMounted(() => {
  // 初始化时间
  updateTime();
  timeInterval = setInterval(updateTime, 1000);

  // 初始化每日一言
  refreshDailyQuote();

  // 获取待办事项数据
  todoStore.fetchTodoList();

  // 初始化通知和消息数据
  notificationStore.init();
  messageStore.init();

  // 这里可以添加获取天气、系统状态等API调用
});

onUnmounted(() => {
  // 清除定时器
  if (timeInterval) clearInterval(timeInterval);
});
</script>

<style scoped>
.dashboard-container {
  padding: 24px;
  background-color: var(--bg-color, #f0f2f5);
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

.dashboard-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: radial-gradient(circle at 10% 20%, rgba(0, 0, 0, 0.02) 0%, transparent 8%),
  radial-gradient(circle at 90% 30%, rgba(0, 0, 0, 0.02) 0%, transparent 8%),
  radial-gradient(circle at 30% 70%, rgba(0, 0, 0, 0.02) 0%, transparent 8%),
  radial-gradient(circle at 80% 90%, rgba(0, 0, 0, 0.02) 0%, transparent 8%);
  z-index: 0;
  pointer-events: none;
  opacity: 0.7;
}

.top-section {
  margin-bottom: 24px;
  position: relative;
  z-index: 1;
}

/* 卡片通用样式 */
.dashboard-card {
  height: 100%;
  transition: all 0.3s ease;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  position: relative;
  backdrop-filter: blur(10px);
}

.dashboard-card::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 5px;
  background: linear-gradient(90deg, var(--primary-color, #1890ff), #7546c9);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.dashboard-card:hover::after {
  opacity: 1;
}

.dashboard-card:hover {
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  transform: translateY(-5px);
}

.dashboard-card :deep(.ant-card-head) {
  padding: 0 20px;
  min-height: 50px;
  font-weight: 500;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  position: relative;
  overflow: hidden;
}

.dashboard-card :deep(.ant-card-head)::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, rgba(0, 0, 0, 0.01), transparent);
  pointer-events: none;
}

.dashboard-card :deep(.ant-card-head-title) {
  padding: 12px 0;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.dashboard-card :deep(.ant-card-body) {
  padding: 20px;
  position: relative;
}

.card-decoration {
  position: absolute;
  top: 0;
  right: 0;
  width: 100px;
  height: 100px;
  background-image: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  border-radius: 50%;
  transform: translate(30%, -30%);
  pointer-events: none;
}

/* 欢迎卡片样式 */
.welcome-card {
  background: linear-gradient(135deg, #8e2de2, #4a00e0);
  color: white;
  position: relative;
  overflow: hidden;
}

.welcome-card::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 100%;
  height: 100%;
  background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="100" height="100" viewBox="0 0 100 100"><circle cx="50" cy="50" r="40" fill="white" opacity="0.05"/></svg>') no-repeat top right,
  url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 20 20"><circle cx="10" cy="10" r="8" fill="white" opacity="0.05"/></svg>') repeat;
  background-size: 200px, 20px;
  pointer-events: none;
  opacity: 0.8;
}

.welcome-card .card-decoration {
  background-image: radial-gradient(circle, rgba(255, 255, 255, 0.2) 0%, transparent 70%);
}

.welcome-card :deep(.ant-card-body) {
  position: relative;
  z-index: 2;
}

.welcome-card::after {
  display: none;
}

.weather-time-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.welcome-info h2 {
  font-size: 28px;
  margin-bottom: 8px;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  font-weight: 600;
  letter-spacing: 0.5px;
}

.date-info {
  color: rgba(255, 255, 255, 0.85);
  font-size: 14px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  letter-spacing: 0.5px;
}

.weather-info {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.1);
  padding: 8px 12px;
  border-radius: 12px;
  backdrop-filter: blur(5px);
}

.weather-icon {
  font-size: 36px;
  color: white;
  margin-right: 10px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.temperature {
  font-size: 24px;
  font-weight: 500;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.location {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.time-display {
  font-size: 42px;
  font-weight: 300;
  text-align: center;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  letter-spacing: 2px;
}

/* 每日一言卡片样式 */
.daily-quote-card {
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #ffffff, #f9f9f9);
  transition: all 0.3s ease;
}

.daily-quote-card::after {
  background: linear-gradient(90deg, #722ed1, #eb2f96);
}

.daily-quote-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-3px);
}

.daily-quote-card::before {
  content: '';
  position: absolute;
  bottom: 0;
  right: 0;
  width: 120px;
  height: 120px;
  background: radial-gradient(circle at bottom right, rgba(0, 0, 0, 0.03), transparent 70%);
  pointer-events: none;
}

.quote-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 100%;
  padding: 10px 5px;
  position: relative;
}

.quote-loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100px;
}

.quote-category {
  margin-bottom: 12px;
}

.quote-category :deep(.ant-tag) {
  border-radius: 4px;
  padding: 2px 8px;
  font-weight: 600;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.quote-content-wrapper {
  position: relative;
  padding: 0 15px;
  margin-bottom: 16px;
  transition: opacity 0.3s ease;
  opacity: 1;
}

.quote-content-wrapper.fade-out {
  opacity: 0;
}

.quote-mark {
  font-family: "Georgia", serif;
  font-size: 56px;
  line-height: 1;
  position: absolute;
  opacity: 0.15;
  color: var(--primary-color, #1890ff);
}

.quote-mark-left {
  top: -15px;
  left: -8px;
}

.quote-mark-right {
  bottom: -35px;
  right: -8px;
}

.quote-content {
  font-family: "STKaiti", "楷体", "KaiTi", serif;
  font-size: 18px;
  line-height: 1.8;
  color: #333;
  text-align: justify;
  font-weight: 600;
  position: relative;
  z-index: 2;
  text-shadow: 0 1px 1px rgba(255, 255, 255, 0.8);
  max-height: 150px;
  overflow-y: auto;
  padding-right: 5px;
  letter-spacing: 0.5px;
}

.quote-content::-webkit-scrollbar {
  width: 4px;
}

.quote-content::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.1);
  border-radius: 4px;
}

.quote-content::-webkit-scrollbar-track {
  background-color: rgba(0, 0, 0, 0.02);
}

.quote-author {
  text-align: right;
  font-family: "STXingkai", "华文行楷", cursive;
  font-size: 16px;
  color: #666;
  margin-top: 8px;
  padding-right: 20px;
  font-weight: 600;
  transition: opacity 0.3s ease;
  opacity: 1;
}

.quote-author.fade-out {
  opacity: 0;
}

.refresh-btn {
  color: rgba(0, 0, 0, 0.45);
  transition: all 0.3s;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.refresh-btn:hover {
  color: var(--primary-color, #1890ff);
  transform: rotate(90deg);
  background-color: rgba(0, 0, 0, 0.02);
}

/* 快捷操作卡片样式 */
.action-card {
  background: linear-gradient(to bottom, #ffffff, #f9f9f9);
}

.action-card::after {
  background: linear-gradient(90deg, #1890ff, #52c41a);
}

/* 快捷操作按钮样式增强 */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.action-button {
  height: 48px;
  border-radius: 12px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  position: relative;
  overflow: hidden;
  font-weight: 500;
}

.action-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.12);
}

.action-button :deep(.anticon) {
  margin-right: 8px;
  font-size: 18px;
}

.action-button:first-child {
  background: linear-gradient(45deg, #1890ff, #36cfc9);
  border-color: transparent;
}

.action-button:first-child:hover {
  opacity: 0.9;
}

/* 待办事项样式 */
.todo-card :deep(.ant-card-head) {
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.todo-card::after {
  background: linear-gradient(90deg, #fa8c16, #fa541c);
}

.todo-summary {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px dashed rgba(0, 0, 0, 0.1);
}

.todo-stat {
  text-align: center;
  flex: 1;
  padding: 12px 8px;
  border-radius: 10px;
  transition: all 0.3s;
  position: relative;
  background: rgba(0, 0, 0, 0.01);
}

.todo-stat::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 50%;
  width: 0;
  height: 2px;
  background-color: var(--primary-color, #1890ff);
  transition: all 0.3s ease;
  transform: translateX(-50%);
}

.todo-stat:hover::after {
  width: 60%;
}

.todo-stat:hover {
  background-color: rgba(0, 0, 0, 0.03);
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

.todo-stat-number {
  font-size: 28px;
  font-weight: 600;
  color: var(--primary-color, #1890ff);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.todo-stat-label {
  font-size: 14px;
  color: rgba(0, 0, 0, 0.45);
  margin-top: 4px;
}

.todo-list {
  max-height: 250px;
  overflow-y: auto;
  padding-right: 4px;
}

.todo-list::-webkit-scrollbar {
  width: 4px;
}

.todo-list::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.1);
  border-radius: 4px;
}

.todo-list::-webkit-scrollbar-track {
  background-color: rgba(0, 0, 0, 0.02);
}

.todo-item {
  padding: 10px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
  border-radius: 6px;
  cursor: pointer;
}

.todo-item:hover {
  background-color: rgba(0, 0, 0, 0.02);
  transform: translateX(2px);
}

.todo-item :deep(.ant-checkbox-wrapper) {
  font-size: 14px;
}

.todo-item :deep(.ant-tag) {
  border-radius: 4px;
  font-weight: 500;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.completed {
  text-decoration: line-through;
  color: rgba(0, 0, 0, 0.45);
}

/* 公告样式 */
.announcement-card::after {
  background: linear-gradient(90deg, #52c41a, #13c2c2);
}

.announcement-list,
.message-list {
  max-height: 300px;
  overflow-y: auto;
  padding-right: 4px;
}

.announcement-list::-webkit-scrollbar,
.message-list::-webkit-scrollbar {
  width: 4px;
}

.announcement-list::-webkit-scrollbar-thumb,
.message-list::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.1);
  border-radius: 4px;
}

.announcement-list::-webkit-scrollbar-track,
.message-list::-webkit-scrollbar-track {
  background-color: rgba(0, 0, 0, 0.02);
}

.announcement-item,
.message-item {
  padding: 12px 8px;
  margin: 4px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
  border-radius: 6px;
}

.announcement-item:hover,
.message-item:hover {
  background-color: rgba(0, 0, 0, 0.02);
  transform: translateX(2px);
}

.announcement-title {
  font-weight: 500;
  margin-bottom: 4px;
  font-size: 15px;
}

.announcement-content {
  color: rgba(0, 0, 0, 0.65);
  margin-bottom: 8px;
  line-height: 1.5;
  font-size: 14px;
}

.announcement-footer {
  display: flex;
  justify-content: space-between;
  color: rgba(0, 0, 0, 0.45);
  font-size: 12px;
}

.announcement-footer span {
  display: flex;
  align-items: center;
}

.announcement-footer :deep(.anticon) {
  margin-right: 4px;
}

/* 消息样式 */
.message-card::after {
  background: linear-gradient(90deg, #2f54eb, #1890ff);
}

.message-card :deep(.ant-tabs-nav) {
  margin-bottom: 12px;
}

.message-card :deep(.ant-tabs-tab) {
  padding: 6px 0;
}

.message-card :deep(.ant-tabs-tab-btn) {
  font-size: 14px;
  font-weight: 500;
}

.message-item {
  padding: 12px 8px;
  border-radius: 6px;
}

.message-item:hover {
  transform: translateX(2px);
}

.message-content {
  color: rgba(0, 0, 0, 0.65);
  line-height: 1.5;
  font-size: 14px;
}

.message-time {
  color: rgba(0, 0, 0, 0.45);
  font-size: 12px;
  margin-top: 4px;
  display: flex;
  align-items: center;
}

.message-time :deep(.anticon) {
  margin-right: 4px;
  font-size: 12px;
}

.message-item :deep(.ant-list-item-meta-avatar) {
  margin-right: 12px;
}

.message-item :deep(.ant-avatar) {
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.message-item :deep(.ant-badge-dot) {
  box-shadow: 0 0 0 2px #fff;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }

  .quick-actions {
    grid-template-columns: 1fr;
  }

  .time-display {
    font-size: 28px;
  }

  .weather-icon {
    font-size: 28px;
  }

  .temperature {
    font-size: 20px;
  }

  .status-value {
    font-size: 20px;
  }

  .todo-summary {
    flex-direction: column;
    align-items: center;
    gap: 10px;
  }

  .todo-stat {
    width: 100%;
    max-width: 150px;
  }

  .weather-info {
    padding: 6px 8px;
    border-radius: 8px;
  }

  .welcome-info h2 {
    font-size: 22px;
  }

  .dashboard-card {
    border-radius: 12px;
  }

  .dashboard-card :deep(.ant-card-body) {
    padding: 16px;
  }

  .dashboard-card :deep(.ant-card-head-title) {
    font-size: 15px;
  }

  .quote-content {
    font-size: 16px;
  }

  .quote-author {
    font-size: 14px;
  }

  .todo-stat-number {
    font-size: 22px;
  }
}

/* 暗色模式适配 */
.dark-mode .dashboard-background {
  background-image: radial-gradient(circle at 10% 20%, rgba(255, 255, 255, 0.02) 0%, transparent 8%),
  radial-gradient(circle at 90% 30%, rgba(255, 255, 255, 0.02) 0%, transparent 8%),
  radial-gradient(circle at 30% 70%, rgba(255, 255, 255, 0.02) 0%, transparent 8%),
  radial-gradient(circle at 80% 90%, rgba(255, 255, 255, 0.02) 0%, transparent 8%);
}

.dark-mode .dashboard-card {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  background-color: rgba(30, 30, 30, 0.8);
}

.dark-mode .daily-quote-card {
  background: linear-gradient(135deg, #2a2a2a, #1f1f1f);
}

.dark-mode .action-card {
  background: linear-gradient(to bottom, #2a2a2a, #1f1f1f);
}

.dark-mode .action-button:first-child {
  background: linear-gradient(45deg, #096dd9, #08979c);
}

.dark-mode .todo-stat {
  background: rgba(255, 255, 255, 0.02);
}

.dark-mode .todo-stat:hover {
  background: rgba(255, 255, 255, 0.05);
}

.dark-mode .todo-item:hover,
.dark-mode .announcement-item:hover,
.dark-mode .message-item:hover {
  background-color: rgba(255, 255, 255, 0.02);
}

.dark-mode .status-item:hover {
  background-color: rgba(255, 255, 255, 0.02);
}

.empty-todo {
  text-align: center;
  padding: 20px 0;
  color: rgba(0, 0, 0, 0.45);
}



.dark-mode .refresh-btn:hover {
  background-color: rgba(255, 255, 255, 0.05);
}

.dark-mode :deep(.ant-card-head) {
  border-bottom-color: rgba(255, 255, 255, 0.08);
}

.dark-mode :deep(.ant-card-head)::before {
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.01), transparent);
}

.dark-mode .quote-content {
  color: rgba(255, 255, 255, 0.85);
  text-shadow: 0 1px 1px rgba(0, 0, 0, 0.5);
}

.dark-mode .quote-author {
  color: rgba(255, 255, 255, 0.65);
}
</style>
