package com.starchain.common.util;

/**
 * @author
 * @date 2024-12-18
 * @Description
 */
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;

public class RSA2048Encrypt {
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 245;
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 256;

    private static final String ALGORITHM_NAME = "RSA";
    private static final String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static final String MD5_RSA = "MD5withRSA";

    public static void main(String[] args) {
        try {

            // 生成密钥对
            //KeyPair keyPair = getKeyPair();
            //String privateKey = new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));
            //String publicKey = new String(Base64.encodeBase64(keyPair.getPublic().getEncoded()));
            //System.out.println("私钥 => " + privateKey + "\n");
            //System.out.println("公钥 =>" + publicKey + "\n");

            HashMap<String, String> keyPairMap = getKeyPairMap();
            String privateKey = keyPairMap.get("privateKey");
            String publicKey =  keyPairMap.get("publicKey");
            System.out.println("私钥 => " + privateKey + "\n");
            System.out.println("公钥 =>" + publicKey + "\n");

            // 公钥RSA加密
            String data = "123456";
            String encryptData = encrypt(data, getPublicKey(publicKey));
            System.out.println("加密后内容 => " + encryptData + "\n");
            // 私钥RSA解密
            String decryptData = decrypt(encryptData, getPrivateKey(privateKey));
            System.out.println("解密后内容 => " + decryptData + "\n");


            // 私钥RSA签名
            String sign = sign(data, getPrivateKey(privateKey));
            // 公钥RSA验签
            boolean result = verify(data, getPublicKey(publicKey), sign);
            System.out.println("验签结果 => " + result + "\n");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("RSA加解密异常");
        }
    }

    /**
     * 获取密钥对
     */
    public static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM_NAME);
        generator.initialize(2048);
        return generator.generateKeyPair();
    }
    /**
     * 获取base64加密后密钥对
     */
    public static HashMap<String, String> getKeyPairMap() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM_NAME);
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();
        String privateKey = new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));
        String publicKey = new String(Base64.encodeBase64(keyPair.getPublic().getEncoded()));
        HashMap<String, String> keyMap = new HashMap<>();
        keyMap.put("privateKey", privateKey);
        keyMap.put("publicKey", publicKey);
        return keyMap;
    }

    /**
     * 获取公钥
     *
     * @param publicKey base64加密的公钥字符串
     */
    public static PublicKey getPublicKey(String publicKey) throws Exception {
        byte[] decodedKey = Base64.decodeBase64(publicKey.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_NAME);
        return keyFactory.generatePublic(keySpec);
    }
    /**
     * RSA加密
     *
     * @param data      待加密数据
     * @param publicKey 公钥
     */
    public static String encrypt(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int inputLen = data.getBytes().length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data.getBytes(), offset, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data.getBytes(), offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        // 获取加密内容使用base64进行编码,并以UTF-8为标准转化成字符串
        // 加密后的字符串
        return new String(Base64.encodeBase64(encryptedData));
    }

    /**
     * 获取私钥
     *
     * @param privateKey base64加密的私钥字符串
     */
    public static PrivateKey getPrivateKey(String privateKey) throws Exception {
        byte[] decodedKey = Base64.decodeBase64(privateKey.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_NAME);
        return keyFactory.generatePrivate(keySpec);
    }
    /**
     * RSA解密
     *
     * @param data       待解密数据
     * @param privateKey 私钥
     */
    public static String decrypt(String data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] dataBytes = Base64.decodeBase64(data);
        int inputLen = dataBytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(dataBytes, offset, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(dataBytes, offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        // 解密后的内容
        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    /**
     * 签名
     *
     * @param data       待签名数据
     * @param privateKey 私钥
     */
    public static String sign(String data, PrivateKey privateKey) throws Exception {
        byte[] keyBytes = privateKey.getEncoded();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_NAME);
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        Signature signature = Signature.getInstance(MD5_RSA);
        signature.initSign(key);
        signature.update(data.getBytes());
        return new String(Base64.encodeBase64(signature.sign()));
    }

    /**
     * 验签
     *
     * @param srcData   原始字符串
     * @param publicKey 公钥
     * @param sign      签名
     */
    public static boolean verify(String srcData, PublicKey publicKey, String sign) throws Exception {
        byte[] keyBytes = publicKey.getEncoded();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_NAME);
        PublicKey key = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(MD5_RSA);
        signature.initVerify(key);
        signature.update(srcData.getBytes());
        return signature.verify(Base64.decodeBase64(sign.getBytes()));
    }
}
