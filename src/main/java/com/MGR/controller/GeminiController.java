//package com.MGR.controller;
//
//import com.MGR.service.GeminiService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.HttpClientErrorException;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/gemini")
//public class GeminiController {
//
//    private final GeminiService geminiService;
//
//    @GetMapping("/chat")
//    public ResponseEntity<?> gemini(@RequestParam String message) {
//        try {
//            String response = geminiService.getContents(message);
//            return ResponseEntity.ok().body(response);
//        } catch (HttpClientErrorException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//}