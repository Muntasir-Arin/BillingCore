package com.bc.app.controller;

import com.bc.app.dto.product.ProductDTO;
import com.bc.app.dto.product.CreateProductRequest;
import com.bc.app.dto.product.UpdateProductRequest;
import com.bc.app.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/organizations/{organizationId}/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Organization-level security: Only organization owners and admins can create products
    @PostMapping
    @PreAuthorize("@orgSecurity.isOrganizationOwnerOrAdmin(#organizationId)")
    public ResponseEntity<ProductDTO> createProduct(
            @PathVariable Long organizationId,
            @Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    // Organization-level security: Only organization managers and above can update products
    @PutMapping("/{id}")
    @PreAuthorize("@orgSecurity.isOrganizationManager(#organizationId)")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long organizationId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    // Organization-level security: Only organization owners can delete products
    @DeleteMapping("/{id}")
    @PreAuthorize("@orgSecurity.hasOrganizationRole(#organizationId, 'OWNER')")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long organizationId,
            @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    // Organization-level security: Anyone who belongs to the organization can view products
    @GetMapping("/{id}")
    @PreAuthorize("@orgSecurity.canViewOrganization(#organizationId)")
    public ResponseEntity<ProductDTO> getProduct(
            @PathVariable Long organizationId,
            @PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    // Organization-level security: Anyone who belongs to the organization can list products
    @GetMapping
    @PreAuthorize("@orgSecurity.canViewOrganization(#organizationId)")
    public ResponseEntity<Page<ProductDTO>> getProductsByOrganization(
            @PathVariable Long organizationId,
            Pageable pageable) {
        // This would need to be implemented in ProductService
        // For now, we'll use branch-based method
        return ResponseEntity.ok(Page.empty());
    }

    // Organization-level security with hierarchical permission
    @PutMapping("/{id}/activate")
    @PreAuthorize("@orgSecurity.hasOrganizationPermissionLevel(#organizationId, 'MANAGER')")
    public ResponseEntity<Void> activateProduct(
            @PathVariable Long organizationId,
            @PathVariable Long id) {
        productService.activateProduct(id);
        return ResponseEntity.ok().build();
    }

    // Organization-level security with hierarchical permission
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("@orgSecurity.hasOrganizationPermissionLevel(#organizationId, 'MANAGER')")
    public ResponseEntity<Void> deactivateProduct(
            @PathVariable Long organizationId,
            @PathVariable Long id) {
        productService.deactivateProduct(id);
        return ResponseEntity.ok().build();
    }

    // Combined security: System role OR organization role
    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('DEV') or hasRole('OWNER') or @orgSecurity.isOrganizationManager(#organizationId)")
    public ResponseEntity<Void> updateStock(
            @PathVariable Long organizationId,
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        productService.updateStock(id, quantity);
        return ResponseEntity.ok().build();
    }

    // Alternative endpoint for branches within organization
    @GetMapping("/branches/{branchId}")
    @PreAuthorize("@orgSecurity.belongsToOrganization(#organizationId)")
    public ResponseEntity<List<ProductDTO>> getProductsByBranch(
            @PathVariable Long organizationId,
            @PathVariable Long branchId) {
        return ResponseEntity.ok(productService.getProductsByBranch(branchId));
    }
} 