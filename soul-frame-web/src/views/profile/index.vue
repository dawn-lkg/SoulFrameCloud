<template>
  <div class="profile-container">
    <a-row :gutter="16">
      <!-- 左侧个人信息卡片 -->
      <a-col :span="6">
        <a-card class="profile-card">
          <div class="profile-avatar">
            <a-avatar :size="100" :src="userInfo.avatar || defaultAvatar" />
            <div class="upload-avatar">
              <a-upload
                name="avatar"
                :before-upload="beforeUpload"
                :custom-request="handleAvatarUpload"
                :show-upload-list="false"
              >
                <a-button type="link">更换头像</a-button>
              </a-upload>
            </div>
          </div>
          <div class="profile-info">
            <h2>{{ userInfo.nickName || userInfo.userName }}</h2>
            <div class="profile-role">
              <a-tag v-for="role in userInfo.roles" :key="role" color="blue">{{ role.roleName }}</a-tag>
            </div>
            <div class="profile-detail">
              <p><user-outlined /> {{ userInfo.userName }}</p>
              <p><mail-outlined /> {{ userInfo.email || '未设置邮箱' }}</p>
              <p>
                <phone-outlined/>
                {{ userInfo.phone || '未设置手机号' }}
              </p>
              <p><calendar-outlined /> {{ userInfo.createTime || '未知加入时间' }}</p>
            </div>
          </div>
        </a-card>
      </a-col>

      <!-- 右侧内容区域 -->
      <a-col :span="18">
        <a-card>
          <a-tabs v-model:active-key="activeTab">
            <!-- 基本资料 -->
            <a-tab-pane key="basic" tab="基本资料">
            <a-card>
              <a-form
                :model="userForm"
                :rules="rules"
                ref="userFormRef"
                :label-col="{ span: 4 }"
                :wrapper-col="{ span: 16 }"
              >
                <a-form-item label="用户名" name="userName">
                  <a-input v-model:value="userForm.userName" disabled />
                </a-form-item>
                <a-form-item label="昵称" name="nickName">
                  <a-input v-model:value="userForm.nickName" placeholder="请输入昵称" />
                </a-form-item>
                <a-form-item label="性别" name="sex">
                  <a-radio-group v-model:value="userForm.sex">
                    <a-radio value="0">男</a-radio>
                    <a-radio value="1">女</a-radio>
                  </a-radio-group>
                </a-form-item>
                <a-form-item label="手机号码" name="phone">
                  <a-input v-model:value="userForm.phone" placeholder="请输入手机号码"/>
                </a-form-item>
                <a-form-item label="邮箱" name="email">
                  <a-input v-model:value="userForm.email" placeholder="请输入邮箱" />
                </a-form-item>
                <a-form-item label="个人简介" name="remark">
                  <a-textarea
                    v-model:value="userForm.remark"
                    :rows="4"
                    placeholder="请输入个人简介"
                  />
                </a-form-item>
                <a-form-item :wrapper-col="{ offset: 4 }">
                  <a-button :loading="loading" type="primary" @click="handleUpdateUserInfo">保存修改</a-button>
                </a-form-item>
              </a-form>
            </a-card>
          </a-tab-pane>

          <!-- 修改密码 -->
          <a-tab-pane key="password" tab="修改密码">
            <a-card>
              <a-form
                :model="passwordForm"
                :rules="passwordRules"
                ref="passwordFormRef"
                :label-col="{ span: 4 }"
                :wrapper-col="{ span: 16 }"
              >
                <a-form-item label="当前密码" name="oldPassword">
                  <a-input-password
                    v-model:value="passwordForm.oldPassword"
                    placeholder="请输入当前密码"
                  />
                </a-form-item>
                <a-form-item label="新密码" name="newPassword">
                  <a-input-password
                    v-model:value="passwordForm.newPassword"
                    placeholder="请输入新密码"
                  />
                </a-form-item>
                <a-form-item label="确认新密码" name="confirmPassword">
                  <a-input-password
                    v-model:value="passwordForm.confirmPassword"
                    placeholder="请再次输入新密码"
                  />
                </a-form-item>
                <a-form-item :wrapper-col="{ offset: 4 }">
                  <a-button :loading="loading" type="primary" @click="handleUpdatePassword">修改密码</a-button>
                </a-form-item>
              </a-form>
            </a-card>
          </a-tab-pane>

          <!-- 系统偏好 -->
          <a-tab-pane key="preferences" tab="系统偏好">
            <a-card>
              <a-form
                :model="preferencesForm"
                ref="preferencesFormRef"
                :label-col="{ span: 4 }"
                :wrapper-col="{ span: 16 }"
              >
                <a-form-item label="主题色" name="theme">
                  <a-radio-group v-model:value="preferencesForm.theme">
                    <a-radio value="light">浅色</a-radio>
                    <a-radio value="dark">深色</a-radio>
                    <a-radio value="system">跟随系统</a-radio>
                  </a-radio-group>
                </a-form-item>
                <a-form-item label="导航模式" name="layout">
                  <a-radio-group v-model:value="preferencesForm.layout">
                    <a-radio value="side">侧边菜单</a-radio>
                    <a-radio value="top">顶部菜单</a-radio>
                    <a-radio value="mix">混合菜单</a-radio>
                  </a-radio-group>
                </a-form-item>
                <a-form-item label="主题色" name="primaryColor">
                  <a-radio-group v-model:value="preferencesForm.primaryColor">
                    <a-radio value="#1890ff">
                      <div class="color-block" style="background-color: #1890ff"></div>
                    </a-radio>
                    <a-radio value="#52c41a">
                      <div class="color-block" style="background-color: #52c41a"></div>
                    </a-radio>
                    <a-radio value="#fa8c16">
                      <div class="color-block" style="background-color: #fa8c16"></div>
                    </a-radio>
                    <a-radio value="#f5222d">
                      <div class="color-block" style="background-color: #f5222d"></div>
                    </a-radio>
                    <a-radio value="#722ed1">
                      <div class="color-block" style="background-color: #722ed1"></div>
                    </a-radio>
                  </a-radio-group>
                </a-form-item>
                <a-form-item label="表格大小" name="tableSize">
                  <a-radio-group v-model:value="preferencesForm.tableSize">
                    <a-radio value="large">大</a-radio>
                    <a-radio value="middle">中</a-radio>
                    <a-radio value="small">小</a-radio>
                  </a-radio-group>
                </a-form-item>
                <a-form-item label="固定头部" name="fixedHeader">
                  <a-switch v-model:checked="preferencesForm.fixedHeader" />
                </a-form-item>
                <a-form-item label="标签页" name="tabsBar">
                  <a-switch v-model:checked="preferencesForm.tabsBar" />
                </a-form-item>
                <a-form-item :wrapper-col="{ offset: 4 }">
                  <a-button :loading="loading" type="primary" @click="handleUpdatePreferences">保存设置</a-button>
                  <a-button style="margin-left: 10px" @click="handleResetPreferences">恢复默认</a-button>
                </a-form-item>
              </a-form>
            </a-card>
          </a-tab-pane>

          <!-- 账号安全 -->
          <a-tab-pane key="security" tab="账号安全">
            <a-card>
              <a-list item-layout="horizontal">
                <a-list-item>
                  <a-list-item-meta title="账号密码" description="用于保护账号信息安全">
                    <template #avatar>
                      <lock-outlined />
                    </template>
                  </a-list-item-meta>
                  <template #extra>
                    <a-button type="link" @click="activeTab = 'password'">修改</a-button>
                  </template>
                </a-list-item>
                <a-list-item>
                  <a-list-item-meta :description="userInfo.phone || '未绑定手机号码'" title="手机绑定">
                    <template #avatar>
                      <mobile-outlined />
                    </template>
                  </a-list-item-meta>
                  <template #extra>
                    <a-button type="link" @click="activeTab = 'basic'">修改</a-button>
                  </template>
                </a-list-item>
                <a-list-item>
                  <a-list-item-meta title="邮箱绑定" :description="userInfo.email || '未绑定邮箱'">
                    <template #avatar>
                      <mail-outlined />
                    </template>
                  </a-list-item-meta>
                  <template #extra>
                    <a-button type="link" @click="activeTab = 'basic'">修改</a-button>
                  </template>
                </a-list-item>
                <a-list-item>
                  <a-list-item-meta title="登录日志" description="查看近期登录状态和登录地点">
                    <template #avatar>
                      <history-outlined />
                    </template>
                  </a-list-item-meta>
                  <template #extra>
                    <a-button type="link" @click="showLoginLog = true">查看</a-button>
                  </template>
                </a-list-item>
              </a-list>
            </a-card>
          </a-tab-pane>
        </a-tabs>
        </a-card>
      </a-col>
    </a-row>

    <!-- 登录日志弹窗 -->
    <a-modal
      v-model:visible="showLoginLog"
      title="登录日志"
      width="800px"
      :footer="null"
      @cancel="showLoginLog = false"
    >
      <a-table :columns="loginLogColumns" :data-source="loginLogList" :pagination="pagination"
               @change="handleTableChange">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === '0' ? 'green' : 'red'">
              {{ record.status === '0' ? '成功' : '失败' }}
            </a-tag>
          </template>
        </template>
      </a-table>
    </a-modal>
  </div>
