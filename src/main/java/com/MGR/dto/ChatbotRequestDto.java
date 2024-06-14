package com.MGR.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatbotRequestDto { //챗봇한테 요청하는 dto
    private List<Content> contents; //채팅 요청의 내용을 담고 있는 리스트
    private GenerationConfig generationConfig; //텍스트 생성 설정을 담고 있는 객체

    @Getter @Setter
    public static class Content {
        private Parts parts; //api 한테 메세지 보낼때 json{content{parts}} 이런 형식
    }

    @Getter @Setter
    public static class Parts {
        private String text; //

    }

    @Getter @Setter
    public static class GenerationConfig {
        private int candidate_count; //생성할 텍스트 수
        private int max_output_tokens; //생성할 텍스트의 최대 토큰 수
        private double temperature; // 텍스트 생성의 다양성 조절
    }

    public ChatbotRequestDto(String prompt) { //설정 초기화
        this.contents = new ArrayList<>();
        Content content = new Content();
        Parts parts = new Parts();

        parts.setText(prompt);
        content.setParts(parts);

        this.contents.add(content);
        this.generationConfig = new GenerationConfig();
        this.generationConfig.setCandidate_count(1);
        this.generationConfig.setMax_output_tokens(1000);
        this.generationConfig.setTemperature(0.7);
    }
}