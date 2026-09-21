package com.wangping.ClaimCenter;

import com.wangping.ClaimCenter.ai.AnthropicClient;
import com.wangping.ClaimCenter.dto.ClaimAssessmentRequest;
import com.wangping.ClaimCenter.dto.ClaimAssessmentResponse;
import com.wangping.ClaimCenter.entity.ClaimHistory;
import com.wangping.ClaimCenter.entity.User;
import com.wangping.ClaimCenter.enums.ActionType;
import com.wangping.ClaimCenter.enums.ClaimStatus;
import com.wangping.ClaimCenter.enums.PolicyType;
import com.wangping.ClaimCenter.repository.ClaimHistoryRepository;
import com.wangping.ClaimCenter.repository.UserRepository;
import com.wangping.ClaimCenter.service.impl.AssessmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssessmentServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClaimHistoryRepository claimHistoryRepository;

    @Mock
    private AnthropicClient anthropicClient;

    @InjectMocks
    private AssessmentServiceImpl assessmentService;

    private ClaimAssessmentRequest request;
    private User claimantUser;

    @BeforeEach
    void setUp() {
        request = new ClaimAssessmentRequest();
        request.setClaimId(1L);
        request.setClaimantId(42L);
        request.setType(PolicyType.CAR);
        request.setClaimedAmount(new BigDecimal("2500.00"));
        request.setIncidentDate(LocalDate.of(2026, 8, 1).atStartOfDay());
        request.setCreatedAt(LocalDateTime.of(2026,8,3,10,0));
        request.setDescription("Rear-end collision on the A9");

        claimantUser = mock(User.class);

        lenient().when(claimantUser.isClaimant()).thenReturn(true);
        lenient().when(claimantUser.getUserId()).thenReturn(42L);

    }

    @Test
    void assess_returnParsedResult_whenClaimantValidAndModelReturnsWellFormedJson() {
        when(userRepository.findById(42L)).thenReturn(Optional.of(claimantUser));
        when(claimHistoryRepository.findByClaim_IdOrderByCreatedAtAsc(1L))
                .thenReturn(List.of());

        String modelJson = """
                {
                  "riskScore": 35,
                  "summary": "Straightforward rear-end collision, no red flags.",
                  "fraudIndicators": [],
                  "recommendedAction": "APPROVE",
                  "rawModelNote": ""
                }
               """;
        when(anthropicClient.complete(any(String.class))).thenReturn(modelJson);

        ClaimAssessmentResponse result = assessmentService.assess(request.getClaimId());

        assertThat(result.getRiskScore()).isEqualTo(35);
        assertThat(result.getRecommendedAction()).isEqualTo("APPROVE");
        assertThat(result.getSummary()).contains("Straightforward rear-end collision");

        verify(anthropicClient).complete(any(String.class));
    }

    @Test
    void assess_stripsMarkdownFences_whenModelWrapsJsonInCodeBlock() {
        when(userRepository.findById(42L)).thenReturn(Optional.of(claimantUser));
        when(claimHistoryRepository.findByClaim_IdOrderByCreatedAtAsc(1L)).thenReturn(List.of());

        String fencedJson = """
```json
                {
                  "riskScore": 60,
                  "summary": "Some inconsistencies in the timeline.",
                  "fraudIndicators": ["late reporting"],
                  "recommendedAction": "INVESTIGATE",
                  "rawModelNote": "Timeline gap noted."
                }
```
                """;
        when(anthropicClient.complete(any(String.class))).thenReturn(fencedJson);

        ClaimAssessmentResponse result = assessmentService.assess(request.getClaimId());

        assertThat(result.getRiskScore()).isEqualTo(60);
        assertThat(result.getFraudIndicators().contains("late reporting"));
    }

    @Test
    void assess_returnsFallbackResult_whenModelResponseIsMalformed() {
        when(userRepository.findById(42L)).thenReturn(Optional.of(claimantUser));
        when(claimHistoryRepository.findByClaim_IdOrderByCreatedAtAsc(1L)).thenReturn(List.of());
        when(anthropicClient.complete(any(String.class))).thenReturn("not valid json at all");

        ClaimAssessmentResponse result = assessmentService.assess(request.getClaimId());

        assertThat(result.getRiskScore()).isEqualTo(-1);
        assertThat(result.getRecommendedAction()).isEqualTo("INVESTIGATE");
        assertThat(result.getSummary()).isEqualTo("Could not parse model response");
    }

    @Test
    void assess_throwsIllegalStateException_whenClaimantIdDoesNotResolveToUser() {
        when(userRepository.findById(42L)).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> assessmentService.assess(request.getClaimantId()));

        // Should fail fast - never reach the model call
        verify(anthropicClient, never()).complete(any(String.class));
        verify(claimHistoryRepository, never()).findByClaim_IdOrderByCreatedAtAsc(1L);
    }

    @Test
    void assess_throwsIllegalStateException_whenUserIsNotClaimant() {
        User adjusterUser = mock(User.class);
        when(adjusterUser.isClaimant()).thenReturn(false);
        when(userRepository.findById(42L)).thenReturn(Optional.of(adjusterUser));

        assertThrows(RuntimeException.class, () -> assessmentService.assess(request.getClaimId()));

        verify(anthropicClient, never()).complete(any(String.class));

    }

    @Test
    void assess_buildsHistorySummary_whenPriorClaimHistoryExists() {
        when(userRepository.findById(42L)).thenReturn(Optional.of(claimantUser));

        ClaimHistory historyEntry = mock(ClaimHistory.class);
        when(historyEntry.getActionType()).thenReturn(ActionType.valueOf("ASSIGNED"));
        when(historyEntry.getOldStatus()).thenReturn(ClaimStatus.valueOf("SUBMITTED"));
        when(historyEntry.getNewStatus()).thenReturn(ClaimStatus.valueOf("UNDER_REVIEW"));
        when(historyEntry.getPerformedBy()).thenReturn(null);
        when(historyEntry.getNotes()).thenReturn("Assigned by manager");
        when(historyEntry.getCreatedAt()).thenReturn(LocalDateTime.of(2026, 8, 2, 9, 0));

        when(claimHistoryRepository.findByClaim_IdOrderByCreatedAtAsc(1L)).thenReturn(List.of(historyEntry));

        String validJson = """
                {"riskScore": 20, "summary": "ok", "fraudIndicators": [], "recommendedAction": "APPROVE", "rawModelNote": ""}
                """;
        when(anthropicClient.complete(any(String.class))).thenReturn(validJson);

        assessmentService.assess(claimantUser.getUserId());

        // Captures the actual prompt string sent to the model and checks the
        // history summary made it in, rather than trusting it blindly.
        var promptCaptor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(anthropicClient).complete(promptCaptor.capture());
        assertThat(promptCaptor.getValue()).contains("ASSIGNED");
        assertThat(promptCaptor.getValue()).contains("Assigned by manager");
    }
}
