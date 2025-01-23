package com.starchain.common.enums;

import io.swagger.annotations.ApiModel;

/**
 * @author
 * @date 2024-12-31
 * @Description
 */
@ApiModel(value="卡状态", description="卡状态")
public enum CardStatusEnum {
    NORMAL("normal", "使用中"),
    INIT_FAIL("initFail", "初始化错误"),
    UNACTIVATED("unactivated", "未激活"),
    ACTIVATING("activating", "激活中"),
    FREEZING("freezing", "已锁定"),
    CANCELLED("cancelled", "已注销");

    private final String cardStatus;
    private final String statusDesc;

    CardStatusEnum(String cardStatus, String statusDesc) {
        this.cardStatus = cardStatus;
        this.statusDesc = statusDesc;
    }

    public String getCardStatus() {
        return cardStatus;
    }

    public String getStatusDesc() {
        return statusDesc;
    }
}
