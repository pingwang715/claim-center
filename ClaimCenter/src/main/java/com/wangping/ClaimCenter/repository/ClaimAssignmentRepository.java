package com.wangping.ClaimCenter.repository;

import com.wangping.ClaimCenter.entity.ClaimAssignment;
import com.wangping.ClaimCenter.entity.User;
import com.wangping.ClaimCenter.enums.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClaimAssignmentRepository extends JpaRepository<ClaimAssignment,Long> {

    List<ClaimAssignment> findByClaimIdAndIsActiveTrue(Long claimId);

    ClaimAssignment findTopByClaimIdAndIsActiveTrueOrderByAssignedAtDesc(Long claimId);

}
