package com.starchain.provider;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starchain.common.dubbo.ICardHolderManagerProvider;
import com.starchain.common.entity.CardHolder;
import com.starchain.common.entity.dto.CardHolderDto;
import com.starchain.common.result.ClientResponse;
import com.starchain.common.result.ResultGenerator;
import com.starchain.service.ICardHolderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author
 * @date 2025-02-05
 * @Description
 */
@Slf4j
@DubboService(version = "1.0") // 提供和消费者需要指定同一个version
public class CardHolderManagerProvider implements ICardHolderManagerProvider {

    @Autowired
    private ICardHolderService cardHolderService;

    @Override
    public ClientResponse queryCardManagePage(CardHolderDto dto) {
        if (null == dto) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        LambdaQueryWrapper<CardHolder> queryWrapper = new LambdaQueryWrapper<>();
        if (null != dto.getUserId()) {
            queryWrapper.eq(CardHolder::getUserId, dto.getUserId());
        }
        if (null != dto.getMerchantCardHolderId()) {
            queryWrapper.eq(CardHolder::getMerchantCardHolderId, dto.getMerchantCardHolderId());
        }
        if (null != dto.getBusinessId()) {
            queryWrapper.eq(CardHolder::getBusinessId, dto.getBusinessId());
        }

        Page<CardHolder> cardPage = new Page<>(dto.getPageNum(), dto.getPageSize());

        try {
            Page<CardHolder> page = cardHolderService.page(cardPage, queryWrapper);
            return ResultGenerator.genSuccessResult(page);
        } catch (Exception e) {
            log.error("服务错误:{}", e);
            return ResultGenerator.genFailResult("服务错误！" + e.getMessage());
        }
    }
}
