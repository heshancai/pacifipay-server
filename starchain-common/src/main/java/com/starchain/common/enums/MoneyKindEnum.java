package com.starchain.common.enums;

/**
 * @author
 * @date 2025-01-07
 * @Description
 */
public enum MoneyKindEnum {

    CNY("CNY", "CNY"),
    USD("USD", "USD");

    private final String moneyKindCode;
    private final String moneyKindDesc;

    MoneyKindEnum(String moneyKindCode, String moneyKindDesc) {
        this.moneyKindCode = moneyKindCode;
        this.moneyKindDesc = moneyKindDesc;
    }

    public String getMoneyKindCode() {
        return moneyKindCode;
    }

    public String getMoneyKindDesc() {
        return moneyKindDesc;
    }


}
