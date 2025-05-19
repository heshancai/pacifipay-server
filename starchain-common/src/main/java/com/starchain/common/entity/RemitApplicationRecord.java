package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-07
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("remit_application_record")
@Schema(description = "汇款申请记录表")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RemitApplicationRecord {
    @Schema(description = "主键ID", example = "1")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户Id", example = "1")
    private Long userId;

    @Schema(description = "渠道ID", example = "987654")
    private Long businessId;

    @Schema(description = "汇款类型编码", example = "ELR")
    private String remitCode;

    @Schema(description = "扣款对应币种", example = "CNY")
    private String fromMoneyKind;

    @Schema(description = "汇款目标币种", example = "CNY")
    private String toMoneyKind;

    @Schema(description = "汇款目标金额", example = "可以是 人民币和美元")
    private BigDecimal toAmount;

    @Schema(description = "扣款金额", example = "扣除的是美元")
    private BigDecimal fromAmount;

    @Schema(description = "汇款交易单号", example = "ORDER123456789")
    private String orderId;

    @Schema(description = "发送端汇款", example = "6.500000")
    private BigDecimal remitRate;

    @Schema(description = "返回端汇率", example = "6.500000")
    private BigDecimal tradeRate;

    @Schema(description = "汇款人姓名拼音大写字母", example = "ZHANGSAN")
    private String remitName;

    @Schema(description = "汇款人姓氏", example = "Zhang")
    private String remitLastName;

    @Schema(description = "汇款人名字", example = "San")
    private String remitFirstName;

    @Schema(description = "收款人银行卡号", example = "6222021234567890")
    private String remitBankNo;

    @Schema(description = "汇款目标国家编码", example = "CHN")
    private String toMoneyCountry3;

    @Schema(description = "银行编码", example = "ICBC")
    private String bankCode;

    @Schema(description = "支行编码", example = "ICBC0001")
    private String bankBranchCode;

    @Schema(description = "Tpy汇款卡ID", example = "TPY123456789")
    private String remitTpyCardId;

    @Schema(description = "收款人手机号", example = "13800138000")
    private String mobileNumber;

    @Schema(description = "收款人邮箱", example = "example@example.com")
    private String email;

    @Schema(description = "创建时间", example = "2023-10-01 12:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2023-10-01 12:00:00")
    private LocalDateTime updateTime;

    @Schema(description = "汇款申请状态", example = "创建状态 0创建中 1 创建成功  2 创建失败 3撤销")
    private Integer status;

    @Schema(description = "备注信息", example = "这是一条测试汇款申请")
    private String remarks;

    @Schema(description = "手续费对应币种", example = "CNY")
    private String handlingFeeMoneyKind;

    @Schema(description = "手续费金额", example = "50.00")
    private BigDecimal handlingFeeAmount;

    //    @ApiModelProperty(value = "充值交易单号", example = "RECHARGE123456789")
//    private String rechargeOrderId;

    @Schema(description = "交易流水号", example = "TRADE123456789")
    private String tradeId;

    @Schema(description = "PIN Number", example = "123456")
    private String pinNumber;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime finishTime;
}