</template>

<script setup>
import {message} from 'ant-design-vue'
import {updateUserInfo, updateUserPassword, updateUserAvatar} from '@/api/modules/user'
import router from '@/router'
import {LOGIN_PATH} from '@/config'
import {getCurrentUserLoginLog} from '@/api/modules/loginLog'
import {
  CalendarOutlined,
  HistoryOutlined,
  LockOutlined,
  MailOutlined,
  MobileOutlined,
  PhoneOutlined,
  UserOutlined
} from '@ant-design/icons-vue'


// 当前激活的标签页
const activeTab = ref('basic')

// 加载状态
const loading = ref(false)

// 用户信息
const authStore = useAuthStore()
const userInfo = ref({ ...authStore.userInfo })

// 默认头像
const defaultAvatar = ref('http://120.27.215.0:8081/files/970c267a-ada5-4096-a709-4489a16f885c.gif')

// 用户表单
const userFormRef = ref(null)
const userForm = reactive({
  userId: '',
  userName: '',
  nickName: '',
  sex: '0',
  phone: '',
  email: '',
  remark: ''
})

// 表单校验规则
const rules = {
  nickName: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 密码表单
const passwordFormRef = ref(null)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码校验规则
const passwordRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value) => {
        if (value !== passwordForm.newPassword) {
          return Promise.reject('两次输入的密码不一致')
        }
        return Promise.resolve()
      },
      trigger: 'blur'
    }
  ]
}

