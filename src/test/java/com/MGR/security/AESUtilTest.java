package com.MGR.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.SecureRandom;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AESUtilTest {

    private String base64Key;

    @BeforeEach
    void setUp() {
        byte[] key = new byte[32]; // 256비트 AES 키
        SecureRandom random = new SecureRandom();
        random.nextBytes(key);
        base64Key = Base64.getEncoder().encodeToString(key);
        System.out.println("Base64 키= " + base64Key);
    }

    @Test
    void 비밀번호_암호화_복호화() {
        try {
            String originalText = "1";
            String encryptedText = AESUtil.encrypt(originalText, base64Key);
            System.out.println("Encrypted= " + encryptedText);

            String decryptedText = AESUtil.decrypt(encryptedText, base64Key);
            System.out.println("Decrypted= " + decryptedText);

            assertEquals(originalText, decryptedText);
        } catch (Exception e) {
            System.out.println("e = " + e.getMessage());
        }
    }
}
