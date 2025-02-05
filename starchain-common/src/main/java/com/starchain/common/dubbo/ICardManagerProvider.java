package com.starchain.common.dubbo;

import com.starchain.common.entity.dto.CardDto;
import com.starchain.common.result.ClientResponse;

/**
 * @author
 * @date 2025-01-27
 * @Description
 */
public interface ICardManagerProvider {
    /**
     * 查询卡列表
     * @return
     */
    ClientResponse queryCardManagePage(CardDto cardDto);
}
