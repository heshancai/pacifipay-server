package com.starchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.starchain.entity.SysCoin;
import com.starchain.entity.UserWallet;
import com.starchain.entity.UserWalletBalance;
import com.starchain.entity.WalletCallbackRecord;
import com.starchain.entity.response.WalletRechargeCallbackResponse;
import com.starchain.enums.RechargeRecordStatusEnum;
import com.starchain.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-10
 * @Description
 */
@Slf4j
@Service
public class WalletCallbackImpl implements IWalletCallbackService {


    @Autowired
    private ISysCoinService sysCoinService;

    @Autowired
    private IUserWalletService userWalletService;

    @Autowired
    private IWalletCallbackRecordService walletCallbackRecordService;

    @Autowired
    private IUserWalletTransactionService userWalletTransactionService;

    @Autowired
    private IUserWalletBalanceService userWalletBalanceService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void dealDeposit(WalletRechargeCallbackResponse walletRechargeCallbackResponse, BigDecimal depositAmount, WalletCallbackRecord walletCallbackRecord) {
        try {
            log.info("开始处理充值到账, 币种信息: {}, 充值数量: {}", walletRechargeCallbackResponse.getCurrencySymbol(), depositAmount);

            // 1. 获取币种信息
            SysCoin coin = getCoinInfo(walletRechargeCallbackResponse.getCurrencySymbol());
            Assert.notNull(coin, "币种信息不能为空");

            // 2. 获取用户钱包信息
            UserWallet userWallet = getUserWallet(walletRechargeCallbackResponse.getDepositAddress());
            Assert.notNull(userWallet, "用户钱包信息不存在");

            // 3. 计算手续费和实际到账金额
            BigDecimal localFee = calculateFee(coin, walletRechargeCallbackResponse.getDepositAmount());
            BigDecimal actAmount = walletCallbackRecord.getAmount().subtract(localFee);
            log.debug("手续费: {}, 实际到账金额: {}", localFee, actAmount);

            // 4. 更新充值记录状态
            walletCallbackRecord.setStatus(RechargeRecordStatusEnum.SUCCESS.getKey());

            // 5. 记录交易记录
            recordTransaction(localFee, actAmount, userWallet, walletCallbackRecord);

            // 6. 更新钱包余额
            updateWalletBalance(userWallet, actAmount);

            // 7. 更新充值记录完成时间和状态
            walletCallbackRecord.setFinishTime(LocalDateTime.now());
            walletCallbackRecord.setSuccessConfirm(1);
            walletCallbackRecordService.updateById(walletCallbackRecord);

            log.info("充值处理成功, 用户ID: {}, 充值记录ID: {}", userWallet.getUserId(), walletCallbackRecord.getId());
        } catch (Exception e) {
            log.error("充值处理失败, 异常信息: {}", e.getMessage(), e);
            throw e; // 抛出异常，触发事务回滚
        }
    }

    /**
     * 获取币种信息
     */
    private SysCoin getCoinInfo(String currencySymbol) {
        log.debug("查询币种信息, 币种符号: {}", currencySymbol);
        LambdaQueryWrapper<SysCoin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysCoin::getCoinName, currencySymbol);
        return sysCoinService.getOne(queryWrapper);
    }

    /**
     * 获取用户钱包信息
     */
    private UserWallet getUserWallet(String depositAddress) {
        log.debug("查询用户钱包信息, 充值地址: {}", depositAddress);
        LambdaQueryWrapper<UserWallet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserWallet::getAddress, depositAddress);
        return userWalletService.getOne(queryWrapper);
    }

    /**
     * 计算手续费
     */
    private BigDecimal calculateFee(SysCoin coin, String depositAmount) {
        log.debug("计算手续费, 币种: {}, 充值金额: {}", coin.getCoinName(), depositAmount);
        return depositPoundageCalc(coin, new BigDecimal(depositAmount));
    }

    /**
     * 记录交易记录
     */
    private void recordTransaction(BigDecimal localFee, BigDecimal actAmount, UserWallet userWallet, WalletCallbackRecord walletCallbackRecord) {
        log.debug("记录交易记录, 用户ID: {}, 实际到账金额: {}", userWallet.getUserId(), actAmount);
        userWalletTransactionService.dealRecodeTransaction(localFee, actAmount, userWallet, walletCallbackRecord);
    }

    /**
     * 更新钱包余额
     */
    private void updateWalletBalance(UserWallet userWallet, BigDecimal actAmount) {
        log.debug("更新钱包余额, 用户ID: {}, 渠道ID: {}, 增加余额: {}", userWallet.getUserId(), userWallet.getChannelId(), actAmount);
        LambdaUpdateWrapper<UserWalletBalance> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserWalletBalance::getUserId, userWallet.getUserId())
                .eq(UserWalletBalance::getChannelId, userWallet.getChannelId())
                .setSql("balance = balance + " + actAmount)
                .set(UserWalletBalance::getUpdateTime, LocalDateTime.now());
        userWalletBalanceService.update(updateWrapper);
    }

    /**
     * 手续费计算
     *
     * @param coin   币种
     * @param amount 金额
     * @return 手续费
     */
    private BigDecimal depositPoundageCalc(SysCoin coin, BigDecimal amount) {

        BigDecimal fee;
        if (coin.getDepositFeeType() == 1) {
            //固定金额
            fee = coin.getDepositFee();
        } else {
            fee = amount.multiply(coin.getDepositFee());
            log.info("计算手续费，amount={},depositFee={},fee={}", amount, coin.getDepositFee(), fee);
        }
        return fee;
    }

}
