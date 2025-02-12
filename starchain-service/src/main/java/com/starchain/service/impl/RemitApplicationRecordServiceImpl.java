package com.starchain.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.common.config.PacificPayConfig;
import com.starchain.common.constants.CardRemittanceUrlConstants;
import com.starchain.common.entity.*;
import com.starchain.common.entity.dto.RemitApplicationRecordDto;
import com.starchain.common.entity.dto.RemitRateDto;
import com.starchain.common.enums.MoneyKindEnum;
import com.starchain.common.enums.TransactionTypeEnum;
import com.starchain.common.exception.StarChainException;
import com.starchain.common.util.DateUtil;
import com.starchain.common.util.HttpUtils;
import com.starchain.common.util.OrderIdGenerator;
import com.starchain.dao.RemitApplicationRecordMapper;
import com.starchain.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @date 2025-01-07
 * @Description
 */
@Slf4j
@Service
public class RemitApplicationRecordServiceImpl extends ServiceImpl<RemitApplicationRecordMapper, RemitApplicationRecord> implements IRemitApplicationRecordService {


    @Autowired
    private IRemitCardService remitCardService;
    @Autowired
    private PacificPayConfig pacificPayConfig;
    @Autowired
    private IUserWalletBalanceService userWalletBalanceService;
    @Autowired
    private ICardFeeRuleService cardFeeRuleService;
    @Autowired
    private IUserWalletTransactionService userWalletTransactionService;

