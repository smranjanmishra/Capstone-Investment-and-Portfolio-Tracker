package com.zeta.backend.controller;

import com.zeta.backend.dto.AssetAllocationDTO;
import com.zeta.backend.dto.GainLossDTO;
import com.zeta.backend.dto.PortfolioSummaryDTO;
import com.zeta.backend.service.PortfolioAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * This controller provides endpoints for portfolio analytics such as
 * summary metrics, asset allocation, and gain/loss analysis.
 * All endpoints derive data from Portfolio, Transaction, and InvestmentProduct entities.
 */
@RestController
@RequestMapping("/api/v1/portfolio")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class PortfolioAnalyticsController {

    private final PortfolioAnalyticsService analyticsService;


    // GET /portfolio/summary
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getPortfolioSummary(Authentication authentication) {
        log.info("API Request: GET /portfolio/summary - Fetch portfolio summary");

        try {
            Long userId = Long.valueOf(authentication.getName());
            PortfolioSummaryDTO summary = analyticsService.getPortfolioSummary(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Portfolio summary fetched successfully");
            response.put("data", summary);

            log.info("Portfolio summary retrieved for user {}", userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching portfolio summary: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch portfolio summary: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    // GET /portfolio/allocation
    @GetMapping("/allocation")
    public ResponseEntity<Map<String, Object>> getPortfolioAllocation(Authentication authentication) {
        log.info("API Request: GET /portfolio/allocation - Fetch portfolio allocation");

        try {
            Long userId = Long.valueOf(authentication.getName());
            List<AssetAllocationDTO> allocationList = analyticsService.getPortfolioAllocation(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Portfolio allocation fetched successfully");
            response.put("count", allocationList.size());
            response.put("data", allocationList);

            log.info("Fetched {} allocation records for user {}", allocationList.size(), userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching portfolio allocation: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch portfolio allocation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    // GET /portfolio/gains
    @GetMapping("/gains")
    public ResponseEntity<Map<String, Object>> getPortfolioGains(Authentication authentication) {
        log.info("API Request: GET /portfolio/gains - Fetch gain/loss analysis");

        try {
            Long userId = Long.valueOf(authentication.getName());
            List<GainLossDTO> gainLossList = analyticsService.getPortfolioGains(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Gain/Loss data fetched successfully");
            response.put("count", gainLossList.size());
            response.put("data", gainLossList);

            log.info("Fetched {} gain/loss records for user {}", gainLossList.size(), userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching gain/loss data: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch gain/loss data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
