package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-06
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("card_withdraw_callback_record")
@Schema(description = "卡提现通知记录实体")
public class CardWithdrawCallbackRecord {

    @Schema(description = "主键ID", example = "1")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "通知ID", example = "NOTIFY123456789")
    private String notifyId;

    @Schema(description = "卡类型", example = "CARD_TYPE_001")
    private String cardCode;

    @Schema(description = "业务类型", example = "WITHDRAW")
    private String businessType;

    @Schema(description = "卡ID", example = "1234567890")
    private String cardId;

    @Schema(description = "卡号", example = "1234567890123456")
    private String cardNo;

    @Schema(description = "状态", example = "SUCCESS")
    private String status;

    @Schema(description = "状态描述", example = "提现成功")
    private String statusDesc;

    @Schema(description = "商户订单ID", example = "MCH_ORDER_123456789")
    private String mchOrderId;

    @Schema(description = "申请提现金额", example = "100.00")
    private BigDecimal withdraw;

    @Schema(description = "提现手续费", example = "5.00")
    private BigDecimal handleFee;

    @Schema(description = "实际到账金额", example = "95.00")
    private BigDecimal actual;

    @Schema(description = "创建时间", example = "2023-10-15 12:30:45")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2023-10-15 12:30:45")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}