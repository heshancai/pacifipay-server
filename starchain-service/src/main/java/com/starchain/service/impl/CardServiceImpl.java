package com.starchain.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.common.config.PacificPayConfig;
import com.starchain.common.constants.CardUrlConstants;
import com.starchain.common.entity.*;
import com.starchain.common.entity.dto.CardDto;
import com.starchain.common.enums.CardStatusEnum;
import com.starchain.common.enums.MoneyKindEnum;
import com.starchain.common.enums.TransactionTypeEnum;
import com.starchain.common.exception.StarChainException;
import com.starchain.common.util.DateUtil;
import com.starchain.common.util.HttpUtils;
import com.starchain.dao.CardMapper;
import com.starchain.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @date 2024-12-20
 * @Description
 */
@Slf4j
@Service("cardServiceImpl") // 注意这里的 Bean 名称与 MiPayNotifyType 中的 serviceName 一致
public class CardServiceImpl extends ServiceImpl<CardMapper, Card> implements ICardService {

    @Autowired
    private PacificPayConfig pacificPayConfig;
    @Autowired
    private IUserWalletBalanceService userWalletBalanceService;
    @Autowired
    private ICardFeeRuleService cardFeeRuleService;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private ICardRechargeRecordService cardRechargeRecordService;
    @Autowired
    private ICardCancelRecordService cardCancelRecordService;
    @Autowired
    private IUserWalletTransactionService userWalletTransactionService;

    /**
     * 查询商户余额
     *
     * @return
     */
    public JSONObject mchInfo() {
        String token = null;
        try {
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.MCH_INFO, token, "", pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
        System.out.println("返回的数据：" + str);
        return JSON.parseObject(str);
    }

    @Override
    public Integer checkCardNum(Long channelId, String cardCode, String tpyshCardHolderId) {
        LambdaQueryWrapper<Card> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Card::getCardCode, cardCode).eq(Card::getTpyshCardHolderId, tpyshCardHolderId);
        return this.count(lambdaQueryWrapper);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Card addCard(CardDto cardDto) {
        String token;
        try {
            // 获取最新的卡费规则
            LambdaQueryWrapper<CardFeeRule> cardFeeRuleWrapper = new LambdaQueryWrapper<>();
            cardFeeRuleWrapper.eq(CardFeeRule::getCardCode, cardDto.getCardCode());
            CardFeeRule cardFeeRule = cardFeeRuleService.getOne(cardFeeRuleWrapper);
            if (cardFeeRule == null) {
                throw new StarChainException("未找到有效的开卡费用规则");
            }

            // 获取token
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());

            // 构建发送参数
            CardDto cardDto1 = prepareCardDto(cardDto, cardFeeRule);

            String responseStr = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.ADD_CARD, token, JSONObject.toJSONString(cardDto1), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("返回的数据：{}", responseStr);
            Card returnCard = JSON.parseObject(responseStr, Card.class);
            if (CardStatusEnum.ACTIVATING.getCardStatus().equals(returnCard.getCardStatus())) {
                returnCard.setCreateStatus(1);
            }
            returnCard.setLocalCreateTime(LocalDateTime.now());
            returnCard.setLocalUpdateTime(LocalDateTime.now());
            returnCard.setUserId(cardDto.getUserId());
            this.save(returnCard);

            BigDecimal totalFreezeAmount = calculateTotalFreezeAmount(cardFeeRule);
            UserWalletBalance wallet = userWalletBalanceService.getById(cardDto.getUserId());

            // 更新用户钱包，冻结相应金额
            updateWalletBalance(wallet, totalFreezeAmount);

            // 记录预交易流水
            createPreTransactionRecords(cardDto.getUserId(), wallet.getAvaBalance(), returnCard, cardFeeRule);

            log.info("发起创建卡成功:{}", returnCard);
            return returnCard;
        } catch (Exception e) {
            log.error("服务异常", e);
            throw new RuntimeException(e);
        }
    }

    private CardDto prepareCardDto(CardDto originalCardDto, CardFeeRule cardFeeRule) {
        CardDto cardDto1 = new CardDto();
        cardDto1.setCardCode(originalCardDto.getCardCode());
        cardDto1.setSaveOrderId(String.valueOf(idWorker.nextId()));
        cardDto1.setTpyshCardHolderId(originalCardDto.getTpyshCardHolderId());
        cardDto1.setSaveAmount(cardFeeRule.getSaveAmount());
        return cardDto1;
    }

