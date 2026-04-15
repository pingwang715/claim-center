package com.wangping.ClaimCenter.dto;

import com.wangping.ClaimCenter.enums.ClaimStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ClaimDto {

    private Long claimId;
    private String title;
    private BigDecimal amount;
    private ClaimStatus status;
}
