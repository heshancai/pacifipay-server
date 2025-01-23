package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "SysCoin对象", description = "数字货币基础信息表")
public class SysCoin {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "货币名称", required = true)
    private String coinName;

    @ApiModelProperty(value = "货币中文名称", required = true)
    private String nameCn;

    @ApiModelProperty(value = "数字货币单位", required = true)
    private String unit;

    @ApiModelProperty(value = "币种编号")
    private String coinType;

    @ApiModelProperty(value = "币种精度")
    private Integer scale;

    @ApiModelProperty(value = "提币手续费类型：1-固定金额 2-按比例", required = true)
    private Integer withdrawFeeType;

    @ApiModelProperty(value = "提币手续费收取值", required = true)
    private BigDecimal withdrawFee;

    @ApiModelProperty(value = "充币手续费类型 1-固定金额 2-按比例", required = true)
    private Integer depositFeeType;

    @ApiModelProperty(value = "充币手续费取值", required = true)
    private BigDecimal depositFee;

    @ApiModelProperty(value = "状态")
    private Integer status;
}
