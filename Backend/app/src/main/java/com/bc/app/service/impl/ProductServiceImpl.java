package com.bc.app.service.impl;

import com.bc.app.dto.product.ProductDTO;
import com.bc.app.dto.product.CreateProductRequest;
import com.bc.app.dto.product.UpdateProductRequest;
import com.bc.app.exception.BusinessException;
import com.bc.app.exception.ResourceNotFoundException;
import com.bc.app.model.Branch;
import com.bc.app.model.Product;
import com.bc.app.repository.BranchRepository;
import com.bc.app.repository.ProductRepository;
import com.bc.app.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;

    @Override
    @Transactional
    public ProductDTO createProduct(CreateProductRequest request) {
        if (productRepository.existsBySkuAndBranchId(request.getSku(), request.getBranchId())) {
            throw new BusinessException("SKU already exists in this branch");
        }

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + request.getBranchId()));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setBranch(branch);
        product.setActive(true);

        return mapToDTO(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (request.getName() != null) {
            product.setName(request.getName());
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        if (request.getSku() != null && !request.getSku().equals(product.getSku())) {
            if (productRepository.existsBySkuAndBranchId(request.getSku(), product.getBranch().getId())) {
                throw new BusinessException("SKU already exists in this branch");
            }
            product.setSku(request.getSku());
        }

        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }

        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }

        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }

        return mapToDTO(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    @Override
    public ProductDTO getProduct(Long id) {
        return productRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    public List<ProductDTO> getProductsByBranch(Long branchId) {
        return productRepository.findByBranchId(branchId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getActiveProductsByBranch(Long branchId) {
        return productRepository.findByBranchIdAndActive(branchId, true).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDTO> getProductsByBranch(Long branchId, Pageable pageable) {
        return productRepository.findByBranchId(branchId, pageable)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional
    public void updateStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        int newQuantity = product.getStockQuantity() + quantity;
        if (newQuantity < 0) {
            throw new BusinessException("Insufficient stock");
        }

        product.setStockQuantity(newQuantity);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void activateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setActive(true);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setActive(false);
        productRepository.save(product);
    }

    private ProductDTO mapToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setSku(product.getSku());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setActive(product.isActive());
        dto.setBranchId(product.getBranch().getId());
        dto.setBranchName(product.getBranch().getName());
        return dto;
    }
} 