package com.wangping.ClaimCenter.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "fraud_indicators")
public class FraudIndicator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_assessment_id", nullable = false)
    private ClaimAssessment claimAssessment;

    @Column(nullable = false, length = 100)
    private String code;
}
