package com.clm.common.core.utils;

import cn.dev33.satoken.secure.BCrypt;
import org.springframework.util.DigestUtils;

/**
 * 密码工具类
 *
 * @author 陈黎明
 * @since 2025-03-08
 */
public class PasswordUtils {

    /**
     * BCrypt加密
     *
     * @param password 明文密码
     * @return 加密后的密码
     */
    public static String encryptPassword(String password) {
        return BCrypt.hashpw(password);
    }

    /**
     * 校验密码
     *
     * @param rawPassword     明文密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }

    /**
     * MD5加密（不推荐用于密码加密，仅用于内部数据校验）
     *
     * @param str 原始字符串
     * @return MD5加密后的字符串
     */
    public static String md5(String str) {
        return DigestUtils.md5DigestAsHex(str.getBytes());
    }
} 