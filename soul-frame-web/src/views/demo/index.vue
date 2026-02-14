<template>
  <div class="common-container">
    <div style="margin-bottom: 12px;">
      <a-button size="small" type="primary" @click="connect">
        {{ isConnected ? '断开SockJS' : '连接SockJS' }}
      </a-button>
      <a-divider type="vertical"/>
      <a-button :disabled="!isConnected" size="small" @click="sendSock">发送测试</a-button>
    </div>

    <!-- <div class="sockjs-info">
      <div>SockJS 状态：{{ sockStatus }}</div>
      <div>累计消息数：{{ sockData.length }}</div>
      <div>最后一条消息：</div>
      <pre>{{ formatJson(sockLastMessage) }}</pre>
    </div> -->
  </div>
</template>

<script setup>
import {message} from 'ant-design-vue';
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
import {useAuthStore} from '@/stores/auth'

// let stompClient = null

const authStore = useAuthStore()
const token = authStore.token

// 原生 WebSocket 连接
const ws = new WebSocket('ws://localhost:8080/api/ws', token);

ws.onopen = function (event) {
  console.log('WebSocket connected');
};

ws.onmessage = function (event) {
  console.log('Received: ' + event.data);
};

ws.onclose = function (event) {
  console.log('WebSocket closed');
};

// // 发送消息
// ws.send('Hello Server');


// function connect() {
//   // 后端 WebSocket 端点 - 使用主应用地址
//   const socket = new SockJS('http://localhost:8080/api/ws')
//   stompClient = Stomp.over(socket)

//   stompClient.connect({
//     userId: '1',
//     Authorization: `Bearer ${token}`
//   }, frame => {
//     console.log('Connected: ' + frame)

//     // 订阅某个主题
//     stompClient.subscribe('/topic/messages', message => {
//       console.log('收到消息:', JSON.parse(message.body))
//     })
//   })
// }

// function sendMessage(msg) {
//   stompClient.send('/app/chat', {}, JSON.stringify({ content: msg }))
// }


</script>

<style scoped>
.common-container {
  padding: 16px;
  background-color: #fff;
  border-radius: 4px;
  margin: 16px;
}

.sockjs-info {
  margin-top: 12px;
  font-size: 12px;
  color: #666;
}
</style>