package com.starchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.common.entity.CardFeeRule;
import com.starchain.common.entity.UserWalletBalance;
import com.starchain.common.enums.MiPayNotifyType;
import com.starchain.common.exception.StarChainException;
import com.starchain.dao.UserWalletBalanceMapper;
import com.starchain.service.ICardFeeRuleService;
import com.starchain.service.IUserWalletBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author
 * @date 2025-01-13
 * @Description
 */
@Service
@Slf4j
public class UserWalletBalanceServiceImpl extends ServiceImpl<UserWalletBalanceMapper, UserWalletBalance> implements IUserWalletBalanceService {

    @Autowired
    private ICardFeeRuleService cardFeeRuleService;

    public boolean checkUserBalance(String cardCode, Long userId, Long channelId, BigDecimal saveAmount, MiPayNotifyType type) throws StarChainException {
        log.info("检查用户余额: userId={}, channelId={}, saveAmount={}", userId, channelId, saveAmount);

        // 查询用户钱包余额
        UserWalletBalance userWalletBalance = getUserWalletBalance(userId, channelId);
        if (userWalletBalance == null) {
            throw new StarChainException("用户钱包不存在");
        }

        // 查询卡费规则配置表
        CardFeeRule cardFeeRule = getCardFeeRule(cardCode);
        if (cardFeeRule == null) {
            log.warn("未找到卡费规则。");
            throw new StarChainException("卡费规则未找到");
        }

        BigDecimal balance = userWalletBalance.getAvaBalance();

        try {
            switch (type) {
                case CardOpen: // 卡创建
                    checkCardOpenBalance(balance, cardFeeRule);
                    break;
                case Remit: // 汇款

                    checkRemitBalance(balance, saveAmount, cardFeeRule);
                    break;
                case CardRecharge: // 卡充值
                    checkCardRechargeBalance(balance, saveAmount, cardFeeRule);
                    break;
                default:
                    throw new StarChainException("未知的操作类型: " + type);
            }
        } catch (ArithmeticException e) {
            log.error("计算错误", e);
            throw new StarChainException("计算过程中出现错误: " + e.getMessage());
        }

        return true;
    }


    private CardFeeRule getCardFeeRule(String cardCode) {
        LambdaQueryWrapper<CardFeeRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CardFeeRule::getCardCode, cardCode);
        return cardFeeRuleService.getOne(queryWrapper);
    }

    private void checkCardOpenBalance(BigDecimal balance, CardFeeRule cardFeeRule) throws StarChainException {
        BigDecimal requiredBalance = cardFeeRule.getCardFee()
                .add(cardFeeRule.getSaveAmount())
                .add(cardFeeRule.getMonthlyFee());

        if (balance.compareTo(requiredBalance) < 0) {
            throw new StarChainException("余额不足，需要至少" + requiredBalance + "但只有" + balance);
        }
    }

    private void checkRemitBalance(BigDecimal balance, BigDecimal saveAmount, CardFeeRule cardFeeRule) throws StarChainException {
        BigDecimal totalAmount = saveAmount.multiply(cardFeeRule.getHandleFeeAmount()).add(saveAmount);
        if (balance.compareTo(saveAmount.add(totalAmount)) < 0) {
            throw new StarChainException("余额不足，需要至少" + totalAmount + "但只有" + balance);
        }
    }

    private void checkCardRechargeBalance(BigDecimal balance, BigDecimal saveAmount, CardFeeRule cardFeeRule) throws StarChainException {
        // 计算手续费
        BigDecimal totalAmount = saveAmount.multiply(cardFeeRule.getRechargeFeeRate()).add(saveAmount);
        if (balance.compareTo(saveAmount.add(totalAmount)) < 0) {
            throw new StarChainException("余额不足，需要至少" + totalAmount + "但只有" + balance);
        }
    }


    @Override
    public UserWalletBalance getUserWalletBalance(Long userId, Long channelId) {
        LambdaQueryWrapper<UserWalletBalance> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserWalletBalance::getUserId, userId);
        queryWrapper.eq(UserWalletBalance::getBusinessId, channelId);
        return this.getOne(queryWrapper);
    }
}
