package com.starchain.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.starchain.exception.StarChainException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author
 * @date 2024-12-18
 * @Description
 */
@Slf4j
public class TpyshUtils {

    private static String APPID = "m7567910-0cbf-423b-a39f-47fd418bac8g";
    private static String PUBLICKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArr6raOoFx4fe3KYk7238DvSlfPAEQC5I88aj+5ZlZT6EW0imvCVWZaEWKHDyB2JFXOKOsrZNM+Z9j/Wk1if7AN9e+QTfNSVc0BT+qZ1hCtITnKUFdGmak6DDQpeuT5oAnoPzlhR7cCoaZbbnv+ZWqZ0nbI8Zn6LbF8MoJOuzLyjcAJvx8FGD8s2m9cSEf4o4+GX9eaXgH/6PvSagzZySjfil9nW1/UEKdLLVssF9GrTvUsOeqofVCmNUpIJLOkK2Vew86Aqja2JdnOrcovlF20EHC9y+1IpHHS3gIeVinimafMMenMlgB7uIfAFzXo1/swRPwZ5ofiIs9BixqeHJhwIDAQAB";
    private static String PRIVATEKEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCuvqto6gXHh97cpiTvbfwO9KV88ARALkjzxqP7lmVlPoRbSKa8JVZloRYocPIHYkVc4o6ytk0z5n2P9aTWJ/sA3175BN81JVzQFP6pnWEK0hOcpQV0aZqToMNCl65PmgCeg/OWFHtwKhpltue/5lapnSdsjxmfotsXwygk67MvKNwAm/HwUYPyzab1xIR/ijj4Zf15peAf/o+9JqDNnJKN+KX2dbX9QQp0stWywX0atO9Sw56qh9UKY1Skgks6QrZV7DzoCqNrYl2c6tyi+UXbQQcL3L7UikcdLeAh5WKeKZp8wx6cyWAHu4h8AXNejX+zBE/Bnmh+Iiz0GLGp4cmHAgMBAAECggEATTvMuvQkVcpBwDhWvtRXwaxzsELNPii26bf0JnCUpCj7ivUTbLj79LM7/efdsVHH4oPoGijk+nl3KbeigVzuXbZcXae/l9fJq9Z7sC/1AjlTnbp6LLe4MGOvqbEsw9YHTAh6Eu0+mZaiUC6FQ5Xk29av5BVjd+EdAOWqypa6fa7N12lWlFSrb8lneDwHDL2EMdzDF3wfKcDk4DYtH8Gzor9zxPxeExZ43zTNZBJ4SKMGmfPHZ4KvXvtIoM5+RST6MMnFZu20/ur/vamYXkic/ttXc6MsTnbPGyJeo9583E6DmS+kaB8LqKHBNqI2q/yJDjO4mQ2RAC+SFU9DclkpkQKBgQD6hluImQs9DkFJ/MMSTn2X121rlySf427Yu8zz/bsR8voNbrhoADSoaytf2srnto9Wzxs49MkST9/VJbtLKGr7ROWeSaNE4+JIyPHAv0EaziEFclnE9Txm+oiuplXy8ByDkYfwv76PSZtUIJ2kBYk7Rk800mhWSBtJWIUI3HH3owKBgQCykFYHeO+huaQ2629ccYan0NZya2Tbks00KJTcHQLctefBBZc0JrAdear7zS6hdDzvRtBiyDKwZtAAtImaNOsXemjuZs2khTPW4sYPiP9/LPBL2PHkjvCRemSgD5qHv1f7rtbfPj8w5wPsqP38OZ0VeI+klmNtUJa3KalSxv9UzQKBgAnrdgZjoPlK7jTiyEqaRwjTI+cFthKKq+HzFbt3iYe9aj9L6gtcLrmYfMAv5qbkKKrUSoynzKpn9UH4W4EvWYUXhkwNOGTK7TPamQ/7wrUwAki37WwKFdkyBGV9+ptC/K4M/if/P6lmzbq4C+XpvjRVpV8/EZiRBFuUILhoGi8nAoGBAKCBRyMuXNAVS3umQxJoZW6hNLHiwOTx3ww8UhpM5Y644BnLya8x+2pzO/Nc6sZr8n3uPFAevhl8QQnxPiLj4FHZUUrCE6UHqqJEc13xeh+5qEG1PzGAHtPVnW+WtwPARfjQnwpKLIjrjsaC3NKEgyOSnCOIntTa9K6tfysN/uMdAoGAB48kPpG/8Sr78MWXq0hxPRAWByUhLF6NCnWt1vv3kpCzPDMD2+I9roQ74gXQ2d2cypqLCdhstF2AJUSRmyS2Iu47FK1q9R1LZZ7PBd3xZGLoU13WOMf1frR+csT9AkGR0eY8zwG7tFr/5kYT3FLOKcn/T5PZmOEKtWi09JcBnck=";


    /**
     * 获取哦 token
     *
     * @param baseUrl
     * @param appId
     * @param appSecret
     * @return
     * @throws Exception
     */
    public static String getToken(String baseUrl, String appId, String appSecret,String privateKey) throws Exception {
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
                String decrypt = RSA2048Encrypt.decrypt(dataEncrypt, RSA2048Encrypt.getPrivateKey(PRIVATEKEY));
                JSONObject res = JSON.parseObject(decrypt);
                System.out.println("解密后数据" + res);
                return res.getString("token");
            }
        }
    }

    public static String sign(String stringData) throws Exception {
//        String privateKey = SpringUtils.getYmlStringForActive("TpyshData.privateKey");
        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(PRIVATEKEY));
        KeyFactory keyf = KeyFactory.getInstance("RSA");
        PrivateKey priKey = keyf.generatePrivate(priPKCS8);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(priKey);
        signature.update(stringData.getBytes("UTF-8"));
        return Base64.encodeBase64String(signature.sign());
    }

    public static boolean validate(String dataStr, String signStr) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        String publicKey = SpringUtils.getYmlStringForActive("TpyshData.publicKey");
        byte[] encodedKey = Base64.decodeBase64(PUBLICKEY);
        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(pubKey);
        signature.update(dataStr.getBytes("UTF-8"));
        return signature.verify(Base64.decodeBase64(signStr));
    }

    public static String fileToBase64(String path) {
        String base64 = null;
        FileInputStream inputFile = null;
        try {
            File file = new File(path);
            inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
            base64 = new BASE64Encoder().encode(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputFile != null) {
                try {
                    inputFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return base64;
    }

    public static String doPost(String urlPath, String token, String json, String appId,String serverPublicKey,String privateKey) {
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