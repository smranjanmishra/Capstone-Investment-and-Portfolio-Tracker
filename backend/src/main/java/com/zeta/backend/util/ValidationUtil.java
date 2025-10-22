package com.zeta.backend.util;

import com.zeta.backend.dto.InvestmentProductRequestDTO;
import com.zeta.backend.exceptions.InvalidInputException;

import java.math.BigDecimal;

public class ValidationUtil {
    // Prevents instantiation of utility class
    private ValidationUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // Business logic validation beyond Jakarta Bean Validation constraints
    public static void validateInvestmentProductRequest(InvestmentProductRequestDTO requestDTO) {

        if (requestDTO.getExpectedReturnRate() != null &&
                requestDTO.getExpectedReturnRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException(
                    "expectedReturnRate",
                    requestDTO.getExpectedReturnRate(),
                    "Expected return rate must be a positive number"
            );
        }

        if (requestDTO.getMinInvestment() != null &&
                requestDTO.getMinInvestment().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException(
                    "minInvestment",
                    requestDTO.getMinInvestment(),
                    "Minimum investment must be a positive number"
            );
        }

        if (requestDTO.getCurrentNAV() != null &&
                requestDTO.getCurrentNAV().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException(
                    "currentNAV",
                    requestDTO.getCurrentNAV(),
                    "Current NAV must be a positive number"
            );
        }

        if (requestDTO.getRiskLevel() == null) {
            throw new InvalidInputException(
                    "riskLevel",
                    null,
                    "Risk level must be one of: LOW, MEDIUM, HIGH"
            );
        }

        if (requestDTO.getType() == null) {
            throw new InvalidInputException(
                    "type",
                    null,
                    "Investment type is required"
            );
        }

        // Business rule: Caps unrealistic return rate expectations
        if (requestDTO.getExpectedReturnRate() != null &&
                requestDTO.getExpectedReturnRate().compareTo(new BigDecimal("100")) > 0) {
            throw new InvalidInputException(
                    "expectedReturnRate",
                    requestDTO.getExpectedReturnRate(),
                    "Expected return rate cannot exceed 100%"
            );
        }

        // Catches whitespace-only names that pass @NotBlank
        if (requestDTO.getName() == null || requestDTO.getName().trim().isEmpty()) {
            throw new InvalidInputException(
                    "name",
                    requestDTO.getName(),
                    "Product name cannot be empty"
            );
        }
    }

    // Reusable validation for positive financial values
    public static boolean isPositive(BigDecimal value, String fieldName) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException(
                    fieldName,
                    value,
                    fieldName + " must be a positive number"
            );
        }
        return true;
    }

    public static boolean isNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(
                    fieldName,
                    value,
                    fieldName + " cannot be empty"
            );
        }
        return true;
    }

    // Range validation for bounded financial metrics
    public static boolean isInRange(BigDecimal value, BigDecimal min, BigDecimal max, String fieldName) {
        if (value == null || value.compareTo(min) < 0 || value.compareTo(max) > 0) {
            throw new InvalidInputException(
                    fieldName,
                    value,
                    fieldName + " must be between " + min + " and " + max
            );
        }
        return true;
    }
}