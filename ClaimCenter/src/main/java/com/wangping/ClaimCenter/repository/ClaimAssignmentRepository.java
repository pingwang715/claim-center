package com.wangping.ClaimCenter.repository;

import com.wangping.ClaimCenter.entity.ClaimAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClaimAssignmentRepository extends JpaRepository<ClaimAssignment,Long> {
}
