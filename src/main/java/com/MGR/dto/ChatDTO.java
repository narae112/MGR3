package com.MGR.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatDTO {
    private Long id;
    private Long roomId;
    private Long senderId;
    private String senderNickname;
    private String senderEmail;
    private String profileImgUrl;
    private String message;
    private LocalDateTime sendDate;
}