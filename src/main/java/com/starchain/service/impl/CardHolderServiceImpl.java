package com.starchain.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.config.PacificPayConfig;
import com.starchain.constants.CardUrlConstants;
import com.starchain.dao.CardHolderMapper;
import com.starchain.entity.CardHolder;
import com.starchain.entity.dto.CardHolderDto;
import com.starchain.enums.CardCodeEnum;
import com.starchain.exception.StarChainException;
import com.starchain.service.ICardHolderService;
import com.starchain.util.HttpUtils;
import com.starchain.util.TpyshUtils;
import com.starchain.util.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author
 * @date 2024-12-30
 * @Description
 */
@Slf4j
@Service
public class CardHolderServiceImpl extends ServiceImpl<CardHolderMapper, CardHolder> implements ICardHolderService {


    @Autowired
    private PacificPayConfig pacificPayConfig;

    /**
     * 创建持卡人 并且 创建卡 每个持卡人最多四张卡虚拟卡
     *
     * @param cardHolderDto
     * @return
     */
    public CardHolder addCardHolder(CardHolderDto cardHolderDto) {

        String token = null;
        try {
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            System.out.println("token=>" + token);
            CardHolder cardHolder = new CardHolder();
            cardHolder.setCardCode(CardCodeEnum.TPY_MDN6.getCardCode());
            //商户申请创建持卡人的唯一值
            String merchantCardHolderId = cardHolderDto.getChannelId() + cardHolder.getUserId() + cardHolder.getCardCode() + UUIDUtil.generate8CharUUID();
            cardHolder.setMerchantCardHolderId(merchantCardHolderId);

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
            cardHolder.setCreateTime(LocalDateTime.now());
            cardHolder.setUpdateTime(LocalDateTime.now());

            this.save(cardHolder);
            // 公钥加密传输 数据  我的私钥解密接收到的数据
            String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.addCardHolder,
                    token, JSONObject.toJSONString(cardHolder), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            CardHolder returnCardHolder = JSON.parseObject(str, CardHolder.class);
            System.out.println("返回的数据：" + returnCardHolder);
            BeanUtils.copyProperties(returnCardHolder, cardHolder);
            cardHolder.setUpdateTime(LocalDateTime.now());
            cardHolder.setStatus(1);
            this.updateById(cardHolder);
            log.info("创建持卡人成功:{}", cardHolder);
            return cardHolder;

        } catch (Exception e) {
            log.error("服务错误", e);
            throw new StarChainException(e.getMessage()); // 使用自定义异常
        }
    }

    @Override
    public boolean isExitHolder(Long userId, Integer channelId) {
        LambdaQueryWrapper<CardHolder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CardHolder::getUserId, userId);
        lambdaQueryWrapper.eq(CardHolder::getChannelId, channelId);
        // 持卡人已存在
        if (this.getOne(lambdaQueryWrapper) != null) {
            return true;
        }
        return false;
    }
}
