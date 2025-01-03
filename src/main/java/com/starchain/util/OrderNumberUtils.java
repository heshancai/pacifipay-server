package com.starchain.util;

/**
 * @author
 * @date 2025-01-03
 * @Description
 */
public class OrderNumberUtils {
    /**
     * 生成订单号
     */
    public static String getOrderId(String accountName) {
        // ${账号名}_${时间戳}_${随机字符串}
        return accountName + "_" + System.currentTimeMillis() + "_" + VerifyCodeUtils.generateVerifyCode(6);

    }
}
