package com.MGR.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    public String createCode(){
    //메일 인증 코드 생성
        StringBuilder code = new StringBuilder();

        String numberCode = code.toString();
        Random random = new Random();
        int number = 0;
        random.nextInt(10);

        for(int i = 0; i < 8; i++){
            number = random.nextInt(10);
            code.append(number);
        }

        return code.toString();
    }

    public MimeMessage createMailForm(String email, String code) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            message.addRecipients(Message.RecipientType.TO, email);
            message.setFrom("merrygoride2024@gmail.com");
            message.setSubject("Merry Go Ride 회원가입 인증 메일입니다.");
            message.setText("인증 번호: " + code);
        }catch (Exception e){
            System.out.println("MailService_createMailForm 오류내용 = " + e.getMessage());
        }

        return message;

    }

    public String sendMail(String email, String code) throws MessagingException {
        MimeMessage message = createMailForm(email, code);
        javaMailSender.send(message);
        return code;
    }



}
