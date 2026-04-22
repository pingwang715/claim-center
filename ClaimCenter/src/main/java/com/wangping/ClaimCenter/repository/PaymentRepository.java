package com.wangping.ClaimCenter.repository;

import com.wangping.ClaimCenter.entity.Payment;
import com.wangping.ClaimCenter.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByIdAndPaymentStatus(Long paymentId, PaymentStatus status);
}
