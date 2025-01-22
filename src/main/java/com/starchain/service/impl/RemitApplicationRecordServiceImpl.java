package com.starchain.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.config.PacificPayConfig;
import com.starchain.constants.CardRemittanceUrlConstants;
import com.starchain.dao.RemitApplicationRecordMapper;
import com.starchain.entity.RemitApplicationRecord;
import com.starchain.entity.dto.RemitApplicationRecordDto;
import com.starchain.entity.dto.RemitRateDto;
import com.starchain.exception.StarChainException;
import com.starchain.service.IRemitApplicationRecordService;
import com.starchain.service.IRemitCardService;
import com.starchain.util.HttpUtils;
import com.starchain.util.OrderIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

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


    /**
     * 申请汇款
     *
     * @param remitApplicationRecordDto
     * @return
     */
    @Override
    public Boolean applyRemit(RemitApplicationRecordDto remitApplicationRecordDto) {
        try {
            String token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            RemitRateDto remitRateDto = new RemitRateDto();
            remitRateDto.setRemitCode(remitApplicationRecordDto.getRemitCode()).setToMoneyKind(remitApplicationRecordDto.getToMoneyKind());
            // 获取实时汇率
            RemitRateDto remitRate = remitCardService.getRemitRate(token, remitRateDto);
            log.info("实时汇率，{}", remitRate);
            Assert.notNull(remitRate.getTradeRate(), "汇款汇率为null");
            String orderId = OrderIdGenerator.generateOrderId(String.valueOf(remitApplicationRecordDto.getChannelId()), String.valueOf(remitApplicationRecordDto.getUserId()), 6);
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
            RemitApplicationRecordDto remitApplicationRecordDtoResponse = JSONObject.parseObject(responseStr, RemitApplicationRecordDto.class);
            RemitApplicationRecord remitApplicationRecord = new RemitApplicationRecord();
            remitApplicationRecord.setRemitCode(remitApplicationRecordDto.getRemitCode());
            remitApplicationRecord.setToMoneyKind(remitApplicationRecordDto.getToMoneyKind());
            remitApplicationRecord.setToAmount(remitApplicationRecordDto.getToAmount());
            remitApplicationRecord.setOrderId(remitApplicationRecordDto.getOrderId());
            remitApplicationRecord.setRemitRate(remitApplicationRecordDto.getRemitRate());
            if (remitApplicationRecordDto.getExtraParams()!=null){
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
            remitApplicationRecord.setChannelId(remitApplicationRecordDto.getChannelId());
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
        } catch (Exception e) {
            log.error("申请汇款失败", e);
            throw new StarChainException("申请汇款失败");
        }
        return null;
    }

    @Override
    public boolean isRemitInProgress(Long userId, Long channelId) {
        LambdaQueryWrapper<RemitApplicationRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RemitApplicationRecord::getUserId, userId);
        queryWrapper.eq(RemitApplicationRecord::getChannelId, channelId);
        queryWrapper.orderByDesc(RemitApplicationRecord::getId).last("LIMIT 1");
        RemitApplicationRecord remitApplicationRecord = this.getOne(queryWrapper);
        return remitApplicationRecord != null && remitApplicationRecord.getStatus() == 0;
    }
}
