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
            RemitApplicationRecord remitApplicationRecord = JSONObject.parseObject(responseStr, RemitApplicationRecord.class);
            remitApplicationRecord.setUserId(remitApplicationRecordDto.getUserId());
            remitApplicationRecord.setCreateTime(LocalDateTime.now());
            remitApplicationRecord.setUpdateTime(LocalDateTime.now());
            remitApplicationRecord.setRemitRate(remitRate.getTradeRate());
            remitApplicationRecord.setChannelId(remitApplicationRecordDto.getChannelId());
            remitApplicationRecord.setStatus(0);
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
