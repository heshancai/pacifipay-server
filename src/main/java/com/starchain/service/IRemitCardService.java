package com.starchain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starchain.entity.RemitCard;
import com.starchain.entity.RemitCardNotify;

/**
 * @author
 * @date 2025-01-02
 * @Description
 */
public interface IRemitCardService extends IService<RemitCard> {
    /**
     * 添加收款卡信息
     * @param remitCard
     * @return
     */
    Boolean addRemitCard(RemitCard remitCard);
}
