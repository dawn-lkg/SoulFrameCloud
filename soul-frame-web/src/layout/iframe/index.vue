<template>
    <div class="iframe-container" :class="{ 'standalone': isStandalone }">
      <!-- 独立模式下显示的头部 -->
      <div class="iframe-header" v-if="isStandalone">
        <div class="iframe-title">{{ route.meta?.title || '内链页面' }}</div>
        <div class="iframe-actions">
          <a @click="handleBack" title="返回"><left-outlined /></a>
          <a @click="handleRefresh" title="刷新"><reload-outlined /></a>
        </div>
      </div>
  
      <!-- 加载状态 -->
      <div v-if="loading && iframeUrl" class="iframe-loading">
        <a-spin tip="加载中..."></a-spin>
      </div>
  
      <!-- URL为空时显示错误提示 -->
      <div v-if="!iframeUrl" class="iframe-error">
        <a-result status="warning" title="无法加载页面" sub-title="页面地址为空，请检查菜单配置">
          <template #extra>
            <a-button type="primary" @click="handleBack">
              返回上一页
            </a-button>
          </template>
        </a-result>
      </div>
  
      <!-- 只有在URL不为空时才显示iframe -->
      <iframe v-if="iframeUrl" :src="iframeUrl" class="iframe-content" frameborder="0" scrolling="auto"
        @load="handleIframeLoad" ref="iframeRef" style="min-height:600px;"></iframe>
    </div>
  </template>
  
  <script setup>
  import {computed, onBeforeUnmount, onMounted, ref, watch} from 'vue'
  import {useRoute, useRouter} from 'vue-router'
  import {LeftOutlined, ReloadOutlined} from '@ant-design/icons-vue'

  const route = useRoute()
  const router = useRouter()
  const loading = ref(true)
  const iframeRef = ref(null)
  
  const isStandalone = computed(() => {
    return !route.meta?.parentView
  })
  
  // 获取iframe的URL
  const iframeUrl = computed(() => {
    // 尝试多种方式获取URL
    let url = route.meta?.iframeUrl // 首先从meta中获取
      || route.query?.url           // 其次从查询参数获取
      || '';
  
    // 确保URL是有效的
    if (url && !url.startsWith('http') && !url.startsWith('https')) {
      url = `http://${url}`; // 添加http前缀
    }
  
    if (!url) {
      console.warn('警告: iframe URL为空，路由信息:', route.path, JSON.stringify(route.meta))
    } else {
      console.log('使用iframe URL:', url);
    }
  
    return url;
  })
  
  // 处理iframe加载完成
  const handleIframeLoad = () => {
    console.log('Iframe加载完成')
    // 给一个短暂延迟，确保加载完成
    setTimeout(() => {
      loading.value = false
      // 加载完成后再次设置高度，确保正确
      setIframeHeight()
    }, 200)
  }
  
  // 返回上一页
  const handleBack = () => {
    router.go(-1)
  }
  
  // 刷新iframe内容
  const handleRefresh = () => {
    if (!iframeRef.value || !iframeUrl.value) {
      return
    }
  
    loading.value = true
    const iframe = iframeRef.value
    try {
      console.log('刷新iframe:', iframeUrl.value)
  
      iframe.src = iframeUrl.value
  
      // 设置超时保护
      const loadingTimeout = setTimeout(() => {
        loading.value = false
        console.warn('刷新iframe超时，强制隐藏loading')
      }, 5000)
  
      // 一次性监听加载完成事件
      const handleLoad = () => {
        clearTimeout(loadingTimeout)
        setTimeout(() => {
          loading.value = false
          setIframeHeight()
        }, 200)
        iframe.removeEventListener('load', handleLoad)
      }
      iframe.addEventListener('load', handleLoad)
    } catch (error) {
      console.error('刷新iframe出错:', error)
      loading.value = false
  
      // 确保URL已设置
      if (iframeUrl.value) {
        iframe.src = iframeUrl.value
      }
    }
  }
  
  // 监听URL变化，防止路由更新后iframe不刷新
  watch(
    () => route.meta?.iframeUrl,
    (newUrl, oldUrl) => {
      if (!newUrl) {
        console.warn('路由更新但URL为空')
        return
      }
  
      if (newUrl !== oldUrl) {
        console.log('Iframe URL变更:', newUrl)
        loading.value = true
  
        // 直接设置新URL，不使用about:blank中转
        if (iframeRef.value) {
          const iframe = iframeRef.value
          try {
            console.log('设置新iframe URL:', newUrl)
            iframe.src = newUrl
  
            // 设置超时保护
            const loadingTimeout = setTimeout(() => {
              loading.value = false
              console.warn('更新iframe URL超时，强制隐藏loading')
            }, 5000)
  
            // 一次性监听加载完成事件
            const handleLoad = () => {
              clearTimeout(loadingTimeout)
              setTimeout(() => {
                loading.value = false
                setIframeHeight()
              }, 200)
              iframe.removeEventListener('load', handleLoad)
            }
            iframe.addEventListener('load', handleLoad)
          } catch (error) {
            console.error('更新iframe URL出错:', error)
            loading.value = false
          }
        }
      }
    },
    { immediate: true } // 立即执行一次
  )
  
  // 设置iframe高度的函数
  const setIframeHeight = () => {
    if (!iframeRef.value) return;
    
    // 获取视口高度
    const viewportHeight = window.innerHeight;
    // 获取父容器高度
    const parentHeight = iframeRef.value.parentElement?.offsetHeight || 0;
    // 计算header高度（如果有）
    const headerHeight = isStandalone.value ? 40 : 0;
    
    // 计算可用高度，取视口高度和父容器高度的较大值，确保至少有600px
    const availableHeight = Math.max(viewportHeight - headerHeight, parentHeight, 600);
    
    console.log('设置iframe高度:', availableHeight, 'px');
    iframeRef.value.style.height = `${availableHeight}px`;
  };
  
  onMounted(() => {
    // 初始化加载状态
    loading.value = true
  
    // 确保iframe的src被正确设置
    if (iframeRef.value && iframeUrl.value) {
      const iframe = iframeRef.value
      if (iframe.src !== iframeUrl.value) {
        iframe.src = iframeUrl.value
      }
    }
  
    // 设置加载超时保护
    const initialLoadTimeout = setTimeout(() => {
      if (loading.value) {
        loading.value = false
        console.warn('初始化iframe加载超时，强制隐藏loading')
  
        // 再次确保URL已设置
        if (iframeRef.value && iframeUrl.value && !iframeRef.value.src) {
          console.log('超时后重新设置iframe URL:', iframeUrl.value)
          iframeRef.value.src = iframeUrl.value
        }
      }
    }, 5000) // 5秒后强制隐藏loading
  
    // 确保组件销毁时清除超时
    onBeforeUnmount(() => {
      clearTimeout(initialLoadTimeout)
    })
    
    // 初始设置高度
    setIframeHeight();
    
    // 监听窗口大小变化
    window.addEventListener('resize', setIframeHeight);
    
    // 组件卸载时移除监听
    onBeforeUnmount(() => {
      window.removeEventListener('resize', setIframeHeight);
    });
    
    // 额外添加一个延迟设置，确保在DOM完全渲染后设置高度
    setTimeout(() => {
      setIframeHeight();
    }, 500);
  })
  </script>
  
  <style scoped>
  .iframe-container {
    display: flex;
    flex-direction: column;
    height: 100%;
    width: 100%;
    overflow: hidden;
    position: relative;
    background-color: #fff;
    border: 0;
    padding: 0;
    margin: 0;
    min-height: 100vh;
  }
  
  .iframe-container:not(.standalone) {
    height: 100vh;
  }
  
  .iframe-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 16px;
    background-color: #f5f5f5;
    border-bottom: 1px solid #e8e8e8;
    height: 40px;
    flex-shrink: 0;
    /* 防止头部被压缩 */
  }
  
  .iframe-title {
    font-size: 16px;
    font-weight: 500;
    color: rgba(0, 0, 0, 0.85);
  }
  
  .iframe-actions {
    display: flex;
    gap: 12px;
  }
  
  .iframe-actions a {
    cursor: pointer;
    color: rgba(0, 0, 0, 0.65);
  }
  
  .iframe-actions a:hover {
    color: #1890ff;
  }
  
  .iframe-content {
    flex: 1;
    width: 100%;
    border: 0;
    margin: 0;
    padding: 0;
    height: 100%; 
    min-height: 600px; /* 确保最小高度 */
  }
  
  .iframe-loading {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(255, 255, 255, 0.7);
    z-index: 100;
  }
  
  /* 独立模式下，loading的位置需要调整 */
  .standalone .iframe-loading {
    top: 40px;
    /* 头部高度 */
  }
  
  .iframe-error {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;
    background-color: #fff;
    min-height: 600px;
  }
  </style>