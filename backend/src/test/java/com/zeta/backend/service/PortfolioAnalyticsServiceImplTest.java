package com.zeta.backend.service;

import com.zeta.backend.dto.AssetAllocationDTO;
import com.zeta.backend.dto.GainLossDTO;
import com.zeta.backend.dto.PortfolioSummaryDTO;
import com.zeta.backend.enums.InvestmentType;
import com.zeta.backend.enums.RiskLevel;
import com.zeta.backend.enums.TxnType;
import com.zeta.backend.models.InvestmentProduct;
import com.zeta.backend.models.Portfolio;
import com.zeta.backend.models.Transaction;
import com.zeta.backend.repository.InvestmentProductRepository;
import com.zeta.backend.repository.PortfolioRepository;
import com.zeta.backend.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PortfolioAnalyticsServiceImplTest {

    @Autowired
    private PortfolioAnalyticsServiceImpl analyticsService;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private InvestmentProductRepository investmentProductRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private final Long userId = 1L;
    private InvestmentProduct stockProduct;
    private InvestmentProduct bondProduct;


    @BeforeEach
    void setUp() {
        // Create investment products and persist them
        stockProduct = investmentProductRepository.save(
                InvestmentProduct.builder()
                        .name("Equity Fund")
                        .type(InvestmentType.STOCK)
                        .riskLevel(RiskLevel.HIGH)
                        .currentNAV(BigDecimal.valueOf(110))
                        .expectedReturnRate(BigDecimal.valueOf(12.5))
                        .minInvestment(BigDecimal.valueOf(5000))
                        .isActive(true)
                        .build()
        );

        bondProduct = investmentProductRepository.save(
                InvestmentProduct.builder()
                        .name("Bond Fund")
                        .type(InvestmentType.BOND)
                        .riskLevel(RiskLevel.LOW)
                        .currentNAV(BigDecimal.valueOf(105))
                        .expectedReturnRate(BigDecimal.valueOf(7.0))
                        .minInvestment(BigDecimal.valueOf(2000))
                        .isActive(true)
                        .build()
        );

        // Create portfolio entries for the user
        portfolioRepository.save(
                Portfolio.builder()
                        .userId(userId)
                        .investmentProduct(stockProduct)
                        .avgPurchasePrice(BigDecimal.valueOf(100))
                        .unitsOwned(BigDecimal.valueOf(10))
                        .build()
        );

        portfolioRepository.save(
                Portfolio.builder()
                        .userId(userId)
                        .investmentProduct(bondProduct)
                        .avgPurchasePrice(BigDecimal.valueOf(100))
                        .unitsOwned(BigDecimal.valueOf(10))
                        .build()
        );


        transactionRepository.save(
                Transaction.builder()
                        .userId(userId)
                        .investmentProductId(stockProduct.getId())
                        .txnType(TxnType.BUY)
                        .navAtTxn(BigDecimal.valueOf(100))
                        .units(BigDecimal.valueOf(20))
                        .txnDate(LocalDateTime.now().minusDays(30))
                        .build()
        );
    }

    // ---------- getPortfolioSummary() ----------

    @Test
    void shouldReturnPortfolioSummarySuccessfully() {
        PortfolioSummaryDTO result = analyticsService.getPortfolioSummary(userId);

        assertThat(result).isNotNull();
        assertThat(result.totalInvested()).isEqualByComparingTo(BigDecimal.valueOf(2000.00).setScale(2));
        assertThat(result.currentValue()).isEqualByComparingTo(BigDecimal.valueOf(2150.00).setScale(2));
        assertThat(result.absoluteReturn()).isEqualByComparingTo(BigDecimal.valueOf(150.00).setScale(2));
        assertThat(result.annualizedReturn()).isNotNull();
    }

    @Test
    void shouldThrowExceptionForInvalidUser() {
        assertThrows(Exception.class, () -> analyticsService.getPortfolioSummary(null));
        assertThrows(Exception.class, () -> analyticsService.getPortfolioSummary(0L));
    }

    @Test
    void shouldThrowExceptionForNoPortfolio() {

        portfolioRepository.deleteAll();
        assertThrows(Exception.class, () -> analyticsService.getPortfolioSummary(userId));
    }

    // ---------- getPortfolioAllocation() ----------

    @Test
    void shouldReturnPortfolioAllocationSuccessfully() {
        List<AssetAllocationDTO> result = analyticsService.getPortfolioAllocation(userId);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(AssetAllocationDTO::investmentType)
                .containsExactlyInAnyOrder("STOCK", "BOND");
    }

    @Test
    void shouldThrowExceptionForNoPortfolioInAllocation() {
        portfolioRepository.deleteAll();
        assertThrows(Exception.class, () -> analyticsService.getPortfolioAllocation(userId));
    }

    // ---------- getPortfolioGains() ----------

    @Test
    void shouldReturnPortfolioGainsSuccessfully() {
        List<GainLossDTO> result = analyticsService.getPortfolioGains(userId);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).productName()).isIn("Equity Fund", "Bond Fund");
        assertThat(result.get(0).absoluteGainLoss()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
    }

    @Test
    void shouldThrowExceptionWhenNoPortfolioForGains() {
        portfolioRepository.deleteAll();
        assertThrows(Exception.class, () -> analyticsService.getPortfolioGains(userId));
    }

    // ---------- Edge Case ----------

    @Test
    void shouldHandleEmptyTransactionsGracefully() {
        transactionRepository.deleteAll();

        PortfolioSummaryDTO result = analyticsService.getPortfolioSummary(userId);
        assertThat(result).isNotNull();
        assertThat(result.totalInvested()).isEqualByComparingTo(BigDecimal.valueOf(2000.00).setScale(2));
    }
}
