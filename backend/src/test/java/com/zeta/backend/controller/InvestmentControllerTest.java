package com.zeta.backend.controller;

import com.zeta.backend.config.JwtAuthenticationFilter;
import com.zeta.backend.dto.InvestmentProductResponseDTO;
import com.zeta.backend.service.InvestmentService;
import com.zeta.backend.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

@WebMvcTest(controllers = InvestmentController.class)
class InvestmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InvestmentService investmentService;

    @MockitoBean
    private JwtUtil jwtUtil;  // Mock the security dependencies

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser  // Add a mock authenticated user
    void shouldReturnActiveInvestments() throws Exception {
        InvestmentProductResponseDTO dto = new InvestmentProductResponseDTO();
        dto.setId(1L);

        when(investmentService.getAllActiveInvestments()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/investments/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }
}