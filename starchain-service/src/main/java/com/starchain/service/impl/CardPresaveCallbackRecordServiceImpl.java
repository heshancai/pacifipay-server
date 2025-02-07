package com.starchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.common.entity.*;
import com.starchain.common.entity.response.MiPayCardNotifyResponse;
import com.starchain.common.enums.*;
import com.starchain.common.exception.StarChainException;
import com.starchain.common.util.DateUtil;
import com.starchain.dao.CardPresaveCallbackRecordMapper;
import com.starchain.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @date 2025-01-06
 * @Description 卡预存通知： 创建卡同时会进行预存 并且进行回调
 */
@Service
@Slf4j
public class CardPresaveCallbackRecordServiceImpl extends ServiceImpl<CardPresaveCallbackRecordMapper, CardPresaveCallbackRecord> implements ICardPresaveCallbackRecordService {
    @Autowired
    private ICardService cardService;
    @Autowired
    private ICardFeeRuleService cardFeeRuleService;
    @Autowired
    private IUserWalletBalanceService userWalletBalanceService;
    @Autowired
    private IUserWalletTransactionService userWalletTransactionService;

    @Override
    public Boolean callBack(String callBackJson) {

        MiPayCardNotifyResponse miPayCardNotifyResponse = this.covertToMiPayCardNotifyResponse(callBackJson);
        try {
            // 1. 校验业务类型
            validateBusinessType(miPayCardNotifyResponse);

            // 2. 核实卡开通记录是否存在 卡开通成功才会处理预存
            Card card = validateAndGetRechargeRecord(miPayCardNotifyResponse);


            LambdaQueryWrapper<CardPresaveCallbackRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CardPresaveCallbackRecord::getNotifyId, miPayCardNotifyResponse.getNotifyId());
            CardPresaveCallbackRecord callbackRecord = this.getOne(queryWrapper);
            // 3. 检查是否已经处理成功（幂等性）
            if (callbackRecord != null && CardStatusDescEnum.SUCCESS.getDescription().equals(callbackRecord.getStatusDesc())) {
                log.info("卡预存回调记录已处理成功，无需重复处理，通知ID: {}", miPayCardNotifyResponse.getNotifyId());
                return true;
            }
            // 4. 校验卡开预存费用是否一致
            validateRechargeAmount(miPayCardNotifyResponse, card);


            // 5. 查询或创建回调记录 如果成功则扣除钱包的冻结余额
            CardPresaveCallbackRecord cardPresaveCallbackRecord = createOrUpdateCallbackRecord(miPayCardNotifyResponse);

            LambdaQueryWrapper<CardFeeRule> cardFeeRuleLambdaQueryWrapper = new LambdaQueryWrapper<>();
            cardFeeRuleLambdaQueryWrapper.eq(CardFeeRule::getCardCode, miPayCardNotifyResponse.getCardCode());
            CardFeeRule cardFeeRule = cardFeeRuleService.getOne(cardFeeRuleLambdaQueryWrapper);

            // 6. 处理开卡预存
            switch (miPayCardNotifyResponse.getStatus()) {
                case "SUCCESS":
                    if (card.getCardStatus().equals(CardStatusEnum.ACTIVATING.getCardStatus()) && card.getCreateStatus() == 0) {
                        // 扣除冻结的余额-扣除开卡费-扣除首次开卡月服务费
                        LambdaUpdateWrapper<UserWalletBalance> userWalletBalanceUpdateWrapper = new LambdaUpdateWrapper<>();
                        userWalletBalanceUpdateWrapper.setSql("freeze_balance = freeze_balance - " + cardPresaveCallbackRecord.getActual());
                        userWalletBalanceUpdateWrapper.eq(UserWalletBalance::getUserId, card.getUserId());
                        userWalletBalanceService.update(userWalletBalanceUpdateWrapper);
                        log.info("卡状态更新为开通成功, 卡ID: {}", card.getCardId());
                        return true;
                    }
                    break;
                case "FAILED":
                    LambdaQueryWrapper<UserWalletBalance> userWalletBalanceLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    userWalletBalanceLambdaQueryWrapper.eq(UserWalletBalance::getUserId, card.getUserId());
                    UserWalletBalance userWalletBalance = userWalletBalanceService.getOne(userWalletBalanceLambdaQueryWrapper);
                    List<UserWalletTransaction> transactions = new ArrayList<>();
                    BigDecimal currentBalance = userWalletBalance.getAvaBalance();
                    // 开卡费回退流水
                    currentBalance = addTransaction(transactions, card.getUserId(), MoneyKindEnum.USD.getMoneyKindCode(), currentBalance, cardPresaveCallbackRecord.getActual(), TransactionTypeEnum.CARD_OPEN_FEE, card.getCardId(), card.getSaveOrderId());
                    userWalletTransactionService.saveBatch(transactions);
                    // 金额回滚
                    LambdaUpdateWrapper<UserWalletBalance> userWalletBalanceUpdateWrapper = new LambdaUpdateWrapper<>();
                    userWalletBalanceUpdateWrapper.setSql("freeze_balance = freeze_balance - " + cardPresaveCallbackRecord.getActual());
                    userWalletBalanceUpdateWrapper.setSql("ava_balance = ava_balance +" + cardPresaveCallbackRecord.getActual());
                    userWalletBalanceUpdateWrapper.eq(UserWalletBalance::getUserId, card.getUserId());
                    userWalletBalanceService.update(userWalletBalanceUpdateWrapper);
                    // 创建失败 重新发起
                    log.warn("卡预存失败, 通知ID: {}, 失败原因: {}", cardPresaveCallbackRecord.getNotifyId(), miPayCardNotifyResponse.getStatusDesc());
                    return true;
                default:
                    log.debug("未知状态: {}", miPayCardNotifyResponse.getStatus());
                    break;
            }

            log.info("回调处理完成, 通知ID: {}", miPayCardNotifyResponse.getNotifyId());
            return true;
        } catch (Exception e) {
            log.error("卡预存回调处理失败, 通知ID: {}, 错误信息: {}", miPayCardNotifyResponse.getNotifyId(), e.getMessage(), e);
            throw new StarChainException("卡预存回调处理失败");
        }
    }

    private BigDecimal addTransaction(List<UserWalletTransaction> transactions, Long userId, String coinName, BigDecimal balance, BigDecimal amount, TransactionTypeEnum type, String businessNumber, String orderId) {
        UserWalletTransaction transaction = UserWalletTransaction.builder()
                .userId(userId)
                .coinName(coinName)
                .balance(balance)
                .amount(amount)
                .finaBalance(balance.add(amount))
                .type(type.getCode())
                .businessNumber(businessNumber)
                .createTime(LocalDateTime.now())
                .partitionKey(DateUtil.getMonth())
                .remark(type.getDescription())
                .orderId(orderId)
                .build();
        transactions.add(transaction);
        return balance.add(amount);
    }

    // 1. 校验业务类型
    private void validateBusinessType(MiPayCardNotifyResponse response) {
        Assert.isTrue(
                MiPayNotifyType.Presave.getType().equals(response.getBusinessType()),
                "回调业务类型与接口调用不匹配"
        );
        log.info("回调业务类型校验通过: {}", response.getBusinessType());
    }

    // 2. 核实卡记录是否存在
    private Card validateAndGetRechargeRecord(MiPayCardNotifyResponse response) {
        LambdaQueryWrapper<Card> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Card::getCardCode, response.getCardCode())
                .eq(Card::getCardId, response.getCardId())
                .eq(Card::getSaveOrderId, response.getMchOrderId());
        Card record = cardService.getOne(queryWrapper);
        Assert.notNull(record, "核实卡充值记录不存在");
        Assert.isTrue(record.getCreateStatus().equals(CreateStatusEnum.SUCCESS.getCode()), "卡未创建完成,忽略预存回调信息");
        log.info("卡信息校验通过, 卡ID: {}", response.getCardId());
        return record;
    }

    // 4. 校验卡预存费用
    private void validateRechargeAmount(MiPayCardNotifyResponse response, Card record) {
        BigDecimal actual = (BigDecimal) response.getAmount().get("actual");
        Assert.isTrue(record.getSaveAmount().compareTo(actual) == 0, "预存款实际扣除金额不一致");
        log.info("卡预存费校验通过, 实际手续费: {}", actual);
    }

    // 5. 查询或创建回调记录
    private CardPresaveCallbackRecord createOrUpdateCallbackRecord(MiPayCardNotifyResponse response) {
        LambdaQueryWrapper<CardPresaveCallbackRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CardPresaveCallbackRecord::getNotifyId, response.getNotifyId())
                .eq(CardPresaveCallbackRecord::getCardCode, response.getCardCode())
                .eq(CardPresaveCallbackRecord::getBusinessType, response.getBusinessType())
                .eq(CardPresaveCallbackRecord::getCardId, response.getCardId());

        CardPresaveCallbackRecord callbackRecord = this.getOne(queryWrapper);
        if (callbackRecord == null) {
            callbackRecord = new CardPresaveCallbackRecord();
            callbackRecord.setNotifyId(response.getNotifyId());
            callbackRecord.setCardCode(response.getCardCode());
            callbackRecord.setBusinessType(response.getBusinessType());
            callbackRecord.setCardId(response.getCardId());
            callbackRecord.setCardNo(response.getCardNo());
            callbackRecord.setMchOrderId(response.getMchOrderId());
            callbackRecord.setActual((BigDecimal) response.getAmount().get("actual"));
            callbackRecord.setStatus(response.getStatus());
            callbackRecord.setStatusDesc(response.getStatusDesc());
            callbackRecord.setCreateTime(LocalDateTime.now());
            callbackRecord.setUpdateTime(LocalDateTime.now());
            this.save(callbackRecord);
            log.info("创建新的回调记录, 通知ID: {}", response.getNotifyId());
        } else {
            log.info("回调记录已存在, 处理重复通知 ，通知ID: {}", response.getNotifyId());
        }
        return callbackRecord;
    }

}