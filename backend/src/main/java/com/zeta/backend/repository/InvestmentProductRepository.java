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
    // Returns only active products for public user access
    List<InvestmentProduct> findByIsActiveTrue();

    // Returns all products including inactive - admin use only
    List<InvestmentProduct> findAll();

    // Used for duplicate name validation during create/update
    Optional<InvestmentProduct> findByName(String name);


    // Custom query for return rate range filtering - uses BETWEEN for inclusive bounds
    @Query("SELECT ip FROM InvestmentProduct ip WHERE ip.expectedReturnRate BETWEEN :minRate AND :maxRate AND ip.isActive = true")
    List<InvestmentProduct> findByReturnRateRange(
            @Param("minRate") java.math.BigDecimal minRate,
            @Param("maxRate") java.math.BigDecimal maxRate
    );

    // Dynamic filtering with NULL handling - allows filtering by type, risk, or both
    @Query("SELECT ip FROM InvestmentProduct ip WHERE " +
            "(:type IS NULL OR ip.type = :type) AND " +
            "(:riskLevel IS NULL OR ip.riskLevel = :riskLevel) AND " +
            "ip.isActive = true")
    List<InvestmentProduct> findByFilters(
            @Param("type") InvestmentType type,
            @Param("riskLevel") RiskLevel riskLevel
    );

    boolean existsByName(String name);
}