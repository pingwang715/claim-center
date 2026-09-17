package com.wangping.ClaimCenter.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClaimAssessmentResponse {
    private Long claimId;
    private int riskScore;
    private String summary;
    List<String> fraudIndicators;
    private String recommendedAction;
    private String rawModelNote;
}
