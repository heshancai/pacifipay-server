package com.starchain.util;

import com.starchain.entity.RemitCard;
import io.swagger.models.auth.In;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author
 * @date 2025-01-07
 * @Description
 */
public class OrderIdGenerator {
    /**
     * 生成订单号
     *
     * @param
     * @return 生成的订单号
     */
    public static String generateOrderId(String userId, String channelId, Integer length) {
        // 获取当前时间并格式化为 yyyyMMddHHmm
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));

        // 生成 8 位 UUID
        String uuid = UUIDUtil.generate8CharUUID(length);

        // 拼接订单号
        return channelId + userId + currentTime + uuid;
    }

    public static void main(String[] args) {


        // 生成订单号
        String orderId = generateOrderId("1000000","100000",6);
        System.out.println("生成的订单号: " + orderId);
    }
}
