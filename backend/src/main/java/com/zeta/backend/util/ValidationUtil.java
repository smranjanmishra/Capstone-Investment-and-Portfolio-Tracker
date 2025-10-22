package com.zeta.backend.util;

import com.zeta.backend.dto.InvestmentProductRequestDTO;
import com.zeta.backend.exceptions.InvalidInputException;

import java.math.BigDecimal;

public class ValidationUtil {
    private ValidationUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static void validateInvestmentProductRequest(InvestmentProductRequestDTO requestDTO) {

        if (requestDTO.getExpectedReturnRate() != null &&
                requestDTO.getExpectedReturnRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException(
                    "expectedReturnRate",
                    requestDTO.getExpectedReturnRate(),
                    "Expected return rate must be a positive number"
            );
        }

        // Validate minimum investment (must be positive)
        if (requestDTO.getMinInvestment() != null &&
                requestDTO.getMinInvestment().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException(
                    "minInvestment",
                    requestDTO.getMinInvestment(),
                    "Minimum investment must be a positive number"
            );
        }

        // Validate current NAV (must be positive)
        if (requestDTO.getCurrentNAV() != null &&
                requestDTO.getCurrentNAV().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException(
                    "currentNAV",
                    requestDTO.getCurrentNAV(),
                    "Current NAV must be a positive number"
            );
        }

        // Validate risk level (already handled by enum, but double-check)
        if (requestDTO.getRiskLevel() == null) {
            throw new InvalidInputException(
                    "riskLevel",
                    null,
                    "Risk level must be one of: LOW, MEDIUM, HIGH"
            );
        }

        // Validate investment type
        if (requestDTO.getType() == null) {
            throw new InvalidInputException(
                    "type",
                    null,
                    "Investment type is required"
            );
        }

        // Additional business rule: Return rate should not exceed 100%
        if (requestDTO.getExpectedReturnRate() != null &&
                requestDTO.getExpectedReturnRate().compareTo(new BigDecimal("100")) > 0) {
            throw new InvalidInputException(
                    "expectedReturnRate",
                    requestDTO.getExpectedReturnRate(),
                    "Expected return rate cannot exceed 100%"
            );
        }

        // Validate name is not empty or just whitespace
        if (requestDTO.getName() == null || requestDTO.getName().trim().isEmpty()) {
            throw new InvalidInputException(
                    "name",
                    requestDTO.getName(),
                    "Product name cannot be empty"
            );
        }
    }

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
