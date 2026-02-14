export const useAppStore = defineStore('app', {
  state: () =>
    reactive({
      siderCollapse: false,
      settingDrawerVisible: false,
      siderWidth: 200,
      collapsedWidth: 80,
        //隐藏菜单
        hideMenu: false,
      //组件尺寸
      componentSize: 'default', // default | middle | small
    }),
  actions: {
    // 打开设置抽屉
    openSettingDrawer() {
      this.settingDrawerVisible = true
    },
    // 关闭设置抽屉
    closeSettingDrawer() {
      this.settingDrawerVisible = false
    },
    // 切换设置抽屉
    toggleSettingDrawer() {
      this.settingDrawerVisible = !this.settingDrawerVisible
    },
    // 切换侧边栏
    toggleSiderCollapse() {
      this.siderCollapse = !this.siderCollapse
    },
    // 设置侧边栏宽度
    setSiderWidth(width) {
      this.siderWidth = width
    },
    // 设置折叠宽度
    setCollapsedWidth(width) {
      this.collapsedWidth = width
    },
  },
  persist: {
    key: 'app',
    storage: localStorage,
  },
})
