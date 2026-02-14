import request from "@/utils/request"

// 登录方法
export async function login(data) {
    return await request.post('/auth/login', data)
}

// 退出登录
export async  function logout() {
    return await request.post('/auth/logout')
}

// 获取用户信息
export async function getUserInfo() {
    return await request.get('/auth/getUserInfo')
}   

// 获取验证码
export async function getCaptcha() {
    return await request.get('/auth/captcha')
}

// 重置密码
export async function resetPassword(data) {
    return await request.post('/auth/reset-password', data)
}

// 获取用户菜单
export async function getRoutes() {
    return await request.get('/auth/getRouters')
}