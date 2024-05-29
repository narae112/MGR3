package com.MGR.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponseDto { //챗봇의 응답을 받아오는 dto

    private List<Candidate> candidates; //응답 후보 리스트
    private PromptFeedback promptFeedback;

    @Getter
    @Setter
    public static class Candidate { //응답 후보
        private Content content;
        private String finishReason;
        private int index;
        private List<SafetyRating> safetyRatings;

    }

    @Getter @Setter
    @ToString
    public static class Content { //응답 콘텐츠
        private List<Parts> parts;
        private String role;

    }

    @Getter @Setter
    @ToString
    public static class Parts {
        private String text;

    }

    @Getter @Setter
    public static class SafetyRating {
        private String category;
        private String probability;
    }

    @Getter @Setter
    public static class PromptFeedback {
        private List<SafetyRating> safetyRatings;

    }
}
