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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/portfolio")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class PortfolioController {

    private final PortfolioService portfolioService;

    // buy investment units
    // POST /portfolio/buy
    // request body: PortfolioRequest { investmentProductId, units }
    // response: 201 Created with PortfolioResponse
    @PostMapping("/buy")
    public ResponseEntity<Map<String, Object>> buyInvestment(
            @Valid @RequestBody PortfolioRequest request,
            Authentication authentication) {
        log.info("API Request: POST /portfolio/buy - Buy investment units");

        Long userId = Long.valueOf(authentication.getName());
        PortfolioResponse responseDTO = portfolioService.buyInvestment(userId, request);

        log.info("User {} purchased {} units of product {}",
                userId, request.getUnits(), request.getInvestmentProductId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createResponse("Investment purchase successful", responseDTO.getId(), null, null));
    }

    // sell investment units
    // POST /portfolio/sell
    // request body: PortfolioRequest { investmentProductId, units }
    // response: 200 OK with PortfolioResponse
    @PostMapping("/sell")
    public ResponseEntity<Map<String, Object>> sellInvestment(
            @Valid @RequestBody PortfolioRequest request,
            Authentication authentication) {
        log.info("API Request: POST /portfolio/sell - Sell investment units");

        Long userId = Long.valueOf(authentication.getName());
        PortfolioResponse responseDTO = portfolioService.sellInvestment(userId, request);

        log.info("User {} sold {} units of product {}",
                userId, request.getUnits(), request.getInvestmentProductId());

        return ResponseEntity.ok(
                createResponse("Investment units sold successfully", responseDTO.getId(), null, null));
    }

    // fetch current user's portfolio
    // GET /portfolio
    // response: 200 OK with list of PortfolioResponse
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPortfolio(Authentication authentication) {
        log.info("API Request: GET /portfolio - Fetch user portfolio");

        Long userId = Long.valueOf(authentication.getName());
        List<PortfolioResponse> portfolioResponses = portfolioService.getPortfolioByUser(userId);

        log.info("User {} has {} portfolio records", userId, portfolioResponses.size());

        return ResponseEntity.ok(
                createResponse("Portfolio fetched successfully", null, portfolioResponses.size(), portfolioResponses));
    }

    // fetch all transactions for current user
    // GET /portfolio/transactions
    // response: 200 OK with list of TransactionResponse
    @GetMapping("/transactions")
    public ResponseEntity<Map<String, Object>> getTransactions(Authentication authentication) {
        log.info("API Request: GET /portfolio/transactions - Fetch all user transactions");

        Long userId = Long.valueOf(authentication.getName());
        List<TransactionResponse> transactionResponses = portfolioService.getAllTransactions(userId);

        log.info("Returning {} transactions for user {}", transactionResponses.size(), userId);

        return ResponseEntity.ok(
                createResponse("Transactions fetched successfully", null, transactionResponses.size(), transactionResponses));
    }

    private Map<String, Object> createResponse(String message, Long id, Integer count, Object data) {
        Map<String, Object> response = new LinkedHashMap<>();
        if (id != null) {
            response.put("id", id);
        }
        response.put("message", message);
        if (count != null) {
            response.put("count", count);
        }
        if (data != null) {
            response.put("data", data);
        }
        return response;
    }
}