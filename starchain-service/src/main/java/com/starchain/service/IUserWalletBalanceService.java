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
     * 更新钱包余额
     * @param wallet
     * @param totalFreezeAmount
     */
    boolean updateWalletBalance(UserWalletBalance wallet, BigDecimal totalFreezeAmount);

    /**
     * 扣减冻结金额
     * @param userId
     * @param businessId
     * @param fromAmount  充值金额 / 全球汇款金额 usd 单位
     * @param handlingFeeAmount 充值手续费 / 全球汇款手续费
     */
    void deductionFreezeBalance(Long userId, Long businessId, BigDecimal fromAmount, BigDecimal handlingFeeAmount);

    /**
     * 冻结金额回滚可用余额
     * @param userId
     * @param businessId
     * @param totalFreezeAmount
     */
    void rollbackFreezeBalance(Long userId, Long businessId, BigDecimal totalFreezeAmount);
}
