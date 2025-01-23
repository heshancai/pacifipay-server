package com.starchain.enums;

/**
 * @author
 * @date 2025-01-10
 * @Description
 */
public enum WalletSideEnum {
    DEPOSIT("deposit","充值通知"),
    WITHDRAW("withdraw","提现通知");

    private String key;

    private String desc;
    private WalletSideEnum(String key,String desc){
        this.key = key;
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }

    public String getDesc() {
        return desc;
    }
}
