package com.wangping.ClaimCenter.repository;

import com.wangping.ClaimCenter.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim,Long> {

    List<Claim> findByCreatedBy_Email(String email);

    @Query("""
        SELECT c
        FROM Claim c
        JOIN c.claimAssignments a
        JOIN a.adjuster u
        WHERE u.email = :email
    """)
    List<Claim> findByAdjusterEmail(String email);

    List<Claim> findAll();
}
