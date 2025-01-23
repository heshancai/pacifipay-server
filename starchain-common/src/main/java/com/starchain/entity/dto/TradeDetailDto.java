package com.starchain.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author
 * @date 2024-12-26
 * @Description 查询商户交易明细 分页参数
 */
@Data
@Accessors
public class TradeDetailDto extends PageDto{
    private String orderType;
    private String billsTimeStart;
    private String billsTimeEnd;
    private String orderBy;

}
