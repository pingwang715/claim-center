package com.wangping.ClaimCenter.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.wangping.ClaimCenter.ai.AnthropicClient;
import com.wangping.ClaimCenter.dto.ClaimAssessmentRequest;
import com.wangping.ClaimCenter.dto.ClaimAssessmentResponse;
import com.wangping.ClaimCenter.entity.ClaimHistory;
import com.wangping.ClaimCenter.entity.User;
import com.wangping.ClaimCenter.repository.ClaimHistoryRepository;
import com.wangping.ClaimCenter.repository.UserRepository;
import com.wangping.ClaimCenter.service.IAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentServiceImpl implements IAssessmentService {

    private final UserRepository userRepository;
    private final ClaimHistoryRepository claimHistoryRepository;
    private final AnthropicClient anthropicClient;
    private final ObjectMapper objectWrapper = JsonMapper.builder().build();

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public ClaimAssessmentResponse assess(ClaimAssessmentRequest request){
        // 1. Validate the claimant
        User claimant = userRepository.findById(request.getClaimantId())
                .orElseThrow(() -> new IllegalStateException(
                        "No user found for claimantId " + request.getClaimantId()));

        if (!claimant.isClaimant())  {
            throw new RuntimeException("User " + claimant.getUserId() + " is not claimant");
        }

        // 2. Build the history summary server-side from ClaimHistory
        String historySummary = buildClaimHistorySummary(request.getClaimId());

        // 3. Build the prompt and call the model
        String prompt = buildPrompt(request, historySummary);
        String rawResponse = anthropicClient.complete(prompt);

        // 4. Parse the model's JSON response into our result DTO
        return parseAssessmentResponse(rawResponse);

    }

    @Override
    public String buildClaimHistorySummary(Long claimId) {
        List<ClaimHistory> history = claimHistoryRepository.findByClaim_IdOrderByCreatedAtAsc(claimId);

        if (history.isEmpty()) {
            return "No prior history recorded for this claim.";
        }

        return history.stream()
                .map(h -> String.format(
                        "[%s] %s: %s -> %s by %s%s",
                        h.getCreatedAt(),
                        h.getActionType(),
                        h.getOldStatus(),
                        h.getNewStatus(),
                        h.getPerformedBy() != null ? h.getPerformedBy().getUserId() : "system",
                        h.getNotes() != null && !h.getNotes().isBlank() ? " - " + h.getNotes() : ""
                ))
                .collect(Collectors.joining("\n"));
    }

    @Override
    public  String buildPrompt(ClaimAssessmentRequest request, String historySummary) {
        return """
                You are a claims risk assessor for an insurance company. Analyze the claim below \
                and return your assessment as a single JSON object — nothing else, no markdown \
                fences, no commentary before or after.

                Claim details:
                - Claim ID: %d
                - Policy type: %s
                - Claimed amount: %s
                - Incident date: %s
                - Submitted: %s
                - Description: %s

                Claimant history:
                %s

                Return exactly this JSON shape:
                {
                  "riskScore": <integer 0-100, higher = riskier>,
                  "summary": "<2-3 sentence plain-language summary of the claim>",
                  "fraudIndicators": ["<short phrase>", ...],
                  "recommendedAction": "<one of: APPROVE, INVESTIGATE, DENY>",
                  "rawModelNote": "<any caveat or uncertainty you want to flag>"
                }
                """.formatted(
                        request.getClaimantId(),
                        request.getType(),
                request.getClaimedAmount(),
                request.getIncidentDate() != null ? request.getIncidentDate().format(DATE_FMT) : "unknown",
                request.getCreatedAt(),
                request.getDescription(),
                historySummary

        );
    }

    @Override
    public ClaimAssessmentResponse parseAssessmentResponse(String rawResponse) {
        String json = extractJson(rawResponse);
        try {
            return objectWrapper.readValue(json, ClaimAssessmentResponse.class);
        } catch (Exception e) {
            // Fail soft: surface the raw text rather than losing the model's output entirely
            ClaimAssessmentResponse fallback = new ClaimAssessmentResponse();
            fallback.setRiskScore(-1);
            fallback.setSummary("Could not parse model response");
            fallback.setRecommendedAction("INVESTIGATE");
            fallback.setRawModelNote(rawResponse);
            return fallback;
        }
    }

    @Override
    public String extractJson(String rawResponse) {
        String trimmed = rawResponse.strip();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.replaceAll("^```(json)?", "").replaceAll("```$", "").strip();
        }

        return trimmed;
    }
}
