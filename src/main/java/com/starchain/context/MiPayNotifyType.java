package com.starchain.context;

/**
 * @author
 * @date 2025-01-06
 * @Description 回调服务对应类
 */
public enum MiPayNotifyType {
    /**
     * 卡开通通知
     */
    CardOpen("CardOpen", "cardServiceImpl"),
    /**
     * 卡充值通知
     */
    CardRecharge("CardRecharge", "cardRechargeCallbackRecordServiceImpl"),

    /**
     * 卡提现通知
     */
    CardWithdraw("CardWithdraw", "cardServiceImpl"),

    /**
     * 卡预存通知
     */
    Presave("Presave", "cardPresaveCallbackRecordServiceImpl"),

    /**
     * 卡流水通知
     */
    CardTrade("CardTrade", "cardTradeCallbackRecordServiceImpl"),
    /**
     * 销卡通知
     */
    CardCancel("CardCancel", "cardServiceImpl"),
    /**
     * 卡验证码通知
     */
    VerifyCode("VerifyCode", "cardServiceImpl"),

    /**
     * 申请汇款卡审核通知
     */
    RemitCard("RemitCard", "remitCardServiceImpl");

    private final String type;
    private final String serviceName;

    MiPayNotifyType(String type, String serviceName) {
        this.type = type;
        this.serviceName = serviceName;
    }

    public static MiPayNotifyType getByType(String type) {
        for (MiPayNotifyType miPayNotifyType : MiPayNotifyType.values()) {
            if (miPayNotifyType.getType().equals(type)) {
                return miPayNotifyType;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public String getServiceName() {
        return serviceName;
    }

}
