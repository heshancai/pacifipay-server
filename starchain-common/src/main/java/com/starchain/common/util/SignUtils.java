package com.starchain.common.util;


import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
public class SignUtils {





    /**
     * 验证签名
     * @param message 加密后的数据
     * @param sign 生成的签名
     * @param key 签名秘钥
     * @return
     */
    public static boolean validSign(String message,String sign,String key){
        try {
            String expectedMAC = hmacEncode(message, key);
            if (sign.equalsIgnoreCase(expectedMAC)) {
                return true;
            }
            return false;
        }
        catch (Exception e){
            log.error("validSign error",e);
            return false;
        }
    }

    /**
     * q签名
     * @param data 签名原始数据
     * @param key 签名key
     * @return
     * @throws Exception
     */
    public static String hmacEncode(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return bytesToHex(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars).toLowerCase();
    }

    public static void main(String [] args) throws Exception {
        System.out.println(hmacEncode("WWG1WA", "123456"));
        System.out.println( validSign("123456","4b117229e4a2320868b5ab0627f7aaa3ad279d0ba0e72c5c7f12539b703bb231","WWG1WA"));
    }
}
