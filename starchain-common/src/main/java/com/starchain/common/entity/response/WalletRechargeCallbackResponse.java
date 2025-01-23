package com.starchain.common.entity.response;

import lombok.Data;

/**
 * @author
 * @date 2025-01-10
 * @Description 币充值回调接收模版
 */
@Data
public class WalletRechargeCallbackResponse {
    /**
     * 充值地址
     */
    String depositAddress;
    /**
     * 充值数量
     */
    String depositAmount;
    /**
     * 区块链上的交易id
     */
    String txId;
    /**
     * 当前确认次数
     */
    Integer confirmTimes;
    /**
     * 通知id 唯一
     */
    String notifyId;

    /**
     * 充值id 唯一
     */
    String depositId;

    /**
     * 币种符号
     */
    String currencySymbol;
}
