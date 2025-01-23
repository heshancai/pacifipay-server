package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "CardFeeDetailCallbackRecord对象", description = "卡手续费详情回调记录表")
public class CardFeeDetailCallbackRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "交易 ID")
    private Long transactionId;

    @ApiModelProperty(value = "网关费")
    private BigDecimal gatewayFee;

    @ApiModelProperty(value = "验证费")
    private BigDecimal verifyFee;

    @ApiModelProperty(value = "冲正费")
    private BigDecimal voidFee;

    @ApiModelProperty(value = "退款费")
    private BigDecimal refundFee;

    @ApiModelProperty(value = "授权失败费")
    private BigDecimal authFailFee;

    @ApiModelProperty(value = "授权成功费")
    private BigDecimal authSuccessFee;

    @ApiModelProperty(value = "授权小额费，仅在授权成功时有效")
    private BigDecimal authLittleFee;

    @ApiModelProperty(value = "授权跨境费，仅在授权成功时有效")
    private BigDecimal authBorderFee;


    @ApiModelProperty(value = "回调完成时间")
    private LocalDateTime finishTime;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}
