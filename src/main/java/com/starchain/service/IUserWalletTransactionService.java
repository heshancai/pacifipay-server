package com.starchain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starchain.entity.UserWallet;
import com.starchain.entity.UserWalletTransaction;
import com.starchain.entity.WalletCallbackRecord;

import java.math.BigDecimal;

/**
 * @author
 * @date 2025-01-10
 * @Description
 */
public interface IUserWalletTransactionService extends IService<UserWalletTransaction> {
    /**
     * 记录充币交易记录
     *
     * @param localFee
     * @param
     * @param walletCallbackRecord
     */
    void dealRecodeTransaction(BigDecimal localFee, BigDecimal actAmount, UserWallet userWallet, WalletCallbackRecord walletCallbackRecord);
}
