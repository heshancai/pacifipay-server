package com.starchain.common.entity.dto;

/**
 * @author
 * @date 2024-12-20
 * @Description
 */

import lombok.Data;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class CardDetailsDto {

    // 必填字段
    private String cardCode; // 卡类型编码
    private String saveOrderId; // 卡预存订单号
    private BigDecimal saveAmount; // 卡预存额度

    // 太平洋的持卡人唯一值，后续使用此值进行交互
    private String tpyshCardHolderId;

    // 响应体字段
    private String merchantId; // 客户ID
    private String physicsType; // 物理类型
    private String kycType; // 实名类型
    private String bankType; // 发卡行类型
    private String cardId; // 卡ID
    private String cardNo; // 卡号
    private String cardCvn; // 卡CVV2
    private String cardExpDate; // 卡有效期
    private BigDecimal singleLimit; // 消费单笔限额
    private String createTime; // 修改为 String 类型
    private BigDecimal cardFee; // 开卡手续费
    private String saveTradeId; // 预存款流水号
    private String cardStatus; // 卡状态
    // 其他 getter 和 setter 方法...


    public void setCreateTime(Date createTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.createTime = sdf.format(createTime);
    }

}


