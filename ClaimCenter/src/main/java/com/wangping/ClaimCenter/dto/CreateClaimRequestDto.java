package com.wangping.ClaimCenter.dto;

import com.wangping.ClaimCenter.enums.PolicyType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateClaimRequestDto {

    private String title;
    private String description;
    private BigDecimal claimedAmount;
    private PolicyType type;
}
