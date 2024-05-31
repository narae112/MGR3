package com.MGR.constant;

public enum DiscountType {
    PERCENT("할인율(%)"), AMOUNT("할인액(원)");

    private final String displayName;

    DiscountType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
