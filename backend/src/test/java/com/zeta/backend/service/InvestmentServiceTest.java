package com.zeta.backend.service;

import com.zeta.backend.dto.InvestmentProductRequestDTO;
import com.zeta.backend.dto.InvestmentProductResponseDTO;
import com.zeta.backend.enums.InvestmentType;
import com.zeta.backend.enums.RiskLevel;
import com.zeta.backend.models.InvestmentProduct;
import com.zeta.backend.repository.InvestmentProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.assertj.core.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class InvestmentServiceTest {
    @Mock
    private InvestmentProductRepository repository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private InvestmentServiceImpl service;

    // CRUD - Create
    @Test
    void shouldCreateProduct() {
        when(repository.existsByName(any())).thenReturn(false);
        when(repository.save(any())).thenReturn(buildProduct(1L, true));
        when(modelMapper.map(any(), eq(InvestmentProductResponseDTO.class)))
                .thenReturn(buildResponse(1L));

        InvestmentProductResponseDTO result = service.createInvestment(buildRequest());

        assertThat(result.getId()).isEqualTo(1L);
        verify(repository).save(any());
    }

    // CRUD - Read
    @Test
    void shouldGetProductById() {
        when(repository.findById(1L)).thenReturn(Optional.of(buildProduct(1L, true)));
        when(modelMapper.map(any(), eq(InvestmentProductResponseDTO.class)))
                .thenReturn(buildResponse(1L));

        InvestmentProductResponseDTO result = service.getInvestmentById(1L);

        assertThat(result).isNotNull();
    }

    // CRUD - Update
    @Test
    void shouldUpdateProduct() {
        when(repository.findById(1L)).thenReturn(Optional.of(buildProduct(1L, true)));
        when(repository.save(any())).thenReturn(buildProduct(1L, true));
        when(modelMapper.map(any(), eq(InvestmentProductResponseDTO.class)))
                .thenReturn(buildResponse(1L));

        InvestmentProductResponseDTO result = service.updateInvestment(1L, buildRequest());

        verify(repository).save(any());
    }

    // CRUD - Delete
    @Test
    void shouldDeactivateProduct() {
        when(repository.findById(1L)).thenReturn(Optional.of(buildProduct(1L, true)));

        service.deactivateInvestment(1L);

        verify(repository).save(any());
    }

    // Visibility - isActive
    @Test
    void shouldReturnOnlyActiveProducts() {
        when(repository.findByIsActiveTrue()).thenReturn(Arrays.asList(buildProduct(1L, true)));
        when(modelMapper.map(any(), eq(InvestmentProductResponseDTO.class)))
                .thenReturn(buildResponse(1L));

        List<InvestmentProductResponseDTO> result = service.getAllActiveInvestments();

        assertThat(result).hasSize(1);
        verify(repository).findByIsActiveTrue();
    }

    private InvestmentProductRequestDTO buildRequest() {
        return InvestmentProductRequestDTO.builder()
                .name("Test")
                .type(InvestmentType.STOCK)
                .riskLevel(RiskLevel.MEDIUM)
                .minInvestment(new BigDecimal("10000"))
                .expectedReturnRate(new BigDecimal("12.5"))
                .currentNAV(new BigDecimal("100"))
                .isActive(true)
                .build();
    }

    private InvestmentProduct buildProduct(Long id, boolean isActive) {
        InvestmentProduct product = InvestmentProduct.builder()
                .name("Test")
                .type(InvestmentType.STOCK)
                .riskLevel(RiskLevel.MEDIUM)
                .minInvestment(new BigDecimal("10000"))
                .expectedReturnRate(new BigDecimal("12.5"))
                .currentNAV(new BigDecimal("100"))
                .isActive(isActive)
                .build();
        product.setId(id);
        return product;
    }

    private InvestmentProductResponseDTO buildResponse(Long id) {
        InvestmentProductResponseDTO dto = new InvestmentProductResponseDTO();
        dto.setId(id);
        return dto;
    }
}
