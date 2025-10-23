package com.zeta.backend.service;

import com.zeta.backend.dto.InvestmentProductRequestDTO;
import com.zeta.backend.dto.InvestmentProductResponseDTO;
import com.zeta.backend.enums.InvestmentType;
import com.zeta.backend.enums.RiskLevel;

import java.util.List;

public interface InvestmentService {
    // Returns only active products - public endpoint
    List<InvestmentProductResponseDTO> getAllActiveInvestments();

    InvestmentProductResponseDTO getInvestmentById(Long id);

    InvestmentProductResponseDTO createInvestment(InvestmentProductRequestDTO requestDTO);

    InvestmentProductResponseDTO updateInvestment(Long id, InvestmentProductRequestDTO requestDTO);

    // Soft delete - sets isActive=false, preserves data for audit
    void deactivateInvestment(Long id);
}