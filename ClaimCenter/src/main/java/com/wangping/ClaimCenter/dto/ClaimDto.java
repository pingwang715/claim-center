package com.wangping.ClaimCenter.dto;

import com.wangping.ClaimCenter.enums.ClaimStatus;
import com.wangping.ClaimCenter.enums.PaymentStatus;
import com.wangping.ClaimCenter.enums.PolicyType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ClaimDto {

    private Long claimId;
    private String title;
    private BigDecimal claimedAmount;
    private ClaimStatus status;
    private PaymentStatus paymentStatus;
    private PolicyType type;

}
