package com.zeta.backend.controller;

import com.zeta.backend.dto.PortfolioRequest;
import com.zeta.backend.dto.PortfolioResponse;
import com.zeta.backend.dto.TransactionResponse;
import com.zeta.backend.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
@CrossOrigin(origins = "*", maxAge = 3600)
public class PortfolioController {

    private final PortfolioService portfolioService;

    //     buy investment units
    //     POST /portfolio/buy
    //     request body: PortfolioRequest { investmentProductId, units }
    //     response: 201 Created with PortfolioResponse
    @PostMapping("/buy")
    public ResponseEntity<Map<String, Object>> buyInvestment(
            @Valid @RequestBody PortfolioRequest request,
            Authentication authentication) {

        log.info("API Request: POST /portfolio/buy - Buy investment units");

        try {
            Long userId = Long.valueOf(authentication.getName());
            PortfolioResponse responseDTO = portfolioService.buyInvestment(userId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Investment purchase successful");
            response.put("data", responseDTO);

            log.info("User {} purchased {} units of product {}", userId, request.getUnits(), request.getInvestmentProductId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Error during buyInvestment: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to complete purchase: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    //     sell investment units
    //     POST /portfolio/sell
    //     request body: PortfolioRequest { investmentProductId, units }
    //     response: 200 OK with PortfolioResponse
    @PostMapping("/sell")
    public ResponseEntity<Map<String, Object>> sellInvestment(
            @Valid @RequestBody PortfolioRequest request,
            Authentication authentication) {

        log.info("API Request: POST /portfolio/sell - Sell investment units");

        try {
            Long userId = Long.valueOf(authentication.getName());
            PortfolioResponse responseDTO = portfolioService.sellInvestment(userId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Investment units sold successfully");
            response.put("data", responseDTO);

            log.info("User {} sold {} units of product {}", userId, request.getUnits(), request.getInvestmentProductId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error during sellInvestment: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to complete sale: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    //     fetch current user's portfolio
    //     GET /portfolio
    //     response: 200 OK with list of PortfolioResponse
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPortfolio(Authentication authentication) {
        log.info("API Request: GET /portfolio - Fetch user portfolio");

        try {
            Long userId = Long.valueOf(authentication.getName());
            List<PortfolioResponse> portfolioResponses = portfolioService.getPortfolioByUser(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Portfolio fetched successfully");
            response.put("count", portfolioResponses.size());
            response.put("data", portfolioResponses);

            log.info("User {} has {} portfolio records", userId, portfolioResponses.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching portfolio: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch portfolio: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    //     fetch all transactions for current user
    //     GET /portfolio/transactions
    //     response: 200 OK with list of TransactionResponse
    @GetMapping("/transactions")
    public ResponseEntity<Map<String, Object>> getTransactions(Authentication authentication) {
        log.info("API Request: GET /portfolio/transactions - Fetch all user transactions");

        try {
            Long userId = Long.valueOf(authentication.getName());
            List<TransactionResponse> transactionResponses = portfolioService.getAllTransactions(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Transactions fetched successfully");
            response.put("count", transactionResponses.size());
            response.put("data", transactionResponses);

            log.info("Returning {} transactions for user {}", transactionResponses.size(), userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching transactions: {}", e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch transactions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
