package com.zeta.backend.service;

import com.zeta.backend.dto.AssetAllocationDTO;
import com.zeta.backend.dto.GainLossDTO;
import com.zeta.backend.dto.PortfolioSummaryDTO;
import com.zeta.backend.enums.InvestmentType;
import com.zeta.backend.models.Portfolio;
import com.zeta.backend.models.Transaction;
import com.zeta.backend.repository.PortfolioRepository;
import com.zeta.backend.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PortfolioAnalyticsServiceImpl implements PortfolioAnalyticsService {

    private final PortfolioRepository portfolioRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Calculate overall portfolio summary including total invested, current value,
     * absolute return, and annualized return.
     */

    @Override
    public PortfolioSummaryDTO getPortfolioSummary(Long userId) {
        log.info("Calculating portfolio summary for userId={}", userId);

        List<Portfolio> portfolios = portfolioRepository.findByUserId(userId);

        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal currentValue = BigDecimal.ZERO;

        for (Portfolio p : portfolios) {
            BigDecimal invested = p.getAvgPurchasePrice().multiply(p.getUnitsOwned());
            totalInvested = totalInvested.add(invested);
            BigDecimal current = p.getInvestmentProduct().getCurrentNAV().multiply(p.getUnitsOwned());
            currentValue = currentValue.add(current);
        }

        BigDecimal absoluteReturn = currentValue.subtract(totalInvested);

        BigDecimal annualizedReturn = calculateAnnualizedReturn(userId, totalInvested, currentValue);

        return new PortfolioSummaryDTO(totalInvested, currentValue, absoluteReturn, annualizedReturn);
    }


    /**
     * Calculate the allocation of portfolio by investment type.
     * Returns each type's value and percentage of total portfolio.
     */

    @Override
    public List<AssetAllocationDTO> getPortfolioAllocation(Long userId) {
        log.info("Calculating portfolio allocation for userId={}", userId);

        List<Portfolio> portfolios = portfolioRepository.findByUserId(userId);

        BigDecimal totalValue = portfolios.stream()
                .map(p -> p.getUnitsOwned().multiply(p.getInvestmentProduct().getCurrentNAV()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<InvestmentType, BigDecimal> allocationMap = new HashMap<>();
        for (Portfolio p : portfolios) {
            BigDecimal value = p.getUnitsOwned().multiply(p.getInvestmentProduct().getCurrentNAV());
            allocationMap.merge(p.getInvestmentProduct().getType(), value, BigDecimal::add);
        }

        List<AssetAllocationDTO> allocationList = new ArrayList<>();
        for (Map.Entry<InvestmentType, BigDecimal> entry : allocationMap.entrySet()) {
            BigDecimal percentage = totalValue.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : entry.getValue().multiply(BigDecimal.valueOf(100)).divide(totalValue, 2, RoundingMode.HALF_UP);
            allocationList.add(new AssetAllocationDTO(entry.getKey().name(), entry.getValue(), percentage));
        }

        return allocationList;
    }

    /**
     * Calculate gain or loss per investment product in the user's portfolio.
     */

    @Override
    public List<GainLossDTO> getPortfolioGains(Long userId) {
        log.info("Calculating portfolio gains/losses for userId={}", userId);

        List<Portfolio> portfolios = portfolioRepository.findByUserId(userId);

        List<GainLossDTO> gainLossList = portfolios.stream().map(p -> {
            BigDecimal currentValue = p.getUnitsOwned().multiply(p.getInvestmentProduct().getCurrentNAV());
            BigDecimal invested = p.getUnitsOwned().multiply(p.getAvgPurchasePrice());
            BigDecimal absoluteGainLoss = currentValue.subtract(invested);

            return new GainLossDTO(
                    p.getInvestmentProduct().getName(),
                    p.getUnitsOwned(),
                    p.getAvgPurchasePrice(),
                    p.getInvestmentProduct().getCurrentNAV(),
                    absoluteGainLoss
            );
        }).collect(Collectors.toList());

        return gainLossList;
    }

    /**
     * Helper to calculate annualized return using weighted average holding period of BUY transactions.
     */
    private BigDecimal calculateAnnualizedReturn(Long userId, BigDecimal totalInvested, BigDecimal currentValue) {
        if (totalInvested.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        List<Transaction> transactions = transactionRepository.findByUserIdOrderByTxnDateDesc(userId);
        if (transactions.isEmpty()) {
            return BigDecimal.ZERO;
        }

        LocalDateTime now = LocalDateTime.now();

        // Weighted average holding period (in years)
        BigDecimal weightedDaysSum = BigDecimal.ZERO;
        BigDecimal totalInvestedForWeight = BigDecimal.ZERO;

        for (Transaction txn : transactions) {
            if (txn.getTxnType().name().equalsIgnoreCase("BUY")) {
                BigDecimal invested = txn.getNavAtTxn().multiply(txn.getUnits());
                long days = Duration.between(txn.getTxnDate(), now).toDays();

                // Treat same-day buys as 1-day holding
                if (days <= 0) {
                    days = 1;
                }

                weightedDaysSum = weightedDaysSum.add(BigDecimal.valueOf(days).multiply(invested));
                totalInvestedForWeight = totalInvestedForWeight.add(invested);
            }
        }

        if (totalInvestedForWeight.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        // Convert weighted average holding days → years
        BigDecimal weightedAvgDays = weightedDaysSum.divide(totalInvestedForWeight, 6, RoundingMode.HALF_UP);
        BigDecimal years = weightedAvgDays.divide(BigDecimal.valueOf(365), 6, RoundingMode.HALF_UP);

        if (years.compareTo(BigDecimal.ZERO) <= 0) {
            years = BigDecimal.valueOf(1).divide(BigDecimal.valueOf(365), 6, RoundingMode.HALF_UP);
        }

        // Compute annualized return
        BigDecimal ratio = currentValue.divide(totalInvested, 10, RoundingMode.HALF_UP);
        double annualizedDouble = Math.pow(ratio.doubleValue(), 1.0 / years.doubleValue()) - 1;

        return BigDecimal.valueOf(annualizedDouble * 100).setScale(2, RoundingMode.HALF_UP);
    }
}
