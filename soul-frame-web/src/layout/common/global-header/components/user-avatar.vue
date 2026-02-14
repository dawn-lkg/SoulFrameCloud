<template>
  <a-dropdown>
    <hover-container class="user-avatar">
      <a-avatar :style="{ backgroundColor: '#1890ff', verticalAlign: 'middle' }" :size="32" :gap="12"
        :src="userInfo.avatar">
      </a-avatar>
      <span class="username">{{userInfo.nickName}}</span>
    </hover-container>
    <template #overlay>
      <a-menu class="user-dropdown-menu">
        <a-menu-item key="1" @click="goToProfile">
          <div class="menu-item-content">
            <SettingOutlined style="margin-right: 8px; font-size: 16px;" />
            <span>个人中心</span>
          </div>
        </a-menu-item>
        <a-menu-item key="2" @click="handleLogout">
          <div class="menu-item-content">
            <LogoutOutlined style="margin-right: 8px; font-size: 16px;" />
            <span>退出登录</span>
          </div>
        </a-menu-item>
      </a-menu>
    </template>
  </a-dropdown>
</template>

<script setup>
import {LogoutOutlined, SettingOutlined,} from '@ant-design/icons-vue'
import {useAuthStore} from '@/stores/auth'
import {useRouter} from 'vue-router'
import {LOGIN_PATH} from '@/config'
import {message} from 'ant-design-vue'


const authStore = useAuthStore()  
const router = useRouter()
const userInfo = authStore.userInfo;

// 前往个人中心
const goToProfile = () => {
  router.push('/profile')
}

const handleLogout = () => {
  authStore.logout().then(() => {
    message.success('退出成功')
    router.push(LOGIN_PATH)
  })
}
</script>

<style lang="scss" scoped>
.user-avatar {
  padding: 0 12px;
  display: flex;
  align-items: center;

  .username {
    font-weight: 600;
    font-size: 16px;
    margin-left: 8px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 120px;
  }
}

:deep(.user-dropdown-menu) {
  min-width: 160px;
  
  .ant-dropdown-menu-item {
    padding: 8px 16px;
  }
  
  .menu-item-content {
    display: flex;
    align-items: center;
  
  }
}
</style>