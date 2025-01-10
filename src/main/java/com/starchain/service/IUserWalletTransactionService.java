package com.starchain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starchain.entity.UserWallet;
import com.starchain.entity.UserWalletTransaction;
import com.starchain.entity.WalletCallbackRecord;

/**
 * @author
 * @date 2025-01-10
 * @Description
 */
public interface IUserWalletTransactionService extends IService<UserWalletTransaction> {
    void dealRecodeTransaction(UserWallet userWallet, WalletCallbackRecord walletCallbackRecord);
}
