package com.starchain;

import com.alibaba.fastjson2.JSONObject;
import com.starchain.controller.CardController;
import com.starchain.controller.CardHolderController;
import com.starchain.controller.RemitCardController;
import com.starchain.entity.dto.*;
import com.starchain.enums.MoneyKindEnum;
import com.starchain.result.ClientResponse;
import com.starchain.service.IRemitApplicationRecordService;
import com.starchain.service.impl.CardHolderServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

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
        cardHolder.setChannelId(100000);
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
    @Test
    void addCard() {
        CardHolderDto cardHolder = new CardHolderDto();
        cardHolder.setMerchantCardHolderId("2025010811491392234e378");
        cardHolder.setCardCode("TpyMDN6");
        cardHolder.setTpyshCardHolderId("2025010811491487397");
        cardHolder.setUserId(10000L);
        cardHolder.setChannelId(100000);
        cardHolder.setFirstName("hsc");
        cardHolder.setLastName("starChain");
        cardHolder.setId(13L);
        ClientResponse cardHolder1 = cardController.addCard(cardHolder);
        System.out.println(cardHolder1);
    }

    @Test
    void editCardHolder() {
        CardHolderDto cardHolder = new CardHolderDto();
        cardHolder.setMerchantCardHolderId("2025010811491392234e378");
        cardHolder.setCardCode("TpyMDN8");
        cardHolder.setTpyshCardHolderId("2025010811491487397");
        cardHolder.setUserId(10000L);
        cardHolder.setChannelId(100000);
        cardHolder.setFirstName("hsc");
        cardHolder.setLastName("starChain");
        cardHolder.setId(13L);
        ClientResponse cardHolder1 = cardHolderController.updateCardHolder(cardHolder);
        System.out.println(cardHolder1);
    }



    @Test
    void getCardDetail() {
        CardDto cardDto = new CardDto();
        cardDto.setCardCode("TpyMDN6");
        cardDto.setCardId("2025010910301416050");
        cardController.getCardDetail(cardDto);
    }

    @Test
    void tradeDetail() {
        TradeDetailDto tradeDetailDto = new TradeDetailDto();
        cardController.tradeDetail(tradeDetailDto);
    }

    @Test
    void mchInfo() {

        cardController.mchInfo();
    }

    @Autowired
    RemitCardController remitCardController;

    /**
     * 添加收款卡信息
     */
    @Test
    void addRemitCard() {
        /**
         * 添加收款卡信息
         * Account holder name: Progressive Solutions
         * Account number:  GB55TCCL12345618629629
         * Swift code: TCCLGB3L
         * Bank name: The Currency Cloud Limited
         * Bank address: 12 Steward Street, The Steward Building, London, E1 6FQ, GB
         */
        RemitCardDto remitCard = new RemitCardDto();
        remitCard.setUserId(10000L);
        remitCard.setChannelId(1000001L);
        remitCard.setRemitFirstName("Progressive");
        remitCard.setRemitLastName("Solutions");
        remitCard.setRemitBankNo("GB55TCCL12345618629629");
        remitCard.setRemitCode("UQR_CNH");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("swiftCode", "TCCLGB3L");
        jsonObject.put("remitName", "Progressive Solutions");
        jsonObject.put("remitBank", "The Currency Cloud Limited");
        jsonObject.put("remitBankAddress", "12 Steward Street, The Steward Building, London, E1 6FQ, GB");
        jsonObject.put("toMoneyKind", MoneyKindEnum.CNY.getMoneyKindCode());
        jsonObject.put("toMoneyCountry2", "CN");
        jsonObject.put("idNumber", "GB55TCCL12345618629629");
        jsonObject.put("remitBankBranchCode", "1234567891011"); // 13位数字
        remitCard.setExtraParams(jsonObject);
        ClientResponse response = remitCardController.addRemitCard(remitCard);
        System.out.println(response);

    }

    @Autowired
    IRemitApplicationRecordService remitApplicationRecord;

    // 申请汇款
    @Test
    void applyRemit() {
        /**
         * Account holder name: Progressive Solutions
         * Account number:  GB55TCCL12345618629629
         * Swift code: TCCLGB3L
         * Bank name: The Currency Cloud Limited
         * Bank address: 12 Steward Street, The Steward Building, London, E1 6FQ, GB
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("remitName", "Progressive Solutions");
        jsonObject.put("remitLastName", "Progressive");
        jsonObject.put("remitFirstName", "Solutions");
        jsonObject.put("remitBankNo", "GB55TCCL12345618629629");
        RemitApplicationRecordDto build = RemitApplicationRecordDto.builder().toAmount(BigDecimal.valueOf(10)).userId(123456L).channelId(987654L).extraParams(jsonObject).build();
        remitApplicationRecord.applyRemit(build);

    }
}
