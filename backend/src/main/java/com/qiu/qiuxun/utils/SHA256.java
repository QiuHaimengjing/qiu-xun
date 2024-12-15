package com.qiu.qiuxun.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * @description: SHA-256加密
 * @className: SHA256.java
 * @author: qiu
 * @createTime: 2023/12/9 12:39
 */
@Data
@AllArgsConstructor
public class SHA256 {

    // 加密盐
    private String salt;

    // SHA-256密码
    private String sha256Password;

    // 队伍密码加密盐
    private static final String teamSalt = "KianaK423QiuXun";

    /**
     * @description: 加密
     * @params: [originalPassword]
     * @return: com.qiu.qiuxun.utils.SHA256
     * @author: qiu
     * @dateTime: 2023/12/9 13:01
     */
    public static SHA256 encryptionPassword(String originalPassword) {
        // 1.加盐，生成随机盐
        int saltLength = 16;
        byte[] bytes = new byte[saltLength];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(bytes);
        String salt = Base64.getEncoder().encodeToString(bytes);
        // 2.盐和密码合并
        String saltedPassword = StrUtil.format("{}{}", salt, originalPassword);
        String finalPassword = SecureUtil.sha256(saltedPassword);
        return new SHA256(salt, finalPassword);
    }

    /**
     * @description: 解密
     * @params: [salt, originalPassword]
     * @return: java.lang.String
     * @author: qiu
     * @dateTime: 2023/12/9 13:08
     */
    public static String decryptPassword(String salt, String originalPassword) {
        // 用户输入密码与盐合并
        String saltInputPassword = StrUtil.format("{}{}", salt, originalPassword);
        // 使用相同哈希函数对合并后的字符串进行哈希处理
        return SecureUtil.sha256(saltInputPassword);
    }

    /**
     * @description: 队伍密码加密
     * @params: [originalPassword]
     * @return: java.lang.String
     * @author: qiu
     * @dateTime: 2024/3/1 20:31
     */
    public static String encryptionPasswordForTeam(String originalPassword) {
        String saltedPassword = StrUtil.format("{}{}", teamSalt, originalPassword);
        return SecureUtil.sha256(saltedPassword);
    }

    /**
     * @description: 队伍密码解密
     * @params: [originalPassword]
     * @return: java.lang.String
     * @author: qiu
     * @dateTime: 2024/3/1 20:19
     */
    public static String decryptPasswordForTeam(String originalPassword) {
        return decryptPassword(teamSalt, originalPassword);
    }
}
