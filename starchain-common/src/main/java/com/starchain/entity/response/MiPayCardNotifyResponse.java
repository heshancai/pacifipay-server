package com.starchain.entity.response;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.ApiModelProperty;
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
public class MiPayCardNotifyResponse extends BaseMiPayNotifyResponse{
    @ApiModelProperty(value = "卡类型", example = "卡类型 api 特有字段")
    private String cardCode;
    @ApiModelProperty(value = "卡号", example = "1234567890123456")
    private String cardNo;
    @ApiModelProperty(value = "卡安全码", example = "123")
    private String cardCvn;
    @ApiModelProperty(value = "卡有效期", example = "12/25")
    private String cardExpDate;
    @ApiModelProperty(value = "商户订单ID", example = "卡提现、卡预存拥有字段")
    private String mchOrderId;
    @ApiModelProperty(value = "交易时间", example = "卡流水、卡预存拥有字段")
    private LocalDateTime tradeTime;
    @ApiModelProperty(value = "交易类型", example = "卡流水 枚举值，范围Purchase：授权已结算，Refund：退款，PrePurchase：授权未结算，VoidPurchase：授权撤销")
    private String tradeType;
//    @ApiModelProperty(value = "交易流水号", example = "卡流水、申请汇款、汇款撤销 特有字段")
//    private String tradeId;
    @ApiModelProperty(value = "原单号 原单交易流水号", example = "卡流水 特有字段")
    private String originalTradeId;
    @ApiModelProperty(value = "卡余额",example = "卡流水 特有字段")
    private BigDecimal balance;
    @ApiModelProperty(value = "商户名 上游定义的商户名",example = "卡流水 特有字段")
    private String merchantName;
    @ApiModelProperty(value = "验证码 通常3分钟有效",example = "卡验证码特有字段")
    private String verifyCode;
    @ApiModelProperty(value = "金额信息列表", example = "[{\"actual\": 10.50}]")
    private JSONObject amount;
}
