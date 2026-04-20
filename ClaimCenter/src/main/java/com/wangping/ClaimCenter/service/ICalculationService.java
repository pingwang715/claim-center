package com.wangping.ClaimCenter.service;

import com.wangping.ClaimCenter.entity.Claim;

import java.math.BigDecimal;

public interface ICalculationService {
    BigDecimal calculateAmount(Claim claim);
}
