package com.starchain.entity.dto;

import com.starchain.entity.CardHolder;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @author
 * @date 2025-01-03
 * @Description
 */
@Data
@ApiModel(value = "持卡Dto", description = "持卡Dto")
public class CardHolderDto extends CardHolder  implements Serializable {
    private static final long serialVersionUID = 1L;
}
