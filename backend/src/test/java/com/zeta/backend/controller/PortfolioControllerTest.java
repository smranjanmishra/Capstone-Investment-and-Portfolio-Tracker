package com.zeta.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeta.backend.dto.PortfolioRequest;
import com.zeta.backend.models.InvestmentProduct;
import com.zeta.backend.enums.InvestmentType;
import com.zeta.backend.enums.RiskLevel;
import com.zeta.backend.repository.InvestmentProductRepository;
import com.zeta.backend.repository.PortfolioRepository;
import com.zeta.backend.repository.TransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InvestmentProductRepository investmentRepo;

    @Autowired
    private PortfolioRepository portfolioRepo;

    @Autowired
    private TransactionRepository transactionRepo;

    @Autowired
    private ObjectMapper objectMapper;

    private Long productId;

    @BeforeEach
    void setup() {
        transactionRepo.deleteAll();
        portfolioRepo.deleteAll();
        investmentRepo.deleteAll();

        InvestmentProduct product = InvestmentProduct.builder()
                .name("Demo Product")
                .type(InvestmentType.STOCK)
                .riskLevel(RiskLevel.MEDIUM)
                .minInvestment(new BigDecimal("1000"))
                .expectedReturnRate(new BigDecimal("12.0"))
                .currentNAV(new BigDecimal("100"))
                .isActive(true)
                .build();

        productId = investmentRepo.save(product).getId();
    }

    @AfterEach
    void cleanup() {
        transactionRepo.deleteAll();
        portfolioRepo.deleteAll();
        investmentRepo.deleteAll();
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void shouldBuyInvestmentSuccessfully() throws Exception {
        PortfolioRequest request = new PortfolioRequest();
        request.setInvestmentProductId(productId);
        request.setUnits(BigDecimal.valueOf(10));

        mockMvc.perform(post("/api/v1/portfolio/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Investment purchase successful")));
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void shouldFailBuyWhenUnitsInvalid() throws Exception {
        PortfolioRequest request = new PortfolioRequest();
        request.setInvestmentProductId(productId);
        request.setUnits(BigDecimal.ZERO);

        mockMvc.perform(post("/api/v1/portfolio/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void shouldSellInvestmentSuccessfully() throws Exception {
        PortfolioRequest buy = new PortfolioRequest();
        buy.setInvestmentProductId(productId);
        buy.setUnits(BigDecimal.valueOf(10));

        mockMvc.perform(post("/api/v1/portfolio/buy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buy)));

        PortfolioRequest sell = new PortfolioRequest();
        sell.setInvestmentProductId(productId);
        sell.setUnits(BigDecimal.valueOf(5));

        mockMvc.perform(post("/api/v1/portfolio/sell")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sell)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Investment units sold successfully")));
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void shouldFetchPortfolio() throws Exception {
        mockMvc.perform(get("/api/v1/portfolio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void shouldFetchAllTransactions() throws Exception {
        mockMvc.perform(get("/api/v1/portfolio/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));
    }

    @Test
    void shouldDenyWhenNotLoggedIn() throws Exception {
        mockMvc.perform(get("/api/v1/portfolio"))
                .andExpect(status().isUnauthorized());
    }
}
