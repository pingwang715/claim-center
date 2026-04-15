package com.wangping.ClaimCenter.service.impl;

import com.wangping.ClaimCenter.dto.AssignClaimRequestDto;
import com.wangping.ClaimCenter.dto.ClaimDetailDto;
import com.wangping.ClaimCenter.dto.ClaimDto;
import com.wangping.ClaimCenter.dto.CreateClaimRequestDto;
import com.wangping.ClaimCenter.entity.Claim;
import com.wangping.ClaimCenter.entity.ClaimAssignment;
import com.wangping.ClaimCenter.entity.ClaimHistory;
import com.wangping.ClaimCenter.entity.User;
import com.wangping.ClaimCenter.enums.ActionType;
import com.wangping.ClaimCenter.enums.ClaimStatus;
import com.wangping.ClaimCenter.repository.ClaimAssignmentRepository;
import com.wangping.ClaimCenter.repository.ClaimHistoryRepository;
import com.wangping.ClaimCenter.repository.ClaimRepository;
import com.wangping.ClaimCenter.repository.UserRepository;
import com.wangping.ClaimCenter.service.IClaimService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements IClaimService {

    private final ClaimRepository claimRepository;
    private final UserRepository userRepository;
    private final ClaimHistoryRepository claimHistoryRepository;
    private final ClaimAssignmentRepository claimAssignmentRepository;

    @Override
    public List<ClaimDto> getClaims() {
        return claimRepository.findAll()
                .stream().map(this::transformToDTO).collect(Collectors.toList());
    }

    @Override
    public ClaimDetailDto getClaimDetail(Long id) {
        Claim claim = claimRepository.findById(id).orElseThrow(() -> new RuntimeException("claim not found" + id));
        ClaimDetailDto claimDetailDto = new ClaimDetailDto();
        BeanUtils.copyProperties(claim,claimDetailDto);
        claimDetailDto.setClaimId(claim.getId());
        return claimDetailDto;
    }

    @Override
    @Transactional
    public ClaimDetailDto createClaim(CreateClaimRequestDto createClaimRequestDto) {
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isClaimant()) {
            throw new RuntimeException("Only claimants can create claims");
        }
        Claim newClaim = transformToEntity(createClaimRequestDto);

        Claim savedClaim = claimRepository.save(newClaim);

        ClaimDetailDto claimDetailDto = new ClaimDetailDto();
        BeanUtils.copyProperties(savedClaim,claimDetailDto);
        claimDetailDto.setClaimId(savedClaim.getId());

        ClaimHistory claimHistory = new ClaimHistory();
        claimHistory.setClaim(savedClaim);
        claimHistory.setNewStatus(ClaimStatus.SUBMITTED);
        claimHistory.setActionType(ActionType.SUBMITTED);
        claimHistory.setPerformedBy(user);
        claimHistory.setNotes(null);
        claimHistory.setCreatedAt(LocalDateTime.now());
        System.out.println("Saving history...");
        claimHistoryRepository.save(claimHistory);
        System.out.println("History saved!");

        return claimDetailDto;
    }

    @Transactional
    @Override
    public ClaimDetailDto assignClaim(Long id, AssignClaimRequestDto assignClaimRequestDto) {
        User manager = userRepository.findByEmail("manager@gmail.com").orElseThrow(() -> new RuntimeException("Manager not found"));
        if (!manager.isManager()) {
            throw new RuntimeException("Only managers can assign claims");
        }
        if (id == null) {
            throw new IllegalArgumentException("claim id is null");
        }
        Claim claim = claimRepository.findById(id).orElseThrow(() -> new RuntimeException("claim not found" + id));

        User adjuster = userRepository.findById(assignClaimRequestDto.getAdjusterId()).orElseThrow(() -> new RuntimeException("Adjuster not found"));

        ClaimAssignment claimAssignment = new ClaimAssignment();
        claimAssignment.setClaim(claim);
        claimAssignment.setAdjuster(adjuster);
        claimAssignment.setAssignedAt(LocalDateTime.now());
        claimAssignmentRepository.save(claimAssignment);

        claim.setStatus(ClaimStatus.UNDER_REVIEW);
        claim.setUpdatedAt(LocalDateTime.now());

        claimRepository.save(claim);

        ClaimHistory claimHistory = new ClaimHistory();
        claimHistory.setClaim(claim);
        claimHistory.setActionType(ActionType.ASSIGNED);
        claimHistory.setOldStatus(ClaimStatus.SUBMITTED);
        claimHistory.setNewStatus(ClaimStatus.UNDER_REVIEW);
        claimHistory.setPerformedBy(manager);
        claimHistory.setNotes("Assigned to adjuster ID: " + adjuster.getUserId());
        claimHistory.setCreatedAt(LocalDateTime.now());

        claimHistoryRepository.save(claimHistory);

        ClaimDetailDto claimDetailDto = new ClaimDetailDto();
        BeanUtils.copyProperties(claim,claimDetailDto);
        claimDetailDto.setClaimId(claim.getId());

        return claimDetailDto;
    }

    @Transactional
    @Override
    public ClaimDetailDto approveClaim(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("claim id is null");
        }
        User adjuster = userRepository.findByEmail("adjuster@gmail.com").orElseThrow(() -> new RuntimeException("Adjuster not found"));
        if (!adjuster.isAdjuster()) {
            throw new RuntimeException("Only adjusters can approve claims");
        }

        Claim claim = claimRepository.findById(id).orElseThrow(() -> new RuntimeException("claim not found" + id));
        claim.setStatus(ClaimStatus.APPROVED);
        claim.setClosedAt(LocalDateTime.now());

        claimRepository.save(claim);

        ClaimHistory claimHistory = new ClaimHistory();
        claimHistory.setClaim(claim);
        claimHistory.setActionType(ActionType.APPROVED);
        claimHistory.setOldStatus(ClaimStatus.UNDER_REVIEW);
        claimHistory.setNewStatus(ClaimStatus.APPROVED);
        claimHistory.setPerformedBy(adjuster);
        claimHistory.setNotes("Claim approved by adjuster ID: " + adjuster.getUserId());
        claimHistory.setCreatedAt(LocalDateTime.now());

        claimHistoryRepository.save(claimHistory);

        ClaimDetailDto claimDetailDto = new ClaimDetailDto();
        BeanUtils.copyProperties(claim,claimDetailDto);
        claimDetailDto.setClaimId(claim.getId());
        return claimDetailDto;
    }

    @Transactional
    @Override
    public ClaimDetailDto rejectClaim(Long id) {
        User adjuster = userRepository.findByEmail("adjuster@gmail.com").orElseThrow(() -> new RuntimeException("Adjuster not found"));
        if (!adjuster.isAdjuster()) {
            throw new RuntimeException("Only adjusters can approve claims");
        }

        Claim claim = claimRepository.findById(id).orElseThrow(() -> new RuntimeException("claim not found" + id));
        claim.setStatus(ClaimStatus.REJECTED);
        claim.setClosedAt(LocalDateTime.now());

        claimRepository.save(claim);

        ClaimHistory claimHistory = new ClaimHistory();
        claimHistory.setClaim(claim);
        claimHistory.setActionType(ActionType.REJECTED);
        claimHistory.setOldStatus(ClaimStatus.UNDER_REVIEW);
        claimHistory.setNewStatus(ClaimStatus.REJECTED);
        claimHistory.setPerformedBy(adjuster);
        claimHistory.setNotes("Claim rejected by adjuster ID: " + adjuster.getUserId());
        claimHistory.setCreatedAt(LocalDateTime.now());

        claimHistoryRepository.save(claimHistory);

        ClaimDetailDto claimDetailDto = new ClaimDetailDto();
        BeanUtils.copyProperties(claim,claimDetailDto);
        claimDetailDto.setClaimId(claim.getId());
        return claimDetailDto;
    }

    @Transactional
    @Override
    public ClaimDetailDto overrideClaim(Long id, boolean approve) {
        if (id == null) {
            throw new IllegalArgumentException("claim id is null");
        }
        User manager = userRepository.findByEmail("manager@gmail.com").orElseThrow(() -> new RuntimeException("Manager not found"));
        if (!manager.isManager()) {
            throw new RuntimeException("Only managers can assign claims");
        }

        Claim claim = claimRepository.findById(id).orElseThrow(() -> new RuntimeException("claim not found" + id));
        ClaimStatus oldStatus = claim.getStatus();

        if (approve) {
            claim.setStatus(ClaimStatus.OVERRIDDEN_APPROVED);
        } else {
            claim.setStatus(ClaimStatus.OVERRIDDEN_REJECTED);
        }

        claim.setClosedAt(LocalDateTime.now());
        Claim savedClaim = claimRepository.save(claim);

        ClaimHistory claimHistory = new ClaimHistory();
        claimHistory.setClaim(claim);
        claimHistory.setOldStatus(oldStatus);
        claimHistory.setActionType(ActionType.OVERRIDDEN);
        claimHistory.setNewStatus(savedClaim.getStatus());
        claimHistory.setPerformedBy(manager);
        claimHistory.setNotes("Decision overridden by manager ID: " + manager.getUserId());
        claimHistory.setCreatedAt(LocalDateTime.now());

        claimHistoryRepository.save(claimHistory);

        ClaimDetailDto claimDetailDto = new ClaimDetailDto();
        BeanUtils.copyProperties(savedClaim,claimDetailDto);
        claimDetailDto.setClaimId(savedClaim.getId());

        return  claimDetailDto;
    }

    private ClaimDto transformToDTO(Claim claim) {
        ClaimDto claimDto = new ClaimDto();
        BeanUtils.copyProperties(claim,claimDto);
        claimDto.setClaimId(claim.getId());
        return claimDto;
    }

    private Claim transformToEntity(CreateClaimRequestDto createClaimRequestDto) {
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isClaimant()) {
            throw new RuntimeException("Only claimants can create claims");
        }
        Claim claim = new Claim();
        claim.setTitle(createClaimRequestDto.getTitle());
        claim.setDescription(createClaimRequestDto.getDescription());
        claim.setAmount(createClaimRequestDto.getAmount());
        claim.setCreatedBy(user);
        claim.setStatus(ClaimStatus.SUBMITTED);
        claim.setCreatedAt(LocalDateTime.now());

        return claim;
    }
}