    private BigDecimal calculateTotalFreezeAmount(CardFeeRule cardFeeRule) {
        return cardFeeRule.getCardFee()
                .add(cardFeeRule.getSaveAmount())
                .add(cardFeeRule.getMonthlyFee());
    }

    private void updateWalletBalance(UserWalletBalance wallet, BigDecimal totalFreezeAmount) {
        wallet.setAvaBalance(wallet.getAvaBalance().subtract(totalFreezeAmount));
        wallet.setFreezeBalance(wallet.getFreezeBalance().add(totalFreezeAmount));
        userWalletBalanceService.updateById(wallet);
    }

    private void createPreTransactionRecords(Long userId, BigDecimal initialBalance, Card returnCard, CardFeeRule cardFeeRule) {
        List<UserWalletTransaction> transactions = new ArrayList<>();
        BigDecimal currentBalance = initialBalance;

        // 开卡费
        BigDecimal cardOpenFee = returnCard.getCardFee() != null ? returnCard.getCardFee() : cardFeeRule.getCardFee();
        if (cardFeeRule.getCardFee().compareTo(cardOpenFee) != 0) {
            log.warn("开卡手续费不匹配，预期: {}, 实际: {}", cardFeeRule.getCardFee(), cardOpenFee);
        }
        currentBalance = addTransaction(transactions, userId, MoneyKindEnum.USD.getMoneyKindCode(), currentBalance, cardOpenFee, TransactionTypeEnum.CARD_OPEN_FEE, returnCard.getCardId(), returnCard.getSaveOrderId());
        // 月服务费
        BigDecimal monthlyFee = cardFeeRule.getMonthlyFee();
        currentBalance = addTransaction(transactions, userId, MoneyKindEnum.USD.getMoneyKindCode(), currentBalance, monthlyFee, TransactionTypeEnum.CARD_MONTHLY_SERVICE_FEE, returnCard.getCardId(), returnCard.getSaveOrderId());

        // 预存费
        BigDecimal preStoreAmount = returnCard.getSaveAmount() != null ? returnCard.getSaveAmount() : cardFeeRule.getSaveAmount();
        if (cardFeeRule.getSaveAmount().compareTo(preStoreAmount) != 0) {
            log.warn("预存费用不匹配，预期: {}, 实际: {}", cardFeeRule.getSaveAmount(), preStoreAmount);
        }
        currentBalance = addTransaction(transactions, userId, MoneyKindEnum.USD.getMoneyKindCode(), currentBalance, preStoreAmount, TransactionTypeEnum.CARD_OPEN_DEPOSIT, returnCard.getCardId(), returnCard.getSaveOrderId());

        userWalletTransactionService.saveBatch(transactions);
    }

    private BigDecimal addTransaction(List<UserWalletTransaction> transactions, Long userId, String coinName, BigDecimal balance, BigDecimal amount, TransactionTypeEnum type, String businessNumber, String orderId) {
        UserWalletTransaction transaction = UserWalletTransaction.builder()
                .userId(userId)
                .coinName(coinName)
                .balance(balance)
                .amount(amount.negate())
                .finaBalance(balance.subtract(amount))
                .type(type.getCode())
                .businessNumber(businessNumber)
                .createTime(LocalDateTime.now())
                .partitionKey(DateUtil.getMonth())
                .remark(type.getDescription())
                .orderId(orderId)
                .build();
        transactions.add(transaction);
        return balance.subtract(amount);
    }


    /**
     * 申请销卡
     *
     * @param
     * @param
     * @return
     */
    @Override
    public Boolean deleteCard(CardDto cardDto) {
        try {
            // 获取第三方支付平台 token
            String token = HttpUtils.getTokenByMiPay(
                    pacificPayConfig.getBaseUrl(),
                    pacificPayConfig.getId(),
                    pacificPayConfig.getSecret(),
                    pacificPayConfig.getPrivateKey()
            );

            // 发送销卡请求
            String responseStr = HttpUtils.doPostMiPay(
                    pacificPayConfig.getBaseUrl() + CardUrlConstants.DELETE_CARD,
                    token,
                    JSON.toJSONString(cardDto),
                    pacificPayConfig.getId(),
                    pacificPayConfig.getServerPublicKey(),
                    pacificPayConfig.getPrivateKey()
            );

            log.info("销卡返回数据：{}", responseStr);

            // 解析返回 JSON
            JSONObject responseJson = JSON.parseObject(responseStr);
            String cardId = responseJson.getString("cardId");
            String cardCode = responseJson.getString("cardCode");

            // 记录销卡申请状态
            CardCancelRecord record = new CardCancelRecord();
            record.setCardId(cardId);
            record.setCardCode(cardCode);
            record.setUserId(cardDto.getUserId());
            record.setBusinessId(cardDto.getBusinessId());
            record.setCreateStatus(0); // 0 代表创建中
            record.setCreateTime(LocalDateTime.now());
            record.setUpdateTime(LocalDateTime.now());

            return cardCancelRecordService.save(record);
        } catch (Exception e) {
            log.error("申请销卡失败", e);
            return false;
        }
    }

