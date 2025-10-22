package com.zeta.backend.service;

import com.zeta.backend.dto.InvestmentProductRequestDTO;
import com.zeta.backend.dto.InvestmentProductResponseDTO;
import com.zeta.backend.enums.InvestmentType;
import com.zeta.backend.enums.RiskLevel;
import com.zeta.backend.exceptions.InvalidInputException;
import com.zeta.backend.exceptions.ResourceNotFoundException;
import com.zeta.backend.models.InvestmentProduct;
import com.zeta.backend.repository.InvestmentProductRepository;
import com.zeta.backend.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor

public class InvestmentServiceImpl implements InvestmentService {
    // Dependencies injected via constructor (best practice)
    private final InvestmentProductRepository investmentRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<InvestmentProductResponseDTO> getAllActiveInvestments() {
        log.info("Fetching all active investment products");

        List<InvestmentProduct> products = investmentRepository.findByIsActiveTrue();

        log.debug("Found {} active investment products", products.size());

        return products.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestmentProductResponseDTO> getAllInvestments() {
        log.info("Fetching all investment products (admin operation)");

        List<InvestmentProduct> products = investmentRepository.findAll();

        log.debug("Found {} total investment products", products.size());

        return products.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InvestmentProductResponseDTO getInvestmentById(Long id) {
        log.info("Fetching investment product with ID: {}", id);

        InvestmentProduct product = investmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Investment product not found with ID: {}", id);
                    return new ResourceNotFoundException("InvestmentProduct", "id", id);
                });

        log.debug("Found investment product: {}", product.getName());

        return convertToResponseDTO(product);
    }

    @Override
    public InvestmentProductResponseDTO createInvestment(InvestmentProductRequestDTO requestDTO) {
        log.info("Creating new investment product: {}", requestDTO.getName());

        // Validate business rules
        ValidationUtil.validateInvestmentProductRequest(requestDTO);

        // Check for duplicate name
        if (investmentRepository.existsByName(requestDTO.getName())) {
            log.error("Investment product with name '{}' already exists", requestDTO.getName());
            throw new InvalidInputException(
                    "name",
                    requestDTO.getName(),
                    "Investment product with this name already exists"
            );
        }

        // Convert DTO to Entity
        InvestmentProduct product = convertToEntity(requestDTO);

        // Save to database
        InvestmentProduct savedProduct = investmentRepository.save(product);

        log.info("Successfully created investment product with ID: {}", savedProduct.getId());

        return convertToResponseDTO(savedProduct);
    }

