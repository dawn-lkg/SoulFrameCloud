import { defineStore } from 'pinia'
import { getUserInfo,login,logout,getRoutes } from '@/api/modules/auth'
import { buildTree,getPermissions,getShowMenus,convertMenuTreeToRoutes } from '@/utils/menu'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    // 用户角色列表 
    roles: [],
    // 用户信息
    userInfo: {},
    // 用户token
    token: '',
    // token名称
    tokenName: '',
    //是否初始化了权限路由
    isInitRoutes: false,
    //权限菜单
    menus: [],
    //权限按钮
    permissions: [],
    // 路由列表
    routes: [],
  }),

  actions: {
    //登录
    async login(form) {
      const data = await login(form)
      this.token = data.tokenValue
      this.tokenName = data.tokenName
    },
    // 退出登录
    async logout() {
        await logout()
        this.clearAuth()
    },
    //获取用户信息
    async getUserInfo() {
        const data = await getUserInfo()
        this.userInfo = data
        this.roles = data.roles
        return data
    },
    //是否登录
    isLogin() {
        return Boolean(this.token)
    },
    // 清空权限相关数据
    clearAuth() {
      //用户信息
      this.userInfo = {}
      //用户角色
      this.roles = []
      //token
      this.token = ''
      //token名称
      this.tokenName = ''
      //路由列表
      this.routes = []
      //是否初始化路由
      this.isInitRoutes = false
      //权限菜单
      this.menus = []
      //权限按钮
      this.permissions = [],
      // 左侧菜单展示列表
      this.showMenus = []
    },
    // 初始化路由
    async initRoutes() {
      const data = await getRoutes()
      this.menus = buildTree(data)
      this.routes = convertMenuTreeToRoutes(buildTree(data))
      this.permissions = getPermissions(data)
      this.showMenus = getShowMenus(data)
      this.isInitRoutes = true
      return data
    },

    // 判断是否有某个权限
    hasPermission(permission) {
      return this.permissions.includes(permission)
    },

    // 判断是否有某个角色
    hasRole(role) {
      return this.roles.includes(role)
    }
  },

  persist: {
    storage: localStorage,
    pick: ['token', 'tokenName', 'userInfo', 'roles'],
  },
})
