package com.starchain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author
 * @date 2025-01-02
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_wallet")
@ApiModel(value = "UserWallet", description = "用户钱包信息")
public class UserWallet {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键", example = "1")
    private Long id;

    @ApiModelProperty(value = "渠道ID", example = "1001")
    private Long channelId;

    @ApiModelProperty(value = "用户ID", example = "12345")
    private Long userId;

    @ApiModelProperty(value = "用户持卡信息ID", example = "2001")
    private Long cardHolderId;

    @ApiModelProperty(value = "USDT 钱包地址", example = "1AaBbCcDdEeFfGgHh12345")
    private String usdtAddress;

    @ApiModelProperty(value = "USDT 支持的网络类型：1=TRC20, 2=BEP20", example = "1")
    private Integer usdtNetwork;

    @ApiModelProperty(value = "地址余额", example = "100.123456789012345678")
    private BigDecimal balance;

    @ApiModelProperty(value = "币种符号 ", example = " USDT-TRC20,USDT-BEP20")
    private String coinId;

    @ApiModelProperty(value = "状态", example = "状态：1:有效,0:无效")
    private Integer status;

    @ApiModelProperty(value = "创建时间", example = "2025-01-01T12:00:00")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间", example = "2025-01-01T12:00:00")
    private LocalDateTime updateTime;
}

