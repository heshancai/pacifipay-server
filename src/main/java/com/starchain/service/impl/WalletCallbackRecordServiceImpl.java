package com.starchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.dao.WalletCallbackRecordMapper;
import com.starchain.entity.WalletCallbackRecord;
import com.starchain.entity.response.WalletRechargeCallbackResponse;
import com.starchain.enums.RechargeRecordStatusEnum;
import com.starchain.service.IWalletCallbackRecordService;
import com.starchain.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-10
 * @Description
 */
@Service
@Slf4j
public class WalletCallbackRecordServiceImpl extends ServiceImpl<WalletCallbackRecordMapper, WalletCallbackRecord> implements IWalletCallbackRecordService {
    @Override
    public WalletCallbackRecord checkDepositRecordIsExist(WalletRechargeCallbackResponse walletRechargeCallbackResponse, String side) {
        LambdaQueryWrapper<WalletCallbackRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(WalletCallbackRecord::getTxid, walletRechargeCallbackResponse.getTxId())
                .eq(WalletCallbackRecord::getAddress, walletRechargeCallbackResponse.getDepositAddress())
                .eq(WalletCallbackRecord::getSymbol, walletRechargeCallbackResponse.getCurrencySymbol())
                .eq(WalletCallbackRecord::getSide, side)
                .eq(WalletCallbackRecord::getNotifyId, walletRechargeCallbackResponse.getNotifyId())
                .eq(WalletCallbackRecord::getDepositId, walletRechargeCallbackResponse.getDepositId());
        log.info("txid:{},address:{},symbol:{},side{},notify_id:{},deposit_id:{}", walletRechargeCallbackResponse.getTxId(), walletRechargeCallbackResponse.getDepositAddress(), walletRechargeCallbackResponse.getCurrencySymbol(), side, walletRechargeCallbackResponse.getNotifyId(), walletRechargeCallbackResponse.getDepositId());
        WalletCallbackRecord walletCallbackRecord = this.getOne(lambdaQueryWrapper);
        if (walletCallbackRecord != null) {
            log.info("充值记录已存在:{}", walletCallbackRecord);
            return walletCallbackRecord;
        }
        // 传入的参数进行封装
        walletCallbackRecord = this.convertWalletCallbackRecord(walletRechargeCallbackResponse, side);
        log.info("充值记录不存在:{},插入数据库", walletCallbackRecord);
        boolean isSuccess = this.save(walletCallbackRecord);
        if (isSuccess) {
            return walletCallbackRecord;
        }
        return null;
    }

    public WalletCallbackRecord convertWalletCallbackRecord(WalletRechargeCallbackResponse walletRechargeCallbackResponse, String side) {
        WalletCallbackRecord walletCallbackRecord = new WalletCallbackRecord();
        walletCallbackRecord.setTxid(walletRechargeCallbackResponse.getTxId());
        walletCallbackRecord.setAddress(walletRechargeCallbackResponse.getDepositAddress());
        walletCallbackRecord.setSymbol(walletRechargeCallbackResponse.getCurrencySymbol());
        walletCallbackRecord.setAmount(new BigDecimal(walletRechargeCallbackResponse.getDepositAmount()));
        walletCallbackRecord.setSide(side);
        walletCallbackRecord.setStatus(RechargeRecordStatusEnum.WAIT_CONFIRM.getKey());
        walletCallbackRecord.setNotifyId(walletRechargeCallbackResponse.getNotifyId());
        walletCallbackRecord.setDepositId(walletRechargeCallbackResponse.getDepositId());
        walletCallbackRecord.setConfirmTimes(walletRechargeCallbackResponse.getConfirmTimes());
        walletCallbackRecord.setPartitionKey(DateUtil.getMonth());
        walletCallbackRecord.setCreateTime(LocalDateTime.now());
        walletCallbackRecord.setUpdateTime(LocalDateTime.now());
        return walletCallbackRecord;
    }
}
