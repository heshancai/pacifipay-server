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
     * 卡开通记录列表
     *
     * @return
     */
    ClientResponse queryCardManagePage(CardDto cardDto);


    /**
     * 修改卡限额
     *
     * @param cardDto
     * @return
     */
    ClientResponse updateLimit(CardDto cardDto);


    /**
     * 锁定卡
     * @param cardDto
     * @return
     */
    ClientResponse lockCard(CardDto cardDto);

    /**
     * 解锁卡
     * @param cardDto
     * @return
     */
    ClientResponse unlockCard(CardDto cardDto);
}
