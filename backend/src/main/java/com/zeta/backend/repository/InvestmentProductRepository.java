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

    boolean existsByName(String name);
}