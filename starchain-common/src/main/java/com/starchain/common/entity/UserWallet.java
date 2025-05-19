package com.starchain.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(title = "UserWallet", description = "用户钱包信息")
@Builder
@NoArgsConstructor // 生成无参构造函数
@AllArgsConstructor // 生成全参构造函数
public class UserWallet {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "用户ID", example = "12345")
    private Long userId;

    @Schema(description = "商家Id", example = "12345")
    private Long businessId;

    @Schema(description = "USDT 钱包地址", example = "1AaBbCcDdEeFfGgHh12345")
    private String address;

    @Schema(description = "USDT 支持的网络类型：1=TRC20, 2=BEP20", example = "1")
    private Integer usdtNetwork;

    @Schema(description = "币种符号", example = "USDT-TRC20,USDT-BEP20")
    private String coinId;

    @Schema(description = "钱包状态", example = "钱包是否锁定 0:锁定 1正常")
    private Integer lockStatus;

    @Schema(description = "创建时间", example = "2025-01-01T12:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2025-01-01T12:00:00")
    private LocalDateTime updateTime;
}
