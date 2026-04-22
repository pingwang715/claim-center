package com.wangping.ClaimCenter.service;

import com.wangping.ClaimCenter.dto.ClaimApprovedEvent;
import com.wangping.ClaimCenter.entity.Payment;

public interface IPaymentService {
    void handleClaimApproved(ClaimApprovedEvent event);
    void processPaymentAsync(Long paymentId);
    void processPayment(Long paymentId);
    boolean externalCall();
    void retryPayment(Payment payment);

}
