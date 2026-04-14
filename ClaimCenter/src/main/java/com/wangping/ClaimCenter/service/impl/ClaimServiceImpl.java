package com.wangping.ClaimCenter.service.impl;

import com.wangping.ClaimCenter.repository.ClaimRepository;
import com.wangping.ClaimCenter.service.IClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements IClaimService {
    private final ClaimRepository claimRepository;
}
