
//全局默认配置项

export const HOME_PATH = '/home'
export const LOGIN_PATH = '/login'
export const IFRAME_PATH = '/iframe'
export const NOT_FOUND_PATH = '/404'
export const FORBIDDEN_PATH = '/403'
export const UNAUTHORIZED_PATH = '/401'
export const SERVER_ERROR_PATH = '/500'

//路由白名单
export const WHITE_LIST = [LOGIN_PATH, NOT_FOUND_PATH, FORBIDDEN_PATH, UNAUTHORIZED_PATH, SERVER_ERROR_PATH, '/file-receive', '/file-receive/:id']


// 数据操作类型常量
export const OPERATE_TYPE = {
    QUERY: 0, // 查询
    INSERT: 1, // 新增
    UPDATE: 2, // 修改
    DELETE: 3, // 删除
    EXPORT: 4, //导出
    IMPORT: 5, //导入
    CLEAN: 6, //清空数据
    OTHER: 99, //其他
}


//数据操作状态
export const OPERATE_STATUS = {
    SUCCESS: 0, // 成功
    FAIL: 1, // 失败
}

// 文件状态
export const FILE_STATUS = {
    'active': {
        color: 'green',
        text: '正常'
    },
    'uploading': {
        color: 'blue',
        text: '上传中'
    },
    'deleted': {
        color: 'red',
        text: '已删除'
    },
    'archived': {
        color: 'orange',
        text: '已归档'
    }
}