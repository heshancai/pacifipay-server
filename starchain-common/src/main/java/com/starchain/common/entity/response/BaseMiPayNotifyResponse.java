package com.starchain.common.entity.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author
 * @date 2025-01-04
 * @Description miPay银行卡回调公共字段抽取
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class BaseMiPayNotifyResponse {
    @Schema(description = "通知的唯一ID", example = "NOTIFY_123456")
    private String notifyId;

    @Schema(description = "业务类型", example = "CardOpen")
    private String businessType;

    @Schema(description = "状态", example = "SUCCESS")
    private String status;

    @Schema(description = "状态描述", example = "CardOpen success")
    private String statusDesc;

    @Schema(description = "卡ID", example = "CARD_123456")
    private String cardId;

    @Schema(description = "交易流水号", example = "卡流水、申请汇款、汇款撤销 特有字段")
    private String tradeId;
}