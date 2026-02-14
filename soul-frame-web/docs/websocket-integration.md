# WebSocket与登录状态共享集成指南

## 概述

本文档说明如何实现WebSocket服务与主应用之间的登录状态共享。通过Sa-Token和Redis，两个服务可以共享用户的认证状态。

## 架构说明

### 1. 认证流程
```
用户登录 → base_admin_web → 生成Sa-Token → 存储到Redis
用户连接WebSocket → base_admin_websocket → 验证Sa-Token → 从Redis获取用户信息
```

### 2. 关键组件
- **Sa-Token**: 统一的认证框架
- **Redis**: 共享的session存储
- **WebSocket拦截器**: 验证连接时的token
- **前端WebSocket管理器**: 处理连接和消息

## 配置说明

### 1. Sa-Token配置
两个服务使用相同的Sa-Token配置：
```yaml
sa-token:
  token-name: Authorization
  token-prefix: Bearer
  timeout: 2592000
  jwt-secret-key: dawn
```

### 2. Redis配置
两个服务连接相同的Redis实例：
```yaml
spring:
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      database: 0
```

## 使用方法

### 1. 前端连接WebSocket

```javascript
import websocketManager from '@/utils/websocket'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const token = authStore.token

// 连接WebSocket
websocketManager.connect(
  token,
  // 连接成功回调
  (frame) => {
    console.log('连接成功')
    // 订阅消息
    websocketManager.subscribe('/topic/public', (message) => {
      console.log('收到消息:', message.body)
    })
  },
  // 连接失败回调
  (error) => {
    console.error('连接失败:', error)
  }
)
```

### 2. 发送消息

```javascript
// 发送聊天消息
websocketManager.send('/app/chat.send', {
  content: 'Hello World',
  type: 'CHAT'
})
```

### 3. 在Vue组件中使用

```vue
<template>
  <WebSocketChat />
</template>

<script setup>
import WebSocketChat from '@/components/WebSocketChat.vue'
</script>
```

## 安全考虑

### 1. Token验证
- WebSocket连接时必须在Authorization头中携带有效的Bearer token
- 服务端会验证token的有效性和用户权限

### 2. 白名单配置
WebSocket服务配置了适当的白名单：
```yaml
router:
  whitelist:
    - /ws/**
    - /error
```

### 3. 跨域配置
WebSocket端点允许跨域访问：
```java
registry.addEndpoint("/ws")
    .setAllowedOriginPatterns("*")
    .withSockJS();
```

## 故障排除

### 1. 连接失败
- 检查token是否有效
- 确认Redis服务是否正常运行
- 验证两个服务的Sa-Token配置是否一致

### 2. 认证失败
- 确认token格式正确（Bearer + 空格 + token）
- 检查token是否过期
- 验证用户是否已登录

### 3. 消息发送失败
- 确认WebSocket连接状态
- 检查消息格式是否正确
- 验证目标路径是否存在

## 扩展功能

### 1. 用户状态管理
可以在WebSocket连接时获取更多用户信息：
```java
Object loginId = StpUtil.getLoginIdByToken(token);
// 可以进一步获取用户详细信息
```

### 2. 权限控制
可以基于用户角色限制WebSocket功能：
```java
// 检查用户权限
StpUtil.checkPermission("websocket:chat");
```

### 3. 消息路由
可以根据用户ID进行消息路由：
```java
// 发送给特定用户
messagingTemplate.convertAndSendToUser(userId, "/queue/private", message);
```

## 监控和日志

### 1. 连接监控
- 记录WebSocket连接和断开事件
- 监控在线用户数量
- 跟踪消息发送频率

### 2. 错误日志
- 记录认证失败事件
- 监控连接异常
- 跟踪消息处理错误

## 性能优化

### 1. 连接池管理
- 合理配置Redis连接池
- 优化WebSocket连接数限制

### 2. 消息缓存
- 对频繁访问的用户信息进行缓存
- 实现消息历史缓存机制

### 3. 负载均衡
- 在多个WebSocket服务实例间共享Redis
- 实现会话粘性或会话复制
