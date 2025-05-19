package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-15
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("card_fee_detail_callback_record")
@Schema(title = "CardFeeDetailCallbackRecord对象", description = "卡手续费详情回调记录表")
public class CardFeeDetailCallbackRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "交易 ID")
    private Long transactionId;

    @Schema(description = "网关费")
    private BigDecimal gatewayFee;

    @Schema(description = "验证费")
    private BigDecimal verifyFee;

    @Schema(description = "冲正费")
    private BigDecimal voidFee;

    @Schema(description = "退款费")
    private BigDecimal refundFee;

    @Schema(description = "授权失败费")
    private BigDecimal authFailFee;

    @Schema(description = "授权成功费")
    private BigDecimal authSuccessFee;

    @Schema(description = "授权小额费，仅在授权成功时有效")
    private BigDecimal authLittleFee;

    @Schema(description = "授权跨境费，仅在授权成功时有效")
    private BigDecimal authBorderFee;

    @Schema(description = "回调完成时间")
    private LocalDateTime finishTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
