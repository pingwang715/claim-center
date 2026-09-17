package com.wangping.ClaimCenter.dto;

import com.wangping.ClaimCenter.enums.PolicyType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ClaimAssessmentRequest {
    private Long claimId;
    private Long claimantId;
    private PolicyType type;
    private BigDecimal claimedAmount;
    private LocalDateTime createdAt;
    private String description;
    private LocalDateTime incidentDate;
}
