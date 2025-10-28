package com.zeta.backend.dto;

import java.math.BigDecimal;


/* This DTO is used to communicate detailed performance metrics for an investment,
 * including the number of units held, purchase price, current NAV, and the calculated
 * absolute gain or loss.*/
public record GainLossDTO(
        String productName,
        BigDecimal unitsOwned,
        BigDecimal avgPurchasePrice,
        BigDecimal currentNAV,
        BigDecimal absoluteGainLoss
) {}
