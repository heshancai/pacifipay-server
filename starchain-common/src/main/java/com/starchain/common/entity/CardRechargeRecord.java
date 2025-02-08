package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-08
 * @Description
 */
@Data
@TableName("card_recharge_record")
@ApiModel(value = "卡充值、卡提现记录表", description = "卡充值通知记录实体")
public class CardRechargeRecord {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键ID", example = "1")
    private Long id; // 主键

    @ApiModelProperty(value = "用户Id，第三方传递过来")
    private Long userId; // 用户Id

    @ApiModelProperty(value = "持卡人Id", example = "")
    private String tpyshCardHolderId;

    @ApiModelProperty(value = "商户号", example = "100001")
    private Long businessId; // 商户号

    @ApiModelProperty(value = "卡ID", example = "1234567890")
    private String cardId; // 卡ID

    @ApiModelProperty(value = "卡类型编码", example = "CARD_TYPE_001")
    private String cardCode; // 卡类型编码

    @ApiModelProperty(value = "充值交易单号", example = "ORDER123456789")
    private String orderId; // 充值交易单号

    @ApiModelProperty(value = "充值金额", example = "100.00")
    private BigDecimal orderAmount; // 充值金额

    @ApiModelProperty(value = "实际支付金额", example = "100.00")
    private BigDecimal actAmount; // 充值金额

    @ApiModelProperty(value = "流水号", example = "TRADE123456789")
    private String tradeId; // 充值流水号

    @ApiModelProperty(value = "状态", example = "0 充值中 1 充值成功 2 充值失败 3 充值取消")
    private Integer status; //

    @ApiModelProperty(value = "手续费", example = "5.00")
    private BigDecimal orderFee; // 手续费

    @ApiModelProperty(value = "订单时间", example = "2023-10-15 12:30:45")
    private LocalDateTime orderTime; // 订单时间

    @ApiModelProperty(value = "创建时间", example = "2023-10-15 12:30:45")
    private LocalDateTime createTime; // 创建时间

    @ApiModelProperty(value = "更新时间", example = "2023-10-15 12:30:45")
    private LocalDateTime updateTime; // 更新时间

    @ApiModelProperty(value = "完成时间", example = "2023-10-15 12:30:45")
    private LocalDateTime finishTime;
}
