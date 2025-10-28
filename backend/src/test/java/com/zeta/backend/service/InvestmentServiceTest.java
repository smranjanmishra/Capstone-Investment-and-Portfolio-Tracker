package com.zeta.backend.service;

import com.zeta.backend.dto.InvestmentProductRequestDTO;
import com.zeta.backend.dto.InvestmentProductResponseDTO;
import com.zeta.backend.enums.InvestmentType;
import com.zeta.backend.enums.RiskLevel;
import com.zeta.backend.repository.InvestmentProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class InvestmentServiceTest {

    @Autowired
    private InvestmentService investmentService;

    @Autowired
    private InvestmentProductRepository repository;

    private InvestmentProductRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        repository.deleteAll();

        validRequest = InvestmentProductRequestDTO.builder()
                .name("Test Product " + System.currentTimeMillis())
                .type(InvestmentType.STOCK)
                .riskLevel(RiskLevel.MEDIUM)
                .minInvestment(new BigDecimal("10000"))
                .expectedReturnRate(new BigDecimal("12.5"))
                .currentNAV(new BigDecimal("100"))
                .isActive(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        repository.deleteAll();
    }

    // CRUD - Create
    @Test
    void shouldCreateProduct() {
        InvestmentProductResponseDTO result = investmentService.createInvestment(validRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(validRequest.getName());
    }

    // CRUD - Read
    @Test
    void shouldGetProductById() {
        InvestmentProductResponseDTO created = investmentService.createInvestment(validRequest);

        InvestmentProductResponseDTO result = investmentService.getInvestmentById(created.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(created.getId());
    }

    // CRUD - Update
    @Test
    void shouldUpdateProduct() {
        InvestmentProductResponseDTO created = investmentService.createInvestment(validRequest);

        InvestmentProductRequestDTO updateRequest = InvestmentProductRequestDTO.builder()
                .name("Updated Product " + System.currentTimeMillis())
                .type(InvestmentType.MUTUAL_FUND)
                .riskLevel(RiskLevel.HIGH)
                .minInvestment(new BigDecimal("15000"))
                .expectedReturnRate(new BigDecimal("18.0"))
                .currentNAV(new BigDecimal("150"))
                .isActive(true)
                .build();

        InvestmentProductResponseDTO result = investmentService.updateInvestment(created.getId(), updateRequest);

        assertThat(result.getName()).isEqualTo(updateRequest.getName());
        assertThat(result.getType()).isEqualTo(InvestmentType.MUTUAL_FUND);
    }

    // CRUD - Delete (Deactivate)
    @Test
    void shouldDeactivateProduct() {
        InvestmentProductResponseDTO created = investmentService.createInvestment(validRequest);

        investmentService.deactivateInvestment(created.getId());

        InvestmentProductResponseDTO deactivated = investmentService.getInvestmentById(created.getId());
        assertThat(deactivated.getIsActive()).isFalse();
    }

    // Visibility - isActive
    @Test
    void shouldReturnOnlyActiveProducts() {
        investmentService.createInvestment(validRequest);

        InvestmentProductRequestDTO inactiveRequest = InvestmentProductRequestDTO.builder()
                .name("Inactive Product " + System.currentTimeMillis())
                .type(InvestmentType.BOND)
                .riskLevel(RiskLevel.LOW)
                .minInvestment(new BigDecimal("5000"))
                .expectedReturnRate(new BigDecimal("6.0"))
                .currentNAV(new BigDecimal("80"))
                .isActive(false)
                .build();
        investmentService.createInvestment(inactiveRequest);

        List<InvestmentProductResponseDTO> result = investmentService.getAllActiveInvestments();

        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(product -> product.getIsActive());
    }
}