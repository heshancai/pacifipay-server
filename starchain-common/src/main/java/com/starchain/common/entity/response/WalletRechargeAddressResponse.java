package com.starchain.common.entity.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author
 * @date 2025-01-13
 * @Description
 */
@Data
public class WalletRechargeAddressResponse {
    @Schema(description = "地址id")
    private String addressId;

    @Schema(description = "状态")
    private Integer state;

    @Schema(description = "是否使用")
    private Integer inUse;

    @Schema(description = "币种名称")
    private String currencySymbol;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "批次")
    private String batchNo;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "修改时间")
    private String modifyTime;

    @Schema(description = "钱包资产账户id")
    private String accountId;

    @Schema(description = "钱包id")
    private String walletId;
}