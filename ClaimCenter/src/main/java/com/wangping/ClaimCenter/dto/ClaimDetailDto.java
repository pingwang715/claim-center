package com.wangping.ClaimCenter.dto;

import com.wangping.ClaimCenter.enums.ClaimStatus;
import com.wangping.ClaimCenter.enums.PolicyType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ClaimDetailDto {

    private Long claimId;
    private String title;
    private String description;
    private PolicyType type;
    private BigDecimal claimedAmount;
    private BigDecimal payoutAmount;
    private ClaimStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;
}
