package com.zeta.backend.service;

import com.zeta.backend.dto.PortfolioRequest;
import com.zeta.backend.dto.PortfolioResponse;
import com.zeta.backend.dto.TransactionResponse;

import java.util.List;

public interface PortfolioService {

    PortfolioResponse buyInvestment(Long userId, PortfolioRequest request);

    PortfolioResponse sellInvestment(Long userId, PortfolioRequest request);

    List<PortfolioResponse> getPortfolioByUser(Long userId);

    List<TransactionResponse> getAllTransactions(Long userId);
}
