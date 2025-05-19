package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author
 * @date 2025-01-13
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wallet_coin_config")
@Schema(description = "Wallet Coin Configuration") // 修改: 将 @ApiModel 替换为 @Schema
public class WalletCoinConfig {

    @TableId
    @Schema(description = "Coin Name", example = "Bitcoin") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private String coinName;

    @Schema(description = "Callback Name", example = "BTC") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private String callbackName;
}