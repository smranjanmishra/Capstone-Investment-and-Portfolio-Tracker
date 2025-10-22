package com.zeta.backend.controller;

import com.zeta.backend.dto.InvestmentProductRequestDTO;
import com.zeta.backend.dto.InvestmentProductResponseDTO;
import com.zeta.backend.enums.InvestmentType;
import com.zeta.backend.enums.RiskLevel;
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
@CrossOrigin(origins = "*", maxAge = 3600) // Allow CORS for frontend
public class InvestmentController {
    //    private final InvestmentService investmentService;
//
//    @GetMapping("/investments")
//    public ResponseEntity<Map<String, Object>> getAllActiveInvestments() {
//        log.info("API Request: GET /investments - Fetch all active investment products");
//
//        List<InvestmentProductResponseDTO> investments = investmentService.getAllActiveInvestments();
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "Investment products fetched successfully");
//        response.put("count", investments.size());
//        response.put("data", investments);
//
//        log.info("API Response: Returning {} active investment products", investments.size());
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/investments/{id}")
//    public ResponseEntity<Map<String, Object>> getInvestmentById(@PathVariable Long id) {
//        log.info("API Request: GET /investments/{} - Fetch investment product by ID", id);
//
//        InvestmentProductResponseDTO investment = investmentService.getInvestmentById(id);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "Investment product fetched successfully");
//        response.put("data", investment);
//
//        log.info("API Response: Returning investment product '{}'", investment.getName());
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/investments/type/{type}")
//    public ResponseEntity<Map<String, Object>> getInvestmentsByType(
//            @PathVariable String type) {
//        log.info("API Request: GET /investments/type/{} - Fetch products by type", type);
//
//        InvestmentType investmentType = InvestmentType.fromString(type);
//        List<InvestmentProductResponseDTO> investments = investmentService.getInvestmentsByType(investmentType);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "Investment products fetched successfully");
//        response.put("type", investmentType);
//        response.put("count", investments.size());
//        response.put("data", investments);
//
//        log.info("API Response: Returning {} products of type {}", investments.size(), investmentType);
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/investments/risk/{riskLevel}")
//    public ResponseEntity<Map<String, Object>> getInvestmentsByRiskLevel(
//            @PathVariable String riskLevel) {
//        log.info("API Request: GET /investments/risk/{} - Fetch products by risk level", riskLevel);
//
//        RiskLevel risk = RiskLevel.fromString(riskLevel);
//        List<InvestmentProductResponseDTO> investments = investmentService.getInvestmentsByRiskLevel(risk);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "Investment products fetched successfully");
//        response.put("riskLevel", risk);
//        response.put("count", investments.size());
//        response.put("data", investments);
//
//        log.info("API Response: Returning {} products with risk level {}", investments.size(), risk);
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/investments/search")
//    public ResponseEntity<Map<String, Object>> searchInvestments(
//            @RequestParam String keyword) {
//        log.info("API Request: GET /investments/search?keyword={}", keyword);
//
//        List<InvestmentProductResponseDTO> investments = investmentService.searchInvestmentsByName(keyword);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "Search completed successfully");
//        response.put("keyword", keyword);
//        response.put("count", investments.size());
//        response.put("data", investments);
//
//        log.info("API Response: Found {} products matching keyword '{}'", investments.size(), keyword);
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/investments/stats")
//    public ResponseEntity<Map<String, Object>> getInvestmentStats() {
//        log.info("API Request: GET /investments/stats - Fetch investment statistics");
//
//        long activeCount = investmentService.getActiveInvestmentCount();
//
//        Map<String, Object> stats = new HashMap<>();
//        stats.put("totalActiveProducts", activeCount);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "Statistics fetched successfully");
//        response.put("data", stats);
//
//        log.info("API Response: Returning statistics");
//
//        return ResponseEntity.ok(response);
//    }
//
//    // ==================== ADMIN ENDPOINTS ====================
//    @PostMapping("/admin/investments")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Map<String, Object>> createInvestment(
//            @Valid @RequestBody InvestmentProductRequestDTO requestDTO) {
//        log.info("API Request: POST /admin/investments - Create new investment product '{}'", requestDTO.getName());
//
//        InvestmentProductResponseDTO createdInvestment = investmentService.createInvestment(requestDTO);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "Investment product created successfully");
//        response.put("data", createdInvestment);
//
//        log.info("API Response: Investment product '{}' created with ID: {}",
//                createdInvestment.getName(), createdInvestment.getId());
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    @PutMapping("/admin/investments/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Map<String, Object>> updateInvestment(
//            @PathVariable Long id,
//            @Valid @RequestBody InvestmentProductRequestDTO requestDTO) {
//        log.info("API Request: PUT /admin/investments/{} - Update investment product", id);
//
//        InvestmentProductResponseDTO updatedInvestment = investmentService.updateInvestment(id, requestDTO);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "Investment product updated successfully");
//        response.put("data", updatedInvestment);
//
//        log.info("API Response: Investment product '{}' updated", updatedInvestment.getName());
//
//        return ResponseEntity.ok(response);
//    }
//
//    @DeleteMapping("/admin/investments/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Map<String, Object>> deactivateInvestment(@PathVariable Long id) {
//        log.info("API Request: DELETE /admin/investments/{} - Deactivate investment product", id);
//
//        investmentService.deactivateInvestment(id);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "Investment product deactivated successfully");
//
//        log.info("API Response: Investment product with ID {} deactivated", id);
//
//        return ResponseEntity.ok(response);
//    }
//
//    @PutMapping("/admin/investments/{id}/activate")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Map<String, Object>> activateInvestment(@PathVariable Long id) {
//        log.info("API Request: PUT /admin/investments/{}/activate - Activate investment product", id);
//
//        investmentService.activateInvestment(id);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "Investment product activated successfully");
//
//        log.info("API Response: Investment product with ID {} activated", id);
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/admin/investments")
//    // @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Map<String, Object>> getAllInvestmentsAdmin() {
//        log.info("API Request: GET /admin/investments - Fetch all investment products (admin)");
//
//        List<InvestmentProductResponseDTO> investments = investmentService.getAllInvestments();
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "All investment products fetched successfully");
//        response.put("count", investments.size());
//        response.put("data", investments);
//
//        log.info("API Response: Returning {} total investment products", investments.size());
//
//        return ResponseEntity.ok(response);
//    }
    private final InvestmentService investmentService;

    // ==================== PUBLIC/USER ENDPOINTS ====================

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

    @GetMapping("/investments/{id}")
    public ResponseEntity<Map<String, Object>> getInvestmentById(@PathVariable Long id) {
        log.info("API Request: GET /investments/{} - Fetch investment product by ID", id);

        InvestmentProductResponseDTO investment = investmentService.getInvestmentById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Investment product fetched successfully");
        response.put("data", investment);

        log.info("API Response: Returning investment product '{}'", investment.getName());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/investments/type/{type}")
    public ResponseEntity<Map<String, Object>> getInvestmentsByType(@PathVariable String type) {
        log.info("API Request: GET /investments/type/{} - Fetch products by type", type);

        InvestmentType investmentType = InvestmentType.fromString(type);
        List<InvestmentProductResponseDTO> investments = investmentService.getInvestmentsByType(investmentType);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Investment products fetched successfully");
        response.put("type", investmentType);
        response.put("count", investments.size());
        response.put("data", investments);

        log.info("API Response: Returning {} products of type {}", investments.size(), investmentType);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/investments/risk/{riskLevel}")
    public ResponseEntity<Map<String, Object>> getInvestmentsByRiskLevel(@PathVariable String riskLevel) {
        log.info("API Request: GET /investments/risk/{} - Fetch products by risk level", riskLevel);

        RiskLevel risk = RiskLevel.fromString(riskLevel);
        List<InvestmentProductResponseDTO> investments = investmentService.getInvestmentsByRiskLevel(risk);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Investment products fetched successfully");
        response.put("riskLevel", risk);
        response.put("count", investments.size());
        response.put("data", investments);

        log.info("API Response: Returning {} products with risk level {}", investments.size(), risk);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/investments/search")
    public ResponseEntity<Map<String, Object>> searchInvestments(@RequestParam String keyword) {
        log.info("API Request: GET /investments/search?keyword={}", keyword);

        List<InvestmentProductResponseDTO> investments = investmentService.searchInvestmentsByName(keyword);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Search completed successfully");
        response.put("keyword", keyword);
        response.put("count", investments.size());
        response.put("data", investments);

        log.info("API Response: Found {} products matching keyword '{}'", investments.size(), keyword);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/investments/stats")
    public ResponseEntity<Map<String, Object>> getInvestmentStats() {
        log.info("API Request: GET /investments/stats - Fetch investment statistics");

        long activeCount = investmentService.getActiveInvestmentCount();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalActiveProducts", activeCount);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Statistics fetched successfully");
        response.put("data", stats);

        log.info("API Response: Returning statistics");

        return ResponseEntity.ok(response);
    }

    // ==================== ADMIN ENDPOINTS (No Security for Testing) ====================

    @PostMapping("/admin/investments")
    // @PreAuthorize("hasRole('ADMIN')") // COMMENTED for testing
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
    // @PreAuthorize("hasRole('ADMIN')") // COMMENTED for testing
    public ResponseEntity<Map<String, Object>> updateInvestment(
            @PathVariable Long id,
            @Valid @RequestBody InvestmentProductRequestDTO requestDTO) {
        log.info("API Request: PUT /admin/investments/{} - Update investment product", id);

        InvestmentProductResponseDTO updatedInvestment = investmentService.updateInvestment(id, requestDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Investment product updated successfully");
        response.put("data", updatedInvestment);

        log.info("API Response: Investment product '{}' updated", updatedInvestment.getName());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/investments/{id}")
    // @PreAuthorize("hasRole('ADMIN')") // COMMENTED for testing
    public ResponseEntity<Map<String, Object>> deactivateInvestment(@PathVariable Long id) {
        log.info("API Request: DELETE /admin/investments/{} - Deactivate investment product", id);

        investmentService.deactivateInvestment(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Investment product deactivated successfully");

        log.info("API Response: Investment product with ID {} deactivated", id);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/investments/{id}/activate")
    // @PreAuthorize("hasRole('ADMIN')") // COMMENTED for testing
    public ResponseEntity<Map<String, Object>> activateInvestment(@PathVariable Long id) {
        log.info("API Request: PUT /admin/investments/{}/activate - Activate investment product", id);

        investmentService.activateInvestment(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Investment product activated successfully");

        log.info("API Response: Investment product with ID {} activated", id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/investments")
    // @PreAuthorize("hasRole('ADMIN')") // COMMENTED for testing
    public ResponseEntity<Map<String, Object>> getAllInvestmentsAdmin() {
        log.info("API Request: GET /admin/investments - Fetch all investment products (admin)");

        List<InvestmentProductResponseDTO> investments = investmentService.getAllInvestments();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "All investment products fetched successfully");
        response.put("count", investments.size());
        response.put("data", investments);

        log.info("API Response: Returning {} total investment products", investments.size());

        return ResponseEntity.ok(response);
    }
}
