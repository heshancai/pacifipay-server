package com.starchain.common.constants;

/**
 * @author
 * @date 2024-12-19
 * @Description Pacificpay银行卡对接API
 */
public class CardUrlConstants {
    // 创建持卡人
    public static final String ADD_CARD_HOLDER = "/apiMerchant/addCardHolder";

    // 修改持卡人
    public static final String EDIT_CARD_HOLDER = "/apiMerchant/editCardHolder";

    // 修改持卡人
    public static final String GET_CARD_HOLDER = "/apiMerchant/getCardHolder";

    // 创建卡
    public static final String ADD_CARD = "/apiMerchant/addCard";

    // 查询商户余额
    public static final String MCH_INFO = "/apiMerchant/mchInfo";

    // 查询商户交易明细
    public static final String TRADE_DETAIL = "/apiMerchant/tradeDetail";

    // 修改卡限额
    public static final String UPDATE_LIMIT = "/apiMerchant/updateLimit";

    // 卡充值
    public static final String APPLY_RECHARGE = "/apiMerchant/applyRecharge";

    // 卡提现
    public static final String APPLY_WITHDRAW = "/apiMerchant/applyWithdraw";

    // 锁定卡
    public static final String LOCK_CARD = "/apiMerchant/lockCard";

    // 解锁卡
    public static final String UNLOCK_CARD = "/apiMerchant/unlockCard";

    // 查询卡
    public static final String GET_CARD_DETAIL = "/apiMerchant/getCardDetail";

    // 申请销卡
    public static final String DELETE_CARD = "/apiMerchant/deleteCard";

    // 申请换卡
    public static final String CHANGE_CARD = "/apiMerchant/changeCard";
}
