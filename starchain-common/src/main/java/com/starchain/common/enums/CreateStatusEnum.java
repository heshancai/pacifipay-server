package com.starchain.common.enums;

import io.swagger.annotations.ApiModel;

/**
 * @author
 * @date 2025-01-15
 * @Description
 */
@ApiModel(value = "卡状态创建状态", description = "卡状态创建状态")
public enum CreateStatusEnum {
    CREATING(0, "创建中"),
    SUCCESS(1, "创建成功"),
    FAILED(2, "创建失败"),
    CANCEL(3, "撤销");
    private final int code;
    private final String description;

    CreateStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
