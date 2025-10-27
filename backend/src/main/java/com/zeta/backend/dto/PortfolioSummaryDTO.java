package com.zeta.backend.dto;

import java.math.BigDecimal;

/*This DTO is primarily used in portfolio analytics APIs
 * to provide aggregated metrics such as total invested amount,
 * current portfolio value, absolute return, and annualized return.*/

public record PortfolioSummaryDTO(
        BigDecimal totalInvested,
        BigDecimal currentValue,
        BigDecimal absoluteReturn,
        BigDecimal roi,
        BigDecimal annualizedReturn
) {}