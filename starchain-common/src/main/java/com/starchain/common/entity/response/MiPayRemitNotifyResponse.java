package com.starchain.common.entity.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "太平洋唯一订单号", example = "汇款撤销 特有字段")
    private String orderId;

    // 撤销的会的金额 会跟发起汇款的金额不 会比发起的少 一直 这里扣除了手续费
    @Schema(description = "退回金额", example = "汇款撤销 特有字段")
    private BigDecimal cancelAmount;
    @Schema(description = "汇款卡ID", example = "TPY12345")
    private String tpyCardId;
    @Schema(description = "汇款类型编码", example = "汇款api 特有字段")
    private String remitCode;
}
