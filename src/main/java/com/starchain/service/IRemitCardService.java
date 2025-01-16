package com.starchain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starchain.entity.RemitCard;
import com.starchain.entity.dto.RemitCardDto;
import com.starchain.entity.dto.RemitRateDto;

/**
 * @author
 * @date 2025-01-02
 * @Description
 */
public interface IRemitCardService extends IService<RemitCard> {
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


    /**
     * 修改收款卡信息
     * @param remitCardDto
     * @return
     */
    Boolean updateRemitCard(RemitCardDto remitCardDto);

    /**
     * 删除收款卡信息
     * @param remitCardDto
     * @return
     */
    Boolean delRemitCard(RemitCardDto remitCardDto);

    /**
     * 汇款单详情
     * @param remitCardDto
     * @return
     */
    Boolean remitDetail(RemitCardDto remitCardDto);

    /**
     * 获取收款卡信息
     * @param remitCardDto
     * @return
     */
    RemitCard getRemitCard(RemitCardDto remitCardDto);
}
