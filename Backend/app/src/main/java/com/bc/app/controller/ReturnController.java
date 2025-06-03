package com.bc.app.controller;

import com.bc.app.dto.return_.ReturnDTO;
import com.bc.app.dto.return_.CreateReturnRequest;
import com.bc.app.dto.return_.UpdateReturnRequest;
import com.bc.app.service.ReturnService;
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
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnController {
    private final ReturnService returnService;

    @PostMapping
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<ReturnDTO> createReturn(@Valid @RequestBody CreateReturnRequest request) {
        return ResponseEntity.ok(returnService.createReturn(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<ReturnDTO> updateReturn(@PathVariable Long id, @Valid @RequestBody UpdateReturnRequest request) {
        return ResponseEntity.ok(returnService.updateReturn(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<Void> deleteReturn(@PathVariable Long id) {
        returnService.deleteReturn(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReturnDTO> getReturn(@PathVariable Long id) {
        return ResponseEntity.ok(returnService.getReturn(id));
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReturnDTO>> getReturnsByBranch(@PathVariable Long branchId) {
        return ResponseEntity.ok(returnService.getReturnsByBranch(branchId));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReturnDTO>> getReturnsByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(returnService.getReturnsByCustomer(customerId));
    }

    @GetMapping("/branch/{branchId}/date-range")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReturnDTO>> getReturnsByDateRange(
            @PathVariable Long branchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(returnService.getReturnsByDateRange(branchId, startDate, endDate));
    }

    @GetMapping("/branch/{branchId}/page")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ReturnDTO>> getReturnsByBranch(@PathVariable Long branchId, Pageable pageable) {
        return ResponseEntity.ok(returnService.getReturnsByBranch(branchId, pageable));
    }

    @PostMapping("/{id}/status")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<Void> updateReturnStatus(@PathVariable Long id, @RequestParam String status) {
        returnService.updateReturnStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<Void> cancelReturn(@PathVariable Long id) {
        returnService.cancelReturn(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/organizations/{organizationId}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.hasOrganizationPermissionLevel(#organizationId, 'EMPLOYEE')")
    public ResponseEntity<ReturnDTO> createReturnInOrganization(
            @PathVariable Long organizationId,
            @Valid @RequestBody CreateReturnRequest request) {
        return ResponseEntity.ok(returnService.createReturn(request));
    }

    @PutMapping("/organizations/{organizationId}/returns/{returnId}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.isOrganizationManager(#organizationId)")
    public ResponseEntity<ReturnDTO> updateReturnInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long returnId,
            @Valid @RequestBody UpdateReturnRequest request) {
        return ResponseEntity.ok(returnService.updateReturn(returnId, request));
    }

    @DeleteMapping("/organizations/{organizationId}/returns/{returnId}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.isOrganizationOwnerOrAdmin(#organizationId)")
    public ResponseEntity<Void> deleteReturnInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long returnId) {
        returnService.deleteReturn(returnId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/organizations/{organizationId}/list")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.canViewOrganization(#organizationId)")
    public ResponseEntity<List<ReturnDTO>> getReturnsByOrganization(@PathVariable Long organizationId) {
        return ResponseEntity.ok(List.of());
    }

    @PostMapping("/organizations/{organizationId}/returns/{returnId}/status")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.hasOrganizationPermissionLevel(#organizationId, 'MANAGER')")
    public ResponseEntity<Void> updateReturnStatusInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long returnId,
            @RequestParam String status) {
        returnService.updateReturnStatus(returnId, status);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/organizations/{organizationId}/returns/{returnId}/cancel")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.hasOrganizationPermissionLevel(#organizationId, 'MANAGER')")
    public ResponseEntity<Void> cancelReturnInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long returnId) {
        returnService.cancelReturn(returnId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/organizations/{organizationId}/returns/{returnId}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.canViewOrganization(#organizationId)")
    public ResponseEntity<ReturnDTO> getReturnInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long returnId) {
        return ResponseEntity.ok(returnService.getReturn(returnId));
    }
} 