<template>
  <div class="login-container">
    <div class="login-background">
      <div id="particles-js"></div>
      <div class="wave-container">
        <div class="wave wave1"></div>
        <div class="wave wave2"></div>
        <div class="wave wave3"></div>
      </div>
    </div>
    <div class="login-content">
      <div class="login-box">
        <div class="login-header">
          <h1>Base Admin</h1>
          <p>基于 Vue3 + Vite + Ant Design Vue 的后台管理系统</p>
        </div>
        <a-form :model="formState" :rules="formRules" name="loginForm" @finish="handleSubmit" autocomplete="off">
          <a-form-item name="username">
            <a-input v-model:value="formState.username" size="large" placeholder="用户名" :maxLength="20">
              <template #prefix>
                <UserOutlined />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item name="password">
            <a-input-password v-model:value="formState.password" size="large" placeholder="密码" :maxLength="20">
              <template #prefix>
                <LockOutlined />
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item name="captcha">
            <a-row :gutter="16">
              <a-col :span="16">
                <a-input v-model:value="formState.captcha" size="large" placeholder="验证码" :maxLength="4">
                  <template #prefix>
                    <SafetyOutlined />
                  </template>
                </a-input>
              </a-col>
              <a-col :span="8">
                <div class="captcha-img" @click="refreshCaptcha">
                  <img :src="captchaInfo.captchaSvg" alt="验证码" />
                </div>
              </a-col>
            </a-row>
          </a-form-item>

          <a-form-item>
            <a-row :gutter="16">
              <a-col :span="12">
                <a-checkbox v-model:checked="formState.remember">
                  记住密码
                </a-checkbox>
              </a-col>
              <a-col :span="12" style="text-align: right">
                <a class="forgot-link" @click="handleForgotPassword">忘记密码？</a>
              </a-col>
            </a-row>
          </a-form-item>

          <a-form-item>
            <a-button type="primary" html-type="submit" size="large" :loading="loading" block>
              登录
            </a-button>
          </a-form-item>

          <div class="other-login">
            <div class="divider">
              <span>其他登录方式</span>
            </div>
            <div class="icons">
              <a-tooltip title="Github">
                <GithubOutlined class="icon" @click="handleThirdPartyLogin('Github')" />
              </a-tooltip>
              <a-tooltip title="微信">
                <WechatOutlined class="icon" @click="handleThirdPartyLogin('微信')" />
              </a-tooltip>
              <a-tooltip title="钉钉">
                <DingdingOutlined class="icon" @click="handleThirdPartyLogin('钉钉')" />
              </a-tooltip>
            </div>
          </div>
        </a-form>
      </div>
    </div>
    <div class="footer">
      <p>Copyright © {{ new Date().getFullYear() }} Base Admin</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, reactive, ref } from 'vue'
import {
  DingdingOutlined,
  GithubOutlined,
  LockOutlined,
  SafetyOutlined,
  UserOutlined,
  WechatOutlined
} from '@ant-design/icons-vue'
import {message, notification} from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import { getCaptcha } from '@/api/modules/auth'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const authStore = useAuthStore()

// 验证码相关状态
const captchaInfo = ref({
  captchaSvg: '',
  captchaCode: ''
})

// 表单状态
const formState = reactive({
  username: 'admin',
  password: '123456',
  captcha: 'AHAH',
  remember: true
})

// 表单验证规则
const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应在 3-20 个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度应在 6-20 个字符之间', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 4, message: '验证码长度应为 4 位', trigger: 'blur' }
  ]
}

// 获取验证码
const refreshCaptcha = async () => {
    const data = await getCaptcha()
    captchaInfo.value = data
}

// 检查是否有记住的密码
const checkRemembered = () => {
  try {
    const remembered = localStorage.getItem('remembered')
    if (remembered) {
      const { username, password } = JSON.parse(remembered)
      formState.username = username
      formState.password = password
      formState.remember = true
    }
  } catch (error) {
    console.error('读取记住的密码失败:', error)
    localStorage.removeItem('remembered')
  }
}

// 记住密码处理
const handleRememberPassword = () => {
  if (formState.remember) {
    localStorage.setItem('remembered', JSON.stringify({
      username: formState.username,
      password: formState.password
    }))
  } else {
    localStorage.removeItem('remembered')
  }
}

// 处理第三方登录
const handleThirdPartyLogin = (type) => {
  message.info(`${type}登录功能开发中...`)
}

// 处理忘记密码
const handleForgotPassword = () => {
  message.info('忘记密码功能开发中...')
}

