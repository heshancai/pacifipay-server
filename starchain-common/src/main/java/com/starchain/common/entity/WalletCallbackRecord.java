package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-10
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wallet_callback_record")
@ApiModel(value = "WalletCallbackRecord", description = "钱包回调记录表")
public class WalletCallbackRecord {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "通知类型，充值通知：deposit，提现通知：withdraw")
    private String side;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "充值金额")
    private BigDecimal amount;
    @ApiModelProperty(value = "币种符号")
    private String symbol;
    @ApiModelProperty(value = "区块链上的交易id")
    private String txid;
    @ApiModelProperty(value = "当前确认次数")
    private Integer confirmTimes;
    @ApiModelProperty(value = "通知id")
    private String notifyId;
    @ApiModelProperty(value = "充值id")
    private String depositId;
    @ApiModelProperty(value = "0:等待确认 1成功，2:失败，3取消")
    private Integer status;
    @ApiModelProperty(value = "交易传送入的唯一工单号，确保每一笔是唯一的")
    private String orderNo;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime updateTime;
    @ApiModelProperty(value = "完成时间")
    private LocalDateTime finishTime;
    @ApiModelProperty(value = "是否成功确认")
    private Integer successConfirm;
    @ApiModelProperty(value = "分区键(格式：yyyyMM)")
    private Integer partitionKey;
    @ApiModelProperty(value = "客户汇款交易唯一标识")
    private String orderId;
}
