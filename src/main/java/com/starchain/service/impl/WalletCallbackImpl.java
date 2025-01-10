package com.starchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.starchain.entity.SysCoin;
import com.starchain.entity.UserWallet;
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void dealDeposit(WalletRechargeCallbackResponse walletRechargeCallbackResponse, BigDecimal depositAmount, WalletCallbackRecord walletCallbackRecord) {
        log.info("开始处理充值到账,币种信息为:{}，充值数量为:{}", walletRechargeCallbackResponse.getCurrencySymbol(), depositAmount);
        LambdaQueryWrapper<SysCoin> sysCoinLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysCoinLambdaQueryWrapper.eq(SysCoin::getCoinName, walletRechargeCallbackResponse.getCurrencySymbol());
        SysCoin coin = sysCoinService.getOne(sysCoinLambdaQueryWrapper);
        Assert.notNull(coin, "币种信息不能为空");

        // 获取用户钱包信息
        LambdaQueryWrapper<UserWallet> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserWallet::getCoinId, walletRechargeCallbackResponse.getDepositAddress());
        UserWallet userWallet = userWalletService.getOne(lambdaQueryWrapper);
        Assert.notNull(userWallet, "用户钱包信息不存在");
        // 计算手续费
        BigDecimal localFee = depositPoundageCalc(coin, new BigDecimal(walletRechargeCallbackResponse.getDepositAmount()));
        // 记录交易记录
        userWalletTransactionService.dealRecodeTransaction(userWallet,walletCallbackRecord);
        //  修改用户钱包余额信息
        walletCallbackRecord.setStatus(RechargeRecordStatusEnum.CONFIRMED.getKey());
        walletCallbackRecord.setFinishTime(LocalDateTime.now());
        walletCallbackRecord.setSuccessConfirm(1);
        // 更新记录表信息 设置状态为成功
        walletCallbackRecordService.updateById(walletCallbackRecord);
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
