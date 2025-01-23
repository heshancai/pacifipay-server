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
     * @param channelId
     * @param saveAmount
     */
    void checkUserBalance(Long userId, Long channelId, BigDecimal saveAmount);

    UserWalletBalance getUserWalletBalance(Long userId, Long channelId);
}
