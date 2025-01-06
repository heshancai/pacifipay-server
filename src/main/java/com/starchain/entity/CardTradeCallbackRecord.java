package com.starchain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-06
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("card_trade_callback_record")
@ApiModel(value = "CardTradeCallbackRecord", description = "卡流水通知记录实体")
public class CardTradeCallbackRecord {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "通知ID")
    private String notifyId;

    @ApiModelProperty(value = "卡类型")
    private String cardCode;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "卡ID")
    private String cardId;

    @ApiModelProperty(value = "交易时间")
    private LocalDateTime tradeTime;

    @ApiModelProperty(value = "交易类型")
    private String tradeType;

    @ApiModelProperty(value = "交易流水号")
    private String tradeId;

    @ApiModelProperty(value = "原交易流水号")
    private String originalTradeId;

    @ApiModelProperty(value = "余额")
    private BigDecimal balance;

    @ApiModelProperty(value = "商户名")
    private String merchantName;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "状态描述")
    private String statusDesc;

    @ApiModelProperty(value = "手续费")
    private BigDecimal fee;

    @ApiModelProperty(value = "交易金额")
    private BigDecimal trade;

    @ApiModelProperty(value = "交易币种")
    private String tradeCurrency;

    @ApiModelProperty(value = "网关费")
    private BigDecimal gatewayFee;

    @ApiModelProperty(value = "验证费")
    private BigDecimal verifyFee;

    @ApiModelProperty(value = "冲正费")
    private BigDecimal voidFee;

    @ApiModelProperty(value = "退款费")
    private BigDecimal refundFee;

    @ApiModelProperty(value = "授权失败费")
    private BigDecimal authFailFee;

    @ApiModelProperty(value = "授权成功费")
    private BigDecimal authSuccessFee;

    @ApiModelProperty(value = "授权小额费")
    private BigDecimal authLittleFee;

    @ApiModelProperty(value = "授权跨境费")
    private BigDecimal authBorderFee;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
