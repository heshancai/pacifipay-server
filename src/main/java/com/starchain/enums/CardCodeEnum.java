package com.starchain.enums;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author
 * @date 2024-12-31
 * @Description
 */
@ApiModel(value="卡类型编码", description="卡类型编码")
public enum CardCodeEnum {
    TPY_UDK1("TpyUDK1", "实名银联数字卡1", "需要实名认证；有卡密码；"),
    TPY_MDN5("TpyMDN5", "匿名万事达数字卡5", ""),
    TPY_MDN4("TpyMDN4", "匿名万事达数字卡4", ""),
    TPY_MDN3("TpyMDN3", "匿名万事达数字卡3", ""),
    TPY_MDN2("TpyMDN2", "匿名万事达数字卡2", ""),
    TPY_MDN1("TpyMDN1", "匿名万事达数字卡1", "预存与充值最低20USD"),
    TPY_VDN1("TpyVDN1", "匿名VISA数字卡1", "预存与充值最低20USD"),
    TPY_MDN6("TpyMDN6", "匿名万事达数字卡6", "支持苹果"),
    TPY_MDN8("TpyMDN8", "匿名万事达数字卡8", "不支持苹果");

    private final String cardCode;
    private final String cardDesc;
    private final String remark;

    CardCodeEnum(String cardCode, String cardDesc, String remark) {
        this.cardCode = cardCode;
        this.cardDesc = cardDesc;
        this.remark = remark;
    }

    public String getCardCode() {
        return cardCode;
    }

    public String getCardDesc() {
        return cardDesc;
    }

    public String getRemark() {
        return remark;
    }
}
