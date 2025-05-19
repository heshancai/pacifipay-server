package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2024-12-31
 * @Description 用户银行卡详情表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("card")
@Schema(title = "Card", description = "虚拟卡实体信息")
public class Card implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "用户Id", example = "CARD123456")
    private Long userId;

    @Schema(description = "卡类型编码", example = "123456")
    private String cardCode;

    @Schema(description = "卡预存订单号", example = "SO123456")
    private String saveOrderId;

    @Schema(description = "卡预存额度", example = "1000.00")
    private BigDecimal saveAmount;

    @Schema(description = "TPYSH持卡人ID,通过创建持卡人接口获取", example = "TPY123456")
    private String tpyshCardHolderId;

    // 下面的字段数据为接口返回的

    @Schema(description = "客户ID-银行卡端返回", example = "C123456")
    private String merchantId;

    @Schema(description = "物理类型-银行卡端返回，枚举值：Digital（数字卡），Object（实体卡）", example = "Digital")
    private String physicsType;

    @Schema(description = "实名类型-银行卡端返回，枚举值：NoKyc（匿名卡），Kyc（实名卡）", example = "Kyc")
    private String kycType;

    @Schema(description = "发卡行类型-银行卡端返回", example = "MasterCard, VISA, UnionPay")
    private String bankType;

    @Schema(description = "卡ID-银行卡端返回", example = "CARD123456")
    private String cardId;

    @Schema(description = "卡号-银行卡端返回", example = "1234567890123456")
    private String cardNo;

    @Schema(description = "卡余额", example = "0.00")
    private BigDecimal cardAmount;

    @Schema(description = "卡CVV2-银行卡端返回", example = "123")
    private String cardCvn;

    @Schema(description = "卡有效期-银行卡端返回", example = "MM/yy")
    private String cardExpDate;

    @Schema(description = "消费单笔限额-银行卡端返回", example = "消费单笔限额，可修改")
    private BigDecimal singleLimit;

    @Schema(description = "卡创建时间-银行卡端返回", example = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    @Schema(description = "开卡手续费", example = "50.00")
    private BigDecimal cardFee;

    @Schema(description = "销卡手续费", example = "10.00")
    private BigDecimal handleFeeAmount;

    @Schema(description = "预存款客户订单号同saveOrderId保持一致", example = "SO123456")
    private String saveTradeId;

    @Schema(description = "卡状态创建状态", example = "0 创建中, 1 创建成功, 2 创建失败")
    private Integer createStatus;

    @Schema(description = "卡状态-银行卡端返回", example = "normal, initFail, unactivated, activating, freezing, cancelled")
    private String cardStatus;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime localCreateTime;

    @Schema(description = "开卡完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime finishTime;

    @Schema(description = "销卡时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime cancelTime;

    @Schema(description = "数据更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime localUpdateTime;
}
