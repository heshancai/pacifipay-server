package com.starchain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author
 * @date 2025-01-10
 * @Description
 */
@AllArgsConstructor
@Getter
public enum TranslogStatus {
    //充币
    DEPOSIT(1),
    //提币
    WITHDRAW(2);
    private int value;
}
