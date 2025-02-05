package com.starchain.common.dubbo;

import com.starchain.common.entity.dto.CardHolderDto;
import com.starchain.common.result.ClientResponse;

/**
 * @author
 * @date 2025-01-27
 * @Description
 */
public interface ICardHolderManagerProvider {

    /**
     * 查询用户持卡人
     * @return
     */
    ClientResponse queryCardManagePage(CardHolderDto dto);
}
