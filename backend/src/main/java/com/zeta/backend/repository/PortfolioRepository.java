package com.zeta.backend.repository;

import com.zeta.backend.models.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    Optional<Portfolio> findByUserIdAndInvestmentProductId(Long userId, Long investmentProductId);
    List<Portfolio> findByUserId(Long userId);
}