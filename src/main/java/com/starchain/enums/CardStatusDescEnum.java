package com.starchain.enums;

/**
 * @author
 * @date 2025-01-14
 * @Description
 */
public enum CardStatusDescEnum {
    CARD_OPEN_SUCCESS("CardOpen Success"),
    CARD_RECHARGE_SUCCESS("CardRecharge Success"),
    CARD_WITHDRAW_SUCCESS("CardWithdraw Success"),
    PRESAVE_SUCCESS("Presave success"),
    TRADE_SUCCESS("Trade success"),
    CARD_CANCEL_SUCCESS("CardCancel Success");

    private final String description;

    CardStatusDescEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
