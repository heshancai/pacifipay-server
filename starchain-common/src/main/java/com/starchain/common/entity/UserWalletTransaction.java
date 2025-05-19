package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-10
 * @Description
 */
@Data
@Accessors(chain = true)
@TableName("user_wallet_transaction")
@Schema(description = "用户钱包流水")
@Builder
public class UserWalletTransaction {

    @Schema(description = "主键ID", example = "1")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "会员ID", example = "12345")
    private Long userId;

    @Schema(description = "币种名称", example = "CNY")
    private String coinName;

    @Schema(description = "钱包原始余额", example = "1000.00")
    private BigDecimal balance;

    @Schema(description = "充值金额/汇款金额/提现金额/充值到卡金额/注销回退金额/开卡费/月服务费", example = "100.00")
    private BigDecimal amount;

//    @ApiModelProperty(value = "手续费")
//    private BigDecimal fee;

//    @ApiModelProperty(value = "实际到账金额")
//    private BigDecimal actAmount;

    @Schema(description = "操作后的钱包余额", example = "900.00")
    private BigDecimal finaBalance;

    @Schema(description = "流水类型 1:充币 2.提币 3.全球汇款 4、充值到卡 5、汇款撤销  6、开卡费 7、预存费 8、月服务费 9、充值手续费 10、销卡回退金额 11 销卡手续费 12 汇款手续费 ", example = "1")
    private Integer type;

    @Schema(description = "业务编号", example = "BUS12345")
    private String businessNumber;

    @Schema(description = "创建时间", example = "2023-10-15 12:30:45")
    private LocalDateTime createTime;

    @Schema(description = "分区键(格式:yyyyMMdd)", example = "20231015")
    private Integer partitionKey;

    @Schema(description = "备注信息", example = "这是一条备注信息")
    private String remark;

    @Schema(description = "地址", example = "0x1234567890abcdef")
    private String address;

    @Schema(description = "链上ID", example = "TX1234567890")
    private String txId;

    @Schema(description = "链上交易ID", example = "TRADE1234567890")
    private String tradeId;

    @Schema(description = "订单id", example = "ORDER1234567890")
    private String orderId;
}
