package com.wangping.ClaimCenter.controller;

import com.wangping.ClaimCenter.dto.*;
import com.wangping.ClaimCenter.entity.User;
import com.wangping.ClaimCenter.repository.UserRepository;
import com.wangping.ClaimCenter.service.IClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("api/v1/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final IClaimService iClaimService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<ClaimDto>> getClaims(Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        List<ClaimDto> claimList = iClaimService.getClaims(user);
        return ResponseEntity.ok().body(claimList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaimDetailDto> getClaimDetail(@PathVariable Long id, Authentication authentication) throws AccessDeniedException {
        String username = (String) authentication.getPrincipal();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        ClaimDetailDto claimDetailDto = iClaimService.getClaimDetail(id, user);
        return ResponseEntity.ok().body(claimDetailDto);
    }

    @PostMapping
    public ResponseEntity<ClaimDetailDto> createClaim(@Valid @RequestBody CreateClaimRequestDto createClaimRequestDto, Authentication authentication) throws AccessDeniedException {
        String username = (String) authentication.getPrincipal();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        ClaimDetailDto claimDetailDto = iClaimService.createClaim(createClaimRequestDto, user);
        return ResponseEntity.ok().body(claimDetailDto);
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<AssignClaimResponseDto> assignClaim(@PathVariable Long id, @RequestBody AssignClaimRequestDto assignClaimRequestDto, Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        AssignClaimResponseDto assignClaimResponseDto = iClaimService.assignClaim(id, assignClaimRequestDto, user);
        return ResponseEntity.ok().body(assignClaimResponseDto);
    }

    @PostMapping("/{id}/approve")
    public  ResponseEntity<ClaimDetailDto> approveClaim(@PathVariable Long id, Authentication authentication) throws AccessDeniedException {
        String username = (String) authentication.getPrincipal();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        ClaimDetailDto claimDetailDto = iClaimService.approveClaim(id, user);
        return ResponseEntity.ok().body(claimDetailDto);
    }

    @PostMapping("/{id}/reject")
    public  ResponseEntity<ClaimDetailDto> rejectClaim(@PathVariable Long id, Authentication authentication) throws AccessDeniedException {
        String username = (String) authentication.getPrincipal();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        ClaimDetailDto claimDetailDto = iClaimService.rejectClaim(id, user);
        return ResponseEntity.ok().body(claimDetailDto);
    }

    @PostMapping("/{id}/override")
    public  ResponseEntity<ClaimDetailDto> overrideClaim(@PathVariable Long id, @RequestBody OverrideRequestDto overrideRequestDto, Authentication authentication){
        String username = (String) authentication.getPrincipal();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        ClaimDetailDto claimDetailDto = iClaimService.overrideClaim(id, overrideRequestDto.isApprove(), user);
        return ResponseEntity.ok().body(claimDetailDto);
    }
}
