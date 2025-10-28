package com.zeta.backend.service;

import com.zeta.backend.dto.AssetAllocationDTO;
import com.zeta.backend.dto.GainLossDTO;
import com.zeta.backend.dto.PortfolioSummaryDTO;
import com.zeta.backend.enums.InvestmentType;
import com.zeta.backend.enums.TxnType;
import com.zeta.backend.exceptions.InvalidInputException;
import com.zeta.backend.exceptions.ResourceNotFoundException;
import com.zeta.backend.models.InvestmentProduct;
import com.zeta.backend.models.Portfolio;
import com.zeta.backend.models.Transaction;
import com.zeta.backend.repository.PortfolioRepository;
import com.zeta.backend.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PortfolioAnalyticsServiceImplTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private PortfolioAnalyticsServiceImpl analyticsService;

    private final Long userId = 1L;
    private InvestmentProduct product1;
    private InvestmentProduct product2;
    private Portfolio portfolio1;
    private Portfolio portfolio2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product1 = InvestmentProduct.builder()
                .id(1L)
                .name("Equity Fund")
                .type(InvestmentType.STOCK) // ✅ replaced EQUITY
                .currentNAV(BigDecimal.valueOf(110))
                .build();

        product2 = InvestmentProduct.builder()
                .id(2L)
                .name("Bond Fund")
                .type(InvestmentType.BOND) // ✅ replaced BONDS
                .currentNAV(BigDecimal.valueOf(105))
                .build();

        portfolio1 = Portfolio.builder()
                .userId(userId)
                .investmentProduct(product1)
                .avgPurchasePrice(BigDecimal.valueOf(100))
                .unitsOwned(BigDecimal.valueOf(10))
                .build();

        portfolio2 = Portfolio.builder()
                .userId(userId)
                .investmentProduct(product2)
                .avgPurchasePrice(BigDecimal.valueOf(100))
                .unitsOwned(BigDecimal.valueOf(10))
                .build();
    }

    // ---------- getPortfolioSummary() ----------

    @Test
    void testGetPortfolioSummary_Success() {
        when(portfolioRepository.findByUserId(userId)).thenReturn(Arrays.asList(portfolio1, portfolio2));

        Transaction txn = Transaction.builder()
                .userId(userId)
                .txnType(TxnType.BUY)
                .navAtTxn(BigDecimal.valueOf(100))
                .units(BigDecimal.valueOf(20))
                .txnDate(LocalDateTime.now().minusDays(30))
                .build();

        when(transactionRepository.findByUserIdOrderByTxnDateDesc(userId))
                .thenReturn(Collections.singletonList(txn));

        PortfolioSummaryDTO result = analyticsService.getPortfolioSummary(userId);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(2000.00).setScale(2), result.totalInvested());
        assertEquals(BigDecimal.valueOf(2150.00).setScale(2), result.currentValue());
        assertEquals(BigDecimal.valueOf(150.00).setScale(2), result.absoluteReturn());
        assertTrue(result.annualizedReturn().compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    void testGetPortfolioSummary_InvalidUser() {
        assertThrows(InvalidInputException.class, () -> analyticsService.getPortfolioSummary(null));
        assertThrows(InvalidInputException.class, () -> analyticsService.getPortfolioSummary(0L));
    }

    @Test
    void testGetPortfolioSummary_NoPortfolioFound() {
        when(portfolioRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> analyticsService.getPortfolioSummary(userId));
    }

    // ---------- getPortfolioAllocation() ----------

    @Test
    void testGetPortfolioAllocation_Success() {
        when(portfolioRepository.findByUserId(userId)).thenReturn(Arrays.asList(portfolio1, portfolio2));

        List<AssetAllocationDTO> result = analyticsService.getPortfolioAllocation(userId);

        assertEquals(2, result.size());

        List<String> types = result.stream()
                .map(AssetAllocationDTO::investmentType)
                .toList();

        assertTrue(types.contains("STOCK"));
        assertTrue(types.contains("BOND"));
    }


    @Test
    void testGetPortfolioAllocation_NoPortfolioFound() {
        when(portfolioRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> analyticsService.getPortfolioAllocation(userId));
    }

    @Test
    void testGetPortfolioAllocation_ZeroTotalValue() {
        product1.setCurrentNAV(BigDecimal.ZERO);
        when(portfolioRepository.findByUserId(userId)).thenReturn(Collections.singletonList(portfolio1));

        List<AssetAllocationDTO> result = analyticsService.getPortfolioAllocation(userId);
        assertTrue(result.isEmpty());
    }

    // ---------- getPortfolioGains() ----------

    @Test
    void testGetPortfolioGains_Success() {
        when(portfolioRepository.findByUserId(userId)).thenReturn(Arrays.asList(portfolio1, portfolio2));

        List<GainLossDTO> result = analyticsService.getPortfolioGains(userId);

        assertEquals(2, result.size());
        assertEquals("Equity Fund", result.get(0).productName());
        assertEquals(BigDecimal.valueOf(100.00).setScale(2), result.get(0).absoluteGainLoss());
    }

    @Test
    void testGetPortfolioGains_NoPortfolioFound() {
        when(portfolioRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> analyticsService.getPortfolioGains(userId));
    }

    @Test
    void testGetPortfolioGains_InvalidUser() {
        assertThrows(InvalidInputException.class, () -> analyticsService.getPortfolioGains(0L));
    }

    // ---------- Edge Case ----------

    @Test
    void testGetPortfolioSummary_EmptyTransactions() {
        when(portfolioRepository.findByUserId(userId)).thenReturn(Collections.singletonList(portfolio1));
        when(transactionRepository.findByUserIdOrderByTxnDateDesc(userId))
                .thenReturn(Collections.emptyList());

        PortfolioSummaryDTO result = analyticsService.getPortfolioSummary(userId);
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1000.00).setScale(2), result.totalInvested());
    }
}
