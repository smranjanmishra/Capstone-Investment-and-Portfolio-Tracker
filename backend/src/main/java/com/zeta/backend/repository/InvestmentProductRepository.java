package com.zeta.backend.repository;

import com.zeta.backend.enums.InvestmentType;
import com.zeta.backend.enums.RiskLevel;
import com.zeta.backend.models.InvestmentProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestmentProductRepository extends JpaRepository<InvestmentProduct, Long> {
    List<InvestmentProduct> findByIsActiveTrue();
    List<InvestmentProduct> findAll();
    List<InvestmentProduct> findByType(InvestmentType type);
    List<InvestmentProduct> findByTypeAndIsActiveTrue(InvestmentType type);
    List<InvestmentProduct> findByRiskLevel(RiskLevel riskLevel);
    List<InvestmentProduct> findByRiskLevelAndIsActiveTrue(RiskLevel riskLevel);
    Optional<InvestmentProduct> findByName(String name);
    List<InvestmentProduct> findByNameContainingIgnoreCase(String keyword);

    @Query("SELECT ip FROM InvestmentProduct ip WHERE ip.expectedReturnRate BETWEEN :minRate AND :maxRate AND ip.isActive = true")
    List<InvestmentProduct> findByReturnRateRange(
            @Param("minRate") java.math.BigDecimal minRate,
            @Param("maxRate") java.math.BigDecimal maxRate
    );

    @Query("SELECT ip FROM InvestmentProduct ip WHERE " +
            "(:type IS NULL OR ip.type = :type) AND " +
            "(:riskLevel IS NULL OR ip.riskLevel = :riskLevel) AND " +
            "ip.isActive = true")
    List<InvestmentProduct> findByFilters(
            @Param("type") InvestmentType type,
            @Param("riskLevel") RiskLevel riskLevel
    );

    boolean existsByName(String name);

    @Query("SELECT CASE WHEN COUNT(ip) > 0 THEN true ELSE false END FROM InvestmentProduct ip WHERE ip.id = :id AND ip.isActive = true")
    boolean existsByIdAndIsActiveTrue(@Param("id") Long id);
    long countByIsActiveTrue();
    long countByType(InvestmentType type);
}
