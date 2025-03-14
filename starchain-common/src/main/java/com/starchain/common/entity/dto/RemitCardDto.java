package com.starchain.common.entity.dto;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(name = "用户ID", example = "123456")
    private Long userId;

    @ApiModelProperty(name = "商家Id", example = "987654")
    private Long businessId;

    @ApiModelProperty(name = "汇款类型编码", example = "LNR_IND")
    private String remitCode;

    @ApiModelProperty(name = "汇款卡ID", example = "Tpysh的唯一标识ID")
    private String tpyCardId;

    @ApiModelProperty(name = "汇款卡标识", example = "客户汇款卡唯一标识，唯一标识至少填写一个")
    private String cardId;

    @ApiModelProperty(name = "汇款交易单号", example = "客户汇款交易唯一标识。")
    private String orderId;

    @ApiModelProperty(name = "名", example = "John")
    private String remitFirstName;

    @ApiModelProperty(name = "姓", example = "Doe")
    private String remitLastName;

    @ApiModelProperty(name = "银行卡号", example = "1234567890123456")
    private String remitBankNo;

    @ApiModelProperty(value = "额外参数", example = "{\"key\": \"value\"}")
    private JSONObject extraParams;

}
