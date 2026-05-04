package com.wangping.ClaimCenter.enums;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ClaimStatus {
    SUBMITTED,
    UNDER_REVIEW,
    APPROVED,
    REJECTED,
    OVERRIDDEN_APPROVED,
    OVERRIDDEN_REJECTED;

    @JsonValue
    public String getValue() {
        return this.name();
    }
}