// 提交表单
const handleSubmit = async () => {
  try {
    loading.value = true
    await authStore.login({
      username: formState.username,
      password: formState.password,
      code: formState.captcha,
      uuid: captchaInfo.value.captchaCode
    })

    handleRememberPassword()
    notification.success({
      message: '登录成功',
      description: '欢迎回来，祝您工作顺利！',
      duration: 1,
    })

    // 跳转到来源页面或首页
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (error) {
    message.error(error.message || '登录失败')

    console.error('登录失败:', error)
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

// 按键处理
const handleKeyPress = (e) => {
  if (e.key === 'Enter') {
    handleSubmit()
  }
}

// 初始化粒子背景
const initParticles = () => {
  if (window.particlesJS) {
    window.particlesJS('particles-js', {
      particles: {
        number: {
          value: 80,
          density: {
            enable: true,
            value_area: 800
          }
        },
        color: {
          value: '#ffffff'
        },
        shape: {
          type: 'circle',
          stroke: {
            width: 0,
            color: '#000000'
          },
        },
        opacity: {
          value: 0.5,
          random: true,
          anim: {
            enable: true,
            speed: 1,
            opacity_min: 0.1,
            sync: false
          }
        },
        size: {
          value: 3,
          random: true,
          anim: {
            enable: true,
            speed: 2,
            size_min: 0.1,
            sync: false
          }
        },
        line_linked: {
          enable: true,
          distance: 150,
          color: '#ffffff',
          opacity: 0.4,
          width: 1
        },
        move: {
          enable: true,
          speed: 1,
          direction: 'none',
          random: true,
          straight: false,
          out_mode: 'out',
          bounce: false,
        }
      },
      interactivity: {
        detect_on: 'canvas',
        events: {
          onhover: {
            enable: true,
            mode: 'grab'
          },
          onclick: {
            enable: true,
            mode: 'push'
          },
          resize: true
        },
        modes: {
          grab: {
            distance: 140,
            line_linked: {
              opacity: 1
            }
          },
          push: {
            particles_nb: 4
          }
        }
      },
      retina_detect: true
    })
  }
}

// 加载粒子JS脚本
const loadParticlesScript = () => {
  if (!window.particlesJS) {
    const script = document.createElement('script')
    script.src = 'https://cdn.jsdelivr.net/particles.js/2.0.0/particles.min.js'
    script.onload = () => initParticles()
    document.head.appendChild(script)
  } else {
    initParticles()
  }
}

onMounted(() => {
  refreshCaptcha()
  checkRemembered()
  // 添加键盘事件监听
  window.addEventListener('keypress', handleKeyPress)
  // 加载粒子动画
  loadParticlesScript()
})

onUnmounted(() => {
  // 移除键盘事件监听
  window.removeEventListener('keypress', handleKeyPress)
})
</script>

<style lang="scss" scoped>
// 变量定义
$primary-color: #1890ff;
$secondary-color: #722ed1;
$accent-color: #13c2c2;
$white: #ffffff;
$text-color: rgba(0, 0, 0, 0.65);
$text-secondary: rgba(0, 0, 0, 0.45);
$border-color: #d9d9d9;
$shadow-color: rgba(0, 0, 0, 0.15);

// Mixins
@mixin gradient-bg {
  background: linear-gradient(135deg, $primary-color 0%, $secondary-color 75%, $accent-color 100%);
}

@mixin text-gradient {
  background: linear-gradient(135deg, $primary-color 0%, $secondary-color 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

@mixin hover-transform {
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-1px);
  }
  
  &:active {
    transform: translateY(0);
  }
}

@mixin glass-effect {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 8px;
  box-shadow: 0 8px 24px $shadow-color;
}

.login-container {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  @include gradient-bg;

  .login-background {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 0;
    
    #particles-js {
      position: absolute;
      width: 100%;
      height: 100%;
    }
    
    .wave-container {
      position: absolute;
      bottom: 0;
      left: 0;
      width: 100%;
      height: 150px;
      overflow: hidden;
    }

    .wave {
      position: absolute;
      bottom: 0;
      left: 0;
      width: 200%;
      height: 100%;
      background: transparent;
      
      &.wave1 {
        background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320"><path fill="rgba(255,255,255,0.7)" fill-opacity="1" d="M0,192L48,181.3C96,171,192,149,288,154.7C384,160,480,192,576,202.7C672,213,768,203,864,170.7C960,139,1056,85,1152,85.3C1248,85,1344,139,1392,165.3L1440,192L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path></svg>') repeat-x;
        animation: wave 15s linear infinite;
        z-index: 3;
      }

      &.wave2 {
        background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320"><path fill="rgba(255,255,255,0.5)" fill-opacity="1" d="M0,256L48,229.3C96,203,192,149,288,138.7C384,128,480,160,576,186.7C672,213,768,235,864,224C960,213,1056,171,1152,165.3C1248,160,1344,192,1392,208L1440,224L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path></svg>') repeat-x;
        animation: wave 20s linear infinite;
        z-index: 2;
      }

      &.wave3 {
        background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320"><path fill="rgba(255,255,255,0.3)" fill-opacity="1" d="M0,224L48,213.3C96,203,192,181,288,154.7C384,128,480,96,576,122.7C672,149,768,235,864,250.7C960,267,1056,213,1152,176C1248,139,1344,117,1392,106.7L1440,96L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path></svg>') repeat-x;
        animation: wave 30s linear infinite;
        z-index: 1;
      }
    }
  }

  .login-content {
    position: relative;
    z-index: 1;
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 40px 0;
  }

  .login-box {
    width: 400px;
    padding: 40px;
    @include glass-effect;
    animation: fadeIn 0.5s ease-out;
  }

  .login-header {
    text-align: center;
    margin-bottom: 40px;

    h1 {
      margin: 0;
      font-size: 28px;
      font-weight: 600;
      @include text-gradient;
    }

    p {
      margin: 12px 0 0;
      font-size: 14px;
      color: $text-secondary;
    }
  }

  // Ant Design 组件样式覆盖
  :deep(.ant-input-affix-wrapper) {
    background: transparent;
    border-radius: 4px;
    transition: all 0.3s;

    &:hover,
    &:focus {
      border-color: $primary-color;
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
    }

    .anticon {
      color: #bfbfbf;
      transition: color 0.3s;
    }

    &:hover .anticon {
      color: $primary-color;
    }

    input.ant-input {
      background: transparent;
    }
  }

  .captcha-img {
    height: 40px;
    border: 1px solid $border-color;
    border-radius: 4px;
    cursor: pointer;
    overflow: hidden;
    transition: all 0.3s;

    &:hover {
      border-color: $primary-color;
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
    }

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .forgot-link {
    color: $primary-color;
    transition: all 0.3s;
    cursor: pointer;

    &:hover {
      color: #40a9ff;
      text-decoration: underline;
    }
  }

  :deep(.ant-btn) {
    height: 40px;
    font-size: 16px;
    border-radius: 4px;
    transition: all 0.3s;

    &.ant-btn-primary {
      @include gradient-bg;
      border: none;
      @include hover-transform;

      &:hover {
        box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
      }
    }
  }

  :deep(.ant-checkbox-wrapper) {
    color: $text-color;
  }

  .other-login {
    margin-top: 24px;
    text-align: center;

    .divider {
      position: relative;
      margin: 16px 0;
      color: $text-secondary;

      &::before,
      &::after {
        content: '';
        position: absolute;
        top: 50%;
        width: 30%;
        height: 1px;
        background: rgba(0, 0, 0, 0.06);
      }

      &::before {
        left: 0;
      }

      &::after {
        right: 0;
      }

      span {
        padding: 0 24px;
        background: rgba(255, 255, 255, 0.95);
      }
    }

    .icons {
      display: flex;
      justify-content: center;
      gap: 24px;

      .icon {
        font-size: 24px;
        color: $text-secondary;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          color: $primary-color;
          transform: scale(1.2);
        }
      }
    }
  }

  .footer {
    position: relative;
    z-index: 1;
    text-align: center;
    padding: 24px;
    color: rgba(255, 255, 255, 0.8);
  }
}

// 动画定义
@keyframes wave {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(-50%);
  }
}

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

// 响应式设计
@media (max-width: 768px) {
  .login-container {
    .login-box {
      width: 90%;
      padding: 30px 20px;
      margin: 0 20px;
    }

    .login-header {
      h1 {
        font-size: 24px;
      }
    }

    .other-login {
      .icons {
        gap: 16px;

        .icon {
          font-size: 20px;
        }
      }
    }
  }
}

@media (max-width: 480px) {
  .login-container {
    .login-content {
      padding: 20px 0;
    }

    .login-box {
      padding: 20px;
    }

    .login-header {
      margin-bottom: 30px;

      h1 {
        font-size: 20px;
      }

      p {
        font-size: 12px;
      }
    }
  }
}
</style>
