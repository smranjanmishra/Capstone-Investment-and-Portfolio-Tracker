package com.zeta.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zeta.backend.enums.InvestmentType;
import com.zeta.backend.enums.RiskLevel;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class InvestmentProductResponseDTO {
    private Long id;
    private String name;
    private InvestmentType type;
    private String typeDisplayName; // Human-readable version of type enum for frontend display
    private RiskLevel riskLevel;
    private String riskLevelDisplayName; // Human-readable version of risk enum for frontend display
    private BigDecimal minInvestment;
    private BigDecimal expectedReturnRate;
    private BigDecimal currentNAV;
    private String description;
    private Boolean isActive;

    // Ensures consistent ISO 8601 datetime format across all API responses
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Enriches enum fields with display-friendly names - must be called after mapping from entity
    public void populateDisplayNames() {
        if (type != null) {
            this.typeDisplayName = type.getDisplayName();
        }
        if (riskLevel != null) {
            this.riskLevelDisplayName = riskLevel.getDisplayName();
        }
    }
}