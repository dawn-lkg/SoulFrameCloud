import request from '@/utils/request'

// 获取Bean列表
export const getBeanList = (params) => {
  return request.get('/system/bean/list', { params })
}



