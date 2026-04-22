package com.wangping.ClaimCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class ClaimApprovedEvent {

    private Long claimId;
    private BigDecimal amount;
}
