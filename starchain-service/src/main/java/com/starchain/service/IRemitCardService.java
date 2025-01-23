package com.starchain.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.starchain.common.entity.RemitCard;
import com.starchain.common.entity.dto.RemitCardDto;
import com.starchain.common.entity.dto.RemitRateDto;

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
    RemitRateDto getRemitRate(String token, RemitRateDto remitRateDto);


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
    JSONObject remitDetail(RemitCardDto remitCardDto);

    /**
     * 获取收款卡信息
     * @param remitCardDto
     * @return
     */
    RemitCard getRemitCard(RemitCardDto remitCardDto);
}
