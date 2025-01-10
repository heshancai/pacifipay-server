package com.starchain.enums;

/**
 * @author
 * @date 2025-01-10
 * @Description
 */
public enum RechargeRecordStatusEnum {
    //0 等待确认   1失败  2成功  3取消  5已确认
    WAIT_CONFIRM(0, "等待确认"),
    FAIL(1, "失败"),
    CONFIRMED(2, "已经确认"),
    CANCEL(3, "取消");
    RechargeRecordStatusEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    private int key;
    private String value;

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}