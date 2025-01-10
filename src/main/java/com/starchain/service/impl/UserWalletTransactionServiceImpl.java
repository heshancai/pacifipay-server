package com.starchain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.dao.UserWalletTransactionMapper;
import com.starchain.entity.UserWallet;
import com.starchain.entity.UserWalletTransaction;
import com.starchain.entity.WalletCallbackRecord;
import com.starchain.service.IUserWalletTransactionService;
import org.springframework.stereotype.Service;

/**
 * @author
 * @date 2025-01-10
 * @Description
 */
@Service
public class UserWalletTransactionServiceImpl extends ServiceImpl<UserWalletTransactionMapper, UserWalletTransaction> implements IUserWalletTransactionService {
    @Override
    public void dealRecodeTransaction(UserWallet userWallet, WalletCallbackRecord walletCallbackRecord) {

    }
}
