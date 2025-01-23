package com.starchain.enums;

import io.swagger.annotations.ApiModel;

/**
 * @author
 * @date 2024-12-31
 * @Description
 */
@ApiModel(value="汇款银行支行编码", description="汇款银行支行编码")
public enum BankBranchCodeEnum {
    KKBK000123("KKBK000123", "INDBAN", "IFT Branch"),
    BKID0007114("BKID0007114", "INDBAN", "NOIDA"),
    HDFC0000123("HDFC0000123", "INDHDF", "MADURAI-TAMIL NADU"),
    PUNB0783800("PUNB0783800", "1796", "Jamalpur");

    private final String bankBranchCode;
    private final String bankCode;
    private final String desc;

    BankBranchCodeEnum(String bankBranchCode, String bankCode, String desc) {
        this.bankBranchCode = bankBranchCode;
        this.bankCode = bankCode;
        this.desc = desc;
    }

    public String getBankBranchCode() {
        return bankBranchCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getDesc() {
        return desc;
    }
}
