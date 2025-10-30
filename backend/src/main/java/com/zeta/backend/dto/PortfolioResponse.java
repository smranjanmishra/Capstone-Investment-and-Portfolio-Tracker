package com.zeta.backend.dto;

import lombok.*;
import java.math.BigDecimal;

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
