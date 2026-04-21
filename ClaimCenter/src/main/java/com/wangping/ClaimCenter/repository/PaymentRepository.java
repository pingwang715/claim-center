package com.wangping.ClaimCenter.repository;

import com.wangping.ClaimCenter.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
