package com.zeta.backend.dto;

import java.math.BigDecimal;

public record GainLossDTO(
        String productName,
        BigDecimal unitsOwned,
        BigDecimal avgPurchasePrice,
        BigDecimal currentNAV,
        BigDecimal absoluteGainLoss,
        BigDecimal gainLossPercent
) {}