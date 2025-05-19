package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "钱包回调记录表") // 修改: 将 @ApiModel 替换为 @Schema
public class WalletCallbackRecord {
    @Schema(description = "主键", example = "1") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private Long id;

    @Schema(description = "通知类型，充值通知：deposit，提现通知：withdraw") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private String side;

    @Schema(description = "地址") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private String address;

    @Schema(description = "充值金额") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private BigDecimal amount;

    @Schema(description = "币种符号") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private String symbol;

    @Schema(description = "区块链上的交易id") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private String txid;

    @Schema(description = "当前确认次数") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private Integer confirmTimes;

    @Schema(description = "通知id") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private String notifyId;

    @Schema(description = "充值id") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private String depositId;

    @Schema(description = "0:等待确认 1成功，2:失败，3取消") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private Integer status;

    @Schema(description = "交易传送入的唯一工单号，确保每一笔是唯一的") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private String orderNo;

    @Schema(description = "创建时间") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private LocalDateTime createTime;

    @Schema(description = "创建时间") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private LocalDateTime updateTime;

    @Schema(description = "完成时间") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private LocalDateTime finishTime;

    @Schema(description = "是否成功确认") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private Integer successConfirm;

    @Schema(description = "分区键(格式：yyyyMM)") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private Integer partitionKey;

    @Schema(description = "客户汇款交易唯一标识") // 修改: 将 @ApiModelProperty 替换为 @Schema
    private String orderId;
}