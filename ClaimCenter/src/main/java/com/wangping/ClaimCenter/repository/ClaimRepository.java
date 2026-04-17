package com.wangping.ClaimCenter.repository;

import com.wangping.ClaimCenter.entity.Claim;
import com.wangping.ClaimCenter.enums.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim,Long> {

    List<Claim> findByCreatedBy_Email(String email);

    @Query("""
        SELECT c FROM Claim c
        JOIN c.claimAssignments a
        WHERE a.adjuster.email = :email
        AND a.isActive = true
    """
    )
    List<Claim> findByActiveAdjusterEmail(@Param("email") String email);

    List<Claim> findAll();

    List<Claim> findByStatus(ClaimStatus status);
}
