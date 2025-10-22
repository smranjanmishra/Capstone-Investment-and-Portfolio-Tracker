// java
package com.zeta.backend.controller;

import com.zeta.backend.dto.InvestmentProductResponseDTO;
import com.zeta.backend.service.InvestmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @MockBean
    private InvestmentService investmentService;

    @Test
    void shouldReturnActiveInvestments() throws Exception {
        InvestmentProductResponseDTO dto = new InvestmentProductResponseDTO();
        dto.setId(1L);
        when(investmentService.getAllActiveInvestments()).thenReturn(List.of(dto));

        // adjust the endpoint path if your controller uses a different mapping
        mockMvc.perform(get("/api/investments/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }
}
