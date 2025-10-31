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

public class InvestmentController {

    private final InvestmentService investmentService;

    @GetMapping("/investments")
    public ResponseEntity<Map<String, Object>> getAllActiveInvestments() {
        log.info("API Request: GET /investments - Fetch all active investment products");

        List<InvestmentProductResponseDTO> investments = investmentService.getAllActiveInvestments();

        log.info("API Response: Returning {} active investment products", investments.size());

        return ResponseEntity.ok(buildResponse("Investment products fetched successfully", investments, null, true));
    }

    @PostMapping("/admin/investments")
    public ResponseEntity<Map<String, Object>> createInvestment(
            @Valid @RequestBody InvestmentProductRequestDTO requestDTO) {
        log.info("API Request: POST /admin/investments - Create new investment product '{}'", requestDTO.getName());

        InvestmentProductResponseDTO createdInvestment = investmentService.createInvestment(requestDTO);

        Map<String, Object> response = new HashMap<>();

        response.put("success", true);

        response.put("message", "Investment product created successfully");

        response.put("data", createdInvestment);


        log.info("API Response: Investment product '{}' created with ID: {}",
                createdInvestment.getName(), createdInvestment.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/admin/investments/{id}")
    public ResponseEntity<Map<String, Object>> updateInvestment(
            @PathVariable Long id,
            @Valid @RequestBody InvestmentProductRequestDTO requestDTO) {
        log.info("API Request: PUT /admin/investments/{} - Update investment product", id);

        InvestmentProductResponseDTO updatedInvestment = investmentService.updateInvestment(id, requestDTO);

        log.info("API Response: Investment product '{}' updated", updatedInvestment.getName());

        return ResponseEntity.ok(buildResponse("Investment product updated successfully", updatedInvestment, null, true));
    }

    private Map<String, Object> buildResponse(String message, Object data, Long id, boolean includeSuccess) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        if (id != null) {
            response.put("id", id);
        }
        if (includeSuccess) {
            response.put("success", true);
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