// java
package com.zeta.backend.repository;

import com.zeta.backend.enums.InvestmentType;
import com.zeta.backend.enums.RiskLevel;
import com.zeta.backend.models.InvestmentProduct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class InvestmentProductRepositoryTest {
    @Autowired
    private InvestmentProductRepository repository;

    // CRUD - Create
    @Test
    void shouldCreateProduct() {
        InvestmentProduct product = buildProduct("Test", true);
        InvestmentProduct saved = repository.save(product);
        assertThat(saved.getId()).isNotNull();
    }

    // CRUD - Read
    @Test
    void shouldReadProduct() {
        InvestmentProduct saved = repository.save(buildProduct("Test", true));
        InvestmentProduct found = repository.findById(saved.getId()).get();
        assertThat(found.getName()).isEqualTo("Test");
    }

    // CRUD - Update
    @Test
    void shouldUpdateProduct() {
        InvestmentProduct saved = repository.save(buildProduct("Test", true));
        saved.setName("Updated");
        repository.save(saved);
        assertThat(repository.findById(saved.getId()).get().getName()).isEqualTo("Updated");
    }

    // CRUD - Delete (Deactivate)
    @Test
    void shouldDeactivateProduct() {
        InvestmentProduct saved = repository.save(buildProduct("Test", true));
        saved.setIsActive(false);
        repository.save(saved);
        assertThat(repository.findById(saved.getId()).get().getIsActive()).isFalse();
    }

    // Visibility - isActive
    @Test
    void shouldReturnOnlyActiveProducts() {
        repository.save(buildProduct("Active", true));
        repository.save(buildProduct("Inactive", false));
        List<InvestmentProduct> active = repository.findByIsActiveTrue();
        assertThat(active).hasSize(1);
        assertThat(active.get(0).getIsActive()).isTrue();
    }

    private InvestmentProduct buildProduct(String name, boolean isActive) {
        return InvestmentProduct.builder()
                .name(name)
                .type(InvestmentType.STOCK)
                .riskLevel(RiskLevel.MEDIUM)
                .minInvestment(new BigDecimal("10000"))
                .expectedReturnRate(new BigDecimal("12.5"))
                .currentNAV(new BigDecimal("100"))
                .isActive(isActive)
                .build();
    }
}
