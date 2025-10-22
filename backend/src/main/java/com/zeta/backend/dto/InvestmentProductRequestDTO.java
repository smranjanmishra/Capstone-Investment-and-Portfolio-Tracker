package com.zeta.backend.dto;

import com.zeta.backend.enums.InvestmentType;
import com.zeta.backend.enums.RiskLevel;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class InvestmentProductRequestDTO {
    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 200, message = "Product name must be between 3 and 200 characters")
    private String name;

    @NotNull(message = "Investment type is required")
    private InvestmentType type;

    @NotNull(message = "Risk level is required")
    private RiskLevel riskLevel;

    @NotNull(message = "Minimum investment is required")
    @DecimalMin(value = "0.01", message = "Minimum investment must be at least 0.01")
    @Digits(integer = 13, fraction = 2, message = "Invalid format for minimum investment")
    private BigDecimal minInvestment;

    @NotNull(message = "Expected return rate is required")
    @DecimalMin(value = "0.01", message = "Expected return rate must be positive")
    @DecimalMax(value = "100.00", message = "Expected return rate cannot exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "Invalid format for return rate")
    private BigDecimal expectedReturnRate;

    @NotNull(message = "Current NAV is required")
    @DecimalMin(value = "0.01", message = "Current NAV must be greater than 0")
    @Digits(integer = 11, fraction = 4, message = "Invalid format for NAV")
    private BigDecimal currentNAV;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    private Boolean isActive;

    public Boolean getIsActive() {
        return isActive != null ? isActive : true;
    }
}

