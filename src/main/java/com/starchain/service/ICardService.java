package com.starchain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starchain.entity.Card;
import com.starchain.entity.CardHolder;

/**
 * @author
 * @date 2025-01-03
 * @Description
 */
public interface ICardService extends IService<Card> {
    /**
     * 根据用户id和渠道id查询卡数量
     * @param
     * @param channelId
     * @return
     */
    Integer checkCardNum(Long cardHolderId, Integer channelId,String cardCode);

    /**
     *  根据持卡人创建卡
     * @param cardHolder
     * @return
     */
    Card addCard(CardHolder cardHolder);

}
