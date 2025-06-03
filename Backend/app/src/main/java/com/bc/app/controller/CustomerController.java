package com.bc.app.controller;

import com.bc.app.dto.customer.CustomerDTO;
import com.bc.app.dto.customer.CreateCustomerRequest;
import com.bc.app.dto.customer.UpdateCustomerRequest;
import com.bc.app.security.OrganizationContextResolver;
import com.bc.app.service.CustomerService;
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
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final OrganizationContextResolver organizationContextResolver;

    @PostMapping
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @Valid @RequestBody UpdateCustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomer(id));
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CustomerDTO>> getCustomersByBranch(@PathVariable Long branchId) {
        return ResponseEntity.ok(customerService.getCustomersByBranch(branchId));
    }

    @GetMapping("/branch/{branchId}/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CustomerDTO>> getActiveCustomersByBranch(@PathVariable Long branchId) {
        return ResponseEntity.ok(customerService.getActiveCustomersByBranch(branchId));
    }

    @GetMapping("/branch/{branchId}/page")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<CustomerDTO>> getCustomersByBranch(@PathVariable Long branchId, Pageable pageable) {
        return ResponseEntity.ok(customerService.getCustomersByBranch(branchId, pageable));
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<Void> activateCustomer(@PathVariable Long id) {
        customerService.activateCustomer(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<Void> deactivateCustomer(@PathVariable Long id) {
        customerService.deactivateCustomer(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/org-context")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.canViewOrganization(@organizationContextResolver.resolveOrganizationId().orElse(0L))")
    public ResponseEntity<List<CustomerDTO>> getCustomersWithOrgContext() {
        Long orgId = organizationContextResolver.resolveOrganizationId().orElse(null);
        if (orgId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(List.of());
    }

    @PostMapping("/org-context")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.isOrganizationManager(@organizationContextResolver.resolveOrganizationId().orElse(0L))")
    public ResponseEntity<CustomerDTO> createCustomerWithOrgContext(@Valid @RequestBody CreateCustomerRequest request) {
        Long orgId = organizationContextResolver.resolveOrganizationId().orElse(null);
        if (orgId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @GetMapping("/organizations/{organizationId}/list")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.canViewOrganization(#organizationId)")
    public ResponseEntity<List<CustomerDTO>> getCustomersByOrganization(@PathVariable Long organizationId) {
        return ResponseEntity.ok(List.of());
    }

    @PostMapping("/organizations/{organizationId}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.isOrganizationManager(#organizationId)")
    public ResponseEntity<CustomerDTO> createCustomerInOrganization(
            @PathVariable Long organizationId,
            @Valid @RequestBody CreateCustomerRequest request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @PutMapping("/organizations/{organizationId}/customers/{customerId}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.hasOrganizationPermissionLevel(#organizationId, 'EMPLOYEE')")
    public ResponseEntity<CustomerDTO> updateCustomerInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long customerId,
            @Valid @RequestBody UpdateCustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(customerId, request));
    }

    @DeleteMapping("/organizations/{organizationId}/customers/{customerId}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.hasOrganizationRole(#organizationId, 'OWNER', 'ADMIN')")
    public ResponseEntity<Void> deleteCustomerInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/organizations/{organizationId}/customers/{customerId}/details")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.canViewOrganization(#organizationId)")
    public ResponseEntity<CustomerDTO> getCustomerDetailsInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getCustomer(customerId));
    }

    @PostMapping("/organizations/{organizationId}/customers/{customerId}/activate")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.hasOrganizationPermissionLevel(#organizationId, 'MANAGER')")
    public ResponseEntity<Void> activateCustomerInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long customerId) {
        customerService.activateCustomer(customerId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/organizations/{organizationId}/customers/{customerId}/deactivate")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.hasOrganizationPermissionLevel(#organizationId, 'MANAGER')")
    public ResponseEntity<Void> deactivateCustomerInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long customerId) {
        customerService.deactivateCustomer(customerId);
        return ResponseEntity.ok().build();
    }
} 