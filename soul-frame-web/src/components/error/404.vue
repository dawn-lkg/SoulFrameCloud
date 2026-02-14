<template>
    <div class="not-found">
      <div class="stars">
        <div class="star" v-for="i in 20" :key="i"></div>
      </div>
      
      <div class="content">
        <div class="number">404</div>
        <div class="text">
          <div class="title">页面丢失在太空中了</div>
          <div class="subtitle">抱歉，您访问的页面不存在或已被移除</div>
        </div>
        <div class="astronaut">
          <rocket-outlined class="astronaut-icon" />
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
  import { HomeOutlined, ArrowLeftOutlined, RocketOutlined } from '@ant-design/icons-vue';
  
  const router = useRouter();
  
  const goHome = () => {
    router.push('/');
  };
  
  const goBack = () => {
    router.go(-1);
  };
  </script>
  
  <style scoped>
  .not-found {
    height: 100vh;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(to bottom, #1a1a2e 0%, #16213e 100%);
    position: relative;
    overflow: hidden;
  }
  
  .stars {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 1;
  }
  
  .star {
    position: absolute;
    background-color: #ffffff;
    border-radius: 50%;
    opacity: 0.8;
    animation: twinkle 2s infinite;
  }
  
  @keyframes twinkle {
    0% { opacity: 0.2; }
    50% { opacity: 0.8; }
    100% { opacity: 0.2; }
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
    text-shadow: 0 0 20px rgba(100, 100, 255, 0.8);
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
  
  .astronaut {
    position: absolute;
    top: 30px;
    right: -30px;
    width: 140px;
    height: 140px;
    animation: float 6s ease-in-out infinite;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  @keyframes float {
    0% { transform: translateY(0px) rotate(0deg); }
    50% { transform: translateY(-20px) rotate(5deg); }
    100% { transform: translateY(0px) rotate(0deg); }
  }
  
  .astronaut-icon {
    font-size: 100px;
    color: #fff;
  }
  
  .actions {
    display: flex;
    gap: 16px;
    margin-top: 20px;
  }
  
  /* 生成星星的随机位置和大小 */
  .star:nth-child(1) { top: 10%; left: 20%; width: 2px; height: 2px; animation-delay: 0.5s; }
  .star:nth-child(2) { top: 15%; left: 40%; width: 3px; height: 3px; animation-delay: 1.5s; }
  .star:nth-child(3) { top: 25%; left: 65%; width: 1px; height: 1px; animation-delay: 0.2s; }
  .star:nth-child(4) { top: 30%; left: 80%; width: 2px; height: 2px; animation-delay: 1s; }
  .star:nth-child(5) { top: 40%; left: 25%; width: 2px; height: 2px; animation-delay: 0.7s; }
  .star:nth-child(6) { top: 50%; left: 50%; width: 3px; height: 3px; animation-delay: 1.2s; }
  .star:nth-child(7) { top: 60%; left: 15%; width: 1px; height: 1px; animation-delay: 0.3s; }
  .star:nth-child(8) { top: 70%; left: 70%; width: 2px; height: 2px; animation-delay: 0.9s; }
  .star:nth-child(9) { top: 75%; left: 35%; width: 1px; height: 1px; animation-delay: 1.7s; }
  .star:nth-child(10) { top: 80%; left: 90%; width: 3px; height: 3px; animation-delay: 0.1s; }
  .star:nth-child(11) { top: 5%; left: 50%; width: 2px; height: 2px; animation-delay: 1.1s; }
  .star:nth-child(12) { top: 20%; left: 5%; width: 1px; height: 1px; animation-delay: 0.4s; }
  .star:nth-child(13) { top: 35%; left: 95%; width: 2px; height: 2px; animation-delay: 1.3s; }
  .star:nth-child(14) { top: 45%; left: 60%; width: 3px; height: 3px; animation-delay: 0.6s; }
  .star:nth-child(15) { top: 55%; left: 30%; width: 1px; height: 1px; animation-delay: 1.6s; }
  .star:nth-child(16) { top: 65%; left: 85%; width: 2px; height: 2px; animation-delay: 0.8s; }
  .star:nth-child(17) { top: 85%; left: 45%; width: 1px; height: 1px; animation-delay: 1.9s; }
  .star:nth-child(18) { top: 90%; left: 10%; width: 2px; height: 2px; animation-delay: 1.4s; }
  .star:nth-child(19) { top: 15%; left: 75%; width: 3px; height: 3px; animation-delay: 0.2s; }
  .star:nth-child(20) { top: 95%; left: 55%; width: 1px; height: 1px; animation-delay: 1.8s; }
  
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
    
    .astronaut {
      width: 100px;
      height: 100px;
      top: 20px;
      right: -20px;
    }
    
    .astronaut-icon {
      font-size: 70px;
    }
    
    .actions {
      flex-direction: column;
    }
  }
  </style>