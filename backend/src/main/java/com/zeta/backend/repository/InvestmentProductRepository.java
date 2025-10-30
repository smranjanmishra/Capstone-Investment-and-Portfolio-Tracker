package com.zeta.backend.repository;

import com.zeta.backend.models.InvestmentProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestmentProductRepository extends JpaRepository<InvestmentProduct, Long> {
    List<InvestmentProduct> findByIsActiveTrue();
    Optional<InvestmentProduct> findByName(String name);
    boolean existsByName(String name);
}