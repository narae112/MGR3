package com.MGR.service;

import com.MGR.dto.ChatRequestDto;
import com.MGR.dto.ChatResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Qualifier("geminiRestTemplate")
    private final RestTemplate restTemplate;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public String getContents(String prompt) {

        // Gemini에 요청 전송
        String requestUrl = apiUrl + "?key=" + geminiApiKey;

        ChatRequestDto request = new ChatRequestDto(prompt);
        ChatResponseDto response = restTemplate.postForObject(requestUrl, request, ChatResponseDto.class);

        String message = response.getCandidates().get(0).getContent().getParts().get(0).getText().toString();

        return message;
    }
}