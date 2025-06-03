package com.bc.app.controller;

import com.bc.app.dto.branch.BranchDTO;
import com.bc.app.dto.branch.CreateBranchRequest;
import com.bc.app.dto.branch.UpdateBranchRequest;
import com.bc.app.service.BranchService;
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
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {
    private final BranchService branchService;

    @PostMapping
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<BranchDTO> createBranch(@Valid @RequestBody CreateBranchRequest request) {
        return ResponseEntity.ok(branchService.createBranch(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<BranchDTO> updateBranch(@PathVariable Long id, @Valid @RequestBody UpdateBranchRequest request) {
        return ResponseEntity.ok(branchService.updateBranch(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<Void> deleteBranch(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BranchDTO> getBranch(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.getBranch(id));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<BranchDTO>> getAllBranches() {
        return ResponseEntity.ok(branchService.getAllBranches());
    }

    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<BranchDTO>> getActiveBranches() {
        return ResponseEntity.ok(branchService.getActiveBranches());
    }

    @GetMapping("/page")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<BranchDTO>> getBranches(Pageable pageable) {
        return ResponseEntity.ok(branchService.getBranches(pageable));
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<Void> activateBranch(@PathVariable Long id) {
        branchService.activateBranch(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<Void> deactivateBranch(@PathVariable Long id) {
        branchService.deactivateBranch(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/organizations/{organizationId}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.canViewOrganization(#organizationId)")
    public ResponseEntity<List<BranchDTO>> getBranchesByOrganization(@PathVariable Long organizationId) {
        return ResponseEntity.ok(branchService.getAllBranches());
    }

    @PostMapping("/organizations/{organizationId}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.isOrganizationOwnerOrAdmin(#organizationId)")
    public ResponseEntity<BranchDTO> createBranchInOrganization(
            @PathVariable Long organizationId,
            @Valid @RequestBody CreateBranchRequest request) {
        return ResponseEntity.ok(branchService.createBranch(request));
    }

    @PutMapping("/organizations/{organizationId}/branches/{branchId}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.isOrganizationManager(#organizationId)")
    public ResponseEntity<BranchDTO> updateBranchInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long branchId,
            @Valid @RequestBody UpdateBranchRequest request) {
        return ResponseEntity.ok(branchService.updateBranch(branchId, request));
    }

    @DeleteMapping("/organizations/{organizationId}/branches/{branchId}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.hasOrganizationRole(#organizationId, 'OWNER')")
    public ResponseEntity<Void> deleteBranchInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long branchId) {
        branchService.deleteBranch(branchId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/organizations/{organizationId}/branches/{branchId}/activate")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.hasOrganizationPermissionLevel(#organizationId, 'ADMIN')")
    public ResponseEntity<Void> activateBranchInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long branchId) {
        branchService.activateBranch(branchId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/organizations/{organizationId}/branches/{branchId}/deactivate")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.hasOrganizationPermissionLevel(#organizationId, 'ADMIN')")
    public ResponseEntity<Void> deactivateBranchInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long branchId) {
        branchService.deactivateBranch(branchId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/organizations/{organizationId}/branches/{branchId}/details")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.canViewOrganization(#organizationId)")
    public ResponseEntity<BranchDTO> getBranchDetailsInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long branchId) {
        return ResponseEntity.ok(branchService.getBranch(branchId));
    }
} 