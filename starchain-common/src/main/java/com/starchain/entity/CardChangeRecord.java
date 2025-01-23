package com.starchain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-09
 * @Description
 */
@Data
@TableName("card_change_record") // 指定表名
@ApiModel(value = "CardChangeRecord对象", description = "换卡记录表")
public class CardChangeRecord {

    @ApiModelProperty(value = "主键ID", example = "1")
    private Long id; // 主键ID

    @ApiModelProperty(value = "商户端申请换卡订单ID", example = "2025010811491392234e378")
    private String orderId; // 商户端申请换卡订单ID

    @ApiModelProperty(value = "商户ID", example = "MERCHANT123")
    private String merchantId; // 商户ID

    @ApiModelProperty(value = "渠道编码", example = "TpyMDN6")
    private String cardCode; // 渠道编码，例如：TpyMDN6

    @ApiModelProperty(value = "新卡ID", example = "NEWCARD123")
    private String newCardId; // 新卡ID

    @ApiModelProperty(value = "卡所属人账号", example = "ACCOUNT123")
    private String account; // 卡所属人账号

    @ApiModelProperty(value = "新卡状态", example = "normal")
    private String cardStatus; // 新卡状态，例如：normal

    @ApiModelProperty(value = "新卡有效截止日期", example = "12/25")
    private String cardExpDate; // 新卡有效截止日期，格式：MM/YY

    @ApiModelProperty(value = "新卡CVN", example = "123")
    private String cardCvn; // 新卡CVN

    @ApiModelProperty(value = "新卡持卡人ID", example = "HOLDER123")
    private String cardHolderId; // 新卡持卡人ID

    @ApiModelProperty(value = "新卡类型", example = "MasterCard")
    private String bankType; // 新卡类型，例如：MasterCard/VISA

    @ApiModelProperty(value = "新卡产品号", example = "123456")
    private String channelCardBin; // 新卡产品号，卡号前6位

    @ApiModelProperty(value = "新卡生成时间", example = "2023-10-15 12:30:45")
    private LocalDateTime createTime; // 新卡生成时间

    @ApiModelProperty(value = "币种", example = "USD")
    private String currencyCode; // 币种，默认：USD

    @ApiModelProperty(value = "新卡支持类型", example = "Only_Apple")
    private String bindType; // 新卡支持类型，例如：Only_Apple

    @ApiModelProperty(value = "新卡使用类型", example = "M")
    private String cardUse; // 新卡使用类型，例如：M（多次卡）

    @ApiModelProperty(value = "新卡号", example = "1234567890123456")
    private String cardNo; // 新卡号

    @ApiModelProperty(value = "换卡手续费", example = "10.00")
    private BigDecimal changeFee; // 换卡手续费

    @ApiModelProperty(value = "销卡手续费", example = "5.00")
    private BigDecimal cardCloseFee; // 销卡手续费

    @ApiModelProperty(value = "提现手续费", example = "2.00")
    private BigDecimal cardWithdrawFee; // 提现手续费

    @ApiModelProperty(value = "充值手续费", example = "1.00")
    private BigDecimal cardRechargeFee; // 充值手续费

    @ApiModelProperty(value = "开卡手续费", example = "3.00")
    private BigDecimal addCardFee; // 开卡手续费

    @ApiModelProperty(value = "手续费总和", example = "21.00")
    private BigDecimal totalFee; // 手续费总和

    @ApiModelProperty(value = "每日交易限额（默认币种:USD）", example = "1000.00")
    private BigDecimal dailyLimit; // 每日交易限额（默认币种:USD）

    @ApiModelProperty(value = "单笔交易限额（默认币种:USD）", example = "500.00")
    private BigDecimal singleLimit; // 单笔交易限额（默认币种:USD）

    @ApiModelProperty(value = "卡源唯一标识，旧卡ID", example = "OLDCARD123")
    private String cardId; // 卡源唯一标识，旧卡ID

    @ApiModelProperty(value = "换卡流程状态", example = "PROCESSING")
    private String processStatus; // 换卡流程状态
}
