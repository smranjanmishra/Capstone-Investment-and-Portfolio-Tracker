package com.zeta.backend.repository;

import com.zeta.backend.enums.InvestmentType;
import com.zeta.backend.enums.RiskLevel;
import com.zeta.backend.enums.Role;
import com.zeta.backend.models.InvestmentProduct;
import com.zeta.backend.models.Portfolio;
import com.zeta.backend.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PortfolioRepositoryTest {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvestmentProductRepository investmentProductRepository;

    private User user;
    private InvestmentProduct product1;
    private InvestmentProduct product2;

    @BeforeEach
    void setUp() {
        portfolioRepository.deleteAll();
        investmentProductRepository.deleteAll();
        userRepository.deleteAll();

        // Create user
        user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPasswordHash("pass");
        user.setPhone("9999999999");
        user.setRole(Role.USER);
        userRepository.save(user);

        // Product 1
        product1 = InvestmentProduct.builder()
                .name("Fund A")
                .type(InvestmentType.MUTUAL_FUND)
                .riskLevel(RiskLevel.MEDIUM)
                .minInvestment(new BigDecimal("1000"))
                .expectedReturnRate(new BigDecimal("10"))
                .currentNAV(new BigDecimal("120"))
                .isActive(true)
                .build();
        product1 = investmentProductRepository.save(product1);

        // Product 2
        product2 = InvestmentProduct.builder()
                .name("Stock B")
                .type(InvestmentType.STOCK)
                .riskLevel(RiskLevel.HIGH)
                .minInvestment(new BigDecimal("2000"))
                .expectedReturnRate(new BigDecimal("15"))
                .currentNAV(new BigDecimal("150"))
                .isActive(true)
                .build();
        product2 = investmentProductRepository.save(product2);
    }

    @Test
    @DisplayName("Find by userId and productId")
    void testFindByUserIdAndProductId() throws Exception {

        Portfolio portfolio = new Portfolio();
        portfolio.setInvestmentProduct(product1);
        portfolio.setUnitsOwned(new BigDecimal("10"));
        portfolio.setAvgPurchasePrice(new BigDecimal("100"));
        setPrivateField(portfolio, "userId", user.getId());

        portfolioRepository.save(portfolio);

        Optional<Portfolio> found =
                portfolioRepository.findByUserIdAndInvestmentProductId(user.getId(), product1.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getUnitsOwned()).isEqualTo("10");
        assertThat(found.get().getInvestmentProduct().getName()).isEqualTo("Fund A");
    }

    @Test
    @DisplayName("Find all portfolios by user")
    void testFindByUserId() throws Exception {

        Portfolio p1 = new Portfolio();
        p1.setInvestmentProduct(product1);
        p1.setUnitsOwned(new BigDecimal("10"));
        p1.setAvgPurchasePrice(new BigDecimal("100"));
        setPrivateField(p1, "userId", user.getId());
        portfolioRepository.save(p1);

        Portfolio p2 = new Portfolio();
        p2.setInvestmentProduct(product2);
        p2.setUnitsOwned(new BigDecimal("20"));
        p2.setAvgPurchasePrice(new BigDecimal("110"));
        setPrivateField(p2, "userId", user.getId());
        portfolioRepository.save(p2);

        List<Portfolio> result = portfolioRepository.findByUserId(user.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Return empty Optional when no portfolio found")
    void testNotFoundByUserAndProduct() {
        Optional<Portfolio> result = portfolioRepository.findByUserIdAndInvestmentProductId(999L, 999L);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Return empty list when no portfolios for user")
    void testEmptyListWhenUserHasNoPortfolios() {
        List<Portfolio> result = portfolioRepository.findByUserId(123L);
        assertThat(result).isEmpty();
    }

    // Helper
    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
