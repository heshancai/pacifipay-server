package com.starchain;

import com.starchain.entity.CardHolder;
import com.starchain.entity.dto.CardHolderDto;
import com.starchain.enums.CardCodeEnum;
import com.starchain.service.impl.CardHolderServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StarchainPacificpayServerApplicationTests {

    @Autowired
    private CardHolderServiceImpl cardHolderServiceImpl;
    @Test
    void contextLoads() {
        CardHolderDto cardHolder=new CardHolderDto();
        cardHolder.setUserId(10000L);
        cardHolder.setUserId(100000L);
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

}
