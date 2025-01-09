package com.starchain.entity.dto;

/**
 * @author
 * @date 2024-12-19
 * @Description
 */

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Data
public class CardDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cardCode;
    private String saveOrderId;
    private String saveAmount;
    private String tpyshCardHolderId;
    @ApiModelProperty(value = "卡ID-银行卡端返回", example = "CARD123456")
    private String cardId;
}
