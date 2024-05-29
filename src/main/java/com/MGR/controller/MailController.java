package com.MGR.controller;

import com.MGR.service.MailService;
import com.MGR.service.MemberService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @GetMapping("/member/sendEmail")
    public ResponseEntity<String> sendEmail(@RequestParam("email") String email,
                                            Model model) throws MessagingException {
        //인증 메일 보내기
        String code = mailService.createCode();
        mailService.createMailForm(email, code);
        mailService.sendMail(email, code);
        model.addAttribute("authCode", code);
        return ResponseEntity.ok("인증 메일이 발송되었습니다");
    }

}
