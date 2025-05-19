package com.starchain.common.entity.dto;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author
 * @date 2025-01-07
 * @Description
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RemitCardDto {

    @Schema(description = "用户ID", example = "123456")
    private Long userId;

    @Schema(description = "商家Id", example = "987654")
    private Long businessId;

    @Schema(description = "汇款类型编码", example = "LNR_IND")
    private String remitCode;

    @Schema(description = "汇款卡ID", example = "Tpysh的唯一标识ID")
    private String tpyCardId;

    @Schema(description = "汇款卡标识", example = "客户汇款卡唯一标识，唯一标识至少填写一个")
    private String cardId;

    @Schema(description = "汇款交易单号", example = "客户汇款交易唯一标识。")
    private String orderId;

    @Schema(description = "名", example = "John")
    private String remitFirstName;

    @Schema(description = "姓", example = "Doe")
    private String remitLastName;

    @Schema(description = "银行卡号", example = "1234567890123456")
    private String remitBankNo;

    @Schema(description = "额外参数", example = "{\"key\": \"value\"}")
    private JSONObject extraParams;
}
