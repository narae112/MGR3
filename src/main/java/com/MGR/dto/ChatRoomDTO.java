package com.MGR.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChatRoomDTO {
    private Long id;
    private String name;
    private Long senderId;
    private Long receiverId;
    private Boolean isGlobal;
    private List<ChatDTO> chats;
}