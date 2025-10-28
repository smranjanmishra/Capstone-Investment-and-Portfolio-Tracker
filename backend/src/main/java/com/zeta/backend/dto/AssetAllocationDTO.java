package com.zeta.backend.dto;

import java.math.BigDecimal;

/*This DTO is used to communicate the distribution of investments across
 *different asset types along with their corresponding monetary values and percentage contributions to the total portfolio.*/

public record AssetAllocationDTO(
        String investmentType,
        BigDecimal value,
        BigDecimal percentage
) {}
