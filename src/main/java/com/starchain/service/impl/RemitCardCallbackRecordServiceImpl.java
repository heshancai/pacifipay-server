package com.starchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.context.MiPayNotifyType;
import com.starchain.dao.RemitCardCallbackRecordMapper;
import com.starchain.entity.RemitCard;
import com.starchain.entity.RemitCardCallbackRecord;
import com.starchain.entity.response.MiPayRemitNotifyResponse;
import com.starchain.enums.CardStatusDescEnum;
import com.starchain.enums.CreateStatusEnum;
import com.starchain.exception.StarChainException;
import com.starchain.service.IRemitCardCallbackRecordService;
import com.starchain.service.IRemitCardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-06
 * @Description 申请汇款卡审核通知 处理类
 */
@Service
@Slf4j
public class RemitCardCallbackRecordServiceImpl extends ServiceImpl<RemitCardCallbackRecordMapper, RemitCardCallbackRecord> implements IRemitCardCallbackRecordService {

    @Autowired
    private IRemitCardService remitCardService;

    @Override
    public Boolean callBack(String callBackJson) {

        MiPayRemitNotifyResponse miPayRemitNotifyResponse = this.covertToMiPayRemitNotifyResponse(callBackJson);
        try {
            // 1. 校验业务类型
            validateBusinessType(miPayRemitNotifyResponse);

            // 2. 申请汇款卡记录是否存在
            RemitCard remitCard = validateAndGetRecord(miPayRemitNotifyResponse);

            // 3. 检查是否已经处理成功（幂等性）
            if (remitCard.getCreateStatus() == CreateStatusEnum.SUCCESS.getCode()) {
                log.info("充值记录已处理成功，无需重复处理，通知ID: {}", miPayRemitNotifyResponse.getNotifyId());
                return true;
            }
            // 4. 查询或创建回调记录
            RemitCardCallbackRecord callbackRecord = createOrUpdateCallbackRecord(miPayRemitNotifyResponse);

            // 5. 处理款卡审核状态
            return handleRechargeStatus(miPayRemitNotifyResponse, callbackRecord);
        } catch (Exception e) {
            log.error("卡开通回调处理失败, 通知ID: {}, 错误信息: {}", miPayRemitNotifyResponse.getNotifyId(), e.getMessage(), e);
            throw new StarChainException("卡开通回调处理失败");
        }
    }

    // 1. 校验业务类型
    private void validateBusinessType(MiPayRemitNotifyResponse response) {
        Assert.isTrue(
                MiPayNotifyType.RemitCard.getType().equals(response.getBusinessType()),
                "回调业务类型与接口调用不匹配"
        );
        log.info("回调业务类型校验通过: {}", response.getBusinessType());
    }

    // 2. 核实卡充值记录是否存在
    private RemitCard validateAndGetRecord(MiPayRemitNotifyResponse response) {
        LambdaQueryWrapper<RemitCard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RemitCard::getRemitCode, response.getRemitCode())
                .eq(RemitCard::getCardId, response.getCardId())
                .eq(RemitCard::getTpyCardId, response.getTpyCardId());
        RemitCard record = remitCardService.getOne(queryWrapper);
        Assert.isTrue(record != null, "申请汇款记录不存在");
        log.info("卡信息校验通过, 卡ID: {}", response.getCardId());
        return record;
    }

    // 5. 查询或创建回调记录
    private RemitCardCallbackRecord createOrUpdateCallbackRecord(MiPayRemitNotifyResponse response) {
        LambdaQueryWrapper<RemitCardCallbackRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RemitCardCallbackRecord::getNotifyId, response.getNotifyId())
                .eq(RemitCardCallbackRecord::getRemitCode, response.getRemitCode())
                .eq(RemitCardCallbackRecord::getBusinessType, response.getBusinessType())
                .eq(RemitCardCallbackRecord::getCardId, response.getCardId());

        RemitCardCallbackRecord callbackRecord = this.getOne(queryWrapper);
        if (callbackRecord == null) {
            callbackRecord = new RemitCardCallbackRecord();
            callbackRecord.setNotifyId(response.getNotifyId());
            callbackRecord.setRemitCode(response.getRemitCode());
            callbackRecord.setBusinessType(response.getBusinessType());
            callbackRecord.setTpyCardId(response.getTpyCardId());
            callbackRecord.setCardId(response.getCardId());
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

    // 汇款卡审核状态
    private boolean handleRechargeStatus(MiPayRemitNotifyResponse response, RemitCardCallbackRecord callbackRecord) {
        if (CardStatusDescEnum.SUCCESS.getDescription().equals(response.getStatus())) {

            // 修改卡充值状态为成功
            updateRecordStatus(response, callbackRecord);

            // 更新回调记录
            updateCallbackRecord(callbackRecord, response);

            return true;
        } else if (CardStatusDescEnum.FAILED.getDescription().equals(response.getStatus())) {
            // 处理失败状态
            handleFailedStatus(callbackRecord);
            return false;
        }
        log.info("回调处理完成, 无须重复处理,通知ID: {}", response.getNotifyId());
        return true;
    }

    // 修改卡充值状态为成功
    private void updateRecordStatus(MiPayRemitNotifyResponse response, RemitCardCallbackRecord callbackRecord) {
        LambdaUpdateWrapper<RemitCard> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(RemitCard::getCardId, response.getCardId())
                .set(RemitCard::getStatus, response.getStatus())
                .set(RemitCard::getCreateStatus, CreateStatusEnum.SUCCESS.getCode()).set(RemitCard::getFinishTime, LocalDateTime.now())
                .set(RemitCard::getUpdateTime, LocalDateTime.now());
        boolean isUpdated = remitCardService.update(updateWrapper);
        if (!isUpdated) {
            log.warn("充值记录状态更新失败，可能已被其他线程处理，通知ID: {}", response.getNotifyId());
        }
    }


    // 更新回调记录
    private void updateCallbackRecord(RemitCardCallbackRecord callbackRecord, MiPayRemitNotifyResponse response) {
        callbackRecord.setUpdateTime(LocalDateTime.now());
        callbackRecord.setStatus(response.getStatus());
        callbackRecord.setStatusDesc(response.getStatusDesc());
        callbackRecord.setFinishTime(LocalDateTime.now());
        this.updateById(callbackRecord);
    }

    // 处理失败状态
    private void handleFailedStatus(RemitCardCallbackRecord callbackRecord) {
        LambdaUpdateWrapper<RemitCardCallbackRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(RemitCardCallbackRecord::getNotifyId, callbackRecord.getNotifyId())
                .setSql("retries = retries + 1")
                .set(RemitCardCallbackRecord::getUpdateTime, LocalDateTime.now());
        this.update(updateWrapper);
    }
}
