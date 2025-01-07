package com.starchain;

import com.alibaba.fastjson2.JSONObject;
import com.starchain.controller.CardController;
import com.starchain.controller.CardHolderController;
import com.starchain.controller.RemitCardController;
import com.starchain.entity.RemitCard;
import com.starchain.entity.dto.CardHolderDto;
import com.starchain.entity.dto.RemitApplicationRecordDto;
import com.starchain.entity.dto.RemitCardDto;
import com.starchain.entity.dto.TradeDetailDto;
import com.starchain.enums.RemitCodeEnum;
import com.starchain.result.ClientResponse;
import com.starchain.service.IRemitApplicationRecordService;
import com.starchain.service.impl.CardHolderServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
        cardHolder.setUserId(10000L);
        cardHolder.setFirstName("张");
        cardHolder.setLastName("三");
        cardHolder.setPhoneCountry("+86");
        cardHolder.setPhoneNumber("13800138000");
        cardHolder.setIdAccount("123456789012345678");
        cardHolder.setEmail("zhangsan@example.com");
        cardHolder.setAddress("北京市朝阳区某某路123号");
        cardHolder.setGender("0");
        cardHolder.setBirthday("2000-01-01");
        cardHolderServiceImpl.addCardHolder(cardHolder);
    }

    @Test
    void contextLoads1() {
        CardHolderDto cardHolder = new CardHolderDto();
        cardHolder.setChannelId(100000);
        cardHolder.setUserId(10000L);
        cardHolder.setFirstName("张");
        cardHolder.setLastName("三");
        cardHolder.setPhoneCountry("+86");
        cardHolder.setPhoneNumber("13800138000");
        cardHolder.setIdAccount("123456789012345678");
        cardHolder.setEmail("zhangsan@example.com");
        cardHolder.setAddress("北京市朝阳区某某路123号");
        cardHolder.setGender("0");
        cardHolder.setBirthday("2000-01-01");
        cardHolderController.addCardHolder(cardHolder);
    }


    @Test
    void tradeDetail() {
        TradeDetailDto tradeDetailDto = new TradeDetailDto();
        cardController.tradeDetail(tradeDetailDto);
    }

    @Autowired
    RemitCardController remitCardController;

    @Test
    void addRemitCard() {
        RemitCardDto remitCard = new RemitCardDto();
        remitCard.setUserId(10000L);
        remitCard.setChannelId(1000001L);
        remitCard.setRemitFirstName("Progressive");
        remitCard.setRemitLastName("Solutions");
        remitCard.setRemitBankNo("GB55TCCL12345618629629");
        remitCard.setRemitCode(RemitCodeEnum.UQR_CNH.getRemitCode());
        ClientResponse response = remitCardController.addRemitCard(remitCard);

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
