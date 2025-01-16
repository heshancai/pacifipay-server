package com.starchain.entity.response;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "通知的唯一ID", example = "NOTIFY_123456")
    private String notifyId;
    @ApiModelProperty(value = "业务类型", example = "CardOpen")
    private String businessType;
    @ApiModelProperty(value = "状态", example = "SUCCESS")
    private String status;
    @ApiModelProperty(value = "状态描述", example = "CardOpen success")
    private String statusDesc;
    @ApiModelProperty(value = "卡ID", example = "CARD_123456")
    private String cardId;
    @ApiModelProperty(value = "交易流水号", example = "卡流水、申请汇款、汇款撤销 特有字段")
    private String tradeId;
}
