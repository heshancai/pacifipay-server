package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-09
 * @Description
 */
@Data
@TableName("card_change_record")
@Schema(title = "CardChangeRecord对象", description = "换卡记录表")
public class CardChangeRecord {

    @Schema(description = "主键ID", example = "1")
    private Long id; // 主键ID

    @Schema(description = "商户端申请换卡订单ID", example = "2025010811491392234e378")
    private String orderId; // 商户端申请换卡订单ID

    @Schema(description = "商户ID", example = "MERCHANT123")
    private String merchantId; // 商户ID

    @Schema(description = "渠道编码，例如：TpyMDN6", example = "TpyMDN6")
    private String cardCode; // 渠道编码，例如：TpyMDN6

    @Schema(description = "新卡ID", example = "NEWCARD123")
    private String newCardId; // 新卡ID

    @Schema(description = "卡所属人账号", example = "ACCOUNT123")
    private String account; // 卡所属人账号

    @Schema(description = "新卡状态", example = "normal")
    private String cardStatus; // 新卡状态，例如：normal

    @Schema(description = "新卡有效截止日期，格式：MM/YY", example = "12/25")
    private String cardExpDate; // 新卡有效截止日期，格式：MM/YY

    @Schema(description = "新卡CVN", example = "123")
    private String cardCvn; // 新卡CVN

    @Schema(description = "新卡持卡人ID", example = "HOLDER123")
    private String cardHolderId; // 新卡持卡人ID

    @Schema(description = "新卡类型，例如：MasterCard/VISA", example = "MasterCard")
    private String bankType; // 新卡类型，例如：MasterCard/VISA

    @Schema(description = "新卡产品号，卡号前6位", example = "123456")
    private String channelCardBin; // 新卡产品号，卡号前6位

    @Schema(description = "新卡生成时间", example = "2023-10-15 12:30:45")
    private LocalDateTime createTime; // 新卡生成时间

    @Schema(description = "币种，默认：USD", example = "USD")
    private String currencyCode; // 币种，默认：USD

    @Schema(description = "新卡支持类型，例如：Only_Apple", example = "Only_Apple")
    private String bindType; // 新卡支持类型，例如：Only_Apple

    @Schema(description = "新卡使用类型，例如：M（多次卡）", example = "M")
    private String cardUse; // 新卡使用类型，例如：M（多次卡）

    @Schema(description = "新卡号", example = "1234567890123456")
    private String cardNo; // 新卡号

    @Schema(description = "换卡手续费", example = "10.00")
    private BigDecimal changeFee; // 换卡手续费

    @Schema(description = "销卡手续费", example = "5.00")
    private BigDecimal cardCloseFee; // 销卡手续费

    @Schema(description = "提现手续费", example = "2.00")
    private BigDecimal cardWithdrawFee; // 提现手续费

    @Schema(description = "充值手续费", example = "1.00")
    private BigDecimal cardRechargeFee; // 充值手续费

    @Schema(description = "开卡手续费", example = "3.00")
    private BigDecimal addCardFee; // 开卡手续费

    @Schema(description = "手续费总和", example = "21.00")
    private BigDecimal totalFee; // 手续费总和

    @Schema(description = "每日交易限额（默认币种:USD）", example = "1000.00")
    private BigDecimal dailyLimit; // 每日交易限额（默认币种:USD）

    @Schema(description = "单笔交易限额（默认币种:USD）", example = "500.00")
    private BigDecimal singleLimit; // 单笔交易限额（默认币种:USD）

    @Schema(description = "卡源唯一标识，旧卡ID", example = "OLDCARD123")
    private String cardId; // 卡源唯一标识，旧卡ID

    @Schema(description = "换卡流程状态", example = "PROCESSING")
    private String processStatus; // 换卡流程状态
}
