package com.wangping.ClaimCenter.service;


import com.wangping.ClaimCenter.dto.AssignClaimRequestDto;
import com.wangping.ClaimCenter.dto.ClaimDetailDto;
import com.wangping.ClaimCenter.dto.ClaimDto;
import com.wangping.ClaimCenter.dto.CreateClaimRequestDto;

import java.util.List;

public interface IClaimService {

    List<ClaimDto> getClaims();
    ClaimDetailDto getClaimDetail(Long id);
    ClaimDetailDto createClaim(CreateClaimRequestDto createClaimRequestDto);

    ClaimDetailDto assignClaim(Long id, AssignClaimRequestDto assignClaimRequestDto);
    ClaimDetailDto approveClaim(Long id);
    ClaimDetailDto rejectClaim(Long id);
    ClaimDetailDto overrideClaim(Long id, boolean approve);

}
