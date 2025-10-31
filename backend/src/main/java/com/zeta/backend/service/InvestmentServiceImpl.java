package com.zeta.backend.service;

import com.zeta.backend.dto.InvestmentProductRequestDTO;
import com.zeta.backend.dto.InvestmentProductResponseDTO;
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

    private InvestmentProductResponseDTO convertToResponseDTO(InvestmentProduct product) {
        InvestmentProductResponseDTO dto = modelMapper.map(product, InvestmentProductResponseDTO.class);
        dto.populateDisplayNames();
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
