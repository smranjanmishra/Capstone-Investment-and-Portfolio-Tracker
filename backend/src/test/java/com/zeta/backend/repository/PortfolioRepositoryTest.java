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
        // create user
        user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPasswordHash("hashed");
        user.setPhone("9876543210");
        user.setRole(Role.USER);
        userRepository.save(user);

        // create first product
        product1 = new InvestmentProduct();
        product1.setName("Equity Fund A");
        product1.setType(InvestmentType.MUTUAL_FUND);
        product1.setRiskLevel(RiskLevel.MEDIUM);
        product1.setMinInvestment(new BigDecimal("1000"));
        product1.setExpectedReturnRate(new BigDecimal("10"));
        product1.setCurrentNAV(new BigDecimal("120"));
        product1.setIsActive(true);
        investmentProductRepository.save(product1);

        // create second product
        product2 = new InvestmentProduct();
        product2.setName("Growth Stock B");
        product2.setType(InvestmentType.STOCK);
        product2.setRiskLevel(RiskLevel.HIGH);
        product2.setMinInvestment(new BigDecimal("2000"));
        product2.setExpectedReturnRate(new BigDecimal("15"));
        product2.setCurrentNAV(new BigDecimal("150"));
        product2.setIsActive(true);
        investmentProductRepository.save(product2);
    }

    @Test
    @DisplayName("Should save and find portfolio by userId and investmentProductId (using reflection for userId)")
    void shouldFindByUserIdAndInvestmentProductId() throws Exception {
        Portfolio portfolio = new Portfolio();
        portfolio.setInvestmentProduct(product1);
        portfolio.setUnitsOwned(new BigDecimal("10"));
        portfolio.setAvgPurchasePrice(new BigDecimal("100"));

        // ⚙️ Set userId manually using reflection (since entity has no setUser)
        setField(portfolio, "userId", user.getId().longValue());
        portfolioRepository.save(portfolio);

        Optional<Portfolio> found = portfolioRepository.findByUserIdAndInvestmentProductId(
                user.getId().longValue(), product1.getId().longValue());

        assertThat(found).isPresent();
        assertThat(found.get().getUnitsOwned()).isEqualTo(new BigDecimal("10"));
        assertThat(found.get().getInvestmentProduct().getName()).isEqualTo("Equity Fund A");
    }

    @Test
    @DisplayName("Should find all portfolios for a given userId (using reflection for userId)")
    void shouldFindByUserId() throws Exception {
        Portfolio p1 = new Portfolio();
        p1.setInvestmentProduct(product1);
        p1.setUnitsOwned(new BigDecimal("10"));
        p1.setAvgPurchasePrice(new BigDecimal("100"));
        setField(p1, "userId", user.getId().longValue());
        portfolioRepository.save(p1);

        Portfolio p2 = new Portfolio();
        p2.setInvestmentProduct(product2);
        p2.setUnitsOwned(new BigDecimal("20"));
        p2.setAvgPurchasePrice(new BigDecimal("110"));
        setField(p2, "userId", user.getId().longValue());
        portfolioRepository.save(p2);

        List<Portfolio> result = portfolioRepository.findByUserId(user.getId().longValue());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(p -> p.getInvestmentProduct().getName())
                .containsExactlyInAnyOrder("Equity Fund A", "Growth Stock B");
    }

    @Test
    @DisplayName("Should return empty Optional when portfolio not found by user and product")
    void shouldReturnEmptyWhenNotFoundByUserAndProduct() {
        Optional<Portfolio> result = portfolioRepository.findByUserIdAndInvestmentProductId(999L, 999L);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list when user has no portfolios")
    void shouldReturnEmptyListWhenNoPortfoliosForUser() {
        List<Portfolio> result = portfolioRepository.findByUserId(999L);
        assertThat(result).isEmpty();
    }

    // Helper method: safely set private field value via reflection
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
