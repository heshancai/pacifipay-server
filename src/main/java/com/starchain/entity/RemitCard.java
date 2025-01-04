package com.starchain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-02
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("remit_card")
@ApiModel(value = "RemitCard", description = "收款卡记录表")
public class RemitCard {

    @ApiModelProperty(value = "用户ID", example = "123456")
    private Long userId;

    @ApiModelProperty(value = "渠道ID", example = "987654")
    private Long channelId;

    @ApiModelProperty(value = "汇款类型编码", example = "LNR_IND")
    private String remitCode;

    @ApiModelProperty(value = "卡ID", example = "CARD123456789")
    private String cardId;

    @ApiModelProperty(value = "状态", example = "SUCCESS")
    private String status;

    @ApiModelProperty(value = "状态描述", example = "操作成功")
    private String statusDesc;

    // 以下为 extraParams 对应的字段
    @ApiModelProperty(value = "Swift码", example = "ABCDEF123")
    private String swiftCode;

    @ApiModelProperty(value = "姓名", example = "John Doe")
    private String remitName;

    @ApiModelProperty(value = "银行编码", example = "BANK123")
    private String bankCode;

    @ApiModelProperty(value = "汇款银行名称", example = "Bank of America")
    private String remitBank;

    @ApiModelProperty(value = "汇款银行子行编码", example = "BRANCH123")
    private String remitBankBranchCode;

    @ApiModelProperty(value = "BSB码（澳大利亚）", example = "123456")
    private String bsbCode;

    @ApiModelProperty(value = "Sort Code（英国）", example = "12-34-56")
    private String sortCode;

    @ApiModelProperty(value = "ACH号码（美国）", example = "123456789")
    private String achNumber;

    @ApiModelProperty(value = "身份证号", example = "123456789012345678")
    private String idNumber;

    @ApiModelProperty(value = "汇款银行地址", example = "123 Main St, New York, NY 10001")
    private String remitBankAddress;

    @ApiModelProperty(value = "银行币种（ISO 3位代码）", example = "USD")
    private String toMoneyKind;

    @ApiModelProperty(value = "银行所属国家（ISO 2位代码）", example = "US")
    private String toMoneyCountry2;
}
