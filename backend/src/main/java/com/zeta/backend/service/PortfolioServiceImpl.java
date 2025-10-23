package com.zeta.backend.service;

import com.zeta.backend.dto.PortfolioRequest;
import com.zeta.backend.dto.PortfolioResponse;
import com.zeta.backend.dto.TransactionResponse;
import com.zeta.backend.exceptions.InvalidInputException;
import com.zeta.backend.exceptions.ResourceNotFoundException;
import com.zeta.backend.models.InvestmentProduct;
import com.zeta.backend.models.Portfolio;
import com.zeta.backend.models.Transaction;
import com.zeta.backend.enums.TxnType;
import com.zeta.backend.repository.InvestmentProductRepository;
import com.zeta.backend.repository.PortfolioRepository;
import com.zeta.backend.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final InvestmentProductRepository productRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public PortfolioResponse buyInvestment(Long userId, PortfolioRequest request) {
        log.info("Processing BUY transaction: userId={}, productId={}, units={}",
                userId, request.getInvestmentProductId(), request.getUnits());

        validateRequest(request);

        InvestmentProduct product = productRepository.findById(request.getInvestmentProductId())
                .orElseThrow(() -> new ResourceNotFoundException("InvestmentProduct", "id", request.getInvestmentProductId()));

        Portfolio portfolio = portfolioRepository.findByUserIdAndInvestmentProductId(userId, request.getInvestmentProductId())
                .orElseGet(() -> Portfolio.builder()
                        .userId(userId)
                        .investmentProduct(product)
                        .unitsOwned(BigDecimal.ZERO)
                        .avgPurchasePrice(product.getCurrentNAV())
                        .build());

        portfolio.setUnitsOwned(portfolio.getUnitsOwned().add(request.getUnits()));
        portfolio.setAvgPurchasePrice(product.getCurrentNAV());
        portfolioRepository.save(portfolio);

        Transaction txn = Transaction.builder()
                .userId(userId)
                .investmentProductId(product.getId())
                .txnType(TxnType.BUY)
                .units(request.getUnits())
                .navAtTxn(product.getCurrentNAV())
                .txnDate(LocalDateTime.now())
                .build();
        transactionRepository.save(txn);

        return toPortfolioResponse(portfolio);
    }

    @Override
    public PortfolioResponse sellInvestment(Long userId, PortfolioRequest request) {
        log.info("Processing SELL transaction: userId={}, productId={}, units={}",
                userId, request.getInvestmentProductId(), request.getUnits());

        validateRequest(request);

        Portfolio portfolio = portfolioRepository.findByUserIdAndInvestmentProductId(userId, request.getInvestmentProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio", "investmentProductId", request.getInvestmentProductId()));

        if (portfolio.getUnitsOwned().compareTo(request.getUnits()) < 0) {
            throw new InvalidInputException("units", request.getUnits(), "Insufficient units to sell");
        }

        portfolio.setUnitsOwned(portfolio.getUnitsOwned().subtract(request.getUnits()));
        portfolioRepository.save(portfolio);

        Transaction txn = Transaction.builder()
                .userId(userId)
                .investmentProductId(request.getInvestmentProductId())
                .txnType(TxnType.SELL)
                .units(request.getUnits())
                .navAtTxn(portfolio.getAvgPurchasePrice())
                .txnDate(LocalDateTime.now())
                .build();
        transactionRepository.save(txn);

        return toPortfolioResponse(portfolio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PortfolioResponse> getPortfolioByUser(Long userId) {
        log.info("Fetching portfolio for user {}", userId);

        return portfolioRepository.findByUserId(userId)
                .stream()
                .map(this::toPortfolioResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllTransactions(Long userId) {
        log.info("Fetching transaction history for user {}", userId);

        return transactionRepository.findByUserIdOrderByTxnDateDesc(userId)
                .stream()
                .map(this::toTransactionResponse)
                .collect(Collectors.toList());
    }

    // ---------- Helper Methods ----------
    private void validateRequest(PortfolioRequest request) {
        if (request.getInvestmentProductId() == null)
            throw new InvalidInputException("investmentProductId", null, "Investment product ID is required");
        if (request.getUnits() == null || request.getUnits().compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidInputException("units", request.getUnits(), "Units must be greater than zero");
    }

    private PortfolioResponse toPortfolioResponse(Portfolio p) {
        return PortfolioResponse.builder()
                .id(p.getId())
                .userId(p.getUserId())
                .investmentProductId(p.getInvestmentProduct().getId())
                .investmentProductName(p.getInvestmentProduct().getName())
                .unitsOwned(p.getUnitsOwned())
                .avgPurchasePrice(p.getAvgPurchasePrice())
                .build();
    }

    private TransactionResponse toTransactionResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .userId(t.getUserId())
                .investmentProductId(t.getInvestmentProductId())
                .txnType(t.getTxnType().name())
                .units(t.getUnits())
                .navAtTxn(t.getNavAtTxn())
                .txnDate(t.getTxnDate())
                .build();
    }
}
