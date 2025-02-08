package com.starchain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starchain.common.entity.Card;
import com.starchain.common.entity.CardRechargeRecord;
import com.starchain.common.entity.dto.CardDto;

/**
 * @author
 * @date 2025-01-03
 * @Description 卡操作服务
 */
public interface ICardService extends IService<Card> {
    /**
     * 根据用户id和渠道id查询卡数量
     *
     * @param
     * @param businessId
     * @return
     */
    Integer checkCardNum(Long businessId, String cardCode, String tpyshCardHolderId);

    /**
     * 创建卡
     *
     * @param
     * @return
     */
    Card addCard(CardDto card);


    /**
     * 申请销卡
     *
     * @param
     * @return
     */
    Boolean deleteCard(CardDto cardDto);

    /**
     * 申请换卡
     *
     * @param card
     * @return
     */
    Card changeCard(Card card);


    /**
     * 卡充值
     *
     * @param cardDto
     * @return
     */
    CardRechargeRecord applyRecharge(CardDto cardDto);

    /**
     * 锁定卡
     *
     * @param cardDto
     * @return
     */
    Boolean lockCard(Card cardDto);

    /**
     * 解锁卡
     *
     * @param card
     * @return
     */
    Boolean unlockCard(Card card);

    /**
     * 修改卡限制额度
     *
     * @param cardDto
     * @return
     */
    Boolean updateLimit(CardDto cardDto);


    /**
     * 判断卡充值是否在处理中
     * @param cardDto
     * @return
     */
    boolean isRechargeInProgress(CardDto cardDto);

    /**
     * 检查卡是否存在
     */
    Card cardExists(String cardId, String cardCode);
}
