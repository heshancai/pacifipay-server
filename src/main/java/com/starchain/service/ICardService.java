package com.starchain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starchain.entity.Card;
import com.starchain.entity.CardHolder;
import com.starchain.entity.dto.CardDto;

/**
 * @author
 * @date 2025-01-03
 * @Description
 */
public interface ICardService extends IService<Card>,IMiPayNotifyService {
    /**
     * 根据用户id和渠道id查询卡数量
     * @param
     * @param channelId
     * @return
     */
    Integer checkCardNum(Long cardHolderId, Integer channelId,String cardCode,String tpyshCardHolderId);

    /**
     *  创建卡
     * @param cardHolder
     * @return
     */
    Card addCard(CardHolder cardHolder);


    /**
     * 申请销卡
     * @param
     * @return
     */
    Boolean deleteCard(CardDto cardDto);

    /**
     * 申请换卡
     * @param card
     * @return
     */
    Card changeCard(Card card);
}
