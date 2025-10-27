package com.zeta.backend.service;

import com.zeta.backend.dto.AssetAllocationDTO;
import com.zeta.backend.dto.GainLossDTO;
import com.zeta.backend.dto.PortfolioSummaryDTO;
import com.zeta.backend.enums.InvestmentType;
import com.zeta.backend.exceptions.InvalidInputException;
import com.zeta.backend.exceptions.ResourceNotFoundException;
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

        if (userId == null || userId <= 0) {
            throw new InvalidInputException("userId", userId, "User ID must be valid and greater than zero");
        }

        List<Portfolio> portfolios = portfolioRepository.findByUserId(userId);
        if (portfolios.isEmpty()) {
            log.warn("No portfolio records found for userId={}", userId);
            throw new ResourceNotFoundException("Portfolio", "userId", userId);
        }

        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal currentValue = BigDecimal.ZERO;

        try {
            for (Portfolio p : portfolios) {
                if (p.getInvestmentProduct() == null) {
                    log.error("Portfolio entry missing investment product for userId={}", userId);
                    continue;
                }

                BigDecimal invested = safeMultiply(p.getAvgPurchasePrice(), p.getUnitsOwned());
                BigDecimal current = safeMultiply(p.getInvestmentProduct().getCurrentNAV(), p.getUnitsOwned());

                totalInvested = totalInvested.add(invested);
                currentValue = currentValue.add(current);
            }
        } catch (Exception e) {
            log.error("Error calculating portfolio summary for userId={}", userId, e);
            throw new InvalidInputException("calculation", null, "Error during portfolio summary calculation");
        }

        BigDecimal absoluteReturn = currentValue.subtract(totalInvested);
        BigDecimal roi = BigDecimal.ZERO;

        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            roi = absoluteReturn
                    .divide(totalInvested, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        BigDecimal annualizedReturn = calculateAnnualizedReturn(userId, totalInvested, currentValue);


        return new PortfolioSummaryDTO(
                formatDecimal(totalInvested),
                formatDecimal(currentValue),
                formatDecimal(absoluteReturn),
                formatDecimal(roi),
                formatDecimal(annualizedReturn)
        );
    }

    /**
     * Calculate the allocation of portfolio by investment type.
     * Returns each type's value and percentage of total portfolio.
     */
    @Override
    public List<AssetAllocationDTO> getPortfolioAllocation(Long userId) {
        log.info("Calculating portfolio allocation for userId={}", userId);

        if (userId == null || userId <= 0) {
            throw new InvalidInputException("userId", userId, "User ID must be valid and greater than zero");
        }

        List<Portfolio> portfolios = portfolioRepository.findByUserId(userId);
        if (portfolios.isEmpty()) {
            throw new ResourceNotFoundException("Portfolio", "userId", userId);
        }

        BigDecimal totalValue = portfolios.stream()
                .map(p -> safeMultiply(p.getUnitsOwned(), p.getInvestmentProduct().getCurrentNAV()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalValue.compareTo(BigDecimal.ZERO) == 0) {
            return Collections.emptyList();
        }

        Map<InvestmentType, BigDecimal> allocationMap = new HashMap<>();
        for (Portfolio p : portfolios) {
            BigDecimal value = safeMultiply(p.getUnitsOwned(), p.getInvestmentProduct().getCurrentNAV());
            allocationMap.merge(p.getInvestmentProduct().getType(), value, BigDecimal::add);
        }

        return allocationMap.entrySet().stream()
                .map(entry -> {
                    BigDecimal percentage = entry.getValue()
                            .multiply(BigDecimal.valueOf(100))
                            .divide(totalValue, 2, RoundingMode.HALF_UP);
                    return new AssetAllocationDTO(
                            entry.getKey().name(),
                            formatDecimal(entry.getValue()),
                            formatDecimal(percentage)
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Calculate gain or loss per investment product in the user's portfolio.
     */
    @Override
    public List<GainLossDTO> getPortfolioGains(Long userId) {
        log.info("Calculating portfolio gains/losses for userId={}", userId);

        if (userId == null || userId <= 0) {
            throw new InvalidInputException("userId", userId, "User ID must be valid and greater than zero");
        }

        List<Portfolio> portfolios = portfolioRepository.findByUserId(userId);
        if (portfolios.isEmpty()) {
            throw new ResourceNotFoundException("Portfolio", "userId", userId);
        }

        return portfolios.stream().map(p -> {
            BigDecimal currentValue = safeMultiply(p.getUnitsOwned(), p.getInvestmentProduct().getCurrentNAV());
            BigDecimal invested = safeMultiply(p.getUnitsOwned(), p.getAvgPurchasePrice());
            BigDecimal absoluteGainLoss = currentValue.subtract(invested);

            BigDecimal gainLossPercent = BigDecimal.ZERO;
            if (invested.compareTo(BigDecimal.ZERO) > 0) {
                gainLossPercent = absoluteGainLoss
                        .divide(invested, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }

            return new GainLossDTO(
                    p.getInvestmentProduct().getName(),
                    formatDecimal(p.getUnitsOwned()),
                    formatDecimal(p.getAvgPurchasePrice()),
                    formatDecimal(p.getInvestmentProduct().getCurrentNAV()),
                    formatDecimal(absoluteGainLoss),
                    formatDecimal(gainLossPercent)
            );
        }).collect(Collectors.toList());
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
        BigDecimal weightedDaysSum = BigDecimal.ZERO;
        BigDecimal totalInvestedForWeight = BigDecimal.ZERO;

        for (Transaction txn : transactions) {
            if (txn.getTxnType() == null || !txn.getTxnType().name().equalsIgnoreCase("BUY")) continue;

            BigDecimal invested = safeMultiply(txn.getNavAtTxn(), txn.getUnits());
            long days = Math.max(1, Duration.between(txn.getTxnDate(), now).toDays());

            weightedDaysSum = weightedDaysSum.add(BigDecimal.valueOf(days).multiply(invested));
            totalInvestedForWeight = totalInvestedForWeight.add(invested);
        }

        if (totalInvestedForWeight.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal weightedAvgDays = weightedDaysSum.divide(totalInvestedForWeight, 6, RoundingMode.HALF_UP);
        BigDecimal years = weightedAvgDays.divide(BigDecimal.valueOf(365), 6, RoundingMode.HALF_UP);

        if (years.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal ratio = currentValue.divide(totalInvested, 10, RoundingMode.HALF_UP);
        if (ratio.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        double annualizedDouble;

        try {
            // If holding < 3 months, use ROI instead (to avoid unrealistic compounding)
            if (years.doubleValue() < 0.25) {
                BigDecimal roi = currentValue.subtract(totalInvested)
                        .divide(totalInvested, 6, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                return roi.setScale(2, RoundingMode.HALF_UP);
            }

            double power = 1.0 / years.doubleValue();
            annualizedDouble = Math.pow(ratio.doubleValue(), power) - 1;

        } catch (Exception e) {
            log.error("Error calculating annualized return for userId={}", userId, e);
            return BigDecimal.ZERO;
        }

        // Cap to realistic range (-100% to +200%)
        if (Double.isNaN(annualizedDouble) || Double.isInfinite(annualizedDouble)) {
            annualizedDouble = 0.0;
        } else if (annualizedDouble > 2.0) {
            annualizedDouble = 2.0;
        } else if (annualizedDouble < -1.0) {
            annualizedDouble = -1.0;
        }

        return BigDecimal.valueOf(annualizedDouble * 100).setScale(2, RoundingMode.HALF_UP);
    }



    // ---------- Utility Helpers ----------

    private BigDecimal safeMultiply(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) return BigDecimal.ZERO;
        return a.multiply(b);
    }

    private BigDecimal formatDecimal(BigDecimal value) {
        if (value == null) return BigDecimal.ZERO;
        return value.stripTrailingZeros().setScale(2, RoundingMode.HALF_UP);
    }
}