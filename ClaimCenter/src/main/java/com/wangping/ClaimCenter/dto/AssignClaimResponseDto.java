package com.wangping.ClaimCenter.dto;

import com.wangping.ClaimCenter.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignClaimResponseDto {

    private User adjuster;
    private Long claimId;
    private boolean isAssigned;
}
