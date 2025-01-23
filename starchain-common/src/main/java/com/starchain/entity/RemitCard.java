package com.starchain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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

    @ApiModelProperty(value = "创建状态", example = "创建状态：0 创建中 1 创建成功 2 创建失败")
    private Integer createStatus;

    @ApiModelProperty(name = "创建收款卡 的 响应状态", example = "SUCCESS")
    private String status;

    @ApiModelProperty(name = "状态描述", example = "操作成功")
    private String statusDesc;

    @ApiModelProperty(name = "删除收款卡状态", example = "1 已删除 0 未删除")
    private Integer cancelStatus;

    // 以下为 extraParams 对应的字段
    @ApiModelProperty(name = "Swift码", example = "ABCDEF123")
    private String swiftCode;

    @ApiModelProperty(name = "姓名", example = "John Doe")
    private String remitName;

    @ApiModelProperty(name = "邮箱", example = "john.doe@example.com")
    private String email;

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

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "添加收款卡完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime finishTime;
}
