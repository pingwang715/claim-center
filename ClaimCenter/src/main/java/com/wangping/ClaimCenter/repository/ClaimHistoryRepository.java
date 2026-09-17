package com.wangping.ClaimCenter.repository;

import com.wangping.ClaimCenter.entity.ClaimHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimHistoryRepository extends JpaRepository<ClaimHistory, Long> {
    List<ClaimHistory> findByClaim_IdOrderByCreatedAtAsc(Long claimId);
}