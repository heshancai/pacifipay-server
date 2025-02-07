package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "UserWalletTransaction对象", description = "用户钱包流水")
@Builder
public class UserWalletTransaction {

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "会员ID")
    private Long userId;

    @ApiModelProperty(value = "币种名称")
    private String coinName;

    @ApiModelProperty(value = "钱包原始余额")
    private BigDecimal balance;

    @ApiModelProperty(value = "充值金额/汇款金额/提现金额/充值到卡金额/注销卡到账金额/开卡费/月服务费")
    private BigDecimal amount;

    @ApiModelProperty(value = "手续费")
    private BigDecimal fee;

    @ApiModelProperty(value = "实际到账金额")
    private BigDecimal actAmount;

    @ApiModelProperty(value = "操作后的钱包余额")
    private BigDecimal finaBalance;

    @ApiModelProperty(value = "流水类型 1:充币 2.提币 3.全球汇款 4、充值到卡 5、汇款撤销 6、注销卡 7、开卡费 8、预存费 9、月服务费 ")
    private Integer type;

    @ApiModelProperty(value = "业务编号")
    private String businessNumber;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "分区键(格式:yyyyMMdd)")
    private Integer partitionKey;

    @ApiModelProperty(value = "备注信息")
    private String remark;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "链上ID")
    private String txId;

    @ApiModelProperty(value = "链上交易ID")
    private String tradeId;

    @ApiModelProperty(value = "订单id")
    private String orderId;
}
