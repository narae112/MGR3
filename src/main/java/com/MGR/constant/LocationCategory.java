package com.MGR.constant;

public enum LocationCategory {
    SEOUL("서울"), BUSAN("부산");

    private final String displayName;
    LocationCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
