package com.wangping.ClaimCenter.repository;

import com.wangping.ClaimCenter.entity.ClaimHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimHistoryRepository extends JpaRepository<ClaimHistory, Long> {
}