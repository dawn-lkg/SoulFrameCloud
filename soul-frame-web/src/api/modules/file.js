import request from "@/utils/request"


// 分页获取文件列表
export function getFilePage(params) {
    return request.get('/file/page', {params})
}

// 获取文件列表
export function getFileList(params) {
    return request.get('/file/list', {params})
}

// 上传文件
export async function uploadFile(formData, onProgress) {
    return request.upload('/file/upload', formData, {}, onProgress)
}

// 删除文件
export function deleteFile(fileId) {
    return request.delete(`/file/${fileId}`)
}

// 批量删除文件
export function batchDeleteFiles(fileIds) {
    return request.batchDel('/file/batch', fileIds)
}

// 获取文件url
export async function getFileUrl(id) {
    return request.get(`/file/url/${id}`)
}

// 创建文件夹
export function createFolder(data) {
    return request.post('/file/folder', data)
}

// 分享文件
export function shareFile(data) {
    return request.post('/file/share', data)
}
