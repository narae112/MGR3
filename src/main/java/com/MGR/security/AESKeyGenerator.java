//package com.MGR.security;
//
//import java.security.SecureRandom;
//import java.util.Base64;
// //키 암호화, 복호화 테스트 하는거
//public class AESKeyGenerator {
//    public static void main(String[] args) {
//        byte[] key = new byte[32]; // 256비트 AES 키
//        SecureRandom random = new SecureRandom();
//        random.nextBytes(key);
//        String base64Key = Base64.getEncoder().encodeToString(key);
//        System.out.println("Base64 encoded AES key: " + base64Key);
//
//        try {
//            String originalText = "1";
//            String encryptedText = AESUtil.encrypt(originalText, base64Key);
//            System.out.println("Encrypted Text: " + encryptedText);
//
//            String decryptedText = AESUtil.decrypt(encryptedText, base64Key);
//            System.out.println("Decrypted Text: " + decryptedText);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//}
