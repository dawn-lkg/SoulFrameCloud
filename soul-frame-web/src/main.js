import {createApp} from 'vue'
import {createPinia} from 'pinia'
import './assets/icons/iconfont.js'
import 'ant-design-vue/dist/reset.css'
import 'nprogress/nprogress.css'
import './styles/global.scss'
import zhCN from 'ant-design-vue/es/locale/zh_CN'
import Antd, {ConfigProvider, message} from 'ant-design-vue'
import App from './App.vue'
import router, {setupRouterGuards} from './router'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import {useAuthStore} from '@/stores/auth'
import {useConfigStore} from '@/stores/config'

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

const app = createApp(App)

// 全局配置消息提示
app.config.globalProperties.$message = message


app.use(pinia)
app.use(Antd)
// 初始化 store
const store = useAuthStore()
const configStore = useConfigStore()
// 设置路由守卫
setupRouterGuards(router, store)

app.use(router)
app.provide('antLocale', zhCN)
app.component('ConfigProvider', ConfigProvider)

// 错误处理
app.config.errorHandler = (err, vm, info) => {
  console.error('Vue Error:', err, info)
}

// 性能追踪
app.config.performance = process.env.NODE_ENV !== 'production'

app.mount('#app')

configStore.initConfig()