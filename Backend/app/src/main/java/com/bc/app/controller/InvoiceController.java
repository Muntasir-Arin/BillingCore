package com.bc.app.controller;

import com.bc.app.dto.invoice.InvoiceDTO;
import com.bc.app.dto.invoice.CreateInvoiceRequest;
import com.bc.app.dto.invoice.UpdateInvoiceRequest;
import com.bc.app.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;

    // Only DEV can create invoices without organization context
    @PostMapping
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<InvoiceDTO> createInvoice(@Valid @RequestBody CreateInvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.createInvoice(request));
    }

    // Only DEV can update invoices without organization context
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<InvoiceDTO> updateInvoice(@PathVariable Long id, @Valid @RequestBody UpdateInvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, request));
    }

    // Only DEV can delete invoices
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.ok().build();
    }

    // Any authenticated user can view invoices (service layer should filter)
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InvoiceDTO> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoice(id));
    }

    // Branch and customer based access - service layer should filter based on organization
    @GetMapping("/branch/{branchId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByBranch(@PathVariable Long branchId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByBranch(branchId));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByCustomer(customerId));
    }

    @GetMapping("/branch/{branchId}/date-range")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByDateRange(
            @PathVariable Long branchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(invoiceService.getInvoicesByDateRange(branchId, startDate, endDate));
    }

    @GetMapping("/branch/{branchId}/page")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<InvoiceDTO>> getInvoicesByBranch(@PathVariable Long branchId, Pageable pageable) {
        return ResponseEntity.ok(invoiceService.getInvoicesByBranch(branchId, pageable));
    }

    // Only DEV can update payment status without organization context
    @PutMapping("/{id}/payment-status")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<Void> updatePaymentStatus(@PathVariable Long id, @RequestParam String status) {
        invoiceService.updatePaymentStatus(id, status);
        return ResponseEntity.ok().build();
    }

    // Only DEV can cancel invoices without organization context
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<Void> cancelInvoice(@PathVariable Long id) {
        invoiceService.cancelInvoice(id);
        return ResponseEntity.ok().build();
    }

    // Organization-scoped endpoints

    /**
     * Create invoice within organization - organization managers and above
     */
    @PostMapping("/organizations/{organizationId}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.isOrganizationManager(#organizationId)")
    public ResponseEntity<InvoiceDTO> createInvoiceInOrganization(
            @PathVariable Long organizationId,
            @Valid @RequestBody CreateInvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.createInvoice(request));
    }

    /**
     * Update invoice within organization - organization managers and above
     */
    @PutMapping("/organizations/{organizationId}/invoices/{invoiceId}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.isOrganizationManager(#organizationId)")
    public ResponseEntity<InvoiceDTO> updateInvoiceInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long invoiceId,
            @Valid @RequestBody UpdateInvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.updateInvoice(invoiceId, request));
    }

    /**
     * Delete invoice within organization - only organization owners and admins
     */
    @DeleteMapping("/organizations/{organizationId}/invoices/{invoiceId}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.isOrganizationOwnerOrAdmin(#organizationId)")
    public ResponseEntity<Void> deleteInvoiceInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long invoiceId) {
        invoiceService.deleteInvoice(invoiceId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get invoices by organization - organization members can view
     */
    @GetMapping("/organizations/{organizationId}/list")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.canViewOrganization(#organizationId)")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByOrganization(@PathVariable Long organizationId) {
        // This would need implementation in InvoiceService
        return ResponseEntity.ok(List.of());
    }

    /**
     * Update payment status within organization - employees and above
     */
    @PutMapping("/organizations/{organizationId}/invoices/{invoiceId}/payment-status")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.hasOrganizationPermissionLevel(#organizationId, 'EMPLOYEE')")
    public ResponseEntity<Void> updatePaymentStatusInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long invoiceId,
            @RequestParam String status) {
        invoiceService.updatePaymentStatus(invoiceId, status);
        return ResponseEntity.ok().build();
    }

    /**
     * Cancel invoice within organization - managers and above
     */
    @PostMapping("/organizations/{organizationId}/invoices/{invoiceId}/cancel")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.hasOrganizationPermissionLevel(#organizationId, 'MANAGER')")
    public ResponseEntity<Void> cancelInvoiceInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long invoiceId) {
        invoiceService.cancelInvoice(invoiceId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get invoice details within organization
     */
    @GetMapping("/organizations/{organizationId}/invoices/{invoiceId}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.canViewOrganization(#organizationId)")
    public ResponseEntity<InvoiceDTO> getInvoiceInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long invoiceId) {
        return ResponseEntity.ok(invoiceService.getInvoice(invoiceId));
    }
} 