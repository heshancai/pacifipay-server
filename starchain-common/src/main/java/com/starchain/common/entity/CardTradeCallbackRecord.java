package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "卡交易通知记录实体")
public class CardTradeCallbackRecord {

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "通知ID")
    private String notifyId;

    @Schema(description = "卡类型")
    private String cardCode;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "卡ID")
    private String cardId;

    @Schema(description = "交易时间")
    private LocalDateTime tradeTime;

    @Schema(description = "交易类型")
    private String tradeType;

    @Schema(description = "交易流水号")
    private String tradeId;

    @Schema(description = "原交易流水号")
    private String originalTradeId;

    @Schema(description = "余额")
    private BigDecimal balance;

    @Schema(description = "商户名")
    private String merchantName;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "手续费")
    private BigDecimal fee;

    @Schema(description = "交易金额")
    private BigDecimal trade;

    @Schema(description = "交易币种")
    private String tradeCurrency;

    @Schema(description = "重试次数")
    private Integer retries;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @Schema(description = "完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime finishTime;
}
