package com.zeta.backend.service;

import com.zeta.backend.dto.PortfolioRequest;
import com.zeta.backend.dto.PortfolioResponse;
import com.zeta.backend.dto.TransactionResponse;
import com.zeta.backend.enums.InvestmentType;
import com.zeta.backend.enums.RiskLevel;
import com.zeta.backend.enums.Role;
import com.zeta.backend.exceptions.InvalidInputException;
import com.zeta.backend.exceptions.ResourceNotFoundException;
import com.zeta.backend.models.InvestmentProduct;
import com.zeta.backend.models.User;
import com.zeta.backend.repository.InvestmentProductRepository;
import com.zeta.backend.repository.PortfolioRepository;
import com.zeta.backend.repository.TransactionRepository;
import com.zeta.backend.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PortfolioServiceImplTest {

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private InvestmentProductRepository productRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    private Long userId;
    private InvestmentProduct product;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        portfolioRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPasswordHash("pass");
        user.setPhone("9876543210");
        user.setRole(Role.USER);
        userRepository.save(user);

        userId = user.getId();

        product = InvestmentProduct.builder()
                .name("Test Product")
                .type(InvestmentType.STOCK)
                .riskLevel(RiskLevel.MEDIUM)
                .minInvestment(new BigDecimal("1000"))
                .expectedReturnRate(new BigDecimal("10"))
                .currentNAV(new BigDecimal("100"))
                .isActive(true)
                .build();
        productRepository.save(product);
    }

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        portfolioRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldBuyInvestmentWhenNewPortfolio() {
        PortfolioRequest request = new PortfolioRequest();
        request.setInvestmentProductId(product.getId());
        request.setUnits(new BigDecimal("10"));

        PortfolioResponse response = portfolioService.buyInvestment(userId, request);

        assertThat(response.getUnitsOwned()).isEqualTo("10");
        assertThat(portfolioRepository.findAll()).hasSize(1);
        assertThat(transactionRepository.findAll()).hasSize(1);
    }

    @Test
    void shouldIncreaseUnitsWhenBuyingAgain() {
        PortfolioRequest request = new PortfolioRequest();
        request.setInvestmentProductId(product.getId());
        request.setUnits(new BigDecimal("10"));

        portfolioService.buyInvestment(userId, request);
        PortfolioResponse updated = portfolioService.buyInvestment(userId, request);

        assertThat(updated.getUnitsOwned()).isEqualByComparingTo("20");
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        PortfolioRequest request = new PortfolioRequest();
        request.setInvestmentProductId(999L);
        request.setUnits(BigDecimal.ONE);

        assertThatThrownBy(() -> portfolioService.buyInvestment(userId, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowWhenUnitsInvalid() {
        PortfolioRequest request = new PortfolioRequest();
        request.setInvestmentProductId(product.getId());
        request.setUnits(BigDecimal.ZERO);

        assertThatThrownBy(() -> portfolioService.buyInvestment(userId, request))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void shouldSellInvestmentSuccessfully() {
        PortfolioRequest request = new PortfolioRequest();
        request.setInvestmentProductId(product.getId());
        request.setUnits(new BigDecimal("10"));

        portfolioService.buyInvestment(userId, request);
        PortfolioResponse response = portfolioService.sellInvestment(userId, request);

        assertThat(response.getUnitsOwned()).isZero();
        assertThat(transactionRepository.findAll()).hasSize(2);
    }

    @Test
    void shouldThrowWhenSellingMoreUnitsThanOwned() {
        PortfolioRequest buyRequest = new PortfolioRequest();
        buyRequest.setInvestmentProductId(product.getId());
        buyRequest.setUnits(new BigDecimal("5"));
        portfolioService.buyInvestment(userId, buyRequest);

        PortfolioRequest sellRequest = new PortfolioRequest();
        sellRequest.setInvestmentProductId(product.getId());
        sellRequest.setUnits(new BigDecimal("10"));

        assertThatThrownBy(() -> portfolioService.sellInvestment(userId, sellRequest))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Insufficient units");
    }


    @Test
    void shouldFetchPortfolio() {
        PortfolioRequest request = new PortfolioRequest();
        request.setInvestmentProductId(product.getId());
        request.setUnits(new BigDecimal("10"));
        portfolioService.buyInvestment(userId, request);

        List<PortfolioResponse> list = portfolioService.getPortfolioByUser(userId);
        assertThat(list).hasSize(1);
    }

    @Test
    void shouldFetchTransactions() {
        PortfolioRequest request = new PortfolioRequest();
        request.setInvestmentProductId(product.getId());
        request.setUnits(new BigDecimal("10"));
        portfolioService.buyInvestment(userId, request);

        List<TransactionResponse> transactions = portfolioService.getAllTransactions(userId);

        assertThat(transactions).hasSize(1);
    }
}
