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
        // 修改卡充值记录状态
        try {
            // 1. 校验业务类型
            Assert.isTrue(
                    MiPayNotifyType.CardRecharge.getType().equals(miPayCardNotifyResponse.getBusinessType()),
                    "回调业务类型与接口调用不匹配"
            );
            log.info("回调业务类型校验通过: {}", miPayCardNotifyResponse.getBusinessType());

            // 2. 核实卡充值记录是否存在
            LambdaQueryWrapper<CardRechargeRecord> cardRechargeRecordLambdaQueryWrapper = new LambdaQueryWrapper<>();
            cardRechargeRecordLambdaQueryWrapper.eq(CardRechargeRecord::getCardCode, miPayCardNotifyResponse.getCardCode());
            cardRechargeRecordLambdaQueryWrapper.eq(CardRechargeRecord::getCardId, miPayCardNotifyResponse.getCardId());
            cardRechargeRecordLambdaQueryWrapper.eq(CardRechargeRecord::getOrderId, miPayCardNotifyResponse.getMchOrderId());
            CardRechargeRecord rechargeRecord = cardRechargeRecordService.getOne(cardRechargeRecordLambdaQueryWrapper);
            Assert.isTrue(rechargeRecord != null, "核实卡充值记录不存在");
            log.info("卡信息校验通过, 卡ID: {}", miPayCardNotifyResponse.getCardId());

            // 3. 校验卡开通手续费
            BigDecimal actual = (BigDecimal) miPayCardNotifyResponse.getAmount().get("actual");
            Assert.isTrue(
                    rechargeRecord.getOrderAmount().compareTo(actual) == 0,
                    "充值手续费不一致"
            );
            Assert.isTrue(
                    rechargeRecord.getOrderFee().compareTo((BigDecimal) miPayCardNotifyResponse.getAmount().get("handleFee")) == 0,
                    "充值手续费不一致"
            );
            log.info("卡开通手续费校验通过, 实际手续费: {}", actual);

            // 4. 查询或创建回调记录
            LambdaQueryWrapper<CardRechargeCallbackRecord> recordQueryWrapper = new LambdaQueryWrapper<>();
            recordQueryWrapper.eq(CardRechargeCallbackRecord::getNotifyId, miPayCardNotifyResponse.getNotifyId());
            recordQueryWrapper.eq(CardRechargeCallbackRecord::getCardCode, miPayCardNotifyResponse.getCardCode());
            recordQueryWrapper.eq(CardRechargeCallbackRecord::getBusinessType, miPayCardNotifyResponse.getBusinessType());
            recordQueryWrapper.eq(CardRechargeCallbackRecord::getCardId, miPayCardNotifyResponse.getCardId());

            CardRechargeCallbackRecord cardRechargeCallbackRecord = this.getOne(recordQueryWrapper);
            if (cardRechargeCallbackRecord == null) {
                cardRechargeCallbackRecord = new CardRechargeCallbackRecord();
                cardRechargeCallbackRecord.setNotifyId(miPayCardNotifyResponse.getNotifyId());
                cardRechargeCallbackRecord.setCardCode(miPayCardNotifyResponse.getCardCode());
                cardRechargeCallbackRecord.setBusinessType(miPayCardNotifyResponse.getBusinessType());
                cardRechargeCallbackRecord.setCardId(miPayCardNotifyResponse.getCardId());
                cardRechargeCallbackRecord.setCreateTime(LocalDateTime.now());
                cardRechargeCallbackRecord.setStatus(miPayCardNotifyResponse.getStatus());
                cardRechargeCallbackRecord.setStatusDesc(miPayCardNotifyResponse.getStatusDesc());
                cardRechargeCallbackRecord.setUpdateTime(LocalDateTime.now());
                this.save(cardRechargeCallbackRecord);
                log.info("创建新的回调记录, 通知ID: {}", miPayCardNotifyResponse.getNotifyId());
            } else {
                log.info("回调记录已存在, 通知ID: {}", miPayCardNotifyResponse.getNotifyId());
            }

            // 5. 处理回调状态
            if (rechargeRecord.getStatus() == 0 && "SUCCESS".equals(miPayCardNotifyResponse.getStatus()) && "CardOpen success".equals(miPayCardNotifyResponse.getStatusDesc())) {
                // 5.1 修改卡充值状态为成功
                LambdaUpdateWrapper<CardRechargeRecord> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(CardRechargeRecord::getCardId, miPayCardNotifyResponse.getCardId());
                updateWrapper.set(CardRechargeRecord::getStatus, RechargeRecordStatusEnum.SUCCESS.getKey());
                updateWrapper.set(CardRechargeRecord::getUpdateTime, LocalDateTime.now());
                updateWrapper.set(CardRechargeRecord::getActAmount, cardRechargeCallbackRecord.getActual());
                updateWrapper.set(CardRechargeRecord::getFinishTime, LocalDateTime.now());
                cardRechargeRecordService.update(updateWrapper);

                // 修改用户钱包余额
                LambdaUpdateWrapper<UserWalletBalance> balanceLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                balanceLambdaUpdateWrapper.eq(UserWalletBalance::getUserId, rechargeRecord.getUserId());
                balanceLambdaUpdateWrapper.setSql("balance = balance - " + cardRechargeCallbackRecord.getActual());
                balanceLambdaUpdateWrapper.set(UserWalletBalance::getUpdateTime, LocalDateTime.now());
                userWalletBalanceService.update(balanceLambdaUpdateWrapper);

                // 5.2 更新回调记录
                cardRechargeCallbackRecord.setUpdateTime(LocalDateTime.now());
                cardRechargeCallbackRecord.setStatus(miPayCardNotifyResponse.getStatus());
                cardRechargeCallbackRecord.setStatusDesc(miPayCardNotifyResponse.getStatusDesc());
                cardRechargeCallbackRecord.setFinishTime(LocalDateTime.now());
                this.updateById(cardRechargeCallbackRecord);

                return true;
            } else if ("FAILED".equals(miPayCardNotifyResponse.getStatus())) {
                // 5.3 处理失败状态 失败会重试
                LambdaUpdateWrapper<CardRechargeCallbackRecord> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(CardRechargeCallbackRecord::getNotifyId, cardRechargeCallbackRecord.getNotifyId());
                updateWrapper.setSql("retries = retries + 1");
                updateWrapper.set(CardRechargeCallbackRecord::getUpdateTime, LocalDateTime.now());
                this.update(updateWrapper);

                return false;
            }

            log.info("回调处理完成, 通知ID: {}", miPayCardNotifyResponse.getNotifyId());
            return true;
        } catch (Exception e) {
            log.error("卡开通回调处理失败, 通知ID: {}, 错误信息: {}", miPayCardNotifyResponse.getNotifyId(), e.getMessage(), e);
            throw new StarChainException("卡开通回调处理失败");
        }
    }
}