    /**
     * 申请汇款
     *
     * @param remitApplicationRecordDto
     * @return
     */
    @Override
    public Boolean applyRemit(RemitApplicationRecordDto remitApplicationRecordDto) {
        // 参数检查
        try {
            String token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            RemitRateDto remitRateDto = new RemitRateDto();
            remitRateDto.setRemitCode(remitApplicationRecordDto.getRemitCode()).setToMoneyKind(remitApplicationRecordDto.getToMoneyKind());
            // 获取实时汇率
            RemitRateDto remitRate = remitCardService.getRemitRate(token, remitRateDto);
            log.info("实时汇率，{}", remitRate);
            Assert.notNull(remitRate.getTradeRate(), "汇款汇率为null");
            String orderId = OrderIdGenerator.generateOrderId(String.valueOf(remitApplicationRecordDto.getBusinessId()), String.valueOf(remitApplicationRecordDto.getUserId()), 6);
            remitApplicationRecordDto
                    .setRemitCode(remitApplicationRecordDto.getRemitCode())
                    .setToMoneyKind(remitApplicationRecordDto.getToMoneyKind())
                    .setToAmount(remitApplicationRecordDto.getToAmount())
                    .setOrderId(orderId)
                    .setRemitRate(remitRate.getTradeRate());
            // 发送请求并获取响应

            String requestUrl = pacificPayConfig.getBaseUrl() + CardRemittanceUrlConstants.APPLY_REMIT;
            String requestBody = JSONObject.toJSONString(remitApplicationRecordDto);
            log.info("发送请求，URL：{}，请求体：{}", requestUrl, requestBody);
            String responseStr = HttpUtils.doPostMiPay(requestUrl, token, requestBody, pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("收到响应：{}", responseStr);
            // 记录入库
            RemitApplicationRecordDto remitApplicationRecordDtoResponse = JSONObject.parseObject(responseStr, RemitApplicationRecordDto.class);
            RemitApplicationRecord remitApplicationRecord = new RemitApplicationRecord();
            remitApplicationRecord.setRemitCode(remitApplicationRecordDto.getRemitCode());
            remitApplicationRecord.setToMoneyKind(remitApplicationRecordDto.getToMoneyKind());
            remitApplicationRecord.setToAmount(remitApplicationRecordDto.getToAmount());
            remitApplicationRecord.setOrderId(remitApplicationRecordDto.getOrderId());
            remitApplicationRecord.setRemitRate(remitApplicationRecordDto.getRemitRate());
            if (remitApplicationRecordDto.getExtraParams() != null) {
                remitApplicationRecord.setRemitName(remitApplicationRecordDto.getExtraParams().getString("remitName"));
                remitApplicationRecord.setRemitLastName(remitApplicationRecordDto.getExtraParams().getString("remitLastName"));
                remitApplicationRecord.setRemitFirstName(remitApplicationRecordDto.getExtraParams().getString("remitFirstName"));
                remitApplicationRecord.setRemitBankNo(remitApplicationRecordDto.getExtraParams().getString("remitBankNo"));
                remitApplicationRecord.setToMoneyCountry3(remitApplicationRecordDto.getExtraParams().getString("toMoneyCountry3"));
                remitApplicationRecord.setBankCode(remitApplicationRecordDto.getExtraParams().getString("bankCode"));
                remitApplicationRecord.setBankBranchCode(remitApplicationRecordDto.getExtraParams().getString("bankBranchCode"));
                remitApplicationRecord.setRemitTpyCardId(remitApplicationRecordDto.getExtraParams().getString("remitTpyCardId"));
                remitApplicationRecord.setMobileNumber(remitApplicationRecordDto.getExtraParams().getString("mobileNumber"));
                remitApplicationRecord.setEmail(remitApplicationRecordDto.getExtraParams().getString("email"));
            }
            remitApplicationRecord.setUserId(remitApplicationRecordDto.getUserId());
            remitApplicationRecord.setCreateTime(LocalDateTime.now());
            remitApplicationRecord.setUpdateTime(LocalDateTime.now());
            remitApplicationRecord.setBusinessId(remitApplicationRecordDto.getBusinessId());
            remitApplicationRecord.setStatus(0);

            // 更新remitApplicationRecordDtoResponse 返回的数据
            remitApplicationRecord.setRemitCode(remitApplicationRecordDtoResponse.getRemitCode());
            remitApplicationRecord.setTradeRate(remitApplicationRecordDtoResponse.getTradeRate());
            remitApplicationRecord.setFromMoneyKind(remitApplicationRecordDtoResponse.getFromMoneyKind());
            remitApplicationRecord.setFromAmount(remitApplicationRecordDtoResponse.getFromAmount());
            remitApplicationRecord.setToMoneyKind(remitApplicationRecordDtoResponse.getToMoneyKind());
            remitApplicationRecord.setToAmount(remitApplicationRecordDtoResponse.getToAmount());
            remitApplicationRecord.setHandlingFeeAmount(remitApplicationRecordDtoResponse.getHandlingFeeAmount());
            remitApplicationRecord.setHandlingFeeMoneyKind(remitApplicationRecordDtoResponse.getHandlingFeeMoneyKind());
            remitApplicationRecord.setOrderId(remitApplicationRecordDtoResponse.getOrderId());
            remitApplicationRecord.setTradeId(remitApplicationRecordDtoResponse.getTradeId());
            remitApplicationRecord.setPinNumber(remitApplicationRecordDtoResponse.getPinNumber());
            this.save(remitApplicationRecord);

            // 汇款金额流水
            UserWalletBalance userWalletBalance = userWalletBalanceService.getUserWalletBalance(remitApplicationRecord.getUserId(), remitApplicationRecord.getBusinessId());

            // 查询卡费规则配置表
            CardFeeRule cardFeeRule = cardFeeRuleService.getCardFeeRule(remitApplicationRecord.getRemitCode());
            if (remitApplicationRecordDto.getToMoneyKind().equals(MoneyKindEnum.CNY.getMoneyKindCode())) {
                BigDecimal cnyAmount = remitApplicationRecordDto.getToAmount();
                // 换算成美元
                BigDecimal usdAmount = cnyAmount.multiply(remitRate.getTradeRate()).setScale(2, RoundingMode.HALF_UP);
                // 计算手续费
                BigDecimal remitFeeAmount = usdAmount.multiply(cardFeeRule.getRemitFeeRate()).add(cardFeeRule.getRemitFeeAmount());
                // 生成流水
                createApplyRemitTransaction(remitApplicationRecord.getUserId(), userWalletBalance.getAvaBalance(), usdAmount, remitFeeAmount, remitApplicationRecord);
            }

            // 金额冻结
            BigDecimal totalFreezeAmount = remitApplicationRecord.getFromAmount().add(remitApplicationRecord.getHandlingFeeAmount());
            // 扣减可用余额 金额预冻结
            return userWalletBalanceService.updateWalletBalance(userWalletBalance, totalFreezeAmount);
        } catch (Exception e) {
            log.error("申请汇款失败", e);
            throw new StarChainException("申请汇款失败");
        }
    }

    @Override
    public boolean isRemitInProgress(Long userId, Long channelId) {
        LambdaQueryWrapper<RemitApplicationRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RemitApplicationRecord::getUserId, userId);
        queryWrapper.eq(RemitApplicationRecord::getBusinessId, channelId);
        queryWrapper.orderByDesc(RemitApplicationRecord::getId).last("LIMIT 1");
        RemitApplicationRecord remitApplicationRecord = this.getOne(queryWrapper);
        return remitApplicationRecord != null && remitApplicationRecord.getStatus() == 0;
    }

