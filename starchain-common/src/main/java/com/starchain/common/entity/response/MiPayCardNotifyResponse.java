package com.starchain.common.entity.response;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-03
 * @Description
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class MiPayCardNotifyResponse extends BaseMiPayNotifyResponse {
    @Schema(description = "卡类型", example = "卡类型 api 特有字段")
    private String cardCode;
    @Schema(description = "卡号", example = "1234567890123456")
    private String cardNo;
    @Schema(description = "卡安全码", example = "123")
    private String cardCvn;
    @Schema(description = "卡有效期", example = "12/25")
    private String cardExpDate;
    @Schema(description = "商户订单ID", example = "卡提现、卡预存拥有字段")
    private String mchOrderId;
    @Schema(description = "交易时间", example = "卡流水、卡预存拥有字段")
    private LocalDateTime tradeTime;
    @Schema(description = "交易类型", example = "卡流水 枚举值，范围Purchase：授权已结算，Refund：退款，PrePurchase：授权未结算，VoidPurchase：授权撤销")
    private String tradeType;
    @Schema(description = "原单号 原单交易流水号", example = "卡流水 特有字段")
    private String originalTradeId;
    @Schema(description = "卡余额", example = "卡流水 特有字段")
    private BigDecimal balance;
    @Schema(description = "商户名 上游定义的商户名", example = "卡流水 特有字段")
    private String merchantName;
    @Schema(description = "验证码 通常3分钟有效", example = "卡验证码特有字段")
    private String verifyCode;
    @Schema(description = "金额信息列表", example = "[{\"actual\": 10.50}]")
    private JSONObject amount;
}
