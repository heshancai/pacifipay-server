package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

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
@Builder
@NoArgsConstructor // 生成无参构造函数
@AllArgsConstructor // 生成全参构造函数
public class UserWallet {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键", example = "1")
    private Long id;
    @ApiModelProperty(value = "用户ID", example = "12345")
    private Long userId;
    @ApiModelProperty(value = "渠道Id", example = "12345")
    private Long channelId;

    @ApiModelProperty(value = "USDT 钱包地址", example = "1AaBbCcDdEeFfGgHh12345")
    private String address;

    @ApiModelProperty(value = "USDT 支持的网络类型：1=TRC20, 2=BEP20", example = "1")
    private Integer usdtNetwork;


    @ApiModelProperty(value = "币种符号 ", example = " USDT-TRC20,USDT-BEP20")
    private String coinId;

    @ApiModelProperty(value = "钱包状态", example = "钱包是否锁定 0:锁定 1正常")
    private Integer lockStatus;

    @ApiModelProperty(value = "创建时间", example = "2025-01-01T12:00:00")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间", example = "2025-01-01T12:00:00")
    private LocalDateTime updateTime;
}

