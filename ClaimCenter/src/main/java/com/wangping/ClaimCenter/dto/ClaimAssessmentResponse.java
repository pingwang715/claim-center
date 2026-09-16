package com.wangping.ClaimCenter.dto;

import java.util.List;

public class ClaimAssessmentResponse {
    private Long claimId;
    private int riskScore;
    private String summary;
    List<String> fraudIndicators;
    private String recommendedAction;
    private String rawModelNote;
}
