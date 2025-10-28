package com.zeta.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeta.backend.dto.GainLossDTO;
import com.zeta.backend.dto.PortfolioSummaryDTO;
import com.zeta.backend.service.PortfolioAnalyticsService;
import com.zeta.backend.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PortfolioAnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private InvestmentProductRepository investmentProductRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long userId = 1L;
    private InvestmentProduct stockProduct;
    private InvestmentProduct bondProduct;

    @BeforeEach
    void setUp() {
        // Clean database
        transactionRepository.deleteAll();
        portfolioRepository.deleteAll();
        investmentProductRepository.deleteAll();

        // Create investment products
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

        // Create portfolio entries
        portfolioRepository.save(
                Portfolio.builder()
                        .userId(userId)
                        .investmentProduct(stockProduct)
                        .avgPurchasePrice(BigDecimal.valueOf(100))
                        .unitsOwned(BigDecimal.valueOf(10))
                        .build()
        );

    @Test
    void testGetPortfolioSummary_Success() throws Exception {
        PortfolioSummaryDTO summary = new PortfolioSummaryDTO(
                BigDecimal.valueOf(10000.00),
                BigDecimal.valueOf(11000.00),
                BigDecimal.valueOf(1000.00),
                BigDecimal.valueOf(10.00),
                BigDecimal.valueOf(10.00)
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

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        portfolioRepository.deleteAll();
        investmentProductRepository.deleteAll();
    }

    // ---------- /portfolio/summary ----------

    @Test
    @WithMockUser(username = "1") // Mock authentication
    void shouldReturnPortfolioSummary() throws Exception {
        mockMvc.perform(get("/api/v1/portfolio/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalInvested", is(2000.00)))
                .andExpect(jsonPath("$.data.currentValue", is(2150.00)))
                .andExpect(jsonPath("$.data.absoluteReturn", is(150.00)))
                .andExpect(jsonPath("$.message", containsString("Portfolio summary fetched successfully")));
    }

    // ---------- /portfolio/allocation ----------

    @Test
    void testGetPortfolioGains_Success() throws Exception {
        List<GainLossDTO> gainLossList = Arrays.asList(
                new GainLossDTO("Product A", BigDecimal.valueOf(10),
                        BigDecimal.valueOf(100), BigDecimal.valueOf(110), BigDecimal.valueOf(100), BigDecimal.valueOf(10)),
                new GainLossDTO("Product B", BigDecimal.valueOf(20),
                        BigDecimal.valueOf(200), BigDecimal.valueOf(190), BigDecimal.valueOf(-200), BigDecimal.valueOf(-5))
        );

        Mockito.when(analyticsService.getPortfolioGains(userId)).thenReturn(gainLossList);

        mockMvc.perform(get("/api/v1/portfolio/gains")
                        .with(authenticatedUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count", is(2)))
                .andExpect(jsonPath("$.data[0].investmentType", anyOf(is("STOCK"), is("BOND"))));
    }

    // ---------- /portfolio/gains ----------

    @Test
    @WithMockUser(username = "1")
    void shouldReturnPortfolioGains() throws Exception {
        mockMvc.perform(get("/api/v1/portfolio/gains"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count", is(2)))
                .andExpect(jsonPath("$.data[0].productName", anyOf(is("Equity Fund"), is("Bond Fund"))));
    }

    @Test
    @WithMockUser(username = "1")
    void shouldReturnEmptyPortfolioGainsIfNoPortfolio() throws Exception {
        portfolioRepository.deleteAll();
        mockMvc.perform(get("/api/v1/portfolio/gains"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message", containsString("Failed to fetch gain/loss data")));
    }
}
