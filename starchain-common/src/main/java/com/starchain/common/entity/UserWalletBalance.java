package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-13
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_wallet_balance")
@Schema(description = "用户钱包余额信息") // 修改: 将 @ApiModel 替换为 @Schema
@Builder
public class UserWalletBalance {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键", example = "1") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private Long id;

    @Schema(description = "用户ID", example = "12345") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private Long userId;

    @Schema(description = "商户Id", example = "12345") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private Long businessId;

    @Schema(description = "可用余额", example = "可用余额") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private BigDecimal avaBalance;

    @Schema(description = "冻结金额", example = "冻结金额") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private BigDecimal freezeBalance;

    @Schema(description = "创建时间", example = "2025-01-01T12:00:00") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2025-01-01T12:00:00") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private LocalDateTime updateTime;
}