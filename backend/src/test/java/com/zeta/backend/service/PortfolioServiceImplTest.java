package com.zeta.backend.service;

import com.zeta.backend.dto.PortfolioRequest;
import com.zeta.backend.dto.PortfolioResponse;
import com.zeta.backend.dto.TransactionResponse;
import com.zeta.backend.enums.TxnType;
import com.zeta.backend.exceptions.InvalidInputException;
import com.zeta.backend.exceptions.ResourceNotFoundException;
import com.zeta.backend.models.InvestmentProduct;
import com.zeta.backend.models.Portfolio;
import com.zeta.backend.models.Transaction;
import com.zeta.backend.repository.InvestmentProductRepository;
import com.zeta.backend.repository.PortfolioRepository;
import com.zeta.backend.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceImplTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private InvestmentProductRepository productRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private PortfolioServiceImpl portfolioService;

    private Long userId;
    private PortfolioRequest request;
    private InvestmentProduct product;
    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        userId = 1L;
        request = PortfolioRequest.builder()
                .investmentProductId(1L)
                .units(BigDecimal.valueOf(10))
                .build();

        product = InvestmentProduct.builder()
                .id(1L)
                .name("Test Product")
                .currentNAV(BigDecimal.valueOf(100))
                .build();

        portfolio = Portfolio.builder()
                .id(1L)
                .userId(userId)
                .investmentProduct(product)
                .unitsOwned(BigDecimal.valueOf(5))
                .avgPurchasePrice(BigDecimal.valueOf(100))
                .build();
    }

    // Test cases for buyInvestment
    @Test
    void buyInvestment_ShouldCreateNewPortfolioAndSaveTransaction_WhenPortfolioDoesNotExist() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(portfolioRepository.findByUserIdAndInvestmentProductId(userId, 1L)).thenReturn(Optional.empty());
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(portfolio);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(Transaction.builder().build());

        // Act
        PortfolioResponse response = portfolioService.buyInvestment(userId, request);

        // Assert
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getInvestmentProductId()).isEqualTo(1L);
        assertThat(response.getUnitsOwned()).isEqualTo(BigDecimal.valueOf(10));
        assertThat(response.getAvgPurchasePrice()).isEqualTo(BigDecimal.valueOf(100));

        verify(portfolioRepository).save(any(Portfolio.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void buyInvestment_ShouldUpdateExistingPortfolioAndSaveTransaction_WhenPortfolioExists() {
        // Arrange
        Portfolio existingPortfolio = Portfolio.builder()
                .id(1L)
                .userId(userId)
                .investmentProduct(product)
                .unitsOwned(BigDecimal.valueOf(5))
                .avgPurchasePrice(BigDecimal.valueOf(100))
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(portfolioRepository.findByUserIdAndInvestmentProductId(userId, 1L)).thenReturn(Optional.of(existingPortfolio));
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(existingPortfolio);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(Transaction.builder().build());

        // Act
        PortfolioResponse response = portfolioService.buyInvestment(userId, request);

        // Assert
        assertThat(response.getUnitsOwned()).isEqualTo(BigDecimal.valueOf(15));  // 5 + 10
        assertThat(response.getAvgPurchasePrice()).isEqualTo(BigDecimal.valueOf(100));  // Updated to current NAV

        verify(portfolioRepository).save(existingPortfolio);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void buyInvestment_ShouldThrowResourceNotFoundException_WhenProductNotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> portfolioService.buyInvestment(userId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("InvestmentProduct");

        verify(portfolioRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void buyInvestment_ShouldThrowInvalidInputException_WhenInvestmentProductIdIsNull() {
        // Arrange
        request.setInvestmentProductId(null);

        // Act & Assert
        assertThatThrownBy(() -> portfolioService.buyInvestment(userId, request))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Investment product ID is required");
    }

    @Test
    void buyInvestment_ShouldThrowInvalidInputException_WhenUnitsAreZero() {
        // Arrange
        request.setUnits(BigDecimal.ZERO);

        // Act & Assert
        assertThatThrownBy(() -> portfolioService.buyInvestment(userId, request))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Units must be greater than zero");
    }

    @Test
    void buyInvestment_ShouldThrowInvalidInputException_WhenUnitsAreNegative() {
        // Arrange
        request.setUnits(BigDecimal.valueOf(-5));

        // Act & Assert
        assertThatThrownBy(() -> portfolioService.buyInvestment(userId, request))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Units must be greater than zero");
    }

    @Test
    void sellInvestment_ShouldThrowResourceNotFoundException_WhenPortfolioNotFound() {
        // Arrange
        when(portfolioRepository.findByUserIdAndInvestmentProductId(userId, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> portfolioService.sellInvestment(userId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Portfolio");

        verify(portfolioRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void sellInvestment_ShouldThrowInvalidInputException_WhenInsufficientUnits() {
        // Arrange
        request.setUnits(BigDecimal.valueOf(20));  // More than owned (5)

        when(portfolioRepository.findByUserIdAndInvestmentProductId(userId, 1L)).thenReturn(Optional.of(portfolio));

        // Act & Assert
        assertThatThrownBy(() -> portfolioService.sellInvestment(userId, request))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Insufficient units to sell");

        verify(portfolioRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void sellInvestment_ShouldThrowInvalidInputException_WhenUnitsAreZero() {
        // Arrange
        request.setUnits(BigDecimal.ZERO);

        // Act & Assert
        assertThatThrownBy(() -> portfolioService.sellInvestment(userId, request))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Units must be greater than zero");
    }

    // Test cases for getPortfolioByUser
    @Test
    void getPortfolioByUser_ShouldReturnPortfolioList_WhenPortfoliosExist() {
        // Arrange
        List<Portfolio> portfolios = List.of(portfolio);
        when(portfolioRepository.findByUserId(userId)).thenReturn(portfolios);

        // Act
        List<PortfolioResponse> responses = portfolioService.getPortfolioByUser(userId);

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getUserId()).isEqualTo(userId);

        verify(portfolioRepository).findByUserId(userId);
    }

    @Test
    void getPortfolioByUser_ShouldReturnEmptyList_WhenNoPortfolios() {
        // Arrange
        when(portfolioRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        List<PortfolioResponse> responses = portfolioService.getPortfolioByUser(userId);

        // Assert
        assertThat(responses).isEmpty();

        verify(portfolioRepository).findByUserId(userId);
    }

    // Test cases for getAllTransactions
    @Test
    void getAllTransactions_ShouldReturnTransactionList_WhenTransactionsExist() {
        // Arrange
        Transaction transaction = Transaction.builder()
                .id(1L)
                .userId(userId)
                .investmentProductId(1L)
                .txnType(TxnType.BUY)
                .units(BigDecimal.valueOf(10))
                .navAtTxn(BigDecimal.valueOf(100))
                .txnDate(LocalDateTime.now())
                .build();
        List<Transaction> transactions = List.of(transaction);
        when(transactionRepository.findByUserIdOrderByTxnDateDesc(userId)).thenReturn(transactions);

        // Act
        List<TransactionResponse> responses = portfolioService.getAllTransactions(userId);

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getTxnType()).isEqualTo("BUY");

        verify(transactionRepository).findByUserIdOrderByTxnDateDesc(userId);
    }

    @Test
    void getAllTransactions_ShouldReturnEmptyList_WhenNoTransactions() {
        // Arrange
        when(transactionRepository.findByUserIdOrderByTxnDateDesc(userId)).thenReturn(Collections.emptyList());

        // Act
        List<TransactionResponse> responses = portfolioService.getAllTransactions(userId);

        // Assert
        assertThat(responses).isEmpty();

        verify(transactionRepository).findByUserIdOrderByTxnDateDesc(userId);
    }
}
