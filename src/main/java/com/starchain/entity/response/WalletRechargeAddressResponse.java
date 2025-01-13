package com.starchain.entity.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author
 * @date 2025-01-13
 * @Description
 */
@Data
public class WalletRechargeAddressResponse {
    @ApiModelProperty("地址id")
    private String addressId;

    @ApiModelProperty("状态")
    private Integer state;

    @ApiModelProperty("是否使用")
    private Integer inUse;

    @ApiModelProperty("币种名称")
    private String currencySymbol;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("批次")
    private String batchNo;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("修改时间")
    private String modifyTime;

    @ApiModelProperty("钱包资产账户id")
    private String accountId;

    @ApiModelProperty("钱包id")
    private String walletId;
}
