package com.starchain.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

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
    private static String PRIVATEKEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCuvqto6gXHh97cpiTvbfwO9KV88ARALkjzxqP7lmVlPoRbSKa8JVZloRYocPIHYkVc4o6ytk0z5n2P9aTWJ/sA3175BN81JVzQFP6pnWEK0hOcpQV0aZqToMNCl65PmgCeg/OWFHtwKhpltue/5lapnSdsjxmfotsXwygk67MvKNwAm/HwUYPyzab1xIR/ijj4Zf15peAf/o+9JqDNnJKN+KX2dbX9QQp0stWywX0atO9Sw56qh9UKY1Skgks6QrZV7DzoCqNrYl2c6tyi+UXbQQcL3L7UikcdLeAh5WKeKZp8wx6cyWAHu4h8AFzXo1/swRPwZ5ofiIs9BixqeHJhwIDAQAB";

    public static String sign(String stringData,String privateKey) throws Exception {
        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        KeyFactory keyf = KeyFactory.getInstance("RSA");
        PrivateKey priKey = keyf.generatePrivate(priPKCS8);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(priKey);
        signature.update(stringData.getBytes("UTF-8"));
        return Base64.encodeBase64String(signature.sign());
    }

    public static boolean validate(String dataStr, String signStr) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
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
            base64 = java.util.Base64.getEncoder().encodeToString(buffer); // 使用 java.util.Base64
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
}