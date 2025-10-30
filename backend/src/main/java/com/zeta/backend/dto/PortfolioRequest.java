package com.zeta.backend.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioRequest {

    // ID of the investment product to buy or sell
    @NotNull(message = "Investment product ID is required")
    private Long investmentProductId;

    // Number of units to buy or sell
    @NotNull(message = "Units are required")
    @DecimalMin(value = "0.0001", inclusive = true, message = "Units must be greater than 0")
    private BigDecimal units;
}
