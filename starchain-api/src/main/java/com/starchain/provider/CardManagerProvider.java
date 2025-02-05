package com.starchain.provider;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starchain.common.dubbo.ICardManagerProvider;
import com.starchain.common.entity.Card;
import com.starchain.common.entity.dto.CardDto;
import com.starchain.common.result.ClientResponse;
import com.starchain.common.result.ResultGenerator;
import com.starchain.service.ICardService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author
 * @date 2025-01-27
 * @Description 虚拟卡后台管理
 */
@Slf4j
@DubboService(version = "1.0") // 提供和消费者需要指定同一个version
public class CardManagerProvider implements ICardManagerProvider {

    @Autowired
    private ICardService cardService;

    /**
     * 查询卡列表
     * @param dto
     * @return
     */
    @Override
    public ClientResponse queryCardManagePage(CardDto dto) {
        if (null == dto) {
            return ResultGenerator.genFailResult("dto不能为空");
        }
        LambdaQueryWrapper<Card> queryWrapper = new LambdaQueryWrapper<>();
        if (null != dto.getCardId()) {
            queryWrapper.eq(Card::getCardId, dto.getCardId());
        }
        if (null != dto.getCardCode()) {
            queryWrapper.eq(Card::getCardCode, dto.getCardCode());
        }
        if (null != dto.getCardNo()) {
            queryWrapper.eq(Card::getCardNo, dto.getCardNo());
        }

        Page<Card> cardPage = new Page<>(dto.getPageNum(), dto.getPageSize());

        try {
            Page<Card> page = cardService.page(cardPage, queryWrapper);
            return ResultGenerator.genSuccessResult(page);
        } catch (Exception e) {
            log.error("服务错误:{}", e);
            return ResultGenerator.genFailResult("服务错误！" + e.getMessage());
        }
    }
}
