package com.starchain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@ApiModel(value = "Card", description = "虚拟卡实体信息")
public class Card {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "卡类型编码", example = "123456")
    private String cardCode;

    @ApiModelProperty(value = "卡预存订单号", example = "SO123456")
    private String saveOrderId;

    @ApiModelProperty(value = "卡预存额度", example = "1000.00")
    private BigDecimal saveAmount;


    @ApiModelProperty(value = "TPYSH持卡人ID,通过创建持卡人接口获取", example = "TPY123456")
    private String tpyshCardHolderId;

    // 下面的字段数据为接口返回的

    @ApiModelProperty(value = "客户ID-银行卡端返回", example = "C123456")
    private String merchantId;

    @ApiModelProperty(value = "物理类型-银行卡端返回", example = "枚举值，范围Digital：数字卡，Object：实体卡。")
    private String physicsType;

    @ApiModelProperty(value = "实名类型-银行卡端返回", example = "枚举值，范围NoKyc：匿名卡，Kyc：实名卡。")
    private String kycType;

    @ApiModelProperty(value = "发卡行类型-银行卡端返回", example = "枚举值，范围MasterCard，VISA，UnionPay")
    private String bankType;

    @ApiModelProperty(value = "卡ID-银行卡端返回", example = "CARD123456")
    private String cardId;

    @ApiModelProperty(value = "卡号-银行卡端返回", example = "1234567890123456")
    private String cardNo;

    @ApiModelProperty(value = "卡CVV2-银行卡端返回", example = "123")
    private String cardCvn;

    @ApiModelProperty(value = "卡有效期-银行卡端返回", example = "格式MM/yy")
    private String cardExpDate;

    @ApiModelProperty(value = "消费单笔限额-银行卡端返回", example = "消费单笔限额，可修改")
    private BigDecimal singleLimit;

    @ApiModelProperty(value = "卡创建时间-银行卡端返回", example = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    @ApiModelProperty(value = "开卡手续费", example = "50.00")
    private BigDecimal cardFee;

    @ApiModelProperty(value = "销卡手续费")
    private BigDecimal handleFeeAmount;

    @ApiModelProperty(value = "SO123456", example = "预存款客户订单号同saveOrderId 保持一致")
    private String saveTradeId;

    @ApiModelProperty(value = "卡状态创建状态", example = "卡卡创建状态：0 创建中 1 创建成功 2 创建失败 3 已注销")
    private Integer status;

    @ApiModelProperty(value = "卡状态-银行卡端返回", example = "normal initFail unactivated activating freezing cancelled")
    private String cardStatus;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime localCreateTime;

    @ApiModelProperty(value = "开卡完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime finishTime;


    @ApiModelProperty(value = "销卡时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime cancelTime;


    @ApiModelProperty(value = "数据更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime localUpdateTime;

}
