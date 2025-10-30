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
    private String typeDisplayName;
    private RiskLevel riskLevel;
    private String riskLevelDisplayName;
    private BigDecimal minInvestment;
    private BigDecimal expectedReturnRate;
    private BigDecimal currentNAV;
    private String description;
    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public void populateDisplayNames() {
        if (type != null) {
            this.typeDisplayName = type.getDisplayName();
        }
        if (riskLevel != null) {
            this.riskLevelDisplayName = riskLevel.getDisplayName();
        }
    }
}