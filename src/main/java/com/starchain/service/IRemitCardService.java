package com.starchain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starchain.entity.RemitCard;
import com.starchain.entity.RemitCardNotify;
import com.starchain.entity.dto.RemitCardDto;
import com.starchain.entity.dto.RemitRateDto;

/**
 * @author
 * @date 2025-01-02
 * @Description
 */
public interface IRemitCardService extends IService<RemitCard>,IMiPayNotifyService {
    /**
     * 添加收款卡信息
     * @param
     * @return
     */
    Boolean addRemitCard(RemitCardDto remitCardDto);

    /**
     * 获取汇款汇率
     * @param remitRateDto
     * @return
     */
    RemitRateDto getRemitRate(String token,RemitRateDto remitRateDto);


}
