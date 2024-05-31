package com.MGR.constant;

public enum CouponType {
    BIRTH("생일자"),
    ALL("전체 이용자");

    private final String displayName;

    CouponType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
