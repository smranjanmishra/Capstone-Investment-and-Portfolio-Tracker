package com.zeta.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeta.backend.dto.PortfolioRequest;
import com.zeta.backend.dto.PortfolioResponse;
import com.zeta.backend.dto.TransactionResponse;
import com.zeta.backend.service.PortfolioService;
import com.zeta.backend.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PortfolioController.class)
public class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PortfolioService portfolioService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long userId = 1L;

    // Test successful buy
    @Test
    void testBuyInvestment_Success() throws Exception {
        PortfolioRequest request = new PortfolioRequest();
        request.setInvestmentProductId(1L);
        request.setUnits(BigDecimal.valueOf(10));

        PortfolioResponse response = PortfolioResponse.builder()
                .id(1L)
                .userId(userId)
                .investmentProductId(1L)
                .investmentProductName("Product A")
                .unitsOwned(BigDecimal.valueOf(10))
                .avgPurchasePrice(BigDecimal.valueOf(100.00))
                .build();

        Mockito.when(portfolioService.buyInvestment(eq(userId), any(PortfolioRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/portfolio/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("1")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Investment purchase successful"))
                .andExpect(jsonPath("$.data.investmentProductId").value(1))
                .andExpect(jsonPath("$.data.unitsOwned").value(10));
    }

    // Test buy with invalid request (units below minimum)
    @Test
    void testBuyInvestment_InvalidRequest() throws Exception {
        PortfolioRequest request = new PortfolioRequest();
        request.setInvestmentProductId(1L);
        request.setUnits(BigDecimal.valueOf(0)); // Invalid, below 0.0001

        mockMvc.perform(post("/api/v1/portfolio/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("1")))
                .andExpect(status().isBadRequest());
    }

    // Test buy with negative units
    @Test
    void testBuyInvestment_NegativeUnits() throws Exception {
        PortfolioRequest request = new PortfolioRequest();
        request.setInvestmentProductId(1L);
        request.setUnits(BigDecimal.valueOf(-5)); // Invalid

        mockMvc.perform(post("/api/v1/portfolio/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("1")))
                .andExpect(status().isBadRequest());
    }

    // Test buy with service exception
    @Test
    void testBuyInvestment_ServiceException() throws Exception {
        PortfolioRequest request = new PortfolioRequest();
        request.setInvestmentProductId(1L);
        request.setUnits(BigDecimal.valueOf(10));

        Mockito.when(portfolioService.buyInvestment(eq(userId), any(PortfolioRequest.class)))
                .thenThrow(new RuntimeException("Insufficient funds"));

        mockMvc.perform(post("/api/v1/portfolio/buy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("1")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to complete purchase: Insufficient funds"));
    }

    // Test successful sell
    @Test
    void testSellInvestment_Success() throws Exception {
        PortfolioRequest request = new PortfolioRequest();
        request.setInvestmentProductId(1L);
        request.setUnits(BigDecimal.valueOf(5));

        PortfolioResponse response = PortfolioResponse.builder()
                .id(1L)
                .userId(userId)
                .investmentProductId(1L)
                .investmentProductName("Product A")
                .unitsOwned(BigDecimal.valueOf(5))
                .avgPurchasePrice(BigDecimal.valueOf(100.00))
                .build();

        Mockito.when(portfolioService.sellInvestment(eq(userId), any(PortfolioRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/portfolio/sell")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Investment units sold successfully"))
                .andExpect(jsonPath("$.data.unitsOwned").value(5));
    }

    // Test sell with minimum valid units (edge case)
    @Test
    void testSellInvestment_MinimumUnits() throws Exception {
        PortfolioRequest request = new PortfolioRequest();
        request.setInvestmentProductId(1L);
        request.setUnits(BigDecimal.valueOf(0.0001)); // Minimum valid

        PortfolioResponse response = PortfolioResponse.builder()
                .id(1L)
                .userId(userId)
                .investmentProductId(1L)
                .investmentProductName("Product A")
                .unitsOwned(BigDecimal.valueOf(0.0001))
                .avgPurchasePrice(BigDecimal.valueOf(100.00))
                .build();

        Mockito.when(portfolioService.sellInvestment(eq(userId), any(PortfolioRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/portfolio/sell")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // Test sell with service exception (e.g., insufficient units)
    @Test
    void testSellInvestment_ServiceException() throws Exception {
        PortfolioRequest request = new PortfolioRequest();
        request.setInvestmentProductId(1L);
        request.setUnits(BigDecimal.valueOf(100));

        Mockito.when(portfolioService.sellInvestment(eq(userId), any(PortfolioRequest.class)))
                .thenThrow(new RuntimeException("Insufficient units"));

        mockMvc.perform(post("/api/v1/portfolio/sell")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("1")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to complete sale: Insufficient units"));
    }

    // Test get portfolio success
    @Test
    void testGetPortfolio_Success() throws Exception {
        PortfolioResponse response1 = PortfolioResponse.builder()
                .id(1L)
                .userId(userId)
                .investmentProductId(1L)
                .investmentProductName("Product A")
                .unitsOwned(BigDecimal.valueOf(10))
                .avgPurchasePrice(BigDecimal.valueOf(100.00))
                .build();

        PortfolioResponse response2 = PortfolioResponse.builder()
                .id(2L)
                .userId(userId)
                .investmentProductId(2L)
                .investmentProductName("Product B")
                .unitsOwned(BigDecimal.valueOf(20))
                .avgPurchasePrice(BigDecimal.valueOf(200.00))
                .build();

        List<PortfolioResponse> portfolio = Arrays.asList(response1, response2);

        Mockito.when(portfolioService.getPortfolioByUser(userId)).thenReturn(portfolio);

        mockMvc.perform(get("/api/v1/portfolio")
                        .with(user("1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Portfolio fetched successfully"))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.data[0].investmentProductId").value(1))
                .andExpect(jsonPath("$.data[1].unitsOwned").value(20));
    }

    // Test get portfolio empty
    @Test
    void testGetPortfolio_Empty() throws Exception {
        Mockito.when(portfolioService.getPortfolioByUser(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/portfolio")
                        .with(user("1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(0))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // Test get portfolio service exception
    @Test
    void testGetPortfolio_ServiceException() throws Exception {
        Mockito.when(portfolioService.getPortfolioByUser(userId))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/v1/portfolio")
                        .with(user("1")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to fetch portfolio: Database error"));
    }

    // Test get transactions success
    @Test
    void testGetTransactions_Success() throws Exception {
        TransactionResponse response1 = TransactionResponse.builder()
                .id(1L)
                .userId(userId)
                .investmentProductId(1L)
                .txnType("BUY")
                .units(BigDecimal.valueOf(10))
                .navAtTxn(BigDecimal.valueOf(100.00))
                .txnDate(LocalDateTime.now())
                .build();

        TransactionResponse response2 = TransactionResponse.builder()
                .id(2L)
                .userId(userId)
                .investmentProductId(1L)
                .txnType("SELL")
                .units(BigDecimal.valueOf(5))
                .navAtTxn(BigDecimal.valueOf(105.00))
                .txnDate(LocalDateTime.now())
                .build();

        List<TransactionResponse> transactions = Arrays.asList(response1, response2);

        Mockito.when(portfolioService.getAllTransactions(userId)).thenReturn(transactions);

        mockMvc.perform(get("/api/v1/portfolio/transactions")
                        .with(user("1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Transactions fetched successfully"))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.data[0].txnType").value("BUY"))
                .andExpect(jsonPath("$.data[1].txnType").value("SELL"));
    }

    // Test get transactions empty
    @Test
    void testGetTransactions_Empty() throws Exception {
        Mockito.when(portfolioService.getAllTransactions(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/portfolio/transactions")
                        .with(user("1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(0))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // Test get transactions service exception
    @Test
    void testGetTransactions_ServiceException() throws Exception {
        Mockito.when(portfolioService.getAllTransactions(userId))
                .thenThrow(new RuntimeException("Transaction fetch error"));

        mockMvc.perform(get("/api/v1/portfolio/transactions")
                        .with(user("1")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to fetch transactions: Transaction fetch error"));
    }
}
