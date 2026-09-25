package com.wangping.ClaimCenter.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "claim_assessments")
public class ClaimAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "risk_score", nullable = false)
    private Integer riskScore;

    @Column(name = "summary", nullable = false)
    private String Summary;

    @OneToMany(mappedBy = "claimAssessment", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<FraudIndicator> fraudIndicators = new ArrayList<>();

    @Column(name = "recommended_action", nullable = false)
    private String recommendedAction;

    @Column(name = "raw_model_note", nullable = false)
    private String rawModelNote;

    // helper method to keep both sides in sync
    public void addFraudInsicator(String code) {
        FraudIndicator indicator = new FraudIndicator();
        indicator.setCode(code);
        indicator.setClaimAssessment(this);
        this.fraudIndicators.add(indicator);
    }

}