    @Override
    public InvestmentProductResponseDTO updateInvestment(Long id, InvestmentProductRequestDTO requestDTO) {
        log.info("Updating investment product with ID: {}", id);

        // Validate business rules
        ValidationUtil.validateInvestmentProductRequest(requestDTO);

        // Check if product exists
        InvestmentProduct existingProduct = investmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Investment product not found with ID: {}", id);
                    return new ResourceNotFoundException("InvestmentProduct", "id", id);
                });

        // Check for duplicate name (excluding current product)
        investmentRepository.findByName(requestDTO.getName())
                .ifPresent(product -> {
                    if (!product.getId().equals(id)) {
                        log.error("Another investment product with name '{}' already exists", requestDTO.getName());
                        throw new InvalidInputException(
                                "name",
                                requestDTO.getName(),
                                "Another investment product with this name already exists"
                        );
                    }
                });

        // Update fields
        updateEntityFromDTO(existingProduct, requestDTO);

        // Save updated product
        InvestmentProduct updatedProduct = investmentRepository.save(existingProduct);

        log.info("Successfully updated investment product with ID: {}", id);

        return convertToResponseDTO(updatedProduct);
    }

    @Override
    public void deactivateInvestment(Long id) {
        log.info("Deactivating investment product with ID: {}", id);

        InvestmentProduct product = investmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Investment product not found with ID: {}", id);
                    return new ResourceNotFoundException("InvestmentProduct", "id", id);
                });

        if (!product.getIsActive()) {
            log.warn("Investment product with ID {} is already inactive", id);
            throw new InvalidInputException(
                    "isActive",
                    false,
                    "Investment product is already inactive"
            );
        }

        product.setIsActive(false);
        investmentRepository.save(product);

        log.info("Successfully deactivated investment product with ID: {}", id);
    }

    @Override
    public void activateInvestment(Long id) {
        log.info("Activating investment product with ID: {}", id);

        InvestmentProduct product = investmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Investment product not found with ID: {}", id);
                    return new ResourceNotFoundException("InvestmentProduct", "id", id);
                });

        if (product.getIsActive()) {
            log.warn("Investment product with ID {} is already active", id);
            throw new InvalidInputException(
                    "isActive",
                    true,
                    "Investment product is already active"
            );
        }

        product.setIsActive(true);
        investmentRepository.save(product);

        log.info("Successfully activated investment product with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestmentProductResponseDTO> getInvestmentsByType(InvestmentType type) {
        log.info("Fetching investment products of type: {}", type);

        List<InvestmentProduct> products = investmentRepository.findByTypeAndIsActiveTrue(type);

        log.debug("Found {} products of type {}", products.size(), type);

        return products.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestmentProductResponseDTO> getInvestmentsByRiskLevel(RiskLevel riskLevel) {
        log.info("Fetching investment products with risk level: {}", riskLevel);

        List<InvestmentProduct> products = investmentRepository.findByRiskLevelAndIsActiveTrue(riskLevel);

        log.debug("Found {} products with risk level {}", products.size(), riskLevel);

        return products.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestmentProductResponseDTO> searchInvestmentsByName(String keyword) {
        log.info("Searching investment products with keyword: {}", keyword);

        if (keyword == null || keyword.trim().isEmpty()) {
            log.warn("Empty search keyword provided");
            return getAllActiveInvestments();
        }

        List<InvestmentProduct> products = investmentRepository.findByNameContainingIgnoreCase(keyword);

        // Filter to show only active products
        List<InvestmentProduct> activeProducts = products.stream()
                .filter(InvestmentProduct::getIsActive)
                .collect(Collectors.toList());

        log.debug("Found {} products matching keyword '{}'", activeProducts.size(), keyword);

        return activeProducts.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveInvestmentCount() {
        log.info("Fetching count of active investment products");

        long count = investmentRepository.countByIsActiveTrue();

        log.debug("Total active investment products: {}", count);

        return count;
    }

    private InvestmentProductResponseDTO convertToResponseDTO(InvestmentProduct product) {
        InvestmentProductResponseDTO dto = modelMapper.map(product, InvestmentProductResponseDTO.class);
        dto.populateDisplayNames(); // Add user-friendly display names
        return dto;
    }

    private InvestmentProduct convertToEntity(InvestmentProductRequestDTO requestDTO) {
        return InvestmentProduct.builder()
                .name(requestDTO.getName())
                .type(requestDTO.getType())
                .riskLevel(requestDTO.getRiskLevel())
                .minInvestment(requestDTO.getMinInvestment())
                .expectedReturnRate(requestDTO.getExpectedReturnRate())
                .currentNAV(requestDTO.getCurrentNAV())
                .description(requestDTO.getDescription())
                .isActive(requestDTO.getIsActive())
                .build();
    }

    private void updateEntityFromDTO(InvestmentProduct entity, InvestmentProductRequestDTO requestDTO) {
        entity.setName(requestDTO.getName());
        entity.setType(requestDTO.getType());
        entity.setRiskLevel(requestDTO.getRiskLevel());
        entity.setMinInvestment(requestDTO.getMinInvestment());
        entity.setExpectedReturnRate(requestDTO.getExpectedReturnRate());
        entity.setCurrentNAV(requestDTO.getCurrentNAV());
        entity.setDescription(requestDTO.getDescription());

        // Only update isActive if provided
        if (requestDTO.getIsActive() != null) {
            entity.setIsActive(requestDTO.getIsActive());
        }
    }
}
