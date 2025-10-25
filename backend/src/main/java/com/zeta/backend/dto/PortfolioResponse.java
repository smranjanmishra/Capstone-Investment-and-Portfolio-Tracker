package com.zeta.backend.dto;

import lombok.*;
import java.math.BigDecimal;

/**
 * DTO representing a user's portfolio record response.
 * Returned after buy/sell operations or portfolio retrieval.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioResponse {

    private Long id;
    private Long userId;
    private Long investmentProductId;
    private String investmentProductName;
    private BigDecimal unitsOwned;
    private BigDecimal avgPurchasePrice;
}
