package com.starchain.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.common.entity.RemitCardNotify;
import com.starchain.common.entity.response.MiPayCardNotifyResponse;
import com.starchain.dao.RemitCardNotifyMapper;
import com.starchain.service.IRemitCardNotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author
 * @date 2025-01-02
 * @Description
 */
@Service
@Slf4j
public class RemitCardNotifyServiceImpl extends ServiceImpl<RemitCardNotifyMapper, RemitCardNotify> implements IRemitCardNotifyService {
    @Override
    public RemitCardNotify checkDepositRecordIsExist(MiPayCardNotifyResponse miPayNotifyResponse) {
        log.info("检查数据是否存在: {}", JSON.toJSONString(miPayNotifyResponse));
        LambdaQueryWrapper<RemitCardNotify> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RemitCardNotify::getNotifyId, miPayNotifyResponse.getNotifyId());
        queryWrapper.eq(RemitCardNotify::getBusinessType, miPayNotifyResponse.getBusinessType());
        RemitCardNotify remitCardNotify = this.getOne(queryWrapper);
        if (remitCardNotify == null) {
            log.info("回调记录不存在，创建新的回调记录");
            remitCardNotify = new RemitCardNotify();
            BeanUtils.copyProperties(miPayNotifyResponse, remitCardNotify);
        }
        this.save(remitCardNotify);
        return remitCardNotify;
    }
}
