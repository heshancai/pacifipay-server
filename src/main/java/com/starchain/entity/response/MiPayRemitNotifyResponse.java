package com.starchain.entity.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author
 * @date 2025-01-04
 * @Description
 */
@Data
@Accessors(chain = true)
@ToString
@EqualsAndHashCode(callSuper = false)
public class MiPayRemitNotifyResponse extends BaseMiPayNotifyResponse {
    @ApiModelProperty(value = "太平洋唯一订单号", example = "汇款撤销 特有字段")
    private String orderId;
    @ApiModelProperty(value = "退回金额", example = "汇款撤销 特有字段")
    private BigDecimal cancelAmount;
    @ApiModelProperty(value = "汇款卡ID", example = "TPY12345")
    private String tpyCardId;
    @ApiModelProperty(value = "汇款类型编码", example = "汇款api 特有字段")
    private String remitCode;
}
