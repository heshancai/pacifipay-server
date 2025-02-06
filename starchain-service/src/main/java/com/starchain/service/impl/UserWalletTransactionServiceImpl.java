package com.starchain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.common.entity.UserWalletTransaction;
import com.starchain.common.entity.WalletCallbackRecord;
import com.starchain.common.enums.TransactionTypeEnum;
import com.starchain.dao.UserWalletTransactionMapper;
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
    public void dealRecodeTransaction(BigDecimal localFee, BigDecimal actAmount, BigDecimal balance, BigDecimal finalBalance, Long userId, WalletCallbackRecord walletCallbackRecord) {

        // 充值金额 实际到账金额 扣除手续费金额
        UserWalletTransaction userWalletTransaction = UserWalletTransaction.builder()
                .userId(userId)
                .balance(balance)
                .coinName(walletCallbackRecord.getSymbol())
                .amount(walletCallbackRecord.getAmount())
                .fee(localFee)
                .actAmount(actAmount)
                .finaBalance(finalBalance)
                .type(walletCallbackRecord.getDepositId().equals("deposit") ? TransactionTypeEnum.DEPOSIT.getCode() : TransactionTypeEnum.WITHDRAWAL.getCode())
                .createTime(LocalDateTime.now())
                .partitionKey(walletCallbackRecord.getPartitionKey())
                .businessNumber(walletCallbackRecord.getNotifyId())
                .remark(walletCallbackRecord.getDepositId().equals("deposit") ? TransactionTypeEnum.DEPOSIT.getDescription() : TransactionTypeEnum.WITHDRAWAL.getDescription())
                .address(walletCallbackRecord.getAddress())
                .txId(walletCallbackRecord.getTxid())
                .tradeId(walletCallbackRecord.getDepositId()).build();
        this.save(userWalletTransaction);
        log.info("记录交易记录成功,交易信息为:{}", userWalletTransaction);
    }
}
