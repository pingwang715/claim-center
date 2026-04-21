package com.wangping.ClaimCenter.service;

import com.wangping.ClaimCenter.entity.Payment;
import com.wangping.ClaimCenter.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;

public interface IPaymentService {
    void createPayment(Long claimId, BigDecimal amount);
    void processPayment(Long paymentId);
    void updateStatus(Payment payment, PaymentStatus newStatus);
    void retryPayment(Long paymentId);
    Payment getPayment(Long paymentId);
    List<Payment> getPaymentsByClaim(Long claimId);
}
