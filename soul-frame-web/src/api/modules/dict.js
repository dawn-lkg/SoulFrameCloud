import request from '@/utils/request'

// 获取字典类型分页列表
export function getDictTypePage(params) {
    return request.get('/system/dict/type/page', {params})
}

// 获取字典类型列表
export function getDictTypeList(params) {
    return request.get('/system/dict/type/list', {params})
}

// 获取字典类型详情
export function getDictType(dictId) {
    return request.get(`/system/dict/type/${dictId}`)
}

// 新增字典类型
export function addDictType(data) {
    return request.post('/system/dict/type', data)
}

// 修改字典类型
export function updateDictType(data) {
    return request.put('/system/dict/type', data)
}

// 删除字典类型
export function deleteDictType(dictId) {
    return request.delete(`/system/dict/type/${dictId}`)
}

// 批量删除字典类型
export function batchDeleteDictType(dictIds) {
    return request.batchDel('/system/dict/type/batch', dictIds)
}

// 获取字典数据分页列表
export function getDictDataPage(params) {
    return request.get('/system/dict/data/page', {params})
}

// 根据字典类型获取字典数据列表
export function getDictDataList(dictType) {
    return request.get(`/system/dict/data/type/${dictType}`)
}

// 获取字典数据详情
export function getDictData(dictCode) {
    return request.get(`/system/dict/data/${dictCode}`)
}

// 新增字典数据
export function addDictData(data) {
    return request.post('/system/dict/data', data)
}

// 修改字典数据
export function updateDictData(data) {
    return request.put('/system/dict/data', data)
}

// 删除字典数据
export function deleteDictData(dictCode) {
    return request.delete(`/system/dict/data/${dictCode}`)
}

// 刷新字典缓存
export function refreshDictCache() {
    return request.delete('/system/dict/type/refreshCache')
} 