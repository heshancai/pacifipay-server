package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-17
 * @Description
 */
@Data
@Accessors(chain = true)
@TableName("merchant_wallet")
@ApiModel(value = "MerchantWallet", description = "商户钱包表")
public class MerchantWallet {
    @ApiModelProperty(value = "主键ID", example = "1")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "appid", example = "app123")
    private String appId;

    @ApiModelProperty(value = "商户ID", example = "merchant123")
    private String merchantId;

    @ApiModelProperty(value = "余额", example = "1000.00")
    private BigDecimal amount;
    @ApiModelProperty(value = "冻结金额", example = "1000.00")
    private BigDecimal freeze;
    @ApiModelProperty(value = "币种", example = "CNY")
    private String moneyKind;

    @ApiModelProperty(value = "更新时间", example = "2023-10-01 12:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "创建时间", example = "2023-10-01 12:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
