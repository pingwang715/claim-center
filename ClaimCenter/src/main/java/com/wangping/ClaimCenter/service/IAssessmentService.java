package com.wangping.ClaimCenter.service;

import com.wangping.ClaimCenter.dto.ClaimAssessmentRequest;
import com.wangping.ClaimCenter.dto.ClaimAssessmentResponse;

public interface IAssessmentService {
    ClaimAssessmentResponse assess(ClaimAssessmentRequest request);
    String buildClaimHistorySummary(Long claimId);
    String buildPrompt(ClaimAssessmentRequest request, String historySummary);
    ClaimAssessmentResponse parseAssessmentResponse(String rawResponse);
    String extractJson(String rawResponse);
}
