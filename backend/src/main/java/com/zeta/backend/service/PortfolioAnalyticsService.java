package com.zeta.backend.service;

import com.zeta.backend.dto.AssetAllocationDTO;
import com.zeta.backend.dto.GainLossDTO;
import com.zeta.backend.dto.PortfolioSummaryDTO;

import java.util.List;

public interface PortfolioAnalyticsService {

    PortfolioSummaryDTO getPortfolioSummary(Long userId);

    List<AssetAllocationDTO> getPortfolioAllocation(Long userId);

    List<GainLossDTO> getPortfolioGains(Long userId);
}
