package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-02-07
 * @Description
 */
@Schema(title = "CardFeeRule", description = "卡费规则配置表")
@Data
@TableName("card_fee_rules")
public class CardFeeRule {

    @Schema(description = "主键ID", example = "1")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "开卡费")
    private BigDecimal cardFee;

    @Schema(description = "预存款")
    private BigDecimal saveAmount;

    @Schema(description = "月服务费")
    private BigDecimal monthlyFee;

    @Schema(description = "销卡费")
    private BigDecimal cancelFee;

    @Schema(description = "充值手续费率")
    private BigDecimal rechargeFeeRate;

    @Schema(description = "提现手续费率")
    private BigDecimal withdrawFeeRate;

    @Schema(description = "汇款手续费率")
    private BigDecimal remitFeeRate;

    @Schema(description = "汇款手续费")
    private BigDecimal remitFeeAmount;

    @Schema(description = "卡类型编码", example = "TpyMDN6")
    private String cardCode;

    @Schema(description = "每日充值次数限制")
    private Integer dailyRechargeLimit;

    @Schema(description = "最低充值金额")
    private BigDecimal minRechargeAmount;

    @Schema(description = "单笔充值限额")
    private BigDecimal maxSingleRecharge;

    @Schema(description = "每日充值限额")
    private BigDecimal dailyRechargeMax;

    @Schema(description = "卡最大余额")
    private BigDecimal cardBalanceMax;

    @Schema(description = "销卡手续费")
    private BigDecimal handleFeeAmount;

    @Schema(description = "汇款手续费率")
    private BigDecimal handleFeeRate;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}