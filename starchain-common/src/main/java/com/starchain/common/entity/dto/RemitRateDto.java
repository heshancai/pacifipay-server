package com.starchain.common.entity.dto;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author
 * @date 2025-01-07
 * @Description 获取汇款汇率
 */
@Data
@Schema(title = "获取汇款汇率-PageDto", description = "获取汇款汇率")
@Accessors(chain = true)
public class RemitRateDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "汇款类型编码", example = "例：CNY、USD")
    private String remitCode;

    @Schema(description = "汇款目标币种", example = "例：CNY、USD。")
    private String toMoneyKind;

    @Schema(description = "银行编码", example = "BANK123")
    private String bankCode;

    @Schema(description = "汇款国家编码", example = "BANK123")
    private String toMoneyCountry3;

    @Schema(description = "汇款汇率", example = "6.5000")
    private BigDecimal tradeRate;

    @Schema(description = "额外参数", example = "{\"swiftCode\":\"ABCDEF123\"}")
    private List<JSONObject> extraParams;
}