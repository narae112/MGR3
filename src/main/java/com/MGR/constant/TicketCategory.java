package com.MGR.constant;

public enum TicketCategory {
    ADULT("성인"),
    CHILD("아동");

    private final String displayName;

    TicketCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
