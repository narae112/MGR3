//package com.MGR.controller;
//
////import com.MGR.service.KakaoService;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@Controller
//@RequestMapping("/member/kakao")
//@RequiredArgsConstructor
//public class KaKaoController {
//
////    private final KakaoService kakaoService;
//
////    @GetMapping("/login")
////    public String kakaoLogin(){
////        StringBuffer url = new StringBuffer();
////        url.append("https://kauth.kakao.com/oauth/authorize?");
////        url.append("client_id=" + "ac3e1526cd9839b6cf1cc8b52c393ff0");
////        url.append("&redirect_uri=http://localhost:8080/member/login/kakao");
////        url.append("&response_type=code");
////
////        return "redirect:" + url.toString();
////    }
//
///*    @RequestMapping("/")
//    public String kakaoLogin(@RequestParam("code") String code, HttpSession session) throws Exception {
//
//        String access_token = kakaoService.getToken(code);//code로 토큰 받음
//        System.out.println("access_token : " + access_token);
//
//
//        return "";
//    }*/
//
//}
