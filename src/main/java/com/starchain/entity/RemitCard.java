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
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "用户Id 第三方传递过来")
    private Long userId;

    @ApiModelProperty(value = "汇款类型编码", example = "UQR")
    private String remitCode;

    @ApiModelProperty(value = "汇款卡标识", example = "CARD12345")
    private String cardId;

    @ApiModelProperty(value = "名", example = "John")
    private String remitFirstName;

    @ApiModelProperty(value = "姓", example = "Doe")
    private String remitLastName;

    @ApiModelProperty(value = "银行卡号", example = "1234567890123456")
    private String remitBankNo;

    @ApiModelProperty(value = "额外参数", example = "{\"swiftCode\":\"ABCDEF123\"}")
    private String extraParams;

    @ApiModelProperty(value = "汇款卡ID", example = "TPY12345")
    private String tpyCardId;

    @ApiModelProperty(value = "状态", example = "IN REVIEW")
    private String status;

    @ApiModelProperty(value = "状态描述", example = "IN REVIEW")
    private String statusDesc;

    @ApiModelProperty(value = "银行币种", example = "USD")
    private String toMoneyKind;

    @ApiModelProperty(value = "银行所属国家", example = "US")
    private String toMoneyCountry2;

    @ApiModelProperty(value = "创建时间", example = "2025-01-01T12:00:00")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间", example = "2025-01-01T12:00:00")
    private LocalDateTime updateTime;
}
