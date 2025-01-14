package com.starchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.context.MiPayNotifyType;
import com.starchain.dao.CardRechargeCallbackRecordMapper;
import com.starchain.entity.CardRechargeCallbackRecord;
import com.starchain.entity.CardRechargeRecord;
import com.starchain.entity.UserWalletBalance;
import com.starchain.entity.response.MiPayCardNotifyResponse;
import com.starchain.enums.CardStatusDescEnum;
import com.starchain.enums.RechargeRecordStatusEnum;
import com.starchain.exception.StarChainException;
import com.starchain.service.ICardRechargeCallbackRecordService;
import com.starchain.service.ICardRechargeRecordService;
import com.starchain.service.IUserWalletBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-06
 * @Description 卡充值成功回调
 */
@Service
@Slf4j
public class CardRechargeCallbackRecordServiceImpl extends ServiceImpl<CardRechargeCallbackRecordMapper, CardRechargeCallbackRecord> implements ICardRechargeCallbackRecordService {

    @Autowired
    private ICardRechargeRecordService cardRechargeRecordService;

    @Autowired
    private IUserWalletBalanceService userWalletBalanceService;


    @Override
    public Boolean callBack(MiPayCardNotifyResponse miPayCardNotifyResponse) {
        try {
            // 1. 校验业务类型
            validateBusinessType(miPayCardNotifyResponse);

            // 2. 核实卡充值记录是否存在
            CardRechargeRecord rechargeRecord = validateAndGetRechargeRecord(miPayCardNotifyResponse);

            // 3. 检查是否已经处理成功（幂等性）
            if (rechargeRecord.getStatus() == 1) {
                log.info("充值记录已处理成功，无需重复处理，通知ID: {}", miPayCardNotifyResponse.getNotifyId());
                return true;
            }

            // 4. 校验卡开通手续费
            validateRechargeAmount(miPayCardNotifyResponse, rechargeRecord);

            // 5. 查询或创建回调记录
            CardRechargeCallbackRecord callbackRecord = createOrUpdateCallbackRecord(miPayCardNotifyResponse);

            // 6. 处理充值状态
            return handleRechargeStatus(miPayCardNotifyResponse, rechargeRecord, callbackRecord);
        } catch (Exception e) {
            log.error("卡开通回调处理失败, 通知ID: {}, 错误信息: {}", miPayCardNotifyResponse.getNotifyId(), e.getMessage(), e);
            throw new StarChainException("卡开通回调处理失败");
        }
    }

    // 1. 校验业务类型
    private void validateBusinessType(MiPayCardNotifyResponse response) {
        Assert.isTrue(
                MiPayNotifyType.CardRecharge.getType().equals(response.getBusinessType()),
                "回调业务类型与接口调用不匹配"
        );
        log.info("回调业务类型校验通过: {}", response.getBusinessType());
    }

