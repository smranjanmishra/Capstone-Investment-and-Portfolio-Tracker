package com.zeta.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeta.backend.dto.InvestmentProductRequestDTO;
import com.zeta.backend.enums.InvestmentType;
import com.zeta.backend.enums.RiskLevel;
import com.zeta.backend.repository.InvestmentProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InvestmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
                .currentNAV(new BigDecimal("100.0"))
                .isActive(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        repository.deleteAll();
    }

    // CRUD - Read (GET)
    @Test
    @WithMockUser
    void shouldReturnActiveInvestments() throws Exception {
        mockMvc.perform(get("/api/v1/investments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data").isArray());
    }

    // CRUD - Create (POST) + Role-based access (ADMIN)
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateInvestmentWhenAdmin() throws Exception {
        String requestBody = objectMapper.writeValueAsString(validRequest);

        mockMvc.perform(post("/api/v1/admin/investments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is(validRequest.getName())));
    }

    // CRUD - Update (PUT) + Role-based access (ADMIN)
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateInvestmentWhenAdmin() throws Exception {
        // Create first
        String createBody = objectMapper.writeValueAsString(validRequest);
        String createResponse = mockMvc.perform(post("/api/v1/admin/investments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract product ID from response JSON
        Long productId = objectMapper.readTree(createResponse).get("data").get("id").asLong();

        // Prepare update DTO
        InvestmentProductRequestDTO updateRequest = InvestmentProductRequestDTO.builder()
                .name("Updated Product " + System.currentTimeMillis())
                .type(InvestmentType.MUTUAL_FUND)
                .riskLevel(RiskLevel.HIGH)
                .minInvestment(new BigDecimal("15000"))
                .expectedReturnRate(new BigDecimal("18.0"))
                .currentNAV(new BigDecimal("150.0"))
                .isActive(false)
                .build();

        String updateBody = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/api/v1/admin/investments/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is(updateRequest.getName())));
    }

    // Role-based access - Regular USER should be denied
    @Test
    @WithMockUser(roles = "USER")
    void shouldDenyAccessWhenNotAdmin() throws Exception {
        String requestBody = objectMapper.writeValueAsString(validRequest);

        mockMvc.perform(post("/api/v1/admin/investments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    // Role-based access - Unauthenticated should be denied
    @Test
    void shouldRequireAuthenticationForAdminEndpoint() throws Exception {
        String requestBody = objectMapper.writeValueAsString(validRequest);

        mockMvc.perform(post("/api/v1/admin/investments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }
}