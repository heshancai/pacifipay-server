package com.starchain.enums;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author
 * @date 2024-12-31
 * @Description
 */
@ApiModel(value="汇款银行编码", description="汇款银行编码")
public enum BankCodeEnum {
    INDBAN("INDBAN", "印度国家银行"),
    INDHDF("INDHDF", "印度私营银行"),
    PUNJAB_NATIONAL_BANK("1796", "旁遮普国家银行");

    private final String bankCode;
    private final String desc;

    BankCodeEnum(String bankCode, String desc) {
        this.bankCode = bankCode;
        this.desc = desc;
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getDesc() {
        return desc;
    }
}
