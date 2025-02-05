package com.starchain.common.entity.dto;
import com.alibaba.fastjson2.JSONObject;
import com.starchain.common.entity.RemitApplicationRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author
 * @date 2025-01-07
 * @Description
 */
@Data
@ApiModel(value = "申请汇款", description = "申请汇款")
@Accessors(chain = true)
public class RemitApplicationRecordDto extends RemitApplicationRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;

    @ApiModelProperty(name = "商家Id", example = "987654")
    private Long businessId;

    @ApiModelProperty(value = "汇款类型编码", example = "ELR")
    private String remitCode;

    @ApiModelProperty(value = "汇款目标币种", example = "CNY")
    private String toMoneyKind;

    @ApiModelProperty(value = "汇款目标金额", example = "1000.00")
    private BigDecimal toAmount;

    @ApiModelProperty(value = "汇款交易单号", example = "ORDER123456789")
    private String orderId;

    @ApiModelProperty(value = "汇款汇率", example = "6.500000")
    private BigDecimal remitRate;

    @ApiModelProperty(value = "额外参数", example = "{\"key\": \"value\"}")
    private JSONObject extraParams;
}
