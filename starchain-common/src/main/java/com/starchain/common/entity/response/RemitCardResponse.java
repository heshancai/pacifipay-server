package com.starchain.common.entity.response;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author
 * @date 2025-01-04
 * @Description 添加收款卡得到的响应对象
 */
@Data
public class RemitCardResponse {
    @Schema(description = "商家Id 第三方传递过来")
    private Long businessId;

    @Schema(description = "汇款类型编码", example = "UQR")
    private String remitCode;

    @Schema(description = "汇款卡标识", example = "CARD12345")
    private String cardId;

    @Schema(description = "名", example = "John")
    private String remitFirstName;

    @Schema(description = "姓", example = "Doe")
    private String remitLastName;

    @Schema(description = "银行卡号", example = "1234567890123456")
    private String remitBankNo;

    @Schema(description = "汇款卡ID-银行卡端响应唯一", example = "TPY12345")
    private String tpyCardId;

    @Schema(description = "状态", example = "通常固定为IN REVIEW")
    private String status;

    @Schema(description = "状态描述", example = "通常固定为IN REVIEW")
    private String statusDesc;

    @Schema(description = "额外参数", example = "{\"swiftCode\":\"ABCDEF123\"}")
    private JSONObject extraParams;
}

