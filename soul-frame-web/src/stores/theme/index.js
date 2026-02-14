export const useThemeStore = defineStore('theme', {
  state: () =>
    reactive({
      primaryColor: '#1890ff',
      isDarkMode: false,
      menuTheme: 'light',
      layout: 'side', // side | top | mix
      layoutConfig: {
        showBreadcrumb: true,
        fixedHeader: true,
        fixedSidebar: true,
        splitMenus: false,
        showLogo: true,
        showTagsView: true,
        showFooter: false,
        contentWidth: 'fluid', // fluid | fixed
      },
    }),
  actions: {
    // 切换主题色
    changePrimaryColor(color) {
      this.primaryColor = color
      // 这里可以添加动态修改主题色的逻辑
    },
    //切换主题模式
    toggleDarkMode() {
      this.isDarkMode = !this.isDarkMode
    },
    changeLayout(layout) {
      this.layout = layout
    },
    changeMenuTheme(theme) {
      this.menuTheme = theme
    },
    // 设置面包屑显示
    setShowBreadcrumb(showBreadcrumb) {
      this.layoutConfig.showBreadcrumb = showBreadcrumb
    },
    // 设置固定头部
    setFixedHeader(fixedHeader) {
      this.layoutConfig.fixedHeader = fixedHeader
    },
    // 设置固定侧边栏
    setFixedSidebar(fixedSidebar) {
      this.layoutConfig.fixedSidebar = fixedSidebar
    },
  },
  persist: {
    key: 'theme',
    storage: localStorage,
  },
})
