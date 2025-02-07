package com.starchain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starchain.common.entity.UserWalletBalance;

import java.math.BigDecimal;

/**
 * @author
 * @date 2025-01-13
 * @Description
 */
public interface IUserWalletBalanceService extends IService<UserWalletBalance> {
    /**
     * 检查用户余额是否满足要求
     * @param userId
     * @param businessId
     * @param saveAmount
     */
    boolean checkUserBalance(Long userId, Long businessId, BigDecimal saveAmount,String type);

    /**
     * 获取用户钱包余额
     * @param userId
     * @param businessId
     * @return
     */
    UserWalletBalance getUserWalletBalance(Long userId, Long businessId);
}
