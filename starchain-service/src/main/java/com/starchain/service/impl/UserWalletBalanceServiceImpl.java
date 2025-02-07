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

    public boolean checkUserBalance(Long userId, Long channelId, BigDecimal saveAmount, String type) throws StarChainException {
        log.info("checkUserBalance userId:{}, channelId:{}, saveAmount:{}", userId, channelId, saveAmount);

        // 查询用户钱包余额
        LambdaQueryWrapper<UserWalletBalance> userWalletBalanceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userWalletBalanceLambdaQueryWrapper.eq(UserWalletBalance::getUserId, userId);
        userWalletBalanceLambdaQueryWrapper.eq(UserWalletBalance::getBusinessId, channelId);
        UserWalletBalance userWalletBalance = this.getOne(userWalletBalanceLambdaQueryWrapper);

        if (userWalletBalance == null) {
            throw new StarChainException("用户钱包不存在");
        }

        BigDecimal balance = userWalletBalance.getBalance();

        if (type.equals(MiPayNotifyType.CardOpen.getType())) {
            // 查询卡费规则配置表
            LambdaQueryWrapper<CardFeeRule> cardFeeRuleWrapper = new LambdaQueryWrapper<>();
            cardFeeRuleWrapper.last("LIMIT 1"); // 获取最新的一条规则记录
            CardFeeRule cardFeeRule = cardFeeRuleService.getOne(cardFeeRuleWrapper);

            if (cardFeeRule == null) {
                log.warn("Card fee rules not found.");
                throw new StarChainException("卡费规则未找到");
            }

            // 计算用户需要的最小余额
            BigDecimal requiredBalance = cardFeeRule.getCardFee()
                    .add(cardFeeRule.getSaveAmount())
                    .add(cardFeeRule.getMonthlyFee());

            // 检查钱包余额是否足够
            if (balance.compareTo(requiredBalance) < 0) {
                throw new StarChainException("余额不足，需要至少" + requiredBalance + "但只有" + balance);
            }

        } else if (type.equals(MiPayNotifyType.Remit.getType())) {
            // 钱包余额必须大于等于 汇款金额 + 1 USD（手续费）
            if (balance.compareTo(saveAmount.add(BigDecimal.ONE)) < 0) {
                throw new StarChainException("余额不足，需要至少" + saveAmount.add(BigDecimal.ONE) + "但只有" + balance);
            }
        }

        return true;
    }


    @Override
    public UserWalletBalance getUserWalletBalance(Long userId, Long channelId) {
        LambdaQueryWrapper<UserWalletBalance> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserWalletBalance::getUserId, userId);
        queryWrapper.eq(UserWalletBalance::getBusinessId, channelId);
        return this.getOne(queryWrapper);
    }
}
