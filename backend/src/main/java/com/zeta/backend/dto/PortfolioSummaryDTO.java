package com.zeta.backend.dto;

import java.math.BigDecimal;

public record PortfolioSummaryDTO(
        BigDecimal totalInvested,
        BigDecimal currentValue,
        BigDecimal absoluteReturn,
        BigDecimal roi,
        BigDecimal annualizedReturn
) {}