// 系统偏好表单
const preferencesFormRef = ref(null)
const preferencesForm = reactive({
  theme: 'light',
  layout: 'side',
  primaryColor: '#1890ff',
  tableSize: 'middle',
  fixedHeader: true,
  tabsBar: true
})

// 登录日志相关
const showLoginLog = ref(false)
const loginLogList = ref([])

const loginLogColumns = [
  {
    title: 'IP地址',
    dataIndex: 'ipaddr',
    key: 'ipaddr'
  },
  {
    title: '登录地点',
    dataIndex: 'loginLocation',
    key: 'loginLocation'
  },
  {
    title: '浏览器',
    dataIndex: 'browser',
    key: 'browser'
  },
  {
    title: '操作系统',
    dataIndex: 'os',
    key: 'os'
  },
  {
    title: '登录状态',
    dataIndex: 'status',
    key: 'status'
  },
  {
    title: '登录时间',
    dataIndex: 'loginTime',
    key: 'loginTime'
  }
]

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 条记录`
});

const getUserLoginLog = async () => {
  try {
    loading.value = true
    const data = await getCurrentUserLoginLog({
      pageNum: pagination.current,
      pageSize: pagination.pageSize
    })
    loginLogList.value = data.records
    pagination.total = data.total
  } catch (error) {
    console.error('获取登录日志失败:', error)
    message.error('获取登录日志失败')
  } finally {
    loading.value = false
  }
}


// 组件挂载时获取用户详情
onMounted(async () => {
  try {
    Object.assign(userForm, userInfo.value)
  } catch (error) {
    console.error('获取用户详情失败:', error)
    message.error('获取用户详情失败')
  }
  await getUserLoginLog()
})

// 头像上传前校验
const beforeUpload = (file) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/gif'
  if (!isJpgOrPng) {
    message.error('只能上传JPG或PNG格式的图片!')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片大小不能超过2MB!')
    return false
  }
  return true
}

// 处理头像上传（使用 customRequest 避免重复上传）
const handleAvatarUpload = async ({ file, onSuccess, onError }) => {
  try {
    const formData = new FormData()
    formData.append('file', file)
    
    // 调用上传接口
    const result = await updateUserAvatar(formData)
    
    // 上传成功，更新头像
    userInfo.value.avatar = result
    await authStore.getUserInfo()
    Object.assign(userInfo.value, userForm.value)
    
    message.success('头像更新成功')
    onSuccess(result)
  } catch (error) {
    message.error(error.message || '头像上传失败')
    onError(error)
  }
}

const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  getUserLoginLog()
}

// 更新用户信息
const handleUpdateUserInfo = async () => {
  try {
    loading.value = true
    await userFormRef.value.validate()
    await updateUserInfo(userForm)
    await authStore.getUserInfo();
    Object.assign(userInfo.value, userForm)
    message.success('个人信息更新成功')
  } catch (error) {
    console.error('表单校验失败:', error)
  } finally {
    loading.value = false
  }
}

// 更新密码
const handleUpdatePassword = async () => {
  try {
    loading.value = true
    await passwordFormRef.value.validate()
    await updateUserPassword(passwordForm)
    
    message.success('密码修改成功，请重新登录')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    loading.value = false
    await authStore.logout()
    router.push(LOGIN_PATH)
  } catch (error) {
    console.error('表单校验失败:', error)
  } finally {
    loading.value = false
  }
}

// 更新系统偏好
const handleUpdatePreferences = () => {
  message.success('系统偏好设置已保存')
}

// 重置系统偏好
const handleResetPreferences = () => {
  preferencesForm.theme = 'light'
  preferencesForm.layout = 'side'
  preferencesForm.primaryColor = '#1890ff'
  preferencesForm.tableSize = 'middle'
  preferencesForm.fixedHeader = true
  preferencesForm.tabsBar = true
  
  message.success('已恢复默认设置')
}
</script>

<style lang="scss" scoped>
// 定义变量
$base-spacing: 20px;
$card-padding: 16px;
$primary-color: #1890ff;
$secondary-color: #52c41a;
$dark-color: #001529;
$text-color: #333;
$light-gray: #f5f5f5;
$border-radius: 8px;
$avatar-size: 100px;
$header-height: 64px;
$box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
$transition-duration: 0.3s;

.profile-container {
  height: 100%;
  padding: $base-spacing;
  // background-color: #f0f2f5;
  
  .ant-row {
    margin-bottom: $base-spacing;
  }
  
  .ant-card {
    border-radius: $border-radius;
    overflow: hidden;
    transition: transform $transition-duration, box-shadow $transition-duration;
    border: none;
  }
  
  .profile-card {
    text-align: center;
    border-radius: $border-radius;
    box-shadow: $box-shadow;
    height: 100%;
    // background: linear-gradient(to bottom, rgba($primary-color, 0.03) 0%, rgba($primary-color, 0) 100%);
    
    .profile-avatar {
      margin-bottom: $base-spacing;
      padding-top: $base-spacing;
      
      .ant-avatar {
        border: 4px solid rgba($primary-color, 0.1);
        box-shadow: 0 4px 8px rgba($primary-color, 0.2);
        transition: all $transition-duration;
        
        &:hover {
          transform: scale(1.05);
          box-shadow: 0 6px 12px rgba($primary-color, 0.3);
        }
      }
      
      .upload-avatar {
        margin-top: $base-spacing / 2;
        
        .ant-btn-link {
          background-color: rgba($primary-color, 0.1);
          border-radius: $border-radius / 2;
          padding: 2px 10px;
          
          &:hover {
            background-color: rgba($primary-color, 0.2);
          }
        }
      }
    }
    
    .profile-info {
      h2 {
        margin-bottom: $base-spacing / 2;
        font-size: $base-spacing - 4px;
        font-weight: 600;
        color: $dark-color;
      }
      
      .profile-role {
        margin-bottom: $base-spacing * 0.75;
        
        :deep(.ant-tag) {
          margin: 0 4px;
          padding: 2px 8px;
          border-radius: 12px;
        }
      }
      
      .profile-detail {
        text-align: left;
        padding: $base-spacing $card-padding / 1.6;
        background-color: rgba(255, 255, 255, 0.6);
        border-radius: $border-radius;
        margin: 0 $base-spacing / 2 $base-spacing / 2;
        
        p {
          margin-bottom: $base-spacing * 0.4;
          display: flex;
          align-items: center;
          padding: $base-spacing / 4 0;
          border-bottom: 1px dashed rgba($dark-color, 0.05);
          
          &:last-child {
            border-bottom: none;
          }
          
          .anticon {
            margin-right: $base-spacing * 0.4;
            color: rgba($primary-color, 0.8);
            font-size: 16px;
          }
        }
      }
    }
  }
  
  // 标签页样式优化
  :deep(.ant-tabs-top > .ant-tabs-nav) {
    margin-bottom: $base-spacing;
    
    &::before {
      border-bottom: none;
    }
    
    .ant-tabs-tab {
      padding: $base-spacing / 2 $base-spacing;
      transition: all $transition-duration;
      
      &:hover {
        color: $primary-color;
      }
      
      &.ant-tabs-tab-active .ant-tabs-tab-btn {
        font-weight: 600;
      }
    }
    
    .ant-tabs-ink-bar {
      height: 3px;
      border-radius: 3px;
      background: linear-gradient(to right, $primary-color, lighten($primary-color, 10%));
    }
  }
  
  // 表单样式优化
  :deep(.ant-form) {
    .ant-form-item-label > label {
      font-weight: 500;
      color: rgba($dark-color, 0.85);
    }
    
    .ant-input, .ant-input-password {
      border-radius: $border-radius / 2;
      border-color: #d9d9d9;
      
      &:hover, &:focus {
        border-color: $primary-color;
        box-shadow: 0 0 0 2px rgba($primary-color, 0.1);
      }
    }
    
    .ant-btn {
      border-radius: $border-radius / 2;
      height: 38px;
      padding: 0 $base-spacing;
      
      &-primary {
        background: linear-gradient(to right, $primary-color, lighten($primary-color, 10%));
        border: none;
        box-shadow: 0 2px 6px rgba($primary-color, 0.3);
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba($primary-color, 0.4);
        }
      }
    }
  }
  
  // 列表样式优化
  :deep(.ant-list) {
    .ant-list-item {
      padding: $base-spacing / 2 0;
      transition: all $transition-duration;
      border-radius: $border-radius / 2;
      
      &:hover {
        background-color: rgba($primary-color, 0.03);
        padding-left: $base-spacing / 2;
        padding-right: $base-spacing / 2;
      }
    }
    
    .ant-list-item-meta-avatar {
      margin-right: $base-spacing;
    }
    
    .ant-list-item-meta-title {
      font-weight: 500;
      color: $dark-color;
    }
    
    .ant-list-item-meta-description {
      color: rgba($dark-color, 0.65);
    }
  }
  
  .color-block {
    width: $base-spacing;
    height: $base-spacing;
    border-radius: $border-radius / 2;
    display: inline-block;
    vertical-align: middle;
    transition: all $transition-duration;
    
    &:hover {
      transform: scale(1.2);
      box-shadow: 0 0 ($base-spacing / 2) rgba($primary-color, 0.5);
    }
  }
  
  // 使用循环生成不同大小的间距类
  @for $i from 1 through 4 {
    .mb-#{$i} {
      margin-bottom: $base-spacing * $i / 4;
    }
    
    .mt-#{$i} {
      margin-top: $base-spacing * $i / 4;
    }
    
    .mr-#{$i} {
      margin-right: $base-spacing * $i / 4;
    }
    
    .ml-#{$i} {
      margin-left: $base-spacing * $i / 4;
    }
  }
}

// 登录日志表格样式
:deep(.ant-table) {
  border-radius: $border-radius;
  overflow: hidden;
  
  .ant-table-thead > tr > th {
    background-color: rgba($primary-color, 0.05);
    font-weight: 500;
  }
  
  .ant-table-tbody > tr > td {
    transition: background $transition-duration;
  }
  
  .ant-table-tbody > tr:hover > td {
    background-color: rgba($primary-color, 0.04);
  }
}

// 添加一些动画效果
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.profile-card, .ant-card {
  animation: fadeIn 0.6s ease-out;
}
</style> 