package com.MGR.controller;

import com.MGR.config.VerifyRecaptcha;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RecaptchaController {
    @PostMapping("/verifyRecaptcha")
    @ResponseBody
    public String verifyRecaptcha(@RequestParam("gRecaptchaResponse") String gRecaptchaResponse) {
        // 비밀 키 설정
        VerifyRecaptcha.setSecretKey("6LcYWugpAAAAAJbmpikQrSMqCKW6g_EVg-WZMxrc");

        try {
            // reCAPTCHA 확인
            if (VerifyRecaptcha.verify(gRecaptchaResponse)) {
                return "reCAPTCHA verification successful!";
            } else {
                return "reCAPTCHA verification failed!";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error while verifying reCAPTCHA!";
        }
    }
}
