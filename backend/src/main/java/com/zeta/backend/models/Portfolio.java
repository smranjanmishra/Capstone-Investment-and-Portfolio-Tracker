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

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "units_owned", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitsOwned;

    @Column(name = "avg_purchase_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal avgPurchasePrice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "investment_product_id", referencedColumnName = "id", nullable = false)
    private InvestmentProduct investmentProduct;

    @Override
    public String toString() {
        return "Portfolio{" +
                "id=" + id +
                ", userId=" + userId +
                ", unitsOwned=" + unitsOwned +
                ", avgPurchasePrice=" + avgPurchasePrice +
                ", investmentProduct=" + investmentProduct +
                '}';
    }
}
