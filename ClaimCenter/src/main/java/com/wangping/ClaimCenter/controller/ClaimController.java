package com.wangping.ClaimCenter.controller;

import com.wangping.ClaimCenter.dto.*;
import com.wangping.ClaimCenter.service.IClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final IClaimService iClaimService;

    @GetMapping
    public ResponseEntity<List<ClaimDto>> getClaims(){
        List<ClaimDto> claimList = iClaimService.getClaims();
        return ResponseEntity.ok().body(claimList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaimDetailDto> getClaimDetail(@PathVariable Long id){
        ClaimDetailDto claimDetailDto = iClaimService.getClaimDetail(id);
        return ResponseEntity.ok().body(claimDetailDto);
    }

    @PostMapping
    public ResponseEntity<ClaimDetailDto> createClaim(@RequestBody CreateClaimRequestDto createClaimRequestDto){
        ClaimDetailDto claimDetailDto = iClaimService.createClaim(createClaimRequestDto);
        return ResponseEntity.ok().body(claimDetailDto);
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<ClaimDetailDto> assignClaim(@PathVariable Long id, @RequestBody AssignClaimRequestDto assignClaimRequestDto){
        ClaimDetailDto claimDetailDto = iClaimService.assignClaim(id, assignClaimRequestDto);
        return ResponseEntity.ok().body(claimDetailDto);
    }

    @PostMapping("/{id}/approve")
    public  ResponseEntity<ClaimDetailDto> approveClaim(@PathVariable Long id){
        ClaimDetailDto claimDetailDto = iClaimService.approveClaim(id);
        return ResponseEntity.ok().body(claimDetailDto);
    }

    @PostMapping("/{id}/reject")
    public  ResponseEntity<ClaimDetailDto> rejectClaim(@PathVariable Long id){
        ClaimDetailDto claimDetailDto = iClaimService.rejectClaim(id);
        return ResponseEntity.ok().body(claimDetailDto);
    }

    @PostMapping("/{id}/override")
    public  ResponseEntity<ClaimDetailDto> overrideClaim(@PathVariable Long id, @RequestBody OverrideRequestDto overrideRequestDto){
        ClaimDetailDto claimDetailDto = iClaimService.overrideClaim(id, overrideRequestDto.isApprove());
        return ResponseEntity.ok().body(claimDetailDto);
    }
}
