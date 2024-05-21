package com.MGR.service;

import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MailServiceTest {

    @Autowired JavaMailSender javaMailSender;

    @Test
    void 메일인증번호생성() {

        StringBuilder code = new StringBuilder();
        Random random = new Random();
        int number = 0;
        random.nextInt(10);

        for(int i = 0; i < 8; i++){
            number = random.nextInt(10);
            code.append(number);
        }

        System.out.println("code: " + code);
    }

    @Test
    void 메일내용확인(){

        String code = "12345678";
        String email = "narae0967@gmail.com";

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.addRecipients(Message.RecipientType.TO, email);
            message.setFrom("merrygoride2024@gmail.com");
            message.setSubject("Merry Go Ride 회원가입 인증 메일입니다.");
            message.setText("인증 번호: " + code);
        }catch (Exception e){
            System.out.println("오류내용 = " + e.getMessage());
        }
        System.out.println("message = " + message);
    }
}