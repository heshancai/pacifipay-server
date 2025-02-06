package com.starchain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.common.entity.CardCancelRecord;
import com.starchain.dao.CardCancelRecordMapper;
import com.starchain.service.ICardCancelRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author
 * @date 2025-02-06
 * @Description
 */
@Slf4j
@Service
public class CardCancelRecordServiceImpl extends ServiceImpl<CardCancelRecordMapper, CardCancelRecord> implements ICardCancelRecordService {

    // 你可以在这里实现自定义的业务逻辑方法
    // 比如销卡申请的处理等
}
