package com.wangping.ClaimCenter.service;


import com.wangping.ClaimCenter.dto.AssignClaimRequestDto;
import com.wangping.ClaimCenter.dto.ClaimDetailDto;
import com.wangping.ClaimCenter.dto.ClaimDto;
import com.wangping.ClaimCenter.dto.CreateClaimRequestDto;
import com.wangping.ClaimCenter.entity.User;

import java.util.List;

public interface IClaimService {

    List<ClaimDto> getClaims(User user);
    ClaimDetailDto getClaimDetail(Long id, User user);
    ClaimDetailDto createClaim(CreateClaimRequestDto createClaimRequestDto, User user);

    ClaimDetailDto assignClaim(Long id, AssignClaimRequestDto assignClaimRequestDto, User user);
    ClaimDetailDto approveClaim(Long id, User user);
    ClaimDetailDto rejectClaim(Long id, User user);
    ClaimDetailDto overrideClaim(Long id, boolean approve, User user);

}
