package com.starchain.entity.dto;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "获取汇款汇率-PageDto", description = "获取汇款汇率")
@Accessors(chain = true)
public class RemitRateDto implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(name = "汇款类型编码", example = "例：CNY、USD")
    private String remitCode;
    @ApiModelProperty(name = "汇款目标币种", example = "例：CNY、USD。")
    private String toMoneyKind;
    @ApiModelProperty(name = "银行编码", example = "BANK123")
    private String bankCode;
    @ApiModelProperty(name = "汇款国家编码", example = "BANK123")
    private String toMoneyCountry3;
    @ApiModelProperty(name = "汇款汇率", example = "汇款汇率")
    private BigDecimal tradeRate;
    @ApiModelProperty(value = "额外参数", example = "{\"swiftCode\":\"ABCDEF123\"}")
    private List<JSONObject> extraParams;
}
