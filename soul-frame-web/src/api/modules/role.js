import request from '@/utils/request'

// 分页获取角色列表
export async function getRolePage(params) {
    return await request.get('/system/role/page', { params })
}

// 获取角色列表
export async function getRoleList() {
    return await request.get('/system/role/list')
}

//获取角色详情
export async function getRoleDetail(id) {
    return await request.get(`/system/role/${id}`)
}

// 新增角色
export async function addRole(data) {
    return await request.post('/system/role', data)
}

// 更新角色
export async function updateRole(data) {
    return await request.put(`/system/role`, data)
}

// 删除角色
export async function deleteRole(id) {
    return await request.delete(`/system/role/${id}`)
}

// 批量删除角色
export async function batchDeleteRole(ids) {
    return await request.batchDel(`/system/role/batch`, ids)
}

// 分配角色菜单权限
export async function assignMenuPermissions(roleId, permissionIds) {
    return await request.put(`/system/role/${roleId}/permission`, permissionIds)
  }


// 查询角色菜单id列表
export async function getRoleMenuList(roleId) {
    return await request.get(`/system/role/${roleId}/menu`)
}





