package com.starchain.constants;

/**
 * @author
 * @date 2024-12-19
 * @Description Pacificpay银行卡对接API
 */
public class CardUrlConstants {
    // 创建持卡人
    public static final String addCardHolder = "/apiMerchant/addCardHolder";

    // 修改持卡人
    public static final String editCardHolder = "/apiMerchant/editCardHolder";

    // 创建卡
    public static final String addCard = "/apiMerchant/addCard";

    // 查询商户余额
    public static final String mchInfo = "/apiMerchant/mchInfo";

    // 查询商户交易明细
    public static final String tradeDetail = "/apiMerchant/tradeDetail";

    // 修改卡限额
    public static final String updateLimit = "/apiMerchant/updateLimit";

    // 卡充值
    public static final String applyRecharge = "/apiMerchant/applyRecharge";

    // 卡提现
    public static final String applyWithdraw = "/apiMerchant/applyWithdraw";

    // 锁定卡
    public static final String lockCard = "/apiMerchant/lockCard";

    // 解锁卡
    public static final String unlockCard = "/apiMerchant/unlockCard";

    // 查询卡
    public static final String getCardDetail = "/apiMerchant/getCardDetail";

    //申请销卡
    public static final String deleteCard = "/apiMerchant/deleteCard";

    //申请换卡
    public static final String changeCard = "/apiMerchant/changeCard";



}
