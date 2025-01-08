package com.starchain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starchain.entity.CardHolder;
import com.starchain.entity.dto.CardHolderDto;

/**
 * @author
 * @date 2025-01-03
 * @Description
 */
public interface ICardHolderService extends IService<CardHolder> {
    /**
     * 判断当前用户是否为持卡人
     *
     * @param userId
     * @param channelId
     * @return
     */
    boolean isExitHolder(Long userId, Integer channelId);

    /**
     * 添加持卡人
     *
     * @param
     * @return
     */
    CardHolder addCardHolder(CardHolderDto cardHolderDto);


    /**
     * 修改持卡人
     * @param cardHolderDto
     * @return
     */
    CardHolder updateCardHolder(CardHolderDto cardHolderDto);

    /**
     * 查询持卡人
     * @param cardHolderDto
     * @return
     */
    CardHolder getCardHolder(CardHolderDto cardHolderDto);
}
