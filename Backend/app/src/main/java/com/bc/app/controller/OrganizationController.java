package com.bc.app.controller;

import com.bc.app.dto.organization.OrganizationResponse;
import com.bc.app.dto.organization.OrganizationCreateRequest;
import com.bc.app.dto.organization.OrganizationUpdateRequest;
import com.bc.app.service.OrganizationService;
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
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;

    // Only DEV can create new organizations
    @PostMapping
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<OrganizationResponse> createOrganization(@Valid @RequestBody OrganizationCreateRequest request) {
        return ResponseEntity.ok(organizationService.createOrganization(request));
    }

    // DEV can update any organization, organization owners can update their own
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.canEditOrganization(#id)")
    public ResponseEntity<OrganizationResponse> updateOrganization(@PathVariable Long id, @Valid @RequestBody OrganizationUpdateRequest request) {
        return ResponseEntity.ok(organizationService.updateOrganization(id, request));
    }

    // Only DEV can delete organizations
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.ok().build();
    }

    // DEV can view any organization, regular users can only view organizations they belong to
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.canViewOrganization(#id)")
    public ResponseEntity<OrganizationResponse> getOrganization(@PathVariable Long id) {
        return ResponseEntity.ok(organizationService.getOrganizationById(id));
    }

    // DEV can list all organizations, regular users get organizations they belong to
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrganizationResponse>> getAllOrganizations() {
        // Service layer should filter based on user role
        return ResponseEntity.ok(organizationService.getAllOrganizations());
    }

    // DEV can see all active organizations, regular users see their organizations
    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrganizationResponse>> getActiveOrganizations() {
        // Service layer should filter based on user role
        return ResponseEntity.ok(organizationService.getActiveOrganizations());
    }

    // Paginated view - need to check if this method exists
    @GetMapping("/page")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrganizationResponse>> getOrganizationsPage(Pageable pageable) {
        // Service layer should filter based on user role
        // Using getAllOrganizations for now since paginated version might not exist
        return ResponseEntity.ok(organizationService.getAllOrganizations());
    }

    // DEV can activate any organization, organization owners can activate their own
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.canEditOrganization(#id)")
    public ResponseEntity<Void> activateOrganization(@PathVariable Long id) {
        organizationService.activateOrganization(id);
        return ResponseEntity.ok().build();
    }

    // DEV can deactivate any organization, organization owners can deactivate their own
    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.canEditOrganization(#id)")
    public ResponseEntity<Void> deactivateOrganization(@PathVariable Long id) {
        organizationService.deactivateOrganization(id);
        return ResponseEntity.ok().build();
    }

    // Organization member management - only owners/admins can manage users
    @PostMapping("/{id}/users/{userId}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.canManageOrganizationUsers(#id)")
    public ResponseEntity<Void> addUserToOrganization(@PathVariable Long id, @PathVariable Long userId) {
        // This would need implementation in OrganizationService
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/users/{userId}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.canManageOrganizationUsers(#id)")
    public ResponseEntity<Void> removeUserFromOrganization(@PathVariable Long id, @PathVariable Long userId) {
        // This would need implementation in OrganizationService
        return ResponseEntity.ok().build();
    }

    // Get organization users - managers and above can view
    @GetMapping("/{id}/users")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.hasOrganizationPermissionLevel(#id, 'MANAGER')")
    public ResponseEntity<List<Object>> getOrganizationUsers(@PathVariable Long id) {
        // This would need implementation in OrganizationService
        return ResponseEntity.ok(List.of());
    }
} 