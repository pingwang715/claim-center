package com.wangping.ClaimCenter.service.impl;

import com.wangping.ClaimCenter.dto.ClaimApprovedEvent;
import com.wangping.ClaimCenter.entity.Claim;
import com.wangping.ClaimCenter.entity.Payment;
import com.wangping.ClaimCenter.entity.PaymentHistory;
import com.wangping.ClaimCenter.enums.PaymentActionType;
import com.wangping.ClaimCenter.enums.PaymentStatus;
import com.wangping.ClaimCenter.repository.ClaimRepository;
import com.wangping.ClaimCenter.repository.PaymentHistoryRepository;
import com.wangping.ClaimCenter.repository.PaymentRepository;
import com.wangping.ClaimCenter.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableAsync
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final ClaimRepository claimRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    @Lazy
    @Autowired
    private IPaymentService self;

    @Autowired
    private TaskScheduler taskScheduler;

    public void scheduleRetry(Long paymentId, int retryCount) {
        long delaySeconds = (long) Math.pow(2, retryCount);
        Duration delay = Duration.ofSeconds(delaySeconds);
        taskScheduler.schedule(() -> retryPayment(paymentId), Instant.now().plus(delay));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Override
    public void handleClaimApproved(ClaimApprovedEvent event) {
        log.info("handleClaimApproved triggered for claimId: {}", event.getClaimId());
        try {
            Claim claim = claimRepository.findById(event.getClaimId()).orElseThrow(() -> new RuntimeException("claim not found"));

            Payment payment = new Payment();
            payment.setAmount(event.getAmount());
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setClaim(claim);
            payment.setRetryCount(0);
            payment.setMaxRetries(3);
            payment = paymentRepository.save(payment);

            PaymentHistory paymentHistory = new PaymentHistory();
            paymentHistory.setPayment(payment);
            paymentHistory.setActionType(PaymentActionType.CREATED);
            paymentHistory.setOldStatus(null);
            paymentHistory.setNewStatus(PaymentStatus.PENDING);
            paymentHistoryRepository.save(paymentHistory);

            self.processPaymentAsync(payment.getId());
        } catch (Exception e) {
            log.error("handleClaimApproved failed", e); // ← add
            throw e;
        }
    }

    @Override
    @Async
    public void processPaymentAsync(Long paymentId) {
        processPayment(paymentId);
    }

    @Transactional
    @Override
    public void processPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new RuntimeException("payment not found"));
        payment.setPaymentStatus(PaymentStatus.PROCESSING);
        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setPayment(payment);

        boolean success = externalCall();

        if (success) {
            payment.setPaymentStatus(PaymentStatus.PAID);
            paymentRepository.save(payment);
            paymentHistory.setActionType(PaymentActionType.SUCCESS);
            paymentHistory.setOldStatus(PaymentStatus.PENDING);
            paymentHistory.setNewStatus(PaymentStatus.PAID);
            paymentHistoryRepository.save(paymentHistory);
        } else  {
            paymentHistory.setActionType(PaymentActionType.FAILURE);
            paymentHistory.setOldStatus(PaymentStatus.PROCESSING);
            paymentHistory.setNewStatus(PaymentStatus.FAILED);
            paymentHistoryRepository.save(paymentHistory);
            payment.setRetryCount(payment.getRetryCount() + 1);
            paymentRepository.save(payment);

            if (payment.getRetryCount() < payment.getMaxRetries()) {
                payment.setPaymentStatus(PaymentStatus.FAILED);

                paymentRepository.save(payment);
                scheduleRetry(paymentId, payment.getRetryCount());
            } else {
                payment.setPaymentStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);

                log.warn("Payment {} reached max retries", payment.getId());
            }
        }
    }
//
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void handlePaymentFailed(PaymentFailedEvent event) {
//        Payment payment = paymentRepository.findById(event.getPaymentId()).orElseThrow(() -> new RuntimeException("payment not found"));
//
//        if (payment.getRetryCount() >= payment.getMaxRetries()) {
//            applicationEventPublisher.publishEvent(
//                    new PaymentFailedEvent(payment.getId())
//            );
//        }
//    }

    @Transactional
    @Override
    public void retryPayment(Long  paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new RuntimeException("payment not found"));

        boolean success = externalCall();
        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setPayment(payment);

        if (success) {
            payment.setPaymentStatus(PaymentStatus.PAID);
            paymentHistory.setActionType(PaymentActionType.SUCCESS);
            paymentHistory.setOldStatus(PaymentStatus.FAILED);
            paymentHistory.setNewStatus(PaymentStatus.PAID);
            paymentRepository.save(payment);
            paymentHistoryRepository.save(paymentHistory);
        } else {

            paymentHistory.setActionType(PaymentActionType.FAILURE);
            paymentHistory.setOldStatus(PaymentStatus.FAILED);
            paymentHistory.setNewStatus(PaymentStatus.FAILED);
            paymentHistoryRepository.save(paymentHistory);
            payment.setRetryCount(payment.getRetryCount() + 1);
            paymentRepository.save(payment);

            if (payment.getRetryCount() < payment.getMaxRetries()) {
//                self.retryPayment(paymentId);
                payment.setPaymentStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                scheduleRetry(paymentId, payment.getRetryCount());

            } else {
                log.warn("Payment {} final failure", payment.getId());
            }
        }
    }


    @Override
    public boolean externalCall() {
        return false;
    }
}