    /**
     * 创建汇款流水 汇款金额和汇款手续费两条流水
     *
     * @param userId
     * @param avaBalance
     * @param cardFee
     * @param
     */
    private void createApplyRemitTransaction(Long userId, BigDecimal avaBalance, BigDecimal remitFeeAmount, BigDecimal cardFee, RemitApplicationRecord remitApplicationRecord) {
        List<UserWalletTransaction> transactions = new ArrayList<>();
        BigDecimal currentBalance = avaBalance;

        if (remitApplicationRecord.getHandlingFeeAmount().compareTo(cardFee) != 0) {
            log.warn("手续费不匹配，预期: {}, 实际: {}", cardFee, remitApplicationRecord.getHandlingFeeAmount());
        }

        if (remitApplicationRecord.getFromAmount().compareTo(remitFeeAmount) != 0) {
            log.warn("汇款金额不一致，预期: {}, 实际: {}", cardFee, remitApplicationRecord.getHandlingFeeAmount());
        }

        // 充值金额交易流水落库
        UserWalletTransaction rechargeTransaction = createUserWalletTransaction(userId, currentBalance, remitApplicationRecord.getFromAmount(), TransactionTypeEnum.GLOBAL_REMITTANCE_FEE, remitApplicationRecord.getBankCode(), remitApplicationRecord.getOrderId(), remitApplicationRecord.getTradeId());
        transactions.add(rechargeTransaction);
        currentBalance = currentBalance.subtract(remitApplicationRecord.getFromAmount());

        // 手续费交易流水落库
        UserWalletTransaction feeTransaction = createUserWalletTransaction(userId, currentBalance, cardFee, TransactionTypeEnum.REMIT_FEE, remitApplicationRecord.getBankCode(), remitApplicationRecord.getOrderId(), remitApplicationRecord.getTradeId());
        transactions.add(feeTransaction);

        // 保存所有交易记录
        userWalletTransactionService.saveBatch(transactions);
    }

    private UserWalletTransaction createUserWalletTransaction(Long userId, BigDecimal balanceBefore, BigDecimal amount, TransactionTypeEnum type, String businessNumber, String orderId, String tradeId) {
        return UserWalletTransaction.builder()
                .userId(userId)
                .coinName(MoneyKindEnum.USD.getMoneyKindCode())
                .balance(balanceBefore)
                .amount(amount.negate()) // 由于是汇款，所以使用负数表示
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

//    private void updateWalletBalance(UserWalletBalance wallet, BigDecimal totalFreezeAmount) {
//        wallet.setAvaBalance(wallet.getAvaBalance().subtract(totalFreezeAmount));
//        wallet.setFreezeBalance(wallet.getFreezeBalance().add(totalFreezeAmount));
//        userWalletBalanceService.updateById(wallet);
//    }
}
