package com.starchain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starchain.entity.WalletCallbackRecord;
import com.starchain.entity.response.WalletRechargeCallbackResponse;

import java.math.BigDecimal;

/**
 * @author
 * @date 2025-01-10
 * @Description
 */
public interface IWalletCallbackRecordService extends IService<WalletCallbackRecord> {
    /**
     * 检查提币记录是否存在 不存在则生成提币记录
     * @param walletRechargeCallbackResponse
     * @param side
     * @return
     */
    WalletCallbackRecord checkDepositRecordIsExist(WalletRechargeCallbackResponse walletRechargeCallbackResponse, String side);


}
