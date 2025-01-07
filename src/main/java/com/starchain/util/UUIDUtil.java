package com.starchain.util;

import java.util.UUID;

/**
 * @author
 * @date 2025-01-03
 * @Description
 */
public class UUIDUtil {

    /**
     * 生成 8 位随机 UUID
     *
     * @return 8 位随机字符串
     */
    public static String generate8CharUUID(Integer length) {
        // 生成标准的 UUID
        String uuid = UUID.randomUUID().toString();
        // 去掉 UUID 中的 "-"，并截取前 8 位
        return uuid.replaceAll("-", "").substring(0, length);
    }
}
