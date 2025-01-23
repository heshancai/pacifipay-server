package com.starchain.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtils {

    /**
     * 对字符串进行 MD5 加签
     *
     * @param input 输入字符串
     * @return MD5 加签后的字符串
     */
    public static String getMD5Str(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    /**
     * 对字符串进行 MD5 加签，并指定字符编码
     *
     * @param input 输入字符串
     * @param charset 字符编码
     * @return MD5 加签后的字符串
     */
    public static String getMD5Str(String input, String charset) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes(charset));
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error during MD5 hashing", e);
        }
    }
}
