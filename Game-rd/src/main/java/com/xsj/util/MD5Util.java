package com.xsj.util;

import cn.hutool.crypto.digest.DigestUtil;

public class MD5Util {

    private static final String SALT = "GameSphere_Salt_2026";

    public static String encrypt(String password) {
        return DigestUtil.md5Hex(password + SALT);
    }

    public static String encrypt(String password, String salt) {
        return DigestUtil.md5Hex(password + salt);
    }

    public static boolean verify(String password, String encryptedPassword) {
        return encrypt(password).equals(encryptedPassword);
    }

    public static boolean verify(String password, String salt, String encryptedPassword) {
        return encrypt(password, salt).equals(encryptedPassword);
    }
}
