package com.starchain.enums;

import io.swagger.annotations.ApiModel;

/**
 * @author
 * @date 2024-12-31
 * @Description
 */
@ApiModel(value="订单类型", description="订单类型")
public enum OrderTypeEnum {
    CARD_FEE("CardFee", "开卡费"),
    ADD_CARD_SAVE("AddCardSave", "开卡预存"),
    CARD_RECHARGE("CardRecharge", "卡充值"),
    CARD_WITHDRAW("CardWithdraw", "卡提现"),
    MONTHLY_FEE("MonthlyFee", "卡月服务费"),
    RECHARGE("Recharge", "充值"),
    REMIT("Remit", "汇款");
    private final String orderType;
    private final String typeDesc;

    OrderTypeEnum(String orderType, String typeDesc) {
        this.orderType = orderType;
        this.typeDesc = typeDesc;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getTypeDesc() {
        return typeDesc;
    }
}
