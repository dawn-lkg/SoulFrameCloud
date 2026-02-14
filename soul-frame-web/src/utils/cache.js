import { ref } from 'vue';

// 用于存储需要额外缓存的路由名称
const extraCachedRoutes = ref([]);

/**
 * 添加路由到缓存列表
 * @param {string|string[]} routeNames - 路由名称或路由名称数组
 */
export function addRouteToCache(routeNames) {
  if (Array.isArray(routeNames)) {
    routeNames.forEach(name => {
      if (!extraCachedRoutes.value.includes(name)) {
        extraCachedRoutes.value.push(name);
      }
    });
  } else if (typeof routeNames === 'string' && !extraCachedRoutes.value.includes(routeNames)) {
    extraCachedRoutes.value.push(routeNames);
  }
}

/**
 * 从缓存列表中移除路由
 * @param {string|string[]} routeNames - 路由名称或路由名称数组
 */
export function removeRouteFromCache(routeNames) {
  if (Array.isArray(routeNames)) {
    extraCachedRoutes.value = extraCachedRoutes.value.filter(
      name => !routeNames.includes(name)
    );
  } else if (typeof routeNames === 'string') {
    extraCachedRoutes.value = extraCachedRoutes.value.filter(
      name => name !== routeNames
    );
  }
}

/**
 * 清空缓存列表
 */
export function clearRouteCache() {
  extraCachedRoutes.value = [];
}

/**
 * 获取额外缓存的路由列表
 * @returns {string[]} - 额外缓存的路由名称数组
 */
export function getExtraCachedRoutes() {
  return extraCachedRoutes.value;
} 