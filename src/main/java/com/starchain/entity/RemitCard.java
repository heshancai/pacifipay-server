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
    @ApiModelProperty(name = "用户ID", example = "123456")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(name = "用户ID", example = "123456")
    private Long userId;

    @ApiModelProperty(name = "渠道ID", example = "987654")
    private Long channelId;

    @ApiModelProperty(name = "汇款类型编码", example = "LNR_IND")
    private String remitCode;

    @ApiModelProperty(name = "汇款卡标识", example = "客户汇款卡唯一标识，唯一标识至少填写一个")
    private String cardId;

    @ApiModelProperty(name = "汇款卡ID", example = "Tpysh的唯一标识ID")
    private String tpyCardId;

    @ApiModelProperty(name = "名", example = "John")
    private String remitFirstName;

    @ApiModelProperty(name = "姓", example = "Doe")
    private String remitLastName;

    @ApiModelProperty(name = "银行卡号", example = "1234567890123456")
    private String remitBankNo;



    @ApiModelProperty(name = "状态", example = "SUCCESS")
    private String status;

    @ApiModelProperty(name = "状态描述", example = "操作成功")
    private String statusDesc;

    // 以下为 extraParams 对应的字段
    @ApiModelProperty(name = "Swift码", example = "ABCDEF123")
    private String swiftCode;

    @ApiModelProperty(name = "姓名", example = "John Doe")
    private String remitName;

    @ApiModelProperty(name = "银行编码", example = "BANK123")
    private String bankCode;

    @ApiModelProperty(name = "汇款银行名称", example = "Bank of America")
    private String remitBank;

    @ApiModelProperty(name = "汇款银行子行编码", example = "BRANCH123")
    private String remitBankBranchCode;

    @ApiModelProperty(name = "BSB码（澳大利亚）", example = "123456")
    private String bsbCode;

    @ApiModelProperty(name = "Sort Code（英国）", example = "12-34-56")
    private String sortCode;

    @ApiModelProperty(name = "ACH号码（美国）", example = "123456789")
    private String achNumber;

    @ApiModelProperty(name = "身份证号", example = "123456789012345678")
    private String idNumber;

    @ApiModelProperty(name = "汇款银行地址", example = "123 Main St, New York, NY 10001")
    private String remitBankAddress;

    @ApiModelProperty(name = "银行币种（ISO 3位代码）", example = "USD")
    private String toMoneyKind;

    @ApiModelProperty(name = "银行所属国家（ISO 2位代码）", example = "US")
    private String toMoneyCountry2;
}
