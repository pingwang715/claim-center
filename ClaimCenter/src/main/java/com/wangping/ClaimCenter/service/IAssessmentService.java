package com.wangping.ClaimCenter.service;

import com.wangping.ClaimCenter.dto.ClaimAssessmentResponse;

public interface IAssessmentService {
    ClaimAssessmentResponse assess(Long  claimId);

}
