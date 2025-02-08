package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-02-07
 * @Description
 */
@ApiModel(value = "CardFeeRule", description = "卡费规则配置表")
@Data
@TableName("card_fee_rules")
public class CardFeeRule {

    @ApiModelProperty(value = "主键ID", example = "1")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "开卡费")
    private BigDecimal cardFee;

    @ApiModelProperty(value = "预存款")
    private BigDecimal saveAmount;

    @ApiModelProperty(value = "月服务费")
    private BigDecimal monthlyFee;

    @ApiModelProperty(value = "销卡费")
    private BigDecimal cancelFee;

    @ApiModelProperty(value = "充值手续费率")
    private BigDecimal rechargeFeeRate;

    @ApiModelProperty(value = "提现手续费率")
    private BigDecimal withdrawFeeRate;

    @ApiModelProperty(value = "汇款手续费率")
    private BigDecimal remitFeeRate;

    @ApiModelProperty(value = "汇款手续费")
    private BigDecimal remitFeeAmount;


    @ApiModelProperty(value = "卡类型编码", example = "TpyMDN6")
    private String cardCode;

    @ApiModelProperty(value = "每日充值次数限制")
    private Integer dailyRechargeLimit;

    @ApiModelProperty(value = "最低充值金额")
    private BigDecimal minRechargeAmount;

    @ApiModelProperty(value = "单笔充值限额")
    private BigDecimal maxSingleRecharge;

    @ApiModelProperty(value = "每日充值限额")
    private BigDecimal dailyRechargeMax;

    @ApiModelProperty(value = "卡最大余额")
    private BigDecimal cardBalanceMax;

    @ApiModelProperty(value = "销卡手续费")
    private BigDecimal handleFeeAmount;

    @ApiModelProperty(value = "汇款手续费率")
    private BigDecimal handleFeeRate;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}