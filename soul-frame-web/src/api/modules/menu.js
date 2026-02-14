import request from '@/utils/request'

// 获取菜单列表(树形结构)
export async function getMenuTreeList(params) {
  return request.get('/system/menu/tree', { params })
}

// 获取菜单列表（扁平结构）
export async function getList(params) {
    return request.get('/system/menu/list', { params })
}

// 新增菜单
export async function addMenu(data) {
    return request.post('/system/menu', data)
}

// 更新菜单
export async function updateMenu(data) {
    return request.put(`/system/menu`, data)
}

// 删除菜单
export async function removeMenu(id) {
    return request.delete(`/system/menu/${id}`)
}

// 批量删除菜单
export async function batchDeleteMenu(ids) {
    return request.batchDel(`/system/menu/batch`, ids)
}

//根据 角色id获取菜单列表
export async function getRoleMenuTreeSelect(roleId) {
    return request.get(`/system/menu/role-menu-tree-select/${roleId}`)
}

// 获取菜单树选择项
export async function getMenuTreeSelect() {
    return request.get('/system/menu/menu-tree-select')
}







