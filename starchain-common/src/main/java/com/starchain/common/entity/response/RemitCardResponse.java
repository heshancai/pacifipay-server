package com.starchain.common.entity.response;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author
 * @date 2025-01-04
 * @Description 添加收款卡得到的响应对象
 */
@Data
public class RemitCardResponse {
    @ApiModelProperty(value = "用户Id 第三方传递过来")
    private Long channelId;
    @ApiModelProperty(value = "汇款类型编码", example = "UQR")
    private String remitCode;
    @ApiModelProperty(value = "汇款卡标识", example = "CARD12345")
    private String cardId;
    @ApiModelProperty(value = "名", example = "John")
    private String remitFirstName;
    @ApiModelProperty(value = "姓", example = "Doe")
    private String remitLastName;
    @ApiModelProperty(value = "银行卡号", example = "1234567890123456")
    private String remitBankNo;
    @ApiModelProperty(value = "汇款卡ID-银行卡端响应唯一", example = "TPY12345")
    private String tpyCardId;
    @ApiModelProperty(value = "状态", example = "通常固定为IN REVIEW")
    private String status;
    @ApiModelProperty(value = "状态描述", example = "通常固定为IN REVIEW")
    private String statusDesc;
    @ApiModelProperty(value = "额外参数", example = "{\"swiftCode\":\"ABCDEF123\"}")
    private JSONObject extraParams;
}
