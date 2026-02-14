import request from '@/utils/request'

// 获取部门列表(树形结构)
export async function getDeptList(params) {
  return request.get('/system/dept/list', { params })
}

// 获取部门详细信息
export async function getDeptInfo(deptId) {
  return request.get(`/system/dept/${deptId}`)
}

// 新增部门
export async function addDept(data) {
  return request.post('/system/dept', data)
}

// 更新部门
export async function updateDept(data) {
  return request.put('/system/dept', data)
}

// 删除部门
export async function removeDept(deptId) {
  return request.delete(`/system/dept/${deptId}`)
}

// 检查部门名称是否唯一
export async function checkDeptNameUnique(deptName, parentId, deptId) {
  return request.get('/system/dept/checkDeptNameUnique', { 
    params: {
      deptName,
      parentId,
      deptId
    }
  })
}

// 部门下拉选择
export async function deptSelect(params) {
  return request.get('/system/dept/select', { params })
}