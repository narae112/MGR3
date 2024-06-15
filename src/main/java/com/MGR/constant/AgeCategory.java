package com.MGR.constant;

public enum AgeCategory {
    NOMATTER("상관 없음"),
    TEENAGER("10대"),
    TWENTYTOTHIRTY("20대-30대"),
    THIRTYTOFORTY("30대-40대"),
    FORTYTOFIFTY("40대-50대"),
    OVERFIFTY("50대 이상");

    private final String displayName;
    AgeCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
