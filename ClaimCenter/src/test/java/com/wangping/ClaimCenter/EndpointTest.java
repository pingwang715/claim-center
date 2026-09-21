package com.wangping.ClaimCenter;

import com.wangping.ClaimCenter.security.ClaimCenterSecurityConfig;
import com.wangping.ClaimCenter.controller.ClaimAssessmentController;
import com.wangping.ClaimCenter.dto.ClaimAssessmentResponse;
import com.wangping.ClaimCenter.service.IAssessmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClaimAssessmentController.class)
@Import(ClaimCenterSecurityConfig.class)   // makes sure @EnableMethodSecurity is active
class ClaimAssessmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IAssessmentService assessmentService;

    @Test
    @WithMockUser(roles = "CLAIMANT")
    void claimantCannotRunAssessment() throws Exception {
        mockMvc.perform(post("/api/claims/1/ai-assessment").with(csrf()))
                .andExpect(status().isForbidden());

        verify(assessmentService, never()).assess(anyLong());   // proves the method body never ran
    }

    @Test
    @WithMockUser(roles = "ADJUSTER")
    void adjusterCanRunAssessment() throws Exception {
        ClaimAssessmentResponse response = new ClaimAssessmentResponse();
        response.setRiskScore(35);
        response.setSummary("Test summary");
        response.setRecommendedAction("INVESTIGATE");

        when(assessmentService.assess(1L)).thenReturn(response);

        mockMvc.perform(post("/api/claims/1/ai-assessment").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskScore").value(35))
                .andExpect(jsonPath("$.recommendedAction").value("INVESTIGATE"));
    }

    @Test
    void unauthenticatedRequestIsRejected() throws Exception {
        mockMvc.perform(post("/api/claims/1/ai-assessment").with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}