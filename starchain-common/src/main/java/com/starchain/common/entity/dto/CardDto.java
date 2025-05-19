package com.starchain.common.entity.dto;

/**
 * @author
 * @date 2024-12-19
 * @Description
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;

@Slf4j
@Data
public class CardDto extends PageDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cardCode;
    private String saveOrderId;

    @Schema(description = "用户Id", example = "CARD123456")
    private BigDecimal saveAmount;

    @Schema(description = "充值金额", example = "小数点后最多2位")
    private BigDecimal orderAmount;

    @Schema(description = "持卡人Id", example = "")
    private String tpyshCardHolderId;

    @Schema(description = "卡ID-银行卡端返回", example = "CARD123456")
    private String cardId;

    @Schema(description = "卡号-银行卡端返回", example = "1234567890123456")
    private String cardNo;

    @Schema(description = "用户Id", example = "CARD123456")
    private Long userId;

    @Schema(description = "商家Id", example = "CARD123456")
    private Long businessId;

    @Schema(description = "客户卡充值交易唯一标识", example = "CARD123456")
    private String orderId;

    @Schema(description = "消费单笔限额-银行卡端返回", example = "消费单笔限额，可修改")
    private BigDecimal singleLimit;
}