    // 2. 核实卡充值记录是否存在
    private CardRechargeRecord validateAndGetRechargeRecord(MiPayCardNotifyResponse response) {
        LambdaQueryWrapper<CardRechargeRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CardRechargeRecord::getCardCode, response.getCardCode())
                .eq(CardRechargeRecord::getCardId, response.getCardId())
                .eq(CardRechargeRecord::getOrderId, response.getMchOrderId());
        CardRechargeRecord record = cardRechargeRecordService.getOne(queryWrapper);
        Assert.isTrue(record != null, "核实卡充值记录不存在");
        log.info("卡信息校验通过, 卡ID: {}", response.getCardId());
        return record;
    }

    // 4. 校验卡开通手续费
    private void validateRechargeAmount(MiPayCardNotifyResponse response, CardRechargeRecord record) {
        BigDecimal actual = (BigDecimal) response.getAmount().get("actual");
        BigDecimal handleFee = (BigDecimal) response.getAmount().get("handleFee");

        Assert.isTrue(record.getOrderAmount().compareTo(actual) == 0, "实际支付金额不一致");
        Assert.isTrue(record.getOrderFee().compareTo(handleFee) == 0, "充值手续费不一致");
        log.info("卡开通手续费校验通过, 实际手续费: {}", record.getOrderFee());
    }

    // 5. 查询或创建回调记录
    private CardRechargeCallbackRecord createOrUpdateCallbackRecord(MiPayCardNotifyResponse response) {
        LambdaQueryWrapper<CardRechargeCallbackRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CardRechargeCallbackRecord::getNotifyId, response.getNotifyId())
                .eq(CardRechargeCallbackRecord::getCardCode, response.getCardCode())
                .eq(CardRechargeCallbackRecord::getBusinessType, response.getBusinessType())
                .eq(CardRechargeCallbackRecord::getCardId, response.getCardId());

        CardRechargeCallbackRecord callbackRecord = this.getOne(queryWrapper);
        if (callbackRecord == null) {
            callbackRecord = new CardRechargeCallbackRecord();
            callbackRecord.setNotifyId(response.getNotifyId());
            callbackRecord.setCardCode(response.getCardCode());
            callbackRecord.setCardNo(response.getCardNo());
            callbackRecord.setBusinessType(response.getBusinessType());
            callbackRecord.setCardId(response.getCardId());
            callbackRecord.setMchOrderId(response.getMchOrderId());
            callbackRecord.setRecharge((BigDecimal) response.getAmount().get("recharge"));
            callbackRecord.setActual((BigDecimal) response.getAmount().get("actual"));
            callbackRecord.setHandleFee((BigDecimal) response.getAmount().get("handleFee"));
            callbackRecord.setCreateTime(LocalDateTime.now());
            callbackRecord.setStatus(response.getStatus());
            callbackRecord.setStatusDesc(response.getStatusDesc());
            callbackRecord.setUpdateTime(LocalDateTime.now());
            this.save(callbackRecord);
            log.info("创建新的回调记录, 通知ID: {}", response.getNotifyId());
        } else {
            log.info("回调记录已存在, 处理重复通知 ，通知ID: {}", response.getNotifyId());
        }
        return callbackRecord;
    }

    // 处理充值状态
    private boolean handleRechargeStatus(MiPayCardNotifyResponse response, CardRechargeRecord rechargeRecord, CardRechargeCallbackRecord callbackRecord) {
        if (rechargeRecord.getStatus() == 0 && "SUCCESS".equals(response.getStatus())
                && CardStatusDescEnum.CARD_RECHARGE_SUCCESS.getDescription().equalsIgnoreCase(response.getStatusDesc())) {
            // 修改卡充值状态为成功
            updateRechargeRecordStatus(response, rechargeRecord, callbackRecord);

            // 修改用户钱包余额
            updateUserWalletBalance(rechargeRecord, callbackRecord);

            // 更新回调记录
            updateCallbackRecord(callbackRecord, response);

            return true;
        } else if ("FAILED".equals(response.getStatus())) {
            // 处理失败状态
            handleFailedStatus(callbackRecord);
            return false;
        }

        log.info("回调处理完成, 无须重复处理,通知ID: {}", response.getNotifyId());
        return true;
    }

    // 更新充值记录状态
    private void updateRechargeRecordStatus(MiPayCardNotifyResponse response, CardRechargeRecord rechargeRecord, CardRechargeCallbackRecord callbackRecord) {
        LambdaUpdateWrapper<CardRechargeRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CardRechargeRecord::getCardId, response.getCardId())
                .eq(CardRechargeRecord::getStatus, 0) // 确保状态为未处理
                .set(CardRechargeRecord::getStatus, RechargeRecordStatusEnum.SUCCESS.getKey())
                .set(CardRechargeRecord::getUpdateTime, LocalDateTime.now())
                .set(CardRechargeRecord::getActAmount, callbackRecord.getActual())
                .set(CardRechargeRecord::getFinishTime, LocalDateTime.now());
        boolean isUpdated = cardRechargeRecordService.update(updateWrapper);
        if (!isUpdated) {
            log.warn("充值记录状态更新失败，可能已被其他线程处理，通知ID: {}", response.getNotifyId());
        }
    }

    // 更新用户钱包余额
    private void updateUserWalletBalance(CardRechargeRecord rechargeRecord, CardRechargeCallbackRecord callbackRecord) {
        LambdaUpdateWrapper<UserWalletBalance> balanceUpdateWrapper = new LambdaUpdateWrapper<>();
        balanceUpdateWrapper.eq(UserWalletBalance::getUserId, rechargeRecord.getUserId())
                .setSql("balance = balance - " + callbackRecord.getActual())
                .set(UserWalletBalance::getUpdateTime, LocalDateTime.now());
        userWalletBalanceService.update(balanceUpdateWrapper);
    }

    // 更新回调记录
    private void updateCallbackRecord(CardRechargeCallbackRecord callbackRecord, MiPayCardNotifyResponse response) {
        callbackRecord.setUpdateTime(LocalDateTime.now());
        callbackRecord.setStatus(response.getStatus());
        callbackRecord.setStatusDesc(response.getStatusDesc());
        callbackRecord.setFinishTime(LocalDateTime.now());
        this.updateById(callbackRecord);
    }

    // 处理失败状态
    private void handleFailedStatus(CardRechargeCallbackRecord callbackRecord) {
        LambdaUpdateWrapper<CardRechargeCallbackRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CardRechargeCallbackRecord::getNotifyId, callbackRecord.getNotifyId())
                .setSql("retries = retries + 1")
                .set(CardRechargeCallbackRecord::getUpdateTime, LocalDateTime.now());
        this.update(updateWrapper);
    }
}
