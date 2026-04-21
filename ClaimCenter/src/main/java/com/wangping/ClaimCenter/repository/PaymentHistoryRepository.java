package com.wangping.ClaimCenter.repository;

import com.wangping.ClaimCenter.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory,Long> {
    List<PaymentHistory> findByPaymentIdOrderByCreatedAtDesc(Long paymentId);
}
