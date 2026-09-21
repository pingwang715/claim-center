package com.wangping.ClaimCenter.service;

import com.wangping.ClaimCenter.dto.ClaimAssessmentRequest;
import com.wangping.ClaimCenter.dto.ClaimAssessmentResponse;
import com.wangping.ClaimCenter.entity.Claim;

public interface IAssessmentService {
    ClaimAssessmentResponse assess(Long  claimId);

}
