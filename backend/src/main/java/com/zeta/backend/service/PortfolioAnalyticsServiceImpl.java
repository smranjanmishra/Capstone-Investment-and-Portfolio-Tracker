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
@Slf4j
@RequiredArgsConstructor
public class PortfolioAnalyticsServiceImpl implements PortfolioAnalyticsService {

    private final PortfolioRepository portfolioRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public PortfolioSummaryDTO getPortfolioSummary(Long userId) {
        log.info("Calculating portfolio summary for userId: {}", userId);

        validateUserId(userId);
        List<Portfolio> portfolios = getPortfoliosOrThrow(userId);

        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal currentValue = BigDecimal.ZERO;

        for (Portfolio p : portfolios) {
            if (p.getInvestmentProduct() == null) {
                log.warn("Portfolio entry missing investment product for userId: {}", userId);
                continue;
            }

            BigDecimal invested = safeMultiply(p.getAvgPurchasePrice(), p.getUnitsOwned());
            BigDecimal current = safeMultiply(p.getInvestmentProduct().getCurrentNAV(), p.getUnitsOwned());

            totalInvested = totalInvested.add(invested);
            currentValue = currentValue.add(current);
        }

        BigDecimal absoluteReturn = currentValue.subtract(totalInvested);
        BigDecimal roi = calculateROI(totalInvested, absoluteReturn);
        BigDecimal annualizedReturn = calculateAnnualizedReturn(userId, totalInvested, currentValue);

        log.debug("Portfolio summary calculated - Total Invested: {}, Current Value: {}", totalInvested, currentValue);

        return new PortfolioSummaryDTO(
                formatDecimal(totalInvested),
                formatDecimal(currentValue),
                formatDecimal(absoluteReturn),
                formatDecimal(roi),
                formatDecimal(annualizedReturn)
        );
    }

    @Override
    public List<AssetAllocationDTO> getPortfolioAllocation(Long userId) {
        log.info("Calculating portfolio allocation for userId: {}", userId);

        validateUserId(userId);
        List<Portfolio> portfolios = getPortfoliosOrThrow(userId);

        BigDecimal totalValue = portfolios.stream()
                .map(p -> safeMultiply(p.getUnitsOwned(), p.getInvestmentProduct().getCurrentNAV()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalValue.compareTo(BigDecimal.ZERO) == 0) {
            log.debug("Total portfolio value is zero for userId: {}", userId);
            return Collections.emptyList();
        }

        Map<InvestmentType, BigDecimal> allocationMap = buildAllocationMap(portfolios);

        log.debug("Calculated allocation for {} investment types", allocationMap.size());

        return allocationMap.entrySet().stream()
                .map(entry -> buildAssetAllocationDTO(entry, totalValue))
                .collect(Collectors.toList());
    }

    @Override
    public List<GainLossDTO> getPortfolioGains(Long userId) {
        log.info("Calculating portfolio gains/losses for userId: {}", userId);

        validateUserId(userId);
        List<Portfolio> portfolios = getPortfoliosOrThrow(userId);

        log.debug("Calculating gains/losses for {} portfolio items", portfolios.size());

        return portfolios.stream()
                .map(this::buildGainLossDTO)
                .collect(Collectors.toList());
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            log.error("Invalid userId provided: {}", userId);
            throw new InvalidInputException("userId", userId, "User ID must be valid and greater than zero");
        }
    }

    private List<Portfolio> getPortfoliosOrThrow(Long userId) {
        List<Portfolio> portfolios = portfolioRepository.findByUserId(userId);
        if (portfolios.isEmpty()) {
            log.warn("No portfolio records found for userId: {}", userId);
            throw new ResourceNotFoundException("Portfolio", "userId", userId);
        }
        return portfolios;
    }

    private BigDecimal calculateROI(BigDecimal totalInvested, BigDecimal absoluteReturn) {
        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            return absoluteReturn
                    .divide(totalInvested, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }

    private Map<InvestmentType, BigDecimal> buildAllocationMap(List<Portfolio> portfolios) {
        Map<InvestmentType, BigDecimal> allocationMap = new HashMap<>();
        for (Portfolio p : portfolios) {
            BigDecimal value = safeMultiply(p.getUnitsOwned(), p.getInvestmentProduct().getCurrentNAV());
            allocationMap.merge(p.getInvestmentProduct().getType(), value, BigDecimal::add);
        }
        return allocationMap;
    }

    private AssetAllocationDTO buildAssetAllocationDTO(Map.Entry<InvestmentType, BigDecimal> entry, BigDecimal totalValue) {
        BigDecimal percentage = entry.getValue()
                .multiply(BigDecimal.valueOf(100))
                .divide(totalValue, 2, RoundingMode.HALF_UP);
        return new AssetAllocationDTO(
                entry.getKey().name(),
                formatDecimal(entry.getValue()),
                formatDecimal(percentage)
        );
    }

    private GainLossDTO buildGainLossDTO(Portfolio p) {
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
    }

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
            if (txn.getTxnType() == null || !txn.getTxnType().name().equalsIgnoreCase("BUY")) {
                continue;
            }

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

        // For holdings < 3 months, use simple ROI to avoid unrealistic annualized returns
        if (years.doubleValue() < 0.25) {
            BigDecimal roi = currentValue.subtract(totalInvested)
                    .divide(totalInvested, 6, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            return roi.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal ratio = currentValue.divide(totalInvested, 10, RoundingMode.HALF_UP);
        if (ratio.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        double annualizedDouble;
        try {
            double power = 1.0 / years.doubleValue();
            annualizedDouble = Math.pow(ratio.doubleValue(), power) - 1;
        } catch (Exception e) {
            log.error("Error calculating annualized return for userId: {}", userId, e);
            return BigDecimal.ZERO;
        }

        // Cap to realistic range (-100% to +200%)
        annualizedDouble = capAnnualizedReturn(annualizedDouble);

        return BigDecimal.valueOf(annualizedDouble * 100).setScale(2, RoundingMode.HALF_UP);
    }

    private double capAnnualizedReturn(double annualizedDouble) {
        if (Double.isNaN(annualizedDouble) || Double.isInfinite(annualizedDouble)) {
            return 0.0;
        }
        if (annualizedDouble > 2.0) {
            return 2.0;
        }
        if (annualizedDouble < -1.0) {
            return -1.0;
        }
        return annualizedDouble;
    }

    private BigDecimal safeMultiply(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) {
            return BigDecimal.ZERO;
        }
        return a.multiply(b);
    }

    private BigDecimal formatDecimal(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}