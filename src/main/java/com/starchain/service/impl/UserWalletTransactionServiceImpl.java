package com.starchain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.dao.UserWalletTransactionMapper;
import com.starchain.entity.UserWallet;
import com.starchain.entity.UserWalletTransaction;
import com.starchain.entity.WalletCallbackRecord;
import com.starchain.service.IUserWalletTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-10
 * @Description
 */
@Service
@Slf4j
public class UserWalletTransactionServiceImpl extends ServiceImpl<UserWalletTransactionMapper, UserWalletTransaction> implements IUserWalletTransactionService {
    @Override
    public void dealRecodeTransaction(BigDecimal localFee, BigDecimal actAmount, UserWallet userWallet, WalletCallbackRecord walletCallbackRecord) {
        // 充值金额 实际到账金额 扣除手续费金额
        UserWalletTransaction userWalletTransaction = UserWalletTransaction.builder().fee(localFee)
                .userId(userWallet.getUserId())
                .coinName(walletCallbackRecord.getSymbol())
                .amount(walletCallbackRecord.getAmount())
                .actAmount(actAmount)
                .type(walletCallbackRecord.getDepositId().equals("deposit") ? 1 : 2)
                .createTime(LocalDateTime.now())
                .partitionKey(walletCallbackRecord.getPartitionKey())
                .businessId(walletCallbackRecord.getNotifyId())
                .remark(walletCallbackRecord.getDepositId().equals("deposit") ? "充币" : "提币")
                .address(walletCallbackRecord.getAddress())
                .txId(walletCallbackRecord.getTxid()).tradeId(walletCallbackRecord.getTxid()).build();
        this.save(userWalletTransaction);
        log.info("记录交易记录成功,交易信息为:{}", userWalletTransaction);
    }
}
