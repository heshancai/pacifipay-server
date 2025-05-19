package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-06
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("card_open_callback_record")
@Schema(title = "CardOpenCallbackRecord", description = "卡开通通知记录实体")
public class CardOpenCallbackRecord {

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "通知ID")
    private String notifyId;

    @Schema(description = "卡类型")
    private String cardCode;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "卡ID")
    private String cardId;

    @Schema(description = "卡号")
    private String cardNo;

    @Schema(description = "卡CVV2")
    private String cardCvn;

    @Schema(description = "卡有效期")
    private String cardExpDate;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "开卡手续费金额")
    private BigDecimal actual;

    @Schema(description = "重试次数")
    private Integer retries;

    @Schema(description = "卡创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime localCreateTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime localUpdateTime;

    @Schema(description = "卡开通时间-回调得到")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime finishTime;
}
