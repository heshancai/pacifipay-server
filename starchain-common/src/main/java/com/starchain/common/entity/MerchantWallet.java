package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "商户钱包表")
public class MerchantWallet {
    @Schema(description = "主键ID", example = "1")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "appid", example = "app123")
    private String appId;

    @Schema(description = "商户ID", example = "merchant123")
    private String merchantId;

    @Schema(description = "余额", example = "1000.00")
    private BigDecimal amount;

    @Schema(description = "冻结金额", example = "1000.00")
    private BigDecimal freeze;

    @Schema(description = "币种", example = "CNY")
    private String moneyKind;

    @Schema(description = "更新时间", example = "2023-10-01 12:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @Schema(description = "创建时间", example = "2023-10-01 12:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
