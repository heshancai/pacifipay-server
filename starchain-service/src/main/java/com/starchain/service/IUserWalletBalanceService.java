package com.starchain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starchain.common.entity.UserWalletBalance;
import com.starchain.common.enums.MiPayNotifyType;

import java.math.BigDecimal;

/**
 * @author
 * @date 2025-01-13
 * @Description
 */
public interface IUserWalletBalanceService extends IService<UserWalletBalance> {
    /**
     * 检查用户余额是否满足要求
     *
     * @param userId
     * @param businessId
     * @param saveAmount
     */
    boolean checkUserBalance(String cardCode, Long userId, Long businessId, BigDecimal saveAmount, MiPayNotifyType type);

    /**
     * 获取用户钱包余额
     *
     * @param userId
     * @param businessId
     * @return
     */
    UserWalletBalance getUserWalletBalance(Long userId, Long businessId);

    /**
     *
     * @param wallet
     * @param totalFreezeAmount
     */
    boolean updateWalletBalance(UserWalletBalance wallet, BigDecimal totalFreezeAmount);
}
