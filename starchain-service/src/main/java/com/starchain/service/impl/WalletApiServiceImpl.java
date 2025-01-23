package com.starchain.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.starchain.common.entity.response.WalletRechargeAddressResponse;
import com.starchain.common.util.AesUtils;
import com.starchain.common.util.OkHttpUtils;
import com.starchain.common.util.SignUtils;
import com.starchain.service.IWalletCoinConfigService;
import com.starchain.service.WalletApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 * @date 2025-01-12
 * @Description
 */
@Service
@Slf4j
public class WalletApiServiceImpl implements WalletApiService {


    @Value("${wallet.domain}")
    private String walletDomain;
    @Value("${wallet.createAddress}")
    private String createAddress;
    @Value("${wallet.appKey}")
    private String appKey;
    @Value("${wallet.contentSecret}")
    private String contentSecret;
    @Value("${wallet.signSecret}")
    private String signSecret;
    @Autowired
    private IWalletCoinConfigService walletCoinConfigService;

    /**
     * 获取钱包地址
     *
     * @param coinId
     * @param count
     * @param batchNo
     * @return
     */
    @Override
    public String createNewAddress(String coinId, int count, String batchNo) {
        log.info("调用钱包服务获取地址");
        try {
            // 构建获钱包地址所需的参数
            String requestJsonStr = buildCreateNewAddress(coinId, count, batchNo);
            // 参数加密并发送请求
            String result = post(walletDomain + createAddress, requestJsonStr);
            log.debug("获取充值地址返回结果：{}", result);

            JSONObject json = JSONObject.parseObject(result);
            if (json == null) {
                log.error("获取充值地址为空");
                return null;
            }

            String code = json.getString("code");
            if (!"1".equals(code)) {
                log.info("获取充值地址请求coinlink钱包失败：[{}]", json.getString("message"));
                return null;
            }

            JSONArray data = json.getJSONArray("data");
            if (data != null && !data.isEmpty()) {
                List<WalletRechargeAddressResponse> walletRechargeAddressResponseList = data.toJavaList(WalletRechargeAddressResponse.class);
                return walletRechargeAddressResponseList.get(0).getAddress();
            }

            return null;
        } catch (Exception e) {
            log.error("获取充值地址返回异常", e);
            return null;
        }
    }

    private String buildCreateNewAddress(String coinId, int count, String batchNo) {
        Map<String, Object> requestParams = new HashMap<>(16);
        //币种符号
        requestParams.put("currencySymbol", walletCoinConfigService.toCallbackName(coinId));
        //个数
        requestParams.put("cnt", count);
        //批次码
        requestParams.put("batchNo", batchNo);
        // appKey
        requestParams.put("appKey", appKey);
        JSONObject requestJson = new JSONObject(requestParams);
        String requestJsonStr = requestJson.toJSONString();
        log.info("获取充值地址请求参数字符串：[{}]", requestJsonStr);
        return requestJsonStr;
    }


    protected String post(String url, String requestJsonStr) throws Exception {
        //对请求内容进行加密
        String encryptStr = AesUtils.encrypt(contentSecret, requestJsonStr);
        Map<String, Object> encryptRequestParams = new HashMap<>(16);
        encryptRequestParams.put("msg", encryptStr);
        JSONObject encryptRequestJson = new JSONObject(encryptRequestParams);
        String encryptRequestJsonStr = encryptRequestJson.toJSONString();
        // 根据生成加密内容和内容秘钥 生成签名
        String sign = SignUtils.hmacEncode(encryptStr, signSecret);
        // 验证签名
        boolean result = SignUtils.validSign(encryptStr, sign, signSecret);
        log.info("签名sign:[{}],签名是否正确：[{}]", sign, result);
        Map<String, String> header = new HashMap<>(16);
        // 签名后的数据
        header.put("Sign", sign);
        //请求接口
        return OkHttpUtils.postDataByForm(url, encryptRequestJsonStr, header);
    }


}
