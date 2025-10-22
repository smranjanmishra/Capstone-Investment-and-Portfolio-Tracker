package com.zeta.backend.models;

import com.zeta.backend.enums.InvestmentType;
import com.zeta.backend.enums.RiskLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "investment_products", indexes = {
        @Index(name = "idx_type", columnList = "type"), // Optimizes filtering by investment type
        @Index(name = "idx_risk_level", columnList = "risk_level"), // Optimizes filtering by risk level
        @Index(name = "idx_is_active", columnList = "is_active") // Optimizes queries excluding inactive products
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class InvestmentProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    @NotBlank(message = "Investment product name is required")
    @Size(min = 3, max = 200, message = "Product name must be between 3 and 200 characters")
    private String name;

    // STRING storage prevents data corruption if enum order changes
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    @NotNull(message = "Investment type is required")
    private InvestmentType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 20)
    @NotNull(message = "Risk level is required")
    private RiskLevel riskLevel;

    // Precision 15,2 supports up to 999,999,999,999.99 for large investments
    @Column(name = "min_investment", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Minimum investment amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Minimum investment must be greater than 0")
    private BigDecimal minInvestment;

    @Column(name = "expected_return_rate", nullable = false, precision = 5, scale = 2)
    @NotNull(message = "Expected return rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Expected return rate must be positive")
    @DecimalMax(value = "100.0", message = "Expected return rate cannot exceed 100%")
    private BigDecimal expectedReturnRate;

    // Scale 4 provides precision for fractional unit prices (e.g., mutual fund NAV)
    @Column(name = "current_nav", nullable = false, precision = 15, scale = 4)
    @NotNull(message = "Current NAV is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Current NAV must be greater than 0")
    private BigDecimal currentNAV;

    // Soft delete flag - preserves historical data and relationships
    @Column(name = "is_active", nullable = false)
    @NotNull(message = "Active status is required")
    private Boolean isActive;

    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    // Automatically set on entity creation, immutable thereafter
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Automatically updated on every entity modification
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Ensures new products are active by default if not explicitly set
    @PrePersist
    protected void onCreate() {
        if (isActive == null) {
            isActive = true;
        }
    }

    @Override
    public String toString() {
        return "InvestmentProduct{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", riskLevel=" + riskLevel +
                ", minInvestment=" + minInvestment +
                ", expectedReturnRate=" + expectedReturnRate +
                ", currentNAV=" + currentNAV +
                ", isActive=" + isActive +
                '}';
    }
}