<template>
    <div class="server-error">
      <div class="circuit">
        <div class="circuit-line" v-for="i in 15" :key="i"></div>
        <div class="circuit-dot" v-for="i in 25" :key="`dot-${i}`"></div>
      </div>
      
      <div class="content">
        <div class="number">500</div>
        <div class="text">
          <div class="title">服务器内部错误</div>
          <div class="subtitle">抱歉，服务器遇到了一个错误，我们正在抓紧修复</div>
        </div>
        <div class="error-icon">
          <bug-outlined class="bug-icon" />
        </div>
        <div class="actions">
          <a-button type="primary" size="large" @click="goHome">
            <template #icon><home-outlined /></template>
            返回首页
          </a-button>
          <a-button size="large" @click="refresh">
            <template #icon><reload-outlined /></template>
            刷新页面
          </a-button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { useRouter } from 'vue-router';
  import { HomeOutlined, ReloadOutlined, BugOutlined } from '@ant-design/icons-vue';
  
  const router = useRouter();
  
  const goHome = () => {
    router.push('/');
  };
  
  const refresh = () => {
    window.location.reload();
  };
  </script>
  
  <style scoped>
  .server-error {
    height: 100vh;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(to bottom, #141e30 0%, #243b55 100%);
    position: relative;
    overflow: hidden;
  }
  
  .circuit {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 1;
  }
  
  .circuit-line {
    position: absolute;
    background-color: rgba(0, 200, 100, 0.15);
    border-radius: 1px;
  }
  
  .circuit-dot {
    position: absolute;
    background-color: rgba(0, 255, 170, 0.8);
    border-radius: 50%;
    width: 4px;
    height: 4px;
    animation: blink 3s infinite;
  }
  
  @keyframes blink {
    0% { opacity: 0.2; box-shadow: 0 0 2px rgba(0, 255, 170, 0.1); }
    50% { opacity: 1; box-shadow: 0 0 8px rgba(0, 255, 170, 0.8); }
    100% { opacity: 0.2; box-shadow: 0 0 2px rgba(0, 255, 170, 0.1); }
  }
  
  .content {
    position: relative;
    z-index: 2;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20px;
    max-width: 800px;
    padding: 40px;
    text-align: center;
  }
  
  .number {
    font-size: 180px;
    font-weight: 900;
    color: #fff;
    text-shadow: 0 0 20px rgba(0, 255, 170, 0.8);
    line-height: 1;
    letter-spacing: -10px;
    margin-bottom: 20px;
    animation: glitch 3s infinite;
  }
  
  @keyframes glitch {
    0% { transform: translate(0); }
    2% { transform: translate(-3px, 2px); }
    4% { transform: translate(3px, -2px); }
    5% { transform: translate(0); }
    45% { transform: translate(0); }
    47% { transform: translate(-2px, -1px); }
    49% { transform: translate(2px, 1px); }
    50% { transform: translate(0); }
    100% { transform: translate(0); }
  }
  
  .text {
    margin-bottom: 40px;
  }
  
  .title {
    font-size: 36px;
    font-weight: 700;
    color: #fff;
    margin-bottom: 10px;
  }
  
  .subtitle {
    font-size: 18px;
    color: rgba(255, 255, 255, 0.7);
  }
  
  .error-icon {
    position: absolute;
    top: 20px;
    right: -20px;
    width: 100px;
    height: 100px;
    animation: rotate 10s linear infinite;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  @keyframes rotate {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
  }
  
  .bug-icon {
    font-size: 80px;
    color: rgba(0, 255, 170, 0.8);
    filter: drop-shadow(0 0 10px rgba(0, 255, 170, 0.6));
  }
  
  .actions {
    display: flex;
    gap: 16px;
    margin-top: 20px;
  }
  
  /* 生成电路线条 */
  .circuit-line:nth-child(1) { top: 10%; left: 0; width: 40%; height: 1px; }
  .circuit-line:nth-child(2) { top: 20%; right: 0; width: 30%; height: 1px; }
  .circuit-line:nth-child(3) { top: 40%; left: 10%; width: 20%; height: 1px; }
  .circuit-line:nth-child(4) { top: 60%; right: 10%; width: 35%; height: 1px; }
  .circuit-line:nth-child(5) { top: 80%; left: 20%; width: 30%; height: 1px; }
  .circuit-line:nth-child(6) { top: 90%; right: 5%; width: 40%; height: 1px; }
  .circuit-line:nth-child(7) { left: 15%; top: 5%; width: 1px; height: 30%; }
  .circuit-line:nth-child(8) { left: 35%; top: 20%; width: 1px; height: 40%; }
  .circuit-line:nth-child(9) { right: 25%; top: 10%; width: 1px; height: 35%; }
  .circuit-line:nth-child(10) { right: 10%; top: 40%; width: 1px; height: 45%; }
  .circuit-line:nth-child(11) { left: 25%; bottom: 0; width: 1px; height: 30%; }
  .circuit-line:nth-child(12) { left: 45%; bottom: 20%; width: 1px; height: 25%; }
  .circuit-line:nth-child(13) { right: 30%; bottom: 10%; width: 1px; height: 30%; }
  .circuit-line:nth-child(14) { right: 15%; bottom: 15%; width: 1px; height: 20%; }
  .circuit-line:nth-child(15) { left: 5%; top: 50%; width: 1px; height: 40%; }
  
  /* 生成电路点 */
  .circuit-dot:nth-child(1) { top: 10%; left: 15%; animation-delay: 0.5s; }
  .circuit-dot:nth-child(2) { top: 20%; left: 35%; animation-delay: 1.5s; }
  .circuit-dot:nth-child(3) { top: 40%; left: 10%; animation-delay: 0.2s; }
  .circuit-dot:nth-child(4) { top: 60%; left: 25%; animation-delay: 1s; }
  .circuit-dot:nth-child(5) { top: 80%; left: 20%; animation-delay: 0.7s; }
  .circuit-dot:nth-child(6) { top: 90%; left: 40%; animation-delay: 1.2s; }
  .circuit-dot:nth-child(7) { top: 15%; right: 25%; animation-delay: 0.3s; }
  .circuit-dot:nth-child(8) { top: 30%; right: 10%; animation-delay: 0.9s; }
  .circuit-dot:nth-child(9) { top: 45%; right: 30%; animation-delay: 1.7s; }
  .circuit-dot:nth-child(10) { top: 70%; right: 15%; animation-delay: 0.1s; }
  .circuit-dot:nth-child(11) { top: 85%; right: 25%; animation-delay: 1.1s; }
  .circuit-dot:nth-child(12) { top: 5%; left: 50%; animation-delay: 0.4s; }
  .circuit-dot:nth-child(13) { top: 25%; left: 70%; animation-delay: 1.3s; }
  .circuit-dot:nth-child(14) { top: 50%; left: 55%; animation-delay: 0.6s; }
  .circuit-dot:nth-child(15) { top: 75%; left: 65%; animation-delay: 1.6s; }
  .circuit-dot:nth-child(16) { top: 12%; left: 85%; animation-delay: 0.8s; }
  .circuit-dot:nth-child(17) { top: 35%; left: 45%; animation-delay: 1.9s; }
  .circuit-dot:nth-child(18) { top: 55%; left: 80%; animation-delay: 1.4s; }
  .circuit-dot:nth-child(19) { top: 65%; left: 15%; animation-delay: 0.2s; }
  .circuit-dot:nth-child(20) { top: 95%; left: 55%; animation-delay: 1.8s; }
  .circuit-dot:nth-child(21) { top: 42%; left: 30%; animation-delay: 0.5s; }
  .circuit-dot:nth-child(22) { top: 62%; left: 60%; animation-delay: 1.0s; }
  .circuit-dot:nth-child(23) { top: 82%; left: 45%; animation-delay: 0.3s; }
  .circuit-dot:nth-child(24) { top: 22%; left: 75%; animation-delay: 1.2s; }
  .circuit-dot:nth-child(25) { top: 48%; left: 90%; animation-delay: 0.7s; }
  
  /* 响应式调整 */
  @media (max-width: 768px) {
    .number {
      font-size: 120px;
    }
    
    .title {
      font-size: 24px;
    }
    
    .subtitle {
      font-size: 16px;
    }
    
    .error-icon {
      width: 80px;
      height: 80px;
      top: 10px;
      right: -10px;
    }
    
    .bug-icon {
      font-size: 60px;
    }
    
    .actions {
      flex-direction: column;
    }
  }
  </style>