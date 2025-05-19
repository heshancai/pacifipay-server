package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author
 * @date 2025-01-10
 * @Description
 */
@Data
@Accessors(chain = true)
@TableName("sys_coin")
@Schema(description = "数字货币基础信息表")
public class SysCoin {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "货币名称", required = true)
    private String coinName;

    @Schema(description = "货币中文名称", required = true)
    private String nameCn;

    @Schema(description = "数字货币单位", required = true)
    private String unit;

    @Schema(description = "币种编号")
    private String coinType;

    @Schema(description = "币种精度")
    private Integer scale;

    @Schema(description = "提币手续费类型：1-固定金额 2-按比例", required = true)
    private Integer withdrawFeeType;

    @Schema(description = "提币手续费收取值", required = true)
    private BigDecimal withdrawFee;

    @Schema(description = "充币手续费类型 1-固定金额 2-按比例", required = true)
    private Integer depositFeeType;

    @Schema(description = "充币手续费取值", required = true)
    private BigDecimal depositFee;

    @Schema(description = "状态")
    private Integer status;
}