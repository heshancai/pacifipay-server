package com.starchain.common.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author
 * @date 2024-12-31
 * @Description
 */
@Schema( description="汇款类型编码")
public enum RemitCodeEnum {
    UQR_CNH("UQR_CNH", "中国"),
    UQR_HKD("UQR_HKD", "香港"),
    UQR_SGD("UQR_SGD", "新加坡"),
    ELR("ELR", "太平洋中国汇款"),
    LNR_IND("LNR_IND", "太平洋印度汇款");

    private final String remitCode;
    private final String desc;

    RemitCodeEnum(String remitCode, String desc) {
        this.remitCode = remitCode;
        this.desc = desc;
    }

    public String getRemitCode() {
        return remitCode;
    }

    public String getDesc() {
        return desc;
    }
}
