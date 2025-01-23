package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "WalletCoinConfig", description = "Wallet Coin Configuration")
public class WalletCoinConfig {

    @TableId
    @ApiModelProperty(value = "Coin Name", example = "Bitcoin")
    private String coinName;

    @ApiModelProperty(value = "Callback Name", example = "BTC")
    private String callbackName;
}
