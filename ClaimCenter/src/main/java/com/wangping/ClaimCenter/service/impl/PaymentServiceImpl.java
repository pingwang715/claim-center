package com.wangping.ClaimCenter.service.impl;

import com.wangping.ClaimCenter.entity.Payment;
import com.wangping.ClaimCenter.enums.PaymentStatus;
import com.wangping.ClaimCenter.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    @Override
    public void createPayment(Long claimId, BigDecimal amount) {

    }

    @Override
    public void processPayment(Long paymentId) {

    }

    @Override
    public void updateStatus(Payment payment, PaymentStatus newStatus) {

    }

    @Override
    public void retryPayment(Long paymentId) {

    }

    @Override
    public Payment getPayment(Long paymentId) {
        return null;
    }

    @Override
    public List<Payment> getPaymentsByClaim(Long claimId) {
        return List.of();
    }
}
