package com.wangping.ClaimCenter.service;


import com.wangping.ClaimCenter.dto.*;
import com.wangping.ClaimCenter.entity.User;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

public interface IClaimService {

    List<ClaimDto> getClaims(User user);
    ClaimDetailDto getClaimDetail(Long id, User user) throws AccessDeniedException, java.nio.file.AccessDeniedException;
    ClaimDetailDto createClaim(CreateClaimRequestDto createClaimRequestDto, User user) throws AccessDeniedException, java.nio.file.AccessDeniedException;

    AssignClaimResponseDto assignClaim(Long id, AssignClaimRequestDto assignClaimRequestDto, User user) throws AccessDeniedException;
    ClaimDetailDto approveClaim(Long id, User user) throws java.nio.file.AccessDeniedException;
    ClaimDetailDto rejectClaim(Long id, User user) throws AccessDeniedException, java.nio.file.AccessDeniedException;
    ClaimDetailDto overrideClaim(Long id, boolean approve, User user);

}
