import {createRouter, createWebHistory} from 'vue-router'
import NProgress from 'nprogress'
import {errorRoute, staticRouter} from './modules/staticRouter'
import {dynamicRouter} from './modules/dynamicRouter'
import {useAuthStore} from '@/stores/auth'
import {HOME_PATH, LOGIN_PATH, WHITE_LIST} from '@/config'

NProgress.configure({ showSpinner: false })

const routes = [
  ...staticRouter,
  ...errorRoute,
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 工具函数
const startProgress = () => NProgress.start()
const finishProgress = () => NProgress.done()

const setPageTitle = (to) => {
  const title = to.meta?.title
  if (title) {
    document.title = `${title}`
  }
}

const logRouteAccess = (to, from) => {
  const logData = {
    timestamp: new Date().toISOString(),
    fromPath: from.path,
    toPath: to.path,
    userAgent: navigator.userAgent,
  }
  // console.log('路由访问日志:', logData)
}

const isRateLimited = (to) => {
  const now = Date.now()
  const key = `route_access_${to.path}`
  const lastAccess = sessionStorage.getItem(key)

  if (lastAccess && now - parseInt(lastAccess) < 1000) {
    return true
  }

  sessionStorage.setItem(key, now.toString())
  return false
}

const hasPermission = (userRoles, routeRoles) => {
  if (!routeRoles || routeRoles.length === 0) return true
  if (!userRoles || userRoles.length === 0) return false
  return routeRoles.some((role) => userRoles.includes(role))
}

// 获取Token函数
const getToken = () => {
  const authStore = useAuthStore()
  return authStore.token
}

// 移除Token函数
const removeToken = () => {
  const authStore = useAuthStore()
  authStore.clearAuth()
}


// 主要守卫函数
export const beforeEachGuard = (store) => async (to, from, next) => {
  startProgress()

  try {
    const token = getToken()    
    if (token) {
      await handleAuthenticatedUser(to, from, next, store)
    } else {
      handleUnauthenticatedUser(to, from, next)
    }
  } catch (error) {
    console.error('路由拦截器错误:', error)
    handleError(error, next, store)
  }
}

export const afterEachGuard = (to, from) => {
  finishProgress()
  setPageTitle(to)
  logRouteAccess(to, from)
}

export const beforeResolveGuard = (to, from, next) => {
  if (to.meta?.requiresAuth && !getToken()) {
    next(LOGIN_PATH)
    return
  }

  // if (isRateLimited(to)) {
  //   console.warn('访问过于频繁，请稍后再试')
  //   next(false)
  //   return
  // }

  next()
}

// 处理已认证用户
const handleAuthenticatedUser = async (to, from, next, store) => {
  if (to.path === LOGIN_PATH) {
    next()
    return
  }

  const authStore = useAuthStore()
  const hasUserInfo = Object.keys(authStore.userInfo).length > 0 

  if (!hasUserInfo) {
    try {
      // 获取用户信息
      await authStore.getUserInfo();
    } catch (error) {
      console.error('获取用户信息失败:', error)
      removeToken()
      next(`${LOGIN_PATH}?redirect=${to.path}`)
    }
  }
  // console.log(router.getRoutes());
  
  // 初始化路由
  if (!authStore.isInitRoutes) {
    console.log('初始化路由');
    await dynamicRouter()
    
    // 检查是否是根路径访问，重定向到首页
    if (to.path === '/') {
      next({ path: HOME_PATH, replace: true })
      return
    }
    
    next({path:to.path,replace:true})
    return
  }
  
  if (to.path === '/') {
    next({ path: HOME_PATH, replace: true })
    return
  }
  
  if (to.matched.length === 0) {
    if (to.path !== '/404') {
      next({ path: '/404' });
    } else {
      next();
    }
    return;
  }
  next()
}

// 处理未认证用户
const handleUnauthenticatedUser = (to, from, next) => {
  if (WHITE_LIST.includes(to.path)) {
    next()
  } else {
    next(`${LOGIN_PATH}?redirect=${to.path}`)
  }
}

// 检查权限
const checkPermission = (to, from, next, store) => {
  const userRoles = store.getters.roles
  const routeRoles = to.meta?.roles

  if (hasPermission(userRoles, routeRoles)) {
    next()
  } else {
    next({ path: FORBIDDEN_PATH, replace: true })
  }
}

// 错误处理
const handleError = (error, next, store) => {
  // removeToken()
  // store.dispatch('user/resetToken')
  // next('/login')
}

const logAccess = (location) => {
  console.log('页面访问:', {
    path: location.pathname,
    timestamp: new Date().toISOString(),
  })
}

// 简化的守卫设置函数
export const setupRouterGuards = (router, store) => {
  router.beforeEach(beforeEachGuard(store))
  router.afterEach(afterEachGuard)
  router.beforeResolve(beforeResolveGuard)
}

// 白名单管理
export const addToWhiteList = (routes) => {
  if (Array.isArray(routes)) {
    WHITE_LIST.push(...routes)
  } else {
    WHITE_LIST.push(routes)
  }
}

export const removeFromWhiteList = (route) => {
  const index = WHITE_LIST.indexOf(route)
  if (index > -1) {
    WHITE_LIST.splice(index, 1)
  }
}

export default router
