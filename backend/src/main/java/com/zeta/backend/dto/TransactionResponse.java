package com.zeta.backend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO representing a transaction record (BUY/SELL).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long id;
    private Long userId;
    private Long investmentProductId;
    private String txnType;
    private BigDecimal units;
    private BigDecimal navAtTxn;
    private LocalDateTime txnDate;
}
