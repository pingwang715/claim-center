package com.wangping.ClaimCenter.dto;

import com.wangping.ClaimCenter.enums.PolicyType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateClaimRequestDto {

    @NotBlank(message = "Title cannot be empty")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @NotNull(message = "Claimed amount cannot be empty")
    @DecimalMin(value = "0.00", inclusive = false, message = "Claimed amount must be greater than 0")
    @DecimalMax(value = "9999999.99", message = "Claimed amount must not exceed 9,999,999.99")
    @Digits(integer =7, fraction = 2, message = "Claimed amount must have at most 7 integer digits and 2 decimal places")
    @Positive(message = "Claimed amount must be positive")
    private BigDecimal claimedAmount;

    @NotNull(message = "Policy type is required")
    private PolicyType type;
}
