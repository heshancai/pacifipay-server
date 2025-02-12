package com.starchain.common.enums;

/**
 * @author
 * @date 2025-02-06
 * @Description 交易枚举
 */
public enum TransactionTypeEnum {
    DEPOSIT(1, "充币"),
    WITHDRAWAL(2, "提币"),
    GLOBAL_REMITTANCE_FEE(3, "汇款金额"),
    BALANCE_RECHARGE_TO_CARD(4, "卡充值"),
    // 汇款撤销
    REMIT_CANCEL_FEE(5, "汇款撤销退回金额"),
    // 开卡费
    CARD_OPEN_FEE(6, "开卡费"),
    // 开卡预存费
    CARD_OPEN_DEPOSIT(7, "开卡预存费"),
    // 卡月服务费
    CARD_MONTHLY_SERVICE_FEE(8, "卡月服务费"),
    // 充值卡手续费
    CARD_RECHARGE_FEE(9, "充值卡手续费"),
    // 销卡回退金额
    CARD_CANCEL_RETURN(10, "销卡回退金额"),
    // 销卡手续费
    CARD_CANCEL_FEE(11, "销卡手续费"),
    // 汇款手续费
    REMIT_FEE(12, "汇款手续费");
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
