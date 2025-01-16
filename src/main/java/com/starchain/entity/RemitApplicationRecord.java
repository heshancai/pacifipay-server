package com.starchain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@ApiModel(value = "RemitApplicationRecord", description = "汇款申请记录表")
@Builder
public class RemitApplicationRecord {
    @ApiModelProperty(value = "主键ID", example = "1")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    @ApiModelProperty(name = "渠道ID", example = "987654")
    private Long channelId;

    @ApiModelProperty(value = "汇款类型编码", example = "ELR")
    private String remitCode;

    @ApiModelProperty(value = "扣款对应币种", example = "CNY")
    private String fromMoneyKind;

    @ApiModelProperty(value = "汇款目标币种", example = "CNY")
    private String toMoneyKind;

    @ApiModelProperty(value = "汇款目标金额", example = "可以是 人民币和美元")
    private BigDecimal toAmount;

    @ApiModelProperty(value = "扣款金额", example = "扣除的是美元 包含手续费")
    private BigDecimal fromAmount;

    @ApiModelProperty(value = "汇款交易单号", example = "ORDER123456789")
    private String orderId;

    @ApiModelProperty(value = "汇款汇率", example = "6.500000")
    private BigDecimal remitRate;

    @ApiModelProperty(value = "银行端返回的汇款汇率", example = "6.500000")
    private BigDecimal tradeRate;

    @ApiModelProperty(value = "额外参数", example = "{\"key\": \"value\"}")
    private String extraParams;

    @ApiModelProperty(value = "汇款人姓名拼音大写字母", example = "ZHANGSAN")
    private String remitName;

    @ApiModelProperty(value = "汇款人姓氏", example = "Zhang")
    private String remitLastName;

    @ApiModelProperty(value = "汇款人名字", example = "San")
    private String remitFirstName;

    @ApiModelProperty(value = "收款人银行卡号", example = "6222021234567890")
    private String remitBankNo;

    @ApiModelProperty(value = "汇款目标国家编码", example = "CHN")
    private String toMoneyCountry3;

    @ApiModelProperty(value = "银行编码", example = "ICBC")
    private String bankCode;

    @ApiModelProperty(value = "支行编码", example = "ICBC0001")
    private String bankBranchCode;

    @ApiModelProperty(value = "Tpy汇款卡ID", example = "TPY123456789")
    private String remitTpyCardId;

    @ApiModelProperty(value = "收款人手机号", example = "13800138000")
    private String mobileNumber;

    @ApiModelProperty(value = "收款人邮箱", example = "example@example.com")
    private String email;

    @ApiModelProperty(value = "创建时间", example = "2023-10-01 12:00:00")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间", example = "2023-10-01 12:00:00")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "汇款申请状态", example = "创建状态 0创建中 1 创建成功  2 创建失败 3撤销")
    private Integer status;

    @ApiModelProperty(value = "备注信息", example = "这是一条测试汇款申请")
    private String remarks;

    @ApiModelProperty(value = "手续费对应币种", example = "CNY")
    private String handlingFeeMoneyKind;

    @ApiModelProperty(value = "手续费金额", example = "50.00")
    private BigDecimal handlingFeeAmount;

    @ApiModelProperty(value = "充值交易单号", example = "RECHARGE123456789")
    private String rechargeOrderId;

    @ApiModelProperty(value = "交易流水号", example = "TRADE123456789")
    private String tradeId;

    @ApiModelProperty(value = "PIN Number", example = "123456")
    private String pinNumber;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime finishTime;
}
