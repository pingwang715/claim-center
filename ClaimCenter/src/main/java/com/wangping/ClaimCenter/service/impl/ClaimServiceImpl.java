package com.wangping.ClaimCenter.service.impl;

import com.wangping.ClaimCenter.dto.*;
import com.wangping.ClaimCenter.entity.Claim;
import com.wangping.ClaimCenter.entity.ClaimAssignment;
import com.wangping.ClaimCenter.entity.ClaimHistory;
import com.wangping.ClaimCenter.entity.User;
import com.wangping.ClaimCenter.enums.ActionType;
import com.wangping.ClaimCenter.enums.ClaimStatus;
import com.wangping.ClaimCenter.enums.Role;
import com.wangping.ClaimCenter.repository.ClaimAssignmentRepository;
import com.wangping.ClaimCenter.repository.ClaimHistoryRepository;
import com.wangping.ClaimCenter.repository.ClaimRepository;
import com.wangping.ClaimCenter.repository.UserRepository;
import com.wangping.ClaimCenter.service.IClaimService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements IClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimHistoryRepository claimHistoryRepository;
    private final ClaimAssignmentRepository claimAssignmentRepository;
    private final UserRepository userRepository;

    @Override
    public List<ClaimDto> getClaims(User user) {
        String email = user.getEmail();
        Role role = user.getRole();
        List<Claim> claims;
        switch (role) {

            case MANAGER:
                claims = claimRepository.findAll();
                break;

            case CLAIMANT:
                claims = claimRepository.findByCreatedBy_Email(email);
                break;

            case ADJUSTER:
                claims = claimRepository.findByActiveAdjusterEmail(email);
                break;
                default:
                    throw new IllegalStateException("Unknown role: " + role );
        }
        return claims.stream().map(this::transformToDTO).collect(Collectors.toList());
    }

    @Override
    public ClaimDetailDto getClaimDetail(Long id, User user) {
        String email = user.getEmail();
        Role role = user.getRole();
        Claim claim = claimRepository.findById(id).orElseThrow(() -> new RuntimeException("claim not found" + id));

        switch (role) {
            case MANAGER:
                break;
            case CLAIMANT:
                if (!claim.getCreatedBy().getEmail().equals(email)) {
                    throw new AccessDeniedException ("Access denied - you're not the claimant who created this claim");
                }
                break;
            case ADJUSTER:
//                boolean assigned = claim.getClaimAssignments().stream().anyMatch(a -> a.getAdjuster().getEmail().equals(email));
                ClaimAssignment activeAssignment = claimAssignmentRepository.findTopByClaimIdAndIsActiveTrueOrderByAssignedAtDesc(id);
                if (!activeAssignment.getAdjuster().getEmail().equals(email)) {
                    throw new AccessDeniedException("Access denied - you're not assigned to this claim, or your manager reassigned another adjuster");
                }
                break;
            default:
                throw new IllegalStateException("Unknown role: " + role );
        }

        ClaimDetailDto claimDetailDto = new ClaimDetailDto();
        BeanUtils.copyProperties(claim,claimDetailDto);
        claimDetailDto.setClaimId(claim.getId());

        return claimDetailDto;
    }

    @Override
    @Transactional
    public ClaimDetailDto createClaim(CreateClaimRequestDto createClaimRequestDto, User user) {
        Role role = user.getRole();

        if (role != Role.CLAIMANT) {
            throw new AccessDeniedException("Only claimants can create claims");
        }
        Claim newClaim = transformToEntity(createClaimRequestDto, user);

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
    public AssignClaimResponseDto assignClaim(Long id, AssignClaimRequestDto assignClaimRequestDto, User user) {
        Role role = user.getRole();
        if (role != Role.MANAGER) {
            throw new RuntimeException("Only managers can assign claims");
        }
        if (id == null) {
            throw new IllegalArgumentException("claim id is null");
        }

        User adjuster = userRepository.findById(assignClaimRequestDto.getAdjusterId()).orElseThrow(() -> new RuntimeException("adjuster not found: " + assignClaimRequestDto.getAdjusterId()));
        if (!adjuster.isAdjuster()) {
            throw new RuntimeException("Assigned user is not an adjuster");
        }
        Claim claim = claimRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("claim not found: " + id));

        if (claim.getStatus().equals(ClaimStatus.OVERRIDDEN_APPROVED) || claim.getStatus().equals(ClaimStatus.OVERRIDDEN_REJECTED)) {
            throw new IllegalStateException("claim status is already overridden - you can't reassign this claim");
        } else if (claim.getStatus().equals(ClaimStatus.APPROVED)) {
            throw new IllegalStateException("claim status is already approved - you can't reassign this claim");
        }

        List<ClaimAssignment> activeAssignments = claimAssignmentRepository.findByClaimIdAndIsActiveTrue(id);
        activeAssignments.forEach(assignment -> assignment.setActive(false));
        claimAssignmentRepository.saveAll(activeAssignments);

        ClaimAssignment claimAssignment = new ClaimAssignment();
        claimAssignment.setClaim(claim);
        claimAssignment.setAdjuster(adjuster);
        claimAssignment.setAssignedAt(LocalDateTime.now());
        claimAssignment.setActive(true);
        claimAssignmentRepository.save(claimAssignment);

        claim.setStatus(ClaimStatus.UNDER_REVIEW);
        claim.setUpdatedAt(LocalDateTime.now());

        claimRepository.save(claim);

        ClaimHistory claimHistory = new ClaimHistory();
        claimHistory.setClaim(claim);
        claimHistory.setActionType(ActionType.ASSIGNED);
        claimHistory.setOldStatus(claim.getStatus());
        claimHistory.setNewStatus(ClaimStatus.UNDER_REVIEW);
        claimHistory.setPerformedBy(user);
        claimHistory.setNotes("Assigned to adjuster ID: " + adjuster.getUserId());
        claimHistory.setCreatedAt(LocalDateTime.now());

        claimHistoryRepository.save(claimHistory);

        AssignClaimResponseDto assignClaimResponseDto = new AssignClaimResponseDto();
        assignClaimResponseDto.setAssigned(true);
        assignClaimResponseDto.setAdjuster(claimAssignment.getAdjuster());
        assignClaimResponseDto.setClaimId(claim.getId());

        return assignClaimResponseDto;
    }

    @Transactional
    @Override
    public ClaimDetailDto approveClaim(Long id, User user) {
        Role  role = user.getRole();
        if (id == null) {
            throw new IllegalArgumentException("claim id is null");
        }

        if (role != Role.ADJUSTER) {
            throw new RuntimeException("Only adjusters can approve claims");
        }

        ClaimAssignment activeAssignment = claimAssignmentRepository.findTopByClaimIdAndIsActiveTrueOrderByAssignedAtDesc(id);
        if (!activeAssignment.getAdjuster().getEmail().equals(user.getEmail())) {
            throw new AccessDeniedException("Access denied - you're not assigned to this claim, or your manager reassigned another adjuster");
        }
        Claim claim = claimRepository.findById(id).orElseThrow(() -> new RuntimeException("claim not found: " + id));
        if (claim.getStatus().equals(ClaimStatus.OVERRIDDEN_REJECTED) || claim.getStatus().equals(ClaimStatus.OVERRIDDEN_APPROVED)) {
            throw new IllegalStateException("claim status is already overridden - you can't approve this claim");
        } else if (claim.getStatus().equals(ClaimStatus.APPROVED)) {
            throw new IllegalStateException("claim status is already approved - you can't modify this claim, contact your manager to override");
        } else  if (claim.getStatus().equals(ClaimStatus.REJECTED)) {
            throw new IllegalStateException("Claim status is already rejected - you can't modify this claim, contact your manager to override");
        }
        claim.setStatus(ClaimStatus.APPROVED);
        claim.setClosedAt(LocalDateTime.now());

        claimRepository.save(claim);
        ClaimHistory claimHistory = new ClaimHistory();
        claimHistory.setClaim(claim);
        claimHistory.setActionType(ActionType.APPROVED);
        claimHistory.setOldStatus(ClaimStatus.UNDER_REVIEW);
        claimHistory.setNewStatus(ClaimStatus.APPROVED);
        claimHistory.setPerformedBy(user);
        claimHistory.setNotes("Claim approved by adjuster ID: " + user.getUserId());
        claimHistory.setCreatedAt(LocalDateTime.now());

        claimHistoryRepository.save(claimHistory);

        ClaimDetailDto claimDetailDto = new ClaimDetailDto();
        BeanUtils.copyProperties(claim,claimDetailDto);
        claimDetailDto.setClaimId(claim.getId());
        return claimDetailDto;
    }

    @Transactional
    @Override
    public ClaimDetailDto rejectClaim(Long id, User user) {
        Role role = user.getRole();
        if (role != Role.ADJUSTER) {
            throw new RuntimeException("Only adjusters can approve claims");
        }

        ClaimAssignment activeAssignment = claimAssignmentRepository.findTopByClaimIdAndIsActiveTrueOrderByAssignedAtDesc(id);
        if (!activeAssignment.getAdjuster().getEmail().equals(user.getEmail())) {
            throw new AccessDeniedException("Access denied - you're not assigned to this claim, or your manager reassigned another adjuster");
        }

        Claim claim = claimRepository.findById(id).orElseThrow(() -> new RuntimeException("claim not found: " + id));
        if (claim.getStatus().equals(ClaimStatus.OVERRIDDEN_REJECTED) || claim.getStatus().equals(ClaimStatus.OVERRIDDEN_APPROVED)) {
            throw new IllegalStateException("claim status is already overridden - you can't approve this claim");
        } else if (claim.getStatus().equals(ClaimStatus.APPROVED)) {
            throw new IllegalStateException("claim status is already approved - you can't modify this claim, contact your manager to override");
        } else  if (claim.getStatus().equals(ClaimStatus.REJECTED)) {
            throw new IllegalStateException("Claim status is already rejected - you can't modify this claim, contact your manager to override");
        }
        claim.setStatus(ClaimStatus.REJECTED);
        claim.setClosedAt(LocalDateTime.now());

        claimRepository.save(claim);

        ClaimHistory claimHistory = new ClaimHistory();
        claimHistory.setClaim(claim);
        claimHistory.setActionType(ActionType.REJECTED);
        claimHistory.setOldStatus(ClaimStatus.UNDER_REVIEW);
        claimHistory.setNewStatus(ClaimStatus.REJECTED);
        claimHistory.setPerformedBy(user);
        claimHistory.setNotes("Claim rejected by adjuster ID: " + user.getUserId());
        claimHistory.setCreatedAt(LocalDateTime.now());

        claimHistoryRepository.save(claimHistory);

        ClaimDetailDto claimDetailDto = new ClaimDetailDto();
        BeanUtils.copyProperties(claim,claimDetailDto);
        claimDetailDto.setClaimId(claim.getId());
        return claimDetailDto;
    }

    @Transactional
    @Override
    public ClaimDetailDto overrideClaim(Long id, boolean approve, User user) {
        Role role = user.getRole();
        if (role != Role.MANAGER) {
            throw new RuntimeException("Only managers can assign claims");
        }
        if (id == null) {
            throw new IllegalArgumentException("claim id is null");
        }

        Claim claim = claimRepository.findById(id).orElseThrow(() -> new RuntimeException("claim not found: " + id));
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
        claimHistory.setPerformedBy(user);
        claimHistory.setNotes("Decision overridden by manager ID: " + user.getUserId());
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

    private Claim transformToEntity(CreateClaimRequestDto createClaimRequestDto, User user) {
        Claim claim = new Claim();
        claim.setTitle(createClaimRequestDto.getTitle());
        claim.setDescription(createClaimRequestDto.getDescription());
        claim.setCreatedBy(user);
        claim.setStatus(ClaimStatus.SUBMITTED);
        claim.setPolicyType(createClaimRequestDto.getPolicyType());
        claim.setCreatedAt(LocalDateTime.now());

        return claim;
    }
}
