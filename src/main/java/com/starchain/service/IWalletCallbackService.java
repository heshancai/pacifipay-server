package com.starchain.service;

import com.starchain.entity.WalletCallbackRecord;
import com.starchain.entity.response.WalletRechargeCallbackResponse;

import java.math.BigDecimal;

/**
 * @author
 * @date 2025-01-10
 * @Description
 */
public interface IWalletCallbackService {
    /**
     * 充值到账处理
     * @param walletRechargeCallbackResponse
     * @param depositAmount
     * @param walletCallbackRecord
     */
    void dealDeposit(WalletRechargeCallbackResponse walletRechargeCallbackResponse, BigDecimal depositAmount, WalletCallbackRecord walletCallbackRecord);
}
