package com.zeta.backend.service;

import com.zeta.backend.dto.InvestmentProductRequestDTO;
import com.zeta.backend.dto.InvestmentProductResponseDTO;
import com.zeta.backend.enums.InvestmentType;
import com.zeta.backend.enums.RiskLevel;

import java.util.List;

public interface InvestmentService {
    List<InvestmentProductResponseDTO> getAllActiveInvestments();

    List<InvestmentProductResponseDTO> getAllInvestments();

    InvestmentProductResponseDTO getInvestmentById(Long id);

    InvestmentProductResponseDTO createInvestment(InvestmentProductRequestDTO requestDTO);

    InvestmentProductResponseDTO updateInvestment(Long id, InvestmentProductRequestDTO requestDTO);

    void deactivateInvestment(Long id);

    void activateInvestment(Long id);

    List<InvestmentProductResponseDTO> getInvestmentsByType(InvestmentType type);

    List<InvestmentProductResponseDTO> getInvestmentsByRiskLevel(RiskLevel riskLevel);

    List<InvestmentProductResponseDTO> searchInvestmentsByName(String keyword);

    long getActiveInvestmentCount();
}
