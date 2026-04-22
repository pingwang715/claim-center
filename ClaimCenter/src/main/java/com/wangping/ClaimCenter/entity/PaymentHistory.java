package com.wangping.ClaimCenter.entity;

import com.wangping.ClaimCenter.enums.PaymentActionType;
import com.wangping.ClaimCenter.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "payment_histories")
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    private PaymentStatus oldStatus;

    @Enumerated(EnumType.STRING)
    private PaymentStatus newStatus;

    @Enumerated(EnumType.STRING)
    private PaymentActionType actionType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
