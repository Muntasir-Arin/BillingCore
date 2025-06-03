package com.bc.app.service;

import com.bc.app.dto.product.ProductDTO;
import com.bc.app.dto.product.CreateProductRequest;
import com.bc.app.dto.product.UpdateProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductDTO createProduct(CreateProductRequest request);
    ProductDTO updateProduct(Long id, UpdateProductRequest request);
    void deleteProduct(Long id);
    ProductDTO getProduct(Long id);
    List<ProductDTO> getProductsByBranch(Long branchId);
    List<ProductDTO> getActiveProductsByBranch(Long branchId);
    Page<ProductDTO> getProductsByBranch(Long branchId, Pageable pageable);
    void updateStock(Long id, Integer quantity);
    void activateProduct(Long id);
    void deactivateProduct(Long id);
} 