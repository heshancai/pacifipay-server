package com.starchain.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.common.config.PacificPayConfig;
import com.starchain.common.constants.CardUrlConstants;
import com.starchain.common.entity.CardHolder;
import com.starchain.common.entity.dto.CardHolderDto;
import com.starchain.common.enums.CardCodeEnum;
import com.starchain.common.exception.StarChainException;
import com.starchain.common.util.DateUtil;
import com.starchain.common.util.HttpUtils;
import com.starchain.common.util.OrderIdGenerator;
import com.starchain.dao.CardHolderMapper;
import com.starchain.service.ICardHolderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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


        try {
            // 每次获取不同的token
            String token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            log.info("token=>" + token);
            CardHolder cardHolder = new CardHolder();
            // 下面的是必传参数
            // 卡类型编码
            cardHolder.setCardCode(CardCodeEnum.TPY_MDN6.getCardCode());
            // 商户持卡人ID 相对于银卡端来说 我们是商户端
            String merchantCardHolderId = OrderIdGenerator.generateOrderId("", "", 6);
            cardHolder.setMerchantCardHolderId(merchantCardHolderId);
            // 用户id
            cardHolder.setUserId(cardHolderDto.getUserId());
            // 商家Id 相对于用户端来说  后续会接入不同的商户
            cardHolder.setBusinessId(cardHolderDto.getBusinessId());
            // 名
            cardHolder.setFirstName(cardHolderDto.getFirstName());
            // 姓
            cardHolder.setLastName(cardHolderDto.getLastName());
            // 手机号国家
            cardHolder.setPhoneCountry(cardHolderDto.getPhoneCountry());
            // 手机号
            cardHolder.setPhoneNumber(cardHolderDto.getPhoneNumber());
            // 证件号
            cardHolder.setIdAccount(cardHolderDto.getIdAccount());
            // 邮箱
            cardHolder.setEmail(cardHolderDto.getEmail());
            // 地址
            cardHolder.setAddress(cardHolderDto.getAddress());
            // 性别
            cardHolder.setGender(cardHolderDto.getGender());
            // 生日
            cardHolder.setBirthday(DateUtil.convertStringToLocalDateTime(cardHolderDto.getBirthday(),DateUtil.YYYY_MM_DD_MM_HH_SS_Str));
            cardHolder.setStatus(0);
            cardHolder.setCreateTime(LocalDateTime.now());
            cardHolder.setUpdateTime(LocalDateTime.now());

            this.save(cardHolder);
            // 公钥加密传输 数据  我的私钥解密接收到的数据
            String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.ADD_CARD_HOLDER,
                    token, JSONObject.toJSONString(cardHolder), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("创建持卡人返回的数据：{}", str);
            // 这里会响应一次数据 后续会有回调数据
            CardHolder returnCardHolder = JSON.parseObject(str, CardHolder.class);
            cardHolder.setTpyshCardHolderId(returnCardHolder.getTpyshCardHolderId());
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
    public CardHolder updateCardHolder(CardHolderDto cardHolderDto) {
        LambdaUpdateWrapper<CardHolder> cardHolderLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        // 不可修改
        cardHolderLambdaUpdateWrapper.eq(CardHolder::getCardCode, cardHolderDto.getCardCode());
        cardHolderLambdaUpdateWrapper.eq(CardHolder::getId, cardHolderDto.getId());
        cardHolderLambdaUpdateWrapper.eq(CardHolder::getMerchantCardHolderId, cardHolderDto.getMerchantCardHolderId());
        cardHolderLambdaUpdateWrapper.eq(CardHolder::getTpyshCardHolderId, cardHolderDto.getTpyshCardHolderId());
        cardHolderLambdaUpdateWrapper.eq(CardHolder::getBusinessId, cardHolderDto.getBusinessId());

        // 设置 firstName
        Optional.ofNullable(cardHolderDto.getFirstName()).ifPresent(firstName -> cardHolderLambdaUpdateWrapper.set(CardHolder::getFirstName, firstName));

        // 设置 lastName
        Optional.ofNullable(cardHolderDto.getLastName()).ifPresent(lastName -> cardHolderLambdaUpdateWrapper.set(CardHolder::getLastName, lastName));

        // 设置 phoneCountry
        Optional.ofNullable(cardHolderDto.getPhoneCountry()).ifPresent(phoneCountry -> cardHolderLambdaUpdateWrapper.set(CardHolder::getPhoneCountry, phoneCountry));

        // 设置 phoneNumber
        Optional.ofNullable(cardHolderDto.getPhoneNumber()).ifPresent(phoneNumber -> cardHolderLambdaUpdateWrapper.set(CardHolder::getPhoneNumber, phoneNumber));

        // 设置 idAccount
        Optional.ofNullable(cardHolderDto.getIdAccount()).ifPresent(idAccount -> cardHolderLambdaUpdateWrapper.set(CardHolder::getIdAccount, idAccount));

        // 设置 email
        Optional.ofNullable(cardHolderDto.getEmail()).ifPresent(email -> cardHolderLambdaUpdateWrapper.set(CardHolder::getEmail, email));

        // 设置 address
        Optional.ofNullable(cardHolderDto.getAddress()).ifPresent(address -> cardHolderLambdaUpdateWrapper.set(CardHolder::getAddress, address));

        // 设置 gender
        Optional.ofNullable(cardHolderDto.getGender()).ifPresent(gender -> cardHolderLambdaUpdateWrapper.set(CardHolder::getGender, gender));

        // 设置 birthday
        Optional.ofNullable(cardHolderDto.getBirthday()).ifPresent(birthday -> cardHolderLambdaUpdateWrapper.set(CardHolder::getBirthday, birthday));

        String token = null;
        try {
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            System.out.println("token=>" + token);
            String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.EDIT_CARD_HOLDER, token, JSONObject.toJSONString(cardHolderDto), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            CardHolder returnCardHolder = JSON.parseObject(str, CardHolder.class);
            cardHolderLambdaUpdateWrapper.set(CardHolder::getUpdateTime, LocalDateTime.now());
            this.update(cardHolderLambdaUpdateWrapper);
            return this.getById(cardHolderDto.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isExitHolder(Long userId, Integer businessId) {
        LambdaQueryWrapper<CardHolder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CardHolder::getUserId, userId);
        lambdaQueryWrapper.eq(CardHolder::getBusinessId, businessId);
        // 持卡人已存在
        if (this.getOne(lambdaQueryWrapper) != null) {
            return true;
        }
        return false;
    }

    @Override
    public CardHolder getCardHolder(CardHolderDto cardHolderDto) {
        String token = null;
        try {
            token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            System.out.println("token=>" + token);
            String str = HttpUtils.doPostMiPay(pacificPayConfig.getBaseUrl() + CardUrlConstants.GET_CARD_HOLDER, token, JSONObject.toJSONString(cardHolderDto), pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            return JSON.parseObject(str, CardHolder.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CardHolder> selectCardHolder(CardHolderDto cardHolderDto) {
        LambdaQueryWrapper<CardHolder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CardHolder::getUserId, cardHolderDto.getUserId()).eq(CardHolder::getBusinessId, cardHolderDto.getBusinessId());
        return this.list(lambdaQueryWrapper);
    }


}
