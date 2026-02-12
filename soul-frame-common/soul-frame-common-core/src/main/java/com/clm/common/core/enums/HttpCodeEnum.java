package com.clm.common.core.enums;

/**
 * 返回状态码枚举
 * 
 * @author admin
 */
public enum HttpCodeEnum {
    
    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),
    
    /**
     * 对象创建成功
     */
    CREATED(201, "对象创建成功"),
    
    /**
     * 请求已经被接受
     */
    ACCEPTED(202, "请求已经被接受"),
    
    /**
     * 操作已经执行成功，但是没有返回数据
     */
    NO_CONTENT(204, "操作已经执行成功，但是没有返回数据"),
    
    /**
     * 资源已被移除
     */
    MOVED_PERM(301, "资源已被移除"),
    
    /**
     * 重定向
     */
    SEE_OTHER(303, "重定向"),
    
    /**
     * 资源没有被修改
     */
    NOT_MODIFIED(304, "资源没有被修改"),
    /**
     * 参数列表错误（缺少，格式不匹配）
     */
    BAD_REQUEST(400, "参数列表错误（缺少，格式不匹配）"),
    /**
     * 新增失败
     */
    FAILED_ADD(422, "新增失败"),
    /**
     * 修改失败
     */
    FAILED_UPDATE(422, "修改失败"),
    /**
     * 删除失败
     */
    FAILED_DELETE(423, "删除失败"),
    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),
    
    /**
     * 访问受限，授权过期
     */
    FORBIDDEN(403, "访问受限，授权过期"),
    
    /**
     * 资源，服务未找到
     */
    NOT_FOUND(404, "资源，服务未找到"),
    
    /**
     * 不允许的http方法
     */
    BAD_METHOD(405, "不允许的http方法"),

    /**
     * 无效Token
     */
    INVALID_TOKEN(406, "无效Token"),
    
    /**
     * 资源冲突，或者资源被锁
     */
    CONFLICT(409, "资源冲突，或者资源被锁"),
    
    /**
     * 不支持的数据，媒体类型
     */
    UNSUPPORTED_TYPE(415, "不支持的数据，媒体类型"),

    /**
     * 密码错误
     */
    PASSWORD_ERROR(416, "密码错误"),

    /**
     * 数据不存在
     */
    DATA_NOT_EXIST(417, "数据不存在"),

    /**
     * 数据已存在
     */
    DATA_EXIST(418, "数据已存在"),

    /**
     * 账号已存在
     */
    ACCOUNT_EXIST(419, "账号已存在"),

    /**
     * 账号已被禁用
     */
    ACCOUNT_DISABLED(420, "账号已被禁用"),

    /**
     * 系统内部错误
     */
    ERROR(500, "系统内部错误"),
    
    /**
     * 接口未实现
     */
    NOT_IMPLEMENTED(501, "接口未实现"),

    /**
     * 系统警告消息
     */
    WARN(601, "系统警告消息"),
    /**
     * 验证码已过期
     */
    CAPTCHA_EXPIRED(420,"验证码已过期"),
    /**
     * 验证码错误
     */
    CAPTCHA_ERROR(421, "验证码错误"),

    /**
     * 部门名称已存在
     */
    DEPT_NAME_EXIST(430, "部门名称已存在"),

    /**
     * 父部门不存在
     */
    PARENT_DEPT_NOT_EXIST(431, "父部门不存在"),

    /**
     * 部门不存在
     */
    DEPT_NOT_EXIST(432, "部门不存在"),

    /**
     * 父部门不能是自己
     */
    PARENT_DEPT_CANNOT_BE_SELF(433, "父部门不能是自己"),

    /**
     * 父部门不能是自己的子部门
     */
    PARENT_DEPT_CANNOT_BE_CHILD(434, "父部门不能是自己的子部门"),

    /**
     * 存在子部门，不允许删除
     */
    DEPT_EXIST_CHILD(435, "存在子部门，不允许删除"),

    /**
     * 部门存在用户，不允许删除
     */
    DEPT_EXIST_USER(436, "部门存在用户，不允许删除");

    private final int code;
    private final String message;

    HttpCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
    
    /**
     * 根据code获取message
     * 
     * @param code 状态码
     * @return 返回信息
     */
    public static String getMessageByCode(int code) {
        for (HttpCodeEnum httpCodeEnum : HttpCodeEnum.values()) {
            if (httpCodeEnum.getCode() == code) {
                return httpCodeEnum.getMessage();
            }
        }
        return null;
    }
} 