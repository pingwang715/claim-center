package com.wangping.ClaimCenter.controller;

import com.wangping.ClaimCenter.dto.ClaimAssessmentRequest;
import com.wangping.ClaimCenter.dto.ClaimAssessmentResponse;
import com.wangping.ClaimCenter.service.IAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/claims")
@RequiredArgsConstructor
public class ClaimAssessmentController {

    private final IAssessmentService iAssessmentService;

    @PostMapping("/{id}/ai-assessment")
    @PreAuthorize("hasAnyRole('ADJUSTER', 'MANAGER')")
    public ResponseEntity<ClaimAssessmentResponse> assessClaim(@PathVariable Long id) {

        return ResponseEntity.ok(iAssessmentService.assess(id));
    }
}
