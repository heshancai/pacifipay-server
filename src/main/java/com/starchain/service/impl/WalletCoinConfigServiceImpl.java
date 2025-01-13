package com.starchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.dao.WalletCoinConfigMapper;
import com.starchain.entity.WalletCoinConfig;
import com.starchain.service.IWalletCoinConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author
 * @date 2025-01-13
 * @Description
 */
@Service
@Slf4j
public class WalletCoinConfigServiceImpl extends ServiceImpl<WalletCoinConfigMapper, WalletCoinConfig> implements IWalletCoinConfigService {


    @Override
    public String toCurrencySymbol(String currencySymbol) {
        LambdaQueryWrapper<WalletCoinConfig> walletCoinConfigLambdaQueryWrapper = new LambdaQueryWrapper<>();
        walletCoinConfigLambdaQueryWrapper.eq(WalletCoinConfig::getCoinName, currencySymbol);
        WalletCoinConfig walletCoinConfig = this.getOne(walletCoinConfigLambdaQueryWrapper);
        return walletCoinConfig.getCallbackName();
    }

    /**
     * 将币种符号 转为接口传递标识 callbackName
     *
     * @param coinId
     * @return
     */
    @Override
    public String toCallbackName(String coinId) {
        LambdaQueryWrapper<WalletCoinConfig> walletCoinConfigLambdaQueryWrapper = new LambdaQueryWrapper<>();
        walletCoinConfigLambdaQueryWrapper.eq(WalletCoinConfig::getCoinName, coinId);
        WalletCoinConfig walletCoinConfig = this.getOne(walletCoinConfigLambdaQueryWrapper);
        Assert.notNull(walletCoinConfig, "获取CallbackName失败");
        return walletCoinConfig.getCallbackName();
    }
}
