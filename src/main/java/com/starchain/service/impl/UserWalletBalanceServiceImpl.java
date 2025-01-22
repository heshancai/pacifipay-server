package com.starchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.dao.UserWalletBalanceMapper;
import com.starchain.entity.UserWalletBalance;
import com.starchain.exception.StarChainException;
import com.starchain.service.IUserWalletBalanceService;
import lombok.extern.slf4j.Slf4j;
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
    @Override
    public void checkUserBalance(Long userId, Long channelId, BigDecimal saveAmount) {
        log.info("checkUserBalance userId:{}, channelId:{}, saveAmount:{}", userId, channelId, saveAmount);

        UserWalletBalance userWalletBalance = this.getOne(new QueryWrapper<UserWalletBalance>()
                .eq("user_id", userId)
                .eq("channel_id", channelId));

        if (userWalletBalance == null) {
            throw new RuntimeException("User wallet balance not found");
        }

        BigDecimal balance = userWalletBalance.getBalance();

        // 钱包余额必须大于等于汇款金额+手续费（1 USD）
        if ( balance.compareTo(saveAmount.add(BigDecimal.ONE)) < 0) {
            throw new StarChainException("余额不足");
        }
    }

    @Override
    public UserWalletBalance getUserWalletBalance(Long userId, Long channelId) {
        LambdaQueryWrapper<UserWalletBalance> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserWalletBalance::getUserId, userId);
        queryWrapper.eq(UserWalletBalance::getChannelId, channelId);
        return this.getOne(queryWrapper);
    }
}
