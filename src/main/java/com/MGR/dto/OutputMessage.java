package com.MGR.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class OutputMessage extends ChatMessage {
    private Date time;

    public OutputMessage(String from, String text, Date date) {
        super();
    }
}
