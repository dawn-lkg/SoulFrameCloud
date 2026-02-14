import request from "@/utils/request"

/**
 * 获取个人信息
 * @returns {Promise}
 */
export function getProfile() {
  return request.get('/system/user/profile')
}

/**
 * 更新个人信息
 * @param {Object} data - 用户信息
 * @returns {Promise}
 */
export function updateProfile(data) {
  return request.put('/system/user/profile', data)
}

/**
 * 更新用户头像
 * @param {FormData} data - 包含头像文件的表单数据
 * @returns {Promise}
 */
export function updateAvatar(data) {
  return request.upload('/system/user/profile/avatar', data)
}

/**
 * 修改用户密码
 * @param {Object} data - 包含旧密码和新密码
 * @returns {Promise}
 */
export function updatePassword(data) {
  return request.put('/system/user/profile/updatePwd', data)
}

/**
 * 获取用户登录日志
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getLoginLog(params) {
  return request.get('/system/loginLog/list', { params })
}

/**
 * 更新用户偏好设置
 * @param {Object} data - 偏好设置
 * @returns {Promise}
 */
export function updatePreferences(data) {
  return request.put('/system/user/profile/preferences', data)
} 