package com.starchain.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.starchain.config.PacificPayConfig;
import com.starchain.constants.CardRemittanceUrlConstants;
import com.starchain.constants.CardUrlConstants;
import com.starchain.dao.RemitCardMapper;
import com.starchain.entity.RemitCard;
import com.starchain.entity.response.MiPayCardNotifyResponse;
import com.starchain.entity.response.RemitCardResponse;
import com.starchain.enums.RemitCodeEnum;
import com.starchain.exception.StarChainException;
import com.starchain.service.IMiPayNotifyService;
import com.starchain.service.IRemitCardService;
import com.starchain.util.HttpUtils;
import com.starchain.util.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author
 * @date 2025-01-02
 * @Description
 */
@Slf4j
@Service("remitCardServiceImpl")
public class RemitCardServiceImpl extends ServiceImpl<RemitCardMapper, RemitCard> implements IRemitCardService {

    @Autowired
    private PacificPayConfig pacificPayConfig;

    @Override
    public Boolean addRemitCard(RemitCard remitCard) {
        // 设置 remitCode 和 cardId
        remitCard.setRemitCode(RemitCodeEnum.UQR_CNH.getRemitCode());
        String cardId = String.valueOf(remitCard.getChannelId()) + remitCard.getUserId() + remitCard.getRemitCode() + UUIDUtil.generate8CharUUID();
        remitCard.setCardId(cardId);
        log.info("汇款卡唯一标识生成,{}", cardId);
        try {
            log.info("开始添加汇款卡，请求参数：{}", remitCard);

            // 获取 Token
            String token = HttpUtils.getTokenByMiPay(pacificPayConfig.getBaseUrl(), pacificPayConfig.getId(), pacificPayConfig.getSecret(), pacificPayConfig.getPrivateKey());
            log.info("成功获取 Token：{}", token);

            // 发送请求并获取响应
            String requestUrl = pacificPayConfig.getBaseUrl() + CardRemittanceUrlConstants.addRemitCard;
            String requestBody = JSONObject.toJSONString(remitCard);
            log.info("发送请求，URL：{}，请求体：{}", requestUrl, requestBody);

            String responseStr = HttpUtils.doPostMiPay(requestUrl, token, requestBody, pacificPayConfig.getId(), pacificPayConfig.getServerPublicKey(), pacificPayConfig.getPrivateKey());
            log.info("收到响应：{}", responseStr);

            // 解析响应
            RemitCardResponse returnRemitCard = JSONObject.parseObject(responseStr, RemitCardResponse.class);
            remitCard.setStatus(returnRemitCard.getStatus());
            remitCard.setStatusDesc(returnRemitCard.getStatusDesc());
            // 返回的收款卡唯一标识
            remitCard.setTpyCardId(returnRemitCard.getTpyCardId());

            // 如果 extraParams 不为空，设置相关字段到 remitCard 对象中
            List<JSONObject> extraParams = returnRemitCard.getExtraParams();
            if (!CollectionUtils.isEmpty(extraParams)) {
                log.info("extraParams 不为空，开始设置额外参数：{}", extraParams);

                // 遍历 extraParams 列表
                for (JSONObject extraParam : extraParams) {
                    remitCard.setSwiftCode(extraParam.getString("swiftCode"));
                    remitCard.setRemitName(extraParam.getString("remitName"));
                    remitCard.setBankCode(extraParam.getString("bankCode"));
                    remitCard.setRemitBank(extraParam.getString("remitBank"));
                    remitCard.setRemitBankBranchCode(extraParam.getString("remitBankBranchCode"));
                    remitCard.setBsbCode(extraParam.getString("bsbCode"));
                    remitCard.setSortCode(extraParam.getString("sortCode"));
                    remitCard.setAchNumber(extraParam.getString("achNumber"));
                    remitCard.setIdNumber(extraParam.getString("idNumber"));
                    remitCard.setRemitBankAddress(extraParam.getString("remitBankAddress"));
                    remitCard.setToMoneyKind(extraParam.getString("toMoneyKind"));
                    remitCard.setToMoneyCountry2(extraParam.getString("toMoneyCountry2"));
                }

                log.info("额外参数设置完成：{}", remitCard);
            }

            log.info("发起申请汇款卡，最终 remitCard 对象：{}", remitCard);
            this.save(remitCard);
        } catch (Exception e) {
            log.error("添加汇款卡时发生异常", e);
            throw new StarChainException("添加汇款卡失败");
        }

        return true;
    }
}
