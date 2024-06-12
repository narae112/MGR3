package com.MGR.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String AES_KEY = "K5SIlHkeI3b7JeioZJ6KEfE3yzLUUj77viXkVBF5FmE=";

    public static String decrypt(String encryptedText) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(AES_KEY);
        SecretKeySpec keySpec = new SecretKeySpec(decodedKey, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }

    public static String encrypt(String plainText, String base64Key) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        SecretKeySpec keySpec = new SecretKeySpec(decodedKey, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
}