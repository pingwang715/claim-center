package com.wangping.ClaimCenter.service.impl;

import com.wangping.ClaimCenter.entity.Claim;
import com.wangping.ClaimCenter.enums.PolicyType;
import com.wangping.ClaimCenter.service.ICalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class CalculationServiceImpl implements ICalculationService {

    @Override
    public BigDecimal calculateAmount(Claim claim) {

        BigDecimal deductible = null;
        BigDecimal coverageRate = null;

        PolicyType type = claim.getPolicyType();

        if (type == PolicyType.HEALTH) {
            deductible = BigDecimal.valueOf(100);
            coverageRate = BigDecimal.valueOf(0.5);
        } else if (type == PolicyType.CAR) {
            deductible = BigDecimal.valueOf(100);
            coverageRate = BigDecimal.valueOf(0.5);
        } else if (type == PolicyType.TRAVEL) {
            deductible = BigDecimal.valueOf(100);
            coverageRate = BigDecimal.valueOf(0.2);
        } else if (type == PolicyType.PET) {
            deductible = BigDecimal.valueOf(50);
            coverageRate = BigDecimal.valueOf(0.2);
        }


        BigDecimal base = claim.getClaimedAmount();

        BigDecimal afterDeductible = base.subtract(deductible);

        if (afterDeductible.compareTo(BigDecimal.ZERO) < 0) {
            afterDeductible = BigDecimal.ZERO;
        }
        return afterDeductible.multiply(coverageRate);
    }
}
