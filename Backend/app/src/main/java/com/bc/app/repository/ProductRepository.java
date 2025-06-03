package com.bc.app.repository;

import com.bc.app.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByBranchId(Long branchId);
    List<Product> findByBranchIdAndActive(Long branchId, boolean active);
    Page<Product> findByBranchId(Long branchId, Pageable pageable);
    boolean existsBySku(String sku);
    boolean existsBySkuAndBranchId(String sku, Long branchId);
    
    List<Product> findByOrganizationId(Long organizationId);
    List<Product> findByOrganizationIdAndActive(Long organizationId, boolean active);
    Page<Product> findByOrganizationId(Long organizationId, Pageable pageable);
    Page<Product> findByOrganizationIdAndActive(Long organizationId, boolean active, Pageable pageable);
    
    List<Product> findByOrganizationIdAndBranchId(Long organizationId, Long branchId);
    List<Product> findByOrganizationIdAndBranchIdAndActive(Long organizationId, Long branchId, boolean active);
    
    @Query("SELECT p FROM Product p WHERE p.organization.id = :organizationId AND p.name LIKE %:name%")
    List<Product> findByOrganizationIdAndNameContaining(@Param("organizationId") Long organizationId, @Param("name") String name);
    
    @Query("SELECT p FROM Product p WHERE p.organization.id = :organizationId AND p.category.id = :categoryId")
    List<Product> findByOrganizationIdAndCategoryId(@Param("organizationId") Long organizationId, @Param("categoryId") Long categoryId);
    
    boolean existsBySkuAndOrganizationId(String sku, Long organizationId);
    boolean existsBySkuAndOrganizationIdAndIdNot(String sku, Long organizationId, Long id);
    
    Optional<Product> findByIdAndOrganizationId(Long id, Long organizationId);
} 