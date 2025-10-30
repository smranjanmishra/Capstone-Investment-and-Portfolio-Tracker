package com.zeta.backend.service;

import com.zeta.backend.dto.InvestmentProductRequestDTO;
import com.zeta.backend.dto.InvestmentProductResponseDTO;

import java.util.List;

public interface InvestmentService {
    List<InvestmentProductResponseDTO> getAllActiveInvestments();
    InvestmentProductResponseDTO getInvestmentById(Long id);
    InvestmentProductResponseDTO createInvestment(InvestmentProductRequestDTO requestDTO);
    InvestmentProductResponseDTO updateInvestment(Long id, InvestmentProductRequestDTO requestDTO);
    void deactivateInvestment(Long id);
}