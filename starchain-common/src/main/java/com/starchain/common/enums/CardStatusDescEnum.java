package com.starchain.common.enums;

/**
 * @author
 * @date 2025-01-14
 * @Description
 */
public enum CardStatusDescEnum {
    SUCCESS("SUCCESS"),
    FAILED("FAILED");

    private final String description;

    CardStatusDescEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
