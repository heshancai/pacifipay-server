package com.starchain;

import com.alibaba.fastjson2.JSONObject;
import com.starchain.controller.RemitCardController;
import com.starchain.entity.dto.RemitApplicationRecordDto;
import com.starchain.entity.dto.RemitCardDto;
import com.starchain.enums.MoneyKindEnum;
import com.starchain.enums.RemitCodeEnum;
import com.starchain.result.ClientResponse;
import com.starchain.service.IRemitApplicationRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

/**
 * @author
 * @date 2025-01-15
 * @Description
 */
@SpringBootTest
public class RemitCardControllerTest {
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
        jsonObject.put("idNumber", "211382199802034875");
        jsonObject.put("remitBankBranchCode", "1234567891011"); // 13位数字
        remitCard.setExtraParams(jsonObject);
        ClientResponse response = remitCardController.addRemitCard(remitCard);
        System.out.println(response);

    }

    /**
     * 删除收款卡信息
     */
    @Test
    void delRemitCard() {
        /**
         * 删除收款卡信息
         * Account holder name: Progressive Solutions
         * Account number:  GB55TCCL12345618629629
         * Swift code: TCCLGB3L
         * Bank name: The Currency Cloud Limited
         * Bank address: 12 Steward Street, The Steward Building, London, E1 6FQ, GB
         */
        RemitCardDto remitCard = new RemitCardDto();
//        remitCard.setUserId(10000L);
        remitCard.setChannelId(1000001L);
        remitCard.setRemitCode("UQR_CNH");
        remitCard.setCardId("1880688340244627456");
        remitCard.setTpyCardId("202501171625450d067");
        ClientResponse response = remitCardController.delRemitCard(remitCard);
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
        jsonObject.put("remitLastName", "Progressive");
        jsonObject.put("remitFirstName", "Solutions");
        jsonObject.put("remitTpyCardId", "2025012114081850c57");
        jsonObject.put("remitBankNo", "GB55TCCL12345618629629");
        RemitApplicationRecordDto build = RemitApplicationRecordDto.builder()
                .toAmount(BigDecimal.valueOf(10)).userId(10000L).channelId(987654L).toMoneyKind(MoneyKindEnum.CNY.getMoneyKindCode()).remitCode(RemitCodeEnum.UQR_CNH.getRemitCode())
                .extraParams(jsonObject).build();
        remitApplicationRecord.applyRemit(build);

    }


    // 查询汇款单详情
    @Test
    void remitDetail() {
        /**
         * Account holder name: Progressive Solutions
         * Account number:  GB55TCCL12345618629629
         * Swift code: TCCLGB3L
         * Bank name: The Currency Cloud Limited
         * Bank address: 12 Steward Street, The Steward Building, London, E1 6FQ, GB
         */
        RemitCardDto build = RemitCardDto.builder().remitCode(RemitCodeEnum.UQR_CNH.getRemitCode()).orderId("123456987654202501171747479482fc458").build();
        remitCardController.remitDetail(build);

    }

}
