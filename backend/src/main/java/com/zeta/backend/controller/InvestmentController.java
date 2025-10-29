package com.zeta.backend.controller;

import com.zeta.backend.dto.InvestmentProductRequestDTO;
import com.zeta.backend.dto.InvestmentProductResponseDTO;
import com.zeta.backend.service.InvestmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600) // Allows all origins - restrict in production to specific frontend URLs

public class InvestmentController {
    private final InvestmentService investmentService;

    // Public endpoint - no authentication required for browsing investments
    @GetMapping("/investments")
    public ResponseEntity<Map<String, Object>> getAllActiveInvestments() {
        log.info("API Request: GET /investments - Fetch all active investment products");

        List<InvestmentProductResponseDTO> investments = investmentService.getAllActiveInvestments();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Investment products fetched successfully");
        response.put("count", investments.size());
        response.put("data", investments);

        log.info("API Response: Returning {} active investment products", investments.size());

        return ResponseEntity.ok(response);
    }

    // Enforces ADMIN role at method level - complements SecurityConfig rules
    @PostMapping("/admin/investments")
    public ResponseEntity<Map<String, Object>> createInvestment(
            @Valid @RequestBody InvestmentProductRequestDTO requestDTO) {
        log.info("API Request: POST /admin/investments - Create new investment product '{}'", requestDTO.getName());

        InvestmentProductResponseDTO createdInvestment = investmentService.createInvestment(requestDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("id", createdInvestment.getId());
        response.put("message", "Investment product created successfully");

        log.info("API Response: Investment product '{}' created with ID: {}",
                createdInvestment.getName(), createdInvestment.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/admin/investments/{id}")
    public ResponseEntity<Map<String, Object>> updateInvestment(
            @PathVariable Long id,
            @Valid @RequestBody InvestmentProductRequestDTO requestDTO) { // @Valid ensures business rules are checked
        log.info("API Request: PUT /admin/investments/{} - Update investment product", id);

        InvestmentProductResponseDTO updatedInvestment = investmentService.updateInvestment(id, requestDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Investment product updated successfully");
        response.put("data", updatedInvestment);

        log.info("API Response: Investment product '{}' updated", updatedInvestment.getName());

        return ResponseEntity.ok(response);
    }
}