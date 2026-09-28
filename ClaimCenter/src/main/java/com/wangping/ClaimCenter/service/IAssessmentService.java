package com.wangping.ClaimCenter.service;

import com.wangping.ClaimCenter.dto.ClaimAssessmentResponse;
import com.wangping.ClaimCenter.entity.ClaimAssessment;

import java.util.Optional;

public interface IAssessmentService {
    ClaimAssessmentResponse assess(Long  claimId);

    ClaimAssessmentResponse getLatestAssessment(Long claimId);

}
