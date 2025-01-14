package com.starchain.entity.dto;

/**
 * @author
 * @date 2024-12-19
 * @Description
 */

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;

@Slf4j
@Data
public class CardDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cardCode;
    private String saveOrderId;
    @ApiModelProperty(value = "用户Id", example = "CARD123456")
    private BigDecimal saveAmount;
    @ApiModelProperty(value = "充值金额", example = "小数点后最多2位")
    private BigDecimal orderAmount;
    @ApiModelProperty(value = "持卡人Id", example = "")
    private String tpyshCardHolderId;
    @ApiModelProperty(value = "卡ID-银行卡端返回", example = "CARD123456")
    private String cardId;
    @ApiModelProperty(value = "用户Id", example = "CARD123456")
    private Long userId;
    @ApiModelProperty(value = "渠道Id", example = "CARD123456")
    private Long channelId;
    @ApiModelProperty(value = "客户卡充值交易唯一标识", example = "CARD123456")
    private String orderId;
    @ApiModelProperty(value = "消费单笔限额-银行卡端返回", example = "消费单笔限额，可修改")
    private BigDecimal singleLimit;
}
