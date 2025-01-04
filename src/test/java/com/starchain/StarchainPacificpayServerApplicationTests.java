package com.starchain;

import com.starchain.controller.CardController;
import com.starchain.controller.RemitCardController;
import com.starchain.entity.CardHolder;
import com.starchain.entity.RemitCard;
import com.starchain.entity.dto.CardHolderDto;
import com.starchain.enums.CardCodeEnum;
import com.starchain.result.ClientResponse;
import com.starchain.service.impl.CardHolderServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class StarchainPacificpayServerApplicationTests {

    @Autowired
    private CardHolderServiceImpl cardHolderServiceImpl;


    @Autowired
    private CardController cardController;

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
        cardController.addCardHolder(cardHolder);
    }


    @Test
    void tradeDetail() {
        cardController.tradeDetail();
    }

    @Autowired
    RemitCardController remitCardController;

    @Test
    void addRemitCard() {
        RemitCard remitCard = new RemitCard();
        remitCard.setUserId(123456L);
        remitCard.setChannelId(987654L);
        remitCard.setRemitFirstName("John");
        remitCard.setRemitLastName("Doe");
        remitCard.setRemitBankNo("1234567890");

        ClientResponse response = remitCardController.addRemitCard(remitCard);

    }
}
