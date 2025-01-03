package com.starchain.entity.dto;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author
 * @date 2025-01-03
 * @Description
 */
@Data
@Accessors(chain = true)
public class MiPayNotifyDto {
    @ApiModelProperty(value = "主键 ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "通知的唯一ID", example = "NOTIFY_123456")
    private String notifyId;

    @ApiModelProperty(value = "卡类型", example = "VISA")
    private String cardCode;

    @ApiModelProperty(value = "业务类型", example = "CardOpen")
    private String businessType;

    @ApiModelProperty(value = "卡ID", example = "CARD_123456")
    private String cardId;

    @ApiModelProperty(value = "卡号", example = "1234567890123456")
    private String cardNo;

    @ApiModelProperty(value = "卡安全码", example = "123")
    private String cardCvn;

    @ApiModelProperty(value = "卡有效期", example = "12/25")
    private String cardExpDate;

    @ApiModelProperty(value = "状态", example = "SUCCESS")
    private String status;

    @ApiModelProperty(value = "状态描述", example = "CardOpen success")
    private String statusDesc;

    @ApiModelProperty(value = "金额信息列表", example = "[{\"actual\": 10.50}]")
    private List<JSONObject> amount;
}
