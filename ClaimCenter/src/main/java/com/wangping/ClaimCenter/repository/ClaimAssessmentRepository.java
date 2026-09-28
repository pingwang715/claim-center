package com.wangping.ClaimCenter.repository;

import com.wangping.ClaimCenter.entity.ClaimAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClaimAssessmentRepository extends JpaRepository<ClaimAssessment,Long> {
    Optional<ClaimAssessment> findFirstByClaim_IdOrderByCreatedAtDesc(Long id);

}
