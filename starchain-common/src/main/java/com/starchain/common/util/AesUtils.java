package com.starchain.common.util;


import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Slf4j
public class AesUtils {

    private static byte[] cipher(int mode,String key, byte[] crypted)   {
        byte[] keyBytes = getKeyBytes(key);
        byte[] buf = new byte[16];
        System.arraycopy(keyBytes, 0, buf, 0, keyBytes.length > buf.length ? keyBytes.length : buf.length);
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(mode, new SecretKeySpec(buf, "AES"), new IvParameterSpec(keyBytes));
			return cipher.doFinal(crypted);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			log.warn("AES编解码失败", e);
			throw new RuntimeException("AES编解码失败");
		}
    }

    private static byte[] getKeyBytes(String key) {
        byte[] bytes = key.getBytes();
        return bytes.length == 16 ? bytes : Arrays.copyOf(bytes, 16);
    }

    /**
     * 加密数据
     * @param key aes key
     * @param val 加密原始数据
     * @return 16进制加密结果
     * @throws GeneralSecurityException
     */
    public static String encrypt(String key, String val)   {
        byte[] origData = val.getBytes();
        byte[] crypted = cipher(Cipher.ENCRYPT_MODE,key, origData);
        return CommonUtils.bytesToHex(crypted);
    }


    /**
     * 解密
     * @param key aeskey
     * @param val 原始16进制加密字符串
     * @return
     * @throws GeneralSecurityException
     */
    public static String decrypt(String key, String val)  {
        byte[] crypted = CommonUtils.hexToByteArray(val);
        byte[] origData = cipher(Cipher.DECRYPT_MODE,key, crypted);
        return new String(origData);
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        System.out.println(encrypt("0123456789abcdeW", "asdf"));
        System.out.println(decrypt("0123456789abcdeW", "3e4028dab28e8780d0d2efb92187233a"));

    }

}
