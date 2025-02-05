package com.starchain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starchain.common.entity.CardHolder;
import com.starchain.common.entity.dto.CardHolderDto;

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
     * @param businessId
     * @return
     */
    boolean isExitHolder(Long userId, Integer businessId);

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
