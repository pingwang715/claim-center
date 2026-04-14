package com.wangping.ClaimCenter.repository;

import com.wangping.ClaimCenter.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimRepository extends JpaRepository<Claim,Long> {

}
