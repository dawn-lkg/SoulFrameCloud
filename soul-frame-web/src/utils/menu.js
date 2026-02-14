import {useRouter} from 'vue-router'
import empty from '@/components/common/empty.vue'
import IFrame from '@/layout/iframe/index.vue'
import {IFRAME_PATH} from '@/config'

const modules = import.meta.glob('@/views/**/*.vue')

const router = useRouter()

// 获取权限
export const getPermissions = (data) => {
  return data.filter(item => item.menuType === 'F').map(item => item.perms)
}

// 获取左侧菜单
export const getShowMenus = (data) => {
  const menus = data.filter(item => item.menuType !== 'F');
  menus.forEach(item => {
    item.path = generatePath(item)
  })
  return sortMenuTree(buildTree(menus));
}

// 转成路由
export const convertMenuTreeToRoutes = (data) => {
  if (!data || data.length === 0) return [];

  const routes = []

  data.filter(item => item.menuType !== 'F').forEach(item => {
    if (item.menuType === 'M' && item.children) {
      routes.push(...convertMenuTreeToRoutes(item.children));
      return;
    }
    // 创建基本路由对象
    const route = {
      path: generatePath(item),
      name: generateRouteName(item),
      component: getComponent(item),
      meta: {
        title: item.menuName,
        icon: item.icon,
          iframeUrl: generateIframeUrl(item),
          keepAlive: item.isCache === 1 // 添加缓存标识，根据后端返回的 isCache 字段判断
      }
    }

    if (item.children && item.children.length > 0) {
      const childRoutes = convertMenuTreeToRoutes(item.children)
      if (childRoutes.length > 0) {
        route.children = childRoutes
      }
    }

    routes.push(route)
  })
  return routes
}

// 生成路径
const generatePath = (item) => {
  if (!item.path) return ''
  if (item.isFrame == 2) {
    return IFRAME_PATH+item.menuId
  }
  return item.path;
}

// 生成内链链接
const generateIframeUrl = (item) => {
  if (!item.path || !item.path.startsWith('http')) return '';

  let url = item.path

  // 如果url中存在?，则将query参数添加到url中
  if (!url.includes('?')) {
    url += '?'
  }

  if (item.query) {
    const query = JSON.parse(item.query)
    Object.keys(query).forEach(key => {
      url += `&${key}=${query[key]}`
    })
  }

  return url
}

// 生成路由名称
const generateRouteName = (item) => {
  return item.menuName + "-" + item.menuId
}

// 获取组件
const getComponent = (item) => {
  if (item.component) {
    const component = modules[`/src/views${item.component}/index.vue`]
    if (component) {
      return markRaw(component)
    } else {
      return markRaw(empty)
    }
  } else if (item.isFrame == 2) {
    return markRaw(IFrame)
  }
  return markRaw(empty)
}

//转成树形结构
export const toTree = (data, id = 'id', parentId = 'parentId', children = 'children') => {
  const tree = []
  const map = {}
  data.forEach(item => {
    map[item[id]] = item
  })
  data.forEach(item => {
    if (item[parentId] === 0) {
      tree.push(item)
    } else {
      if (map[item[parentId]]) {
        map[item[parentId]].children = map[item[parentId]].children || []
        map[item[parentId]].children.push(item)
      } else {
        tree.push(item)
      }
    }
  })
  return tree
}

//转成树(不会更改原对象)
export const buildTree = (value) => {
  const data = JSON.parse(JSON.stringify(value));
  const tree = []
  const map = {}
  data.forEach(item => {
    map[item.menuId] = item
  })
  data.forEach(item => {
    if (item.parentId === 0) {
      tree.push(item)
    } else {
      if (map[item.parentId]) {
        map[item.parentId].children = map[item.parentId].children || []
        map[item.parentId].children.push(item)
      } else {
        tree.push(item)
      }
    }
  })
  return tree;
}

// 树形结构排序
export const sortMenuTree = (menuItems) => {
  if (!menuItems || menuItems.length === 0) return [];

  const sortedItems = [...menuItems].sort((a, b) => a.orderNum - b.orderNum);

  return sortedItems.map(item => {
    if (item.children && item.children.length > 0) {
      return {
        ...item,
        children: sortMenuTree(item.children)
      };
    }
    return item;
  });
};

