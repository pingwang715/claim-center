package com.wangping.ClaimCenter.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PolicyType {
    HEALTH,
    CAR,
    TRAVEL,
    PET,
    PROPERTY;

    @JsonCreator
    public static PolicyType fromString(String policyType){
        return PolicyType.valueOf(policyType.toUpperCase());
    }
}

