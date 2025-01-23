package com.starchain;

import com.starchain.common.entity.dto.CardDto;
import com.starchain.common.entity.dto.CardHolderDto;
import com.starchain.common.entity.dto.TradeDetailDto;
import com.starchain.common.result.ClientResponse;
import com.starchain.controller.CardController;
import com.starchain.controller.CardHolderController;
import com.starchain.service.impl.CardHolderServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

/**
 * @author
 * @date 2025-01-23
 * @Description
 */
@SpringBootTest
class StarchainPacificpayServerApplicationTests {

    @Autowired
    private CardHolderServiceImpl cardHolderServiceImpl;


    @Autowired
    private CardController cardController;


    @Autowired
    private CardHolderController cardHolderController;


    @Test
    void contextLoads() {
        CardHolderDto cardHolder = new CardHolderDto();
        cardHolder.setChannelId(100000L);
        cardHolder.setUserId(100001L);
        cardHolder.setFirstName("张");
        cardHolder.setLastName("三");
        cardHolder.setPhoneCountry("+86");
        cardHolder.setPhoneNumber("13800138000");
        cardHolder.setIdAccount("123456789012345678");
        cardHolder.setEmail("zhangsan@example.com");
        cardHolder.setAddress("北京市朝阳区某某路123号");
        cardHolder.setGender("0");
        cardHolder.setBirthday("2000-01-01");
        ClientResponse clientResponse = cardHolderController.addCardHolder(cardHolder);
        System.out.println(clientResponse);
    }

    @Test
    void getCardHolder() {
        CardHolderDto cardHolderDto = new CardHolderDto();
        cardHolderDto.setMerchantCardHolderId("2025010811491392234e378");
        cardHolderDto.setCardCode("TpyMDN6");
        cardHolderDto.setTpyshCardHolderId("2025010811491487397");
        ClientResponse cardHolder1 = cardHolderController.getCardHolder(cardHolderDto);
        System.out.println(cardHolder1);
    }

    // 创建卡
    @Test
    void addCard() {
        CardDto cardHolder = new CardDto();
        cardHolder.setCardCode("TpyMDN6");
        cardHolder.setTpyshCardHolderId("2025010811465785377");
        cardHolder.setUserId(10000L);
        cardHolder.setChannelId(100000L);
        ClientResponse cardHolder1 = cardController.addCard(cardHolder);
        System.out.println(cardHolder1);
    }

    // 修改持卡人
    @Test
    void editCardHolder() {
        CardHolderDto cardHolder = new CardHolderDto();
        cardHolder.setMerchantCardHolderId("2025010811491392234e378");
        cardHolder.setCardCode("TpyMDN8");
        cardHolder.setTpyshCardHolderId("2025010811491487397");
        cardHolder.setUserId(10000L);
        cardHolder.setChannelId(100000L);
        cardHolder.setFirstName("hsc");
        cardHolder.setLastName("starChain");
        cardHolder.setId(13L);
        ClientResponse cardHolder1 = cardHolderController.updateCardHolder(cardHolder);
        System.out.println(cardHolder1);
    }


    // 卡注销
    @Test
    void deleteCard() {
        CardDto cardDto = new CardDto();
        cardDto.setCardCode("TpyMDN6");
        cardDto.setCardId("20250109102203cbd5d");
        ClientResponse clientResponse = cardController.deleteCard(cardDto);
        System.out.println(clientResponse);
    }

    @Test
    void mchInfo() {
        // 查询商户余额
        cardController.mchInfo();
    }

    // 查询商户交易明细
    @Test
    void tradeDetail() {
        TradeDetailDto tradeDetailDto = new TradeDetailDto();
        System.out.println(cardController.tradeDetail(tradeDetailDto));
    }

    // 卡充值
    @Test
    void applyRecharge() {
        CardDto cardDto = new CardDto();
        cardDto.setCardId("2025010910301416050");
        cardDto.setCardCode("TpyMDN6");
        cardDto.setUserId(10000L);
        cardDto.setTpyshCardHolderId("2025010811491487397");
        cardDto.setChannelId(100000L);
        cardDto.setOrderAmount(BigDecimal.TEN);
        System.out.println(cardController.applyRecharge(cardDto));
    }

    // 查询卡
    @Test
    void getCardDetail() {
        CardDto cardDto = new CardDto();
        cardDto.setCardCode("TpyMDN6");
        cardDto.setCardId("20250114180009ee7c1");
        cardController.getCardDetail(cardDto);
    }


    // 卡锁
    @Test
    void lockCard() {
        CardDto cardDto = new CardDto();
        cardDto.setCardId("2025010910294680992");
        cardDto.setCardCode("TpyMDN6");
        System.out.println(cardController.lockCard(cardDto));
    }


    // 卡解锁
    @Test
    void unlockCard() {
        CardDto cardDto = new CardDto();
        cardDto.setCardId("2025010910294680992");
        cardDto.setCardCode("TpyMDN6");
        System.out.println(cardController.unlockCard(cardDto));
    }

    // 修改卡限制额度
    @Test
    void updateLimit() {
        CardDto cardDto = new CardDto();
        cardDto.setCardId("2025010910294680992");
        cardDto.setCardCode("TpyMDN6");
        cardDto.setSingleLimit(new BigDecimal(11.133));
        System.out.println(cardController.updateLimit(cardDto));
    }





}
