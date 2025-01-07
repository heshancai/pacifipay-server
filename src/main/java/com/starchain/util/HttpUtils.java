package com.starchain.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.starchain.exception.StarChainException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * @author
 * @date 2025-01-03
 * @Description
 */
@Slf4j
public class HttpUtils {

    /**
     * 与miPay接口交互
     * @param urlPath
     * @param token
     * @param json
     * @param appId
     * @param serverPublicKey
     * @param privateKey
     * @return
     */
    public static String doPostMiPay(String urlPath, String token, String json, String appId, String serverPublicKey, String privateKey) {
        System.out.println("doPostMiPay urlPath:{}"+ urlPath);
        System.out.println("速汇卡渠道发送：" + json);
        String res = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 银行卡端公钥进行加密
            String encrypt = RSA2048Encrypt.encrypt(json, RSA2048Encrypt.getPublicKey(serverPublicKey));
            // 加签
            String sign = TpyshUtils.sign(encrypt);

            // 构建请求
            HttpPost httpPost = new HttpPost(urlPath);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("TPYSH-SIGN", sign);
            httpPost.addHeader("TPYSH-TOKEN", token);
            httpPost.addHeader("TPYSH-APP-ID", appId);
            httpPost.setEntity(new StringEntity(encrypt, "UTF-8"));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();

                String resultJsonString = EntityUtils.toString(entity);
                System.err.println("速汇卡渠道返回--------------" + resultJsonString);
                JSONObject resultJson = JSON.parseObject(resultJsonString);
                if (!"1000".equals(resultJson.getString("code"))) {
                    throw new StarChainException(resultJson.getString("message"));
                }
                String dataEncrypt = resultJson.getString("data");
                // 私钥解密
                res = RSA2048Encrypt.decrypt(dataEncrypt, RSA2048Encrypt.getPrivateKey(privateKey));
                System.out.println(res);
            }
        } catch (Exception e) {
            log.error("请求失败", e);
        }
        return res;
    }

    /**
     * 与miPay接口交互需要获取的token
     *
     * @param baseUrl
     * @param appId
     * @param appSecret
     * @return
     * @throws Exception
     */
    public static String getTokenByMiPay(String baseUrl, String appId, String appSecret,String privateKey) throws Exception {
        // MD5 加签
        String macString = appId + "&" + appSecret;
        String md5Str = SecurityUtils.getMD5Str(macString, "utf-8");

        // 创建 HTTP 客户端
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(baseUrl + "/apiMerchantAuth/getToken");
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("Authorization", "auth " + md5Str);
            httpPost.addHeader("TPYSH-APP-ID", appId);
            System.out.println("md5Str:" + md5Str);
            // 执行请求并获取响应
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                String resultJsonString = EntityUtils.toString(entity);
                System.err.println("速汇卡渠道返回--------------" + resultJsonString);

                JSONObject resultJson = JSON.parseObject(resultJsonString);
                if (!resultJson.getString("code").equals("1000")) {
                    log.error(resultJson.getString("message"));
                    throw new RuntimeException(resultJson.getString("message"));
                }

                String dataEncrypt = resultJson.getString("data");
                System.out.println("速汇卡渠道返回未解密token=>" + dataEncrypt);
                // 私钥解密
                String decrypt = RSA2048Encrypt.decrypt(dataEncrypt, RSA2048Encrypt.getPrivateKey(privateKey));
                JSONObject res = JSON.parseObject(decrypt);
                System.out.println("解密后数据" + res);
                return res.getString("token");
            }
        }
    }

    /**
     * 获取钱包地址
     * @param urlPath
     * @param json 要发送的json数据
     * @param serverPublicKey 钱包端公钥
     * @param privateKey 我的私钥
     * @return
     */
    public static String doPostWalletAddress(String urlPath, String json, String serverPublicKey, String privateKey) {
        String res = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 银行卡端公钥进行加密
            String encrypt = RSA2048Encrypt.encrypt(json, RSA2048Encrypt.getPublicKey(serverPublicKey));
            // 加签私钥加签 服务端用我的公钥进行验签
            String sign = TpyshUtils.sign(encrypt);

            // 构建请求
            HttpPost httpPost = new HttpPost(urlPath);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("TPYSH-SIGN", sign);
            httpPost.setEntity(new StringEntity(encrypt, "UTF-8"));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();

                String resultJsonString = EntityUtils.toString(entity);
                System.err.println("钱包服务返回--------------" + resultJsonString);
                JSONObject resultJson = JSON.parseObject(resultJsonString);
                if (!"200".equals(resultJson.getString("code"))) {
                    throw new StarChainException(resultJson.getString("message"));
                }
                String dataEncrypt = resultJson.getString("data");
                // 私钥解密
//                String decrypt = RSA2048Encrypt.decrypt(dataEncrypt, RSA2048Encrypt.getPrivateKey(PRIVATEKEY));
                res = RSA2048Encrypt.decrypt(dataEncrypt, RSA2048Encrypt.getPrivateKey(privateKey));
                // 解析对象的情况
//                res = JSON.parseObject(decrypt);
                // 解析List<T> 得情况
//                List<JSONObject> jsonObjects = JSON.parseArray(decrypt, JSONObject.class);
                System.out.println(res);
            }
        } catch (Exception e) {
            log.error("请求失败", e);
        }
        return res;
    }


}
