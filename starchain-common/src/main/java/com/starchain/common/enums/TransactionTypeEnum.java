package com.starchain.common.enums;

/**
 * @author
 * @date 2025-02-06
 * @Description 交易枚举
 */
public enum TransactionTypeEnum {
    DEPOSIT(1, "充币"),
    WITHDRAWAL(2, "提币"),
    GLOBAL_REMITTANCE(3, "全球汇款"),
    BALANCE_RECHARGE_TO_CARD(4, "余额充值到卡");

    private final int code;
    private final String description;

    TransactionTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TransactionTypeEnum fromCode(int code) {
        for (TransactionTypeEnum type : TransactionTypeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown transaction type code: " + code);
    }
}
