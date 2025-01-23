package com.starchain.common.enums;

import io.swagger.annotations.ApiModel;

/**
 * @author
 * @date 2024-12-31
 * @Description
 */
@ApiModel(value="证件类型", description="证件类型")
public enum RemitIdTypeEnum {
    ID_CARD("ID_CARD", "身份证"),
    HOUSEHOLD_REGISTER("HOUSEHOLD_REGISTER", "户口本"),
    PASSPORT("PASSPORT", "护照"),
    ARMY_CARD("ARMY_CARD", "军官证"),
    SOLDIER_CARD("SOLDIER_CARD", "士兵证"),
    POLICE_CARD("POLICE_CARD", "警官证"),
    MTP_HM_R("MTP_HM_R", "港澳居民通行证"),
    MTP_T_R("MTP_T_R", "台湾居民通行证"),
    TEMP_ID_CARD("TEMP_ID_CARD", "临时身份证"),
    FRP("FRP", "外国人居留证");

    private final String remitIdType;
    private final String typeDesc;

    RemitIdTypeEnum(String remitIdType, String typeDesc) {
        this.remitIdType = remitIdType;
        this.typeDesc = typeDesc;
    }

    public String getRemitIdType() {
        return remitIdType;
    }

    public String getTypeDesc() {
        return typeDesc;
    }
}
