package com.starchain.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.config.PacificPayConfig;
import com.starchain.constants.CardUrlConstants;
import com.starchain.dao.CardHolderMapper;
import com.starchain.entity.CardHolder;
import com.starchain.enums.CardCodeEnum;
import com.starchain.exception.StarChainException;
import com.starchain.util.TpyshUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author
 * @date 2024-12-30
 * @Description
 */
@Component
public class CardHolderService extends ServiceImpl<CardHolderMapper, CardHolder> {


    @Autowired
    private PacificPayConfig pacificPayConfig;

    public Boolean addCardHolder(CardHolder cardHolder) {
        String token = null;
        try {
            token = TpyshUtils.getToken(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret());
            System.out.println("token=>" + token);
            cardHolder.setCardCode(cardHolder.getCardCode());
            // 生成 商户申请创建持卡人的唯一值
            cardHolder.setMerchantCardHolderId(cardHolder.getMerchantCardHolderId());
            cardHolder.setFirstName(cardHolder.getFirstName());
            cardHolder.setLastName(cardHolder.getLastName());
            cardHolder.setPhoneCountry(cardHolder.getPhoneCountry());
            cardHolder.setPhoneNumber(cardHolder.getPhoneNumber());
            cardHolder.setIdAccount(cardHolder.getIdAccount());
            cardHolder.setEmail(cardHolder.getEmail());
            cardHolder.setAddress(cardHolder.getAddress());
            cardHolder.setGender(cardHolder.getGender());
            cardHolder.setBirthday(cardHolder.getBirthday());
            cardHolder.setStatus(0);
            String str = TpyshUtils.doPost(CardUrlConstants.BASEURL + CardUrlConstants.addCardHolder,
                    token, JSONObject.toJSONString(cardHolder), pacificPayConfig.getId(),pacificPayConfig.getServerPublicKey(),pacificPayConfig.getPrivateKey());
            CardHolder returnCardHolder = JSON.parseObject(str, CardHolder.class);
            System.out.println("返回的数据：" + returnCardHolder);
            BeanUtils.copyProperties(returnCardHolder, cardHolder);
            cardHolder.setStatus(1);
        } catch (Exception e) {
            log.error("服务错误", e);
            throw new StarChainException(e.getMessage()); // 使用自定义异常
        }
        return this.save(cardHolder);
    }
}
