package com.zeta.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeta.backend.dto.AssetAllocationDTO;
import com.zeta.backend.dto.GainLossDTO;
import com.zeta.backend.dto.PortfolioSummaryDTO;
import com.zeta.backend.service.PortfolioAnalyticsService;
import com.zeta.backend.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller Unit Tests for PortfolioAnalyticsController
 * Works with Authentication parameter (no controller changes)
 *
 */
@WebMvcTest(PortfolioAnalyticsController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PortfolioAnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PortfolioAnalyticsService analyticsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long userId = 1L;

    /**
     * Helper method to inject Authentication into controller method
     */
    private RequestPostProcessor authenticatedUser() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn(String.valueOf(userId));
        return request -> {
            request.setUserPrincipal(authentication);
            request.setAttribute("authentication", authentication);
            return request;
        };
    }

    // ---------- /portfolio/summary ----------

    @Test
    void testGetPortfolioSummary_Success() throws Exception {
        PortfolioSummaryDTO summary = new PortfolioSummaryDTO(
                BigDecimal.valueOf(10000.00),
                BigDecimal.valueOf(11000.00),
                BigDecimal.valueOf(1000.00),
                BigDecimal.valueOf(10.00)
        );

        Mockito.when(analyticsService.getPortfolioSummary(userId)).thenReturn(summary);

        mockMvc.perform(get("/api/v1/portfolio/summary")
                        .with(authenticatedUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Portfolio summary fetched successfully"))
                .andExpect(jsonPath("$.data.totalInvested").value(10000.00))
                .andExpect(jsonPath("$.data.currentValue").value(11000.00))
                .andExpect(jsonPath("$.data.absoluteReturn").value(1000.00))
                .andExpect(jsonPath("$.data.annualizedReturn").value(10.00));
    }

    @Test
    void testGetPortfolioSummary_ServiceException() throws Exception {
        Mockito.when(analyticsService.getPortfolioSummary(anyLong()))
                .thenThrow(new RuntimeException("Calculation error"));

        mockMvc.perform(get("/api/v1/portfolio/summary")
                        .with(authenticatedUser()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to fetch portfolio summary: Calculation error"));
    }

    // ---------- /portfolio/allocation ----------

    @Test
    void testGetPortfolioAllocation_Empty() throws Exception {
        Mockito.when(analyticsService.getPortfolioAllocation(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/portfolio/allocation")
                        .with(authenticatedUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(0))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testGetPortfolioAllocation_ServiceException() throws Exception {
        Mockito.when(analyticsService.getPortfolioAllocation(anyLong()))
                .thenThrow(new RuntimeException("Allocation fetch error"));

        mockMvc.perform(get("/api/v1/portfolio/allocation")
                        .with(authenticatedUser()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to fetch portfolio allocation: Allocation fetch error"));
    }

    // ---------- /portfolio/gains ----------

    @Test
    void testGetPortfolioGains_Success() throws Exception {
        List<GainLossDTO> gainLossList = Arrays.asList(
                new GainLossDTO("Product A", BigDecimal.valueOf(10),
                        BigDecimal.valueOf(100), BigDecimal.valueOf(110), BigDecimal.valueOf(100)),
                new GainLossDTO("Product B", BigDecimal.valueOf(20),
                        BigDecimal.valueOf(200), BigDecimal.valueOf(190), BigDecimal.valueOf(-200))
        );

        Mockito.when(analyticsService.getPortfolioGains(userId)).thenReturn(gainLossList);

        mockMvc.perform(get("/api/v1/portfolio/gains")
                        .with(authenticatedUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Gain/Loss data fetched successfully"))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.data[0].productName").value("Product A"))
                .andExpect(jsonPath("$.data[1].productName").value("Product B"));
    }

    @Test
    void testGetPortfolioGains_Empty() throws Exception {
        Mockito.when(analyticsService.getPortfolioGains(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/portfolio/gains")
                        .with(authenticatedUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(0))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testGetPortfolioGains_ServiceException() throws Exception {
        Mockito.when(analyticsService.getPortfolioGains(anyLong()))
                .thenThrow(new RuntimeException("Gain calculation failed"));

        mockMvc.perform(get("/api/v1/portfolio/gains")
                        .with(authenticatedUser()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to fetch gain/loss data: Gain calculation failed"));
    }
}

