package com.zeta.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "portfolio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK → User
    @Column(nullable = false)
    private Long userId;

    // FK → InvestmentProduct
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(referencedColumnName = "id", nullable = false)
    private InvestmentProduct investmentProduct;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal unitsOwned;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal avgPurchasePrice;

    @Override
    public String toString() {
        return "Portfolio{" +
                "id=" + id +
                ", userId=" + userId +
                ", unitsOwned=" + unitsOwned +
                ", avgPurchasePrice=" + avgPurchasePrice +
                ", investmentProductId=" +
                (investmentProduct != null ? investmentProduct.getId() : null) +
                '}';
    }
}