    /**
     * 申请换卡 暂时没用
     *
     * @param card
     * @return
     */
    @Override
    public Card changeCard(Card card) {
        String token = null;
        try {
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.DELETE_CARD, token, JSONObject.toJSONString(card), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("返回的数据：{}", str);
            CardDto returnCard = JSON.parseObject(str, CardDto.class);
        } catch (Exception e) {
            log.error("服务异常", e);
        }
        return null;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public CardRechargeRecord applyRecharge(CardDto cardDto) throws StarChainException {
        try {
            // 获取最新的卡费规则
            LambdaQueryWrapper<CardFeeRule> cardFeeRuleWrapper = new LambdaQueryWrapper<>();
            cardFeeRuleWrapper.eq(CardFeeRule::getCardCode, cardDto.getCardCode());
            CardFeeRule cardFeeRule = cardFeeRuleService.getOne(cardFeeRuleWrapper);
            if (cardFeeRule == null) {
                throw new StarChainException("未找到有效的开卡费用规则");
            }

            // 封装传递参数
            cardDto.setOrderId(String.valueOf(idWorker.nextId()));

            String token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            String responseStr = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.APPLY_RECHARGE, token, JSONObject.toJSONString(cardDto), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("返回的数据：{}", responseStr);

            CardRechargeRecord cardRechargeRecord = JSON.parseObject(responseStr, CardRechargeRecord.class);
            cardRechargeRecord.setStatus(0);
            cardRechargeRecord.setUserId(cardDto.getUserId());
            cardRechargeRecord.setTpyshCardHolderId(cardDto.getTpyshCardHolderId());
            cardRechargeRecord.setBusinessId(cardDto.getBusinessId());
            cardRechargeRecord.setCreateTime(LocalDateTime.now());
            cardRechargeRecord.setUpdateTime(LocalDateTime.now());

            // 处理成功 等待回调结果
            cardRechargeRecordService.save(cardRechargeRecord);
            // 手续费
            BigDecimal cardFee = cardDto.getSaveAmount().multiply(cardFeeRule.getRechargeFeeRate());
            BigDecimal totalFreezeAmount = cardDto.getSaveAmount().add(cardFee);
            UserWalletBalance wallet = userWalletBalanceService.getById(cardDto.getUserId());

            // 更新用户钱包，冻结相应金额
            updateWalletBalance(wallet, totalFreezeAmount);

            // 记录预交易流水
            createPreApplyRechargeTransaction(cardDto.getUserId(), wallet.getAvaBalance(), cardFee,cardRechargeRecord);

            return cardRechargeRecord;
        } catch (Exception e) {
            log.error("服务异常", e);
            throw new StarChainException("卡充值过程中发生错误: " + e.getMessage());
        }
    }

    private void createPreApplyRechargeTransaction(Long userId, BigDecimal avaBalance, BigDecimal cardFee, CardRechargeRecord cardRechargeRecord) {
        List<UserWalletTransaction> transactions = new ArrayList<>();
        BigDecimal currentBalance = avaBalance;

        if (cardRechargeRecord.getOrderFee().compareTo(cardFee) != 0) {
            log.warn("手续费不匹配，预期: {}, 实际: {}", cardFee, cardRechargeRecord.getOrderFee());
        }

        // 创建充值金额交易记录
        UserWalletTransaction rechargeTransaction = createUserWalletTransaction(userId, currentBalance, cardRechargeRecord.getOrderAmount(), TransactionTypeEnum.BALANCE_RECHARGE_TO_CARD, cardRechargeRecord.getCardId(), cardRechargeRecord.getOrderId(),cardRechargeRecord.getTradeId());
        transactions.add(rechargeTransaction);
        currentBalance = currentBalance.subtract(cardRechargeRecord.getOrderAmount());

        // 创建手续费交易记录
        UserWalletTransaction feeTransaction = createUserWalletTransaction(userId, currentBalance, cardRechargeRecord.getOrderFee(), TransactionTypeEnum.CARD_RECHARGE_FEE, cardRechargeRecord.getCardId(), cardRechargeRecord.getOrderId(),cardRechargeRecord.getTradeId());
        transactions.add(feeTransaction);

        // 保存所有交易记录
        userWalletTransactionService.saveBatch(transactions);
    }

    private UserWalletTransaction createUserWalletTransaction(Long userId, BigDecimal balanceBefore, BigDecimal amount, TransactionTypeEnum type, String businessNumber, String orderId,String tradeId) {
        return UserWalletTransaction.builder()
                .userId(userId)
                .coinName(MoneyKindEnum.USD.getMoneyKindCode())
                .balance(balanceBefore)
                .amount(amount.negate()) // 由于是扣款，所以使用负数表示
                .finaBalance(balanceBefore.subtract(amount))
                .type(type.getCode())
                .businessNumber(businessNumber) // 确保业务编号为字符串类型
                .createTime(LocalDateTime.now())
                .partitionKey(DateUtil.getMonth())
                .remark(type.getDescription())
                .tradeId(tradeId)
                .orderId(orderId)
                .build();
    }


    // 锁卡
    @Override
    public Boolean lockCard(Card card) {
        String token = null;
        try {
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.LOCK_CARD, token, JSONObject.toJSONString(card), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("返回的数据：{}", str);
            Card returnCard = JSON.parseObject(str, Card.class);
            if (returnCard != null) {
                LambdaUpdateWrapper<Card> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.eq(Card::getCardId, returnCard.getCardId());
                lambdaUpdateWrapper.eq(Card::getMerchantId, returnCard.getMerchantId());
                lambdaUpdateWrapper.eq(Card::getCardCode, returnCard.getCardCode());
                lambdaUpdateWrapper.set(Card::getCardStatus, CardStatusEnum.FREEZING.getCardStatus());
                return this.update(lambdaUpdateWrapper);
            }
            return false;
        } catch (Exception e) {
            log.error("服务异常", e);
            return false;
        }
    }

    @Override
    public Boolean unlockCard(Card card) {
        String token = null;
        try {
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.UNLOCK_CARD, token, JSONObject.toJSONString(card), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("返回的数据：{}", str);
            Card returnCard = JSON.parseObject(str, Card.class);
            if (returnCard != null) {
                LambdaUpdateWrapper<Card> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.eq(Card::getCardId, returnCard.getCardId());
                lambdaUpdateWrapper.eq(Card::getMerchantId, returnCard.getMerchantId());
                lambdaUpdateWrapper.eq(Card::getCardCode, returnCard.getCardCode());
                lambdaUpdateWrapper.set(Card::getCardStatus, CardStatusEnum.UNACTIVATED.getCardStatus());
                return this.update(lambdaUpdateWrapper);
            }
        } catch (Exception e) {
            log.error("服务异常", e);
            return false;
        }
        return false;
    }

    @Override
    public Boolean updateLimit(CardDto cardDto) {
        String token = null;
        cardDto.setSingleLimit(cardDto.getSingleLimit().setScale(2, RoundingMode.HALF_UP));
        try {
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.UPDATE_LIMIT, token, JSONObject.toJSONString(cardDto), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("返回的数据：{}", str);
            Card returnCard = JSON.parseObject(str, Card.class);
            if (returnCard != null) {
                LambdaUpdateWrapper<Card> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.eq(Card::getCardId, returnCard.getCardId());
                lambdaUpdateWrapper.eq(Card::getMerchantId, returnCard.getMerchantId());
                lambdaUpdateWrapper.eq(Card::getCardCode, returnCard.getCardCode());
                lambdaUpdateWrapper.set(Card::getSingleLimit, cardDto.getSingleLimit());
                return this.update(lambdaUpdateWrapper);
            }
        } catch (Exception e) {
            log.error("服务异常", e);
            return false;
        }
        return false;
    }

    @Override
    public boolean isRechargeInProgress(CardDto cardDto) {
        LambdaQueryWrapper<CardRechargeRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CardRechargeRecord::getCardId, cardDto.getCardId());
        lambdaQueryWrapper.eq(CardRechargeRecord::getUserId, cardDto.getUserId());
        lambdaQueryWrapper.eq(CardRechargeRecord::getBusinessId, cardDto.getBusinessId());
        lambdaQueryWrapper.orderByDesc(CardRechargeRecord::getId).last("LIMIT 1");
        CardRechargeRecord cardRechargeRecord = cardRechargeRecordService.getOne(lambdaQueryWrapper);
        return cardRechargeRecord != null && cardRechargeRecord.getStatus() == 0;
    }

    @Override
    public boolean cardExists(String cardId, String cardCode) {
        return false;
    }
}
