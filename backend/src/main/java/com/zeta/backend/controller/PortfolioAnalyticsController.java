package com.zeta.backend.controller;

import com.zeta.backend.dto.AssetAllocationDTO;
import com.zeta.backend.dto.GainLossDTO;
import com.zeta.backend.dto.PortfolioSummaryDTO;
import com.zeta.backend.service.PortfolioAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/portfolio")
@Slf4j
@RequiredArgsConstructor
public class PortfolioAnalyticsController {

    private final PortfolioAnalyticsService analyticsService;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getPortfolioSummary(Authentication authentication) {
        log.info("API Request: GET /portfolio/summary - Fetch portfolio summary");

        Long userId = Long.valueOf(authentication.getName());
        PortfolioSummaryDTO summary = analyticsService.getPortfolioSummary(userId);

        log.info("API Response: Portfolio summary retrieved for user {}", userId);

        return ResponseEntity.ok(buildResponse("Portfolio summary fetched successfully", summary, null));
    }

    @GetMapping("/allocation")
    public ResponseEntity<Map<String, Object>> getPortfolioAllocation(Authentication authentication) {
        log.info("API Request: GET /portfolio/allocation - Fetch portfolio allocation");

        Long userId = Long.valueOf(authentication.getName());
        List<AssetAllocationDTO> allocationList = analyticsService.getPortfolioAllocation(userId);

        log.info("API Response: Fetched {} allocation records for user {}", allocationList.size(), userId);

        return ResponseEntity.ok(buildResponse("Portfolio allocation fetched successfully", allocationList, null));
    }

    @GetMapping("/gains")
    public ResponseEntity<Map<String, Object>> getPortfolioGains(Authentication authentication) {
        log.info("API Request: GET /portfolio/gains - Fetch gain/loss analysis");

        Long userId = Long.valueOf(authentication.getName());
        List<GainLossDTO> gainLossList = analyticsService.getPortfolioGains(userId);

        log.info("API Response: Fetched {} gain/loss records for user {}", gainLossList.size(), userId);

        return ResponseEntity.ok(buildResponse("Gain/Loss data fetched successfully", gainLossList, null));
    }

    private Map<String, Object> buildResponse(String message, Object data, Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        if (id != null) {
            response.put("id", id);
        }
        if (data != null) {
            if (data instanceof List) {
                response.put("count", ((List<?>) data).size());
            }
            response.put("data", data);
        }
        return response;
    }
}