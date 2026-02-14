<template>
    <div class="forbidden">
      <div class="particles">
        <div class="particle" v-for="i in 20" :key="i"></div>
      </div>
      
      <div class="content">
        <div class="number">403</div>
        <div class="text">
          <div class="title">权限不足</div>
          <div class="subtitle">抱歉，您没有权限访问此页面</div>
        </div>
        <div class="lock">
          <lock-outlined class="lock-icon" />
        </div>
        <div class="actions">
          <a-button type="primary" size="large" @click="goHome">
            <template #icon><home-outlined /></template>
            返回首页
          </a-button>
          <a-button size="large" @click="goBack">
            <template #icon><arrow-left-outlined /></template>
            返回上页
          </a-button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { useRouter } from 'vue-router';
  import { HomeOutlined, ArrowLeftOutlined, LockOutlined } from '@ant-design/icons-vue';
  
  const router = useRouter();
  
  const goHome = () => {
    router.push('/');
  };
  
  const goBack = () => {
    router.go(-1);
  };
  </script>
  
  <style scoped>
  .forbidden {
    height: 100vh;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(to bottom, #3c1053 0%, #ad5389 100%);
    position: relative;
    overflow: hidden;
  }
  
  .particles {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 1;
  }
  
  .particle {
    position: absolute;
    background-color: rgba(255, 255, 255, 0.6);
    border-radius: 50%;
    opacity: 0.6;
    animation: float 4s infinite;
  }
  
  @keyframes float {
    0% { transform: translateY(0) rotate(0deg); opacity: 0.2; }
    25% { opacity: 0.7; }
    50% { transform: translateY(-20px) rotate(180deg); opacity: 0.2; }
    75% { opacity: 0.7; }
    100% { transform: translateY(0) rotate(360deg); opacity: 0.2; }
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
    text-shadow: 0 0 20px rgba(255, 100, 255, 0.6);
    line-height: 1;
    letter-spacing: -10px;
    margin-bottom: 20px;
    animation: pulse 2s infinite;
  }
  
  @keyframes pulse {
    0% { transform: scale(1); }
    50% { transform: scale(1.05); }
    100% { transform: scale(1); }
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
  
  .lock {
    position: absolute;
    top: 40px;
    right: -30px;
    width: 100px;
    height: 100px;
    animation: shake 2s ease-in-out infinite;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  @keyframes shake {
    0% { transform: rotate(0deg); }
    25% { transform: rotate(-5deg); }
    50% { transform: rotate(0deg); }
    75% { transform: rotate(5deg); }
    100% { transform: rotate(0deg); }
  }
  
  .lock-icon {
    font-size: 80px;
    color: #fff;
    filter: drop-shadow(0 0 10px rgba(255, 100, 255, 0.6));
  }
  
  .actions {
    display: flex;
    gap: 16px;
    margin-top: 20px;
  }
  
  /* 生成随机位置和大小的粒子 */
  .particle:nth-child(1) { top: 10%; left: 20%; width: 4px; height: 4px; animation-delay: 0.5s; }
  .particle:nth-child(2) { top: 15%; left: 40%; width: 6px; height: 6px; animation-delay: 1.5s; }
  .particle:nth-child(3) { top: 25%; left: 65%; width: 3px; height: 3px; animation-delay: 0.2s; }
  .particle:nth-child(4) { top: 30%; left: 80%; width: 5px; height: 5px; animation-delay: 1s; }
  .particle:nth-child(5) { top: 40%; left: 25%; width: 4px; height: 4px; animation-delay: 0.7s; }
  .particle:nth-child(6) { top: 50%; left: 50%; width: 6px; height: 6px; animation-delay: 1.2s; }
  .particle:nth-child(7) { top: 60%; left: 15%; width: 3px; height: 3px; animation-delay: 0.3s; }
  .particle:nth-child(8) { top: 70%; left: 70%; width: 5px; height: 5px; animation-delay: 0.9s; }
  .particle:nth-child(9) { top: 75%; left: 35%; width: 4px; height: 4px; animation-delay: 1.7s; }
  .particle:nth-child(10) { top: 80%; left: 90%; width: 6px; height: 6px; animation-delay: 0.1s; }
  .particle:nth-child(11) { top: 5%; left: 50%; width: 5px; height: 5px; animation-delay: 1.1s; }
  .particle:nth-child(12) { top: 20%; left: 5%; width: 3px; height: 3px; animation-delay: 0.4s; }
  .particle:nth-child(13) { top: 35%; left: 95%; width: 4px; height: 4px; animation-delay: 1.3s; }
  .particle:nth-child(14) { top: 45%; left: 60%; width: 6px; height: 6px; animation-delay: 0.6s; }
  .particle:nth-child(15) { top: 55%; left: 30%; width: 3px; height: 3px; animation-delay: 1.6s; }
  .particle:nth-child(16) { top: 65%; left: 85%; width: 5px; height: 5px; animation-delay: 0.8s; }
  .particle:nth-child(17) { top: 85%; left: 45%; width: 4px; height: 4px; animation-delay: 1.9s; }
  .particle:nth-child(18) { top: 90%; left: 10%; width: 5px; height: 5px; animation-delay: 1.4s; }
  .particle:nth-child(19) { top: 15%; left: 75%; width: 6px; height: 6px; animation-delay: 0.2s; }
  .particle:nth-child(20) { top: 95%; left: 55%; width: 3px; height: 3px; animation-delay: 1.8s; }
  
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
    
    .lock {
      width: 80px;
      height: 80px;
      top: 20px;
      right: -20px;
    }
    
    .lock-icon {
      font-size: 60px;
    }
    
    .actions {
      flex-direction: column;
    }
  }
  </style>