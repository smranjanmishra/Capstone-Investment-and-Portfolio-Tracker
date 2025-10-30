package com.zeta.backend.dto;

import java.math.BigDecimal;

public record AssetAllocationDTO(
        String investmentType,
        BigDecimal value,
        BigDecimal percentage
) {}