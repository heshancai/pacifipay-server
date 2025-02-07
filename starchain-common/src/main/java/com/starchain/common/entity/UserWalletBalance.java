package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "UserWalletBalance", description = "用户钱包余额信息")
@Builder
public class UserWalletBalance {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键", example = "1")
    private Long id;

    @ApiModelProperty(value = "用户ID", example = "12345")
    private Long userId;

    @ApiModelProperty(value = "商户Id", example = "12345")
    private Long businessId;

    @ApiModelProperty(value = "可用余额", example = "可用余额")
    private BigDecimal avaBalance;

    @ApiModelProperty(value = "冻结金额", example = "冻结金额")
    private BigDecimal freezeBalance;

    @ApiModelProperty(value = "创建时间", example = "2025-01-01T12:00:00")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间", example = "2025-01-01T12:00:00")
    private LocalDateTime updateTime;
}
