package com.wangping.ClaimCenter.enums;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentStatus {
    PENDING,
    PROCESSING,
    PAID,
    FAILED;

    @JsonValue
    public String getValue() {
        return this.name();
    }
}
