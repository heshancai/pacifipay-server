package com.starchain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starchain.common.entity.WalletCoinConfig;

/**
 * @author
 * @date 2025-01-13
 * @Description
 */
public interface IWalletCoinConfigService extends IService<WalletCoinConfig> {
    String toCurrencySymbol(String currencySymbol);

    String toCallbackName(String coinId);
}
