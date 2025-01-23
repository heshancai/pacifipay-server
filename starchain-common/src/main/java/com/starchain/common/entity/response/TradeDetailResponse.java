package com.starchain.common.entity.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author
 * @date 2025-01-04
 * @Description 查询商户交易明细 响应实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "TradeDetail", description = "交易明细实体信息")
public class TradeDetailResponse {

    @ApiModelProperty(value = "交易金额", example = "100.00")
    private BigDecimal amount;

    @ApiModelProperty(value = "可用余额", example = "500.00")
    private BigDecimal balance;

    @ApiModelProperty(value = "交易时间", example = "2023-10-01 12:34:56")
    private String billsTime;

    @ApiModelProperty(value = "账单类型", example = "SUB")
    private String billsType;

    @ApiModelProperty(value = "币种", example = "USD")
    private String currencyCode;

    @ApiModelProperty(value = "订单id", example = "123456789")
    private String orderId;

    @ApiModelProperty(value = "订单类型", example = "PAYMENT")
    private String orderType;

    @ApiModelProperty(value = "商户账户", example = "merchant_account")
    private String userAccount;

    @ApiModelProperty(value = "商户id", example = "987654321")
    private String userId;

    @ApiModelProperty(value = "商户名", example = "Merchant Name")
    private String userName;
}
