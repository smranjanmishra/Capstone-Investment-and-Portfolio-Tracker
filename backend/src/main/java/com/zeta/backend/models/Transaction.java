package com.zeta.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zeta.backend.enums.TxnType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK → User
    @Column(nullable = false)
    private Long userId;

    // FK → InvestmentProduct
    @Column(nullable = false)
    private Long investmentProductId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TxnType txnType;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal units;

    @Column( nullable = false, precision = 19, scale = 4)
    private BigDecimal navAtTxn;

    @Column(nullable = false)
    private LocalDateTime txnDate;
}
