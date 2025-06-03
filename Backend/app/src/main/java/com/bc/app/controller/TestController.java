package com.bc.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/all")
    public ResponseEntity<Map<String, String>> allEndpoint() {
        return ResponseEntity.ok(Map.of(
                "message", "This endpoint is accessible to all users"
        ));
    }

    // Only DEV has system-level access to all test endpoints
    @GetMapping("/dev-only")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<Map<String, String>> devOnlyEndpoint() {
        return ResponseEntity.ok(Map.of(
            "message", "This endpoint is accessible only to DEV role users",
            "access_level", "SYSTEM_DEV"
        ));
    }

    // Any authenticated user (Regular users need organization context)
    @GetMapping("/authenticated")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> authenticatedEndpoint() {
        return ResponseEntity.ok(Map.of(
            "message", "This endpoint is accessible to any authenticated user",
            "access_level", "AUTHENTICATED"
        ));
    }

    // Organization-based access - any organization member can access
    @GetMapping("/organizations/{organizationId}/basic")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.canViewOrganization(#organizationId)")
    public ResponseEntity<Map<String, String>> organizationBasicEndpoint(@PathVariable Long organizationId) {
        return ResponseEntity.ok(Map.of(
            "message", "This endpoint is accessible to DEV or organization members",
            "organizationId", organizationId.toString(),
            "access_level", "ORG_MEMBER"
        ));
    }

    // Organization managers and above
    @GetMapping("/organizations/{organizationId}/manager")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.isOrganizationManager(#organizationId)")
    public ResponseEntity<Map<String, String>> organizationManagerEndpoint(@PathVariable Long organizationId) {
        return ResponseEntity.ok(Map.of(
            "message", "This endpoint requires organization MANAGER role or above",
            "organizationId", organizationId.toString(),
            "access_level", "ORG_MANAGER+"
        ));
    }

    // Organization owners and admins only
    @GetMapping("/organizations/{organizationId}/admin")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.isOrganizationOwnerOrAdmin(#organizationId)")
    public ResponseEntity<Map<String, String>> organizationAdminEndpoint(@PathVariable Long organizationId) {
        return ResponseEntity.ok(Map.of(
            "message", "This endpoint requires organization ADMIN or OWNER role",
            "organizationId", organizationId.toString(),
            "access_level", "ORG_ADMIN+"
        ));
    }

    // Organization owners only
    @GetMapping("/organizations/{organizationId}/owner")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.hasOrganizationRole(#organizationId, 'OWNER')")
    public ResponseEntity<Map<String, String>> organizationOwnerEndpoint(@PathVariable Long organizationId) {
        return ResponseEntity.ok(Map.of(
            "message", "This endpoint requires organization OWNER role",
            "organizationId", organizationId.toString(),
            "access_level", "ORG_OWNER"
        ));
    }

    // Hierarchical permission test
    @GetMapping("/organizations/{organizationId}/hierarchical/{level}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.hasOrganizationPermissionLevel(#organizationId, #level)")
    public ResponseEntity<Map<String, String>> hierarchicalEndpoint(
            @PathVariable Long organizationId, 
            @PathVariable String level) {
        return ResponseEntity.ok(Map.of(
            "message", "This endpoint requires minimum " + level + " permission in organization",
            "organizationId", organizationId.toString(),
            "requiredLevel", level,
            "access_level", "ORG_HIERARCHICAL"
        ));
    }

    // Test organization context from headers/parameters
    @GetMapping("/org-context-test")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.canViewOrganization(@organizationContextResolver.resolveOrganizationId().orElse(0L))")
    public ResponseEntity<Map<String, String>> orgContextEndpoint() {
        return ResponseEntity.ok(Map.of(
            "message", "This endpoint uses organization context from headers/parameters",
            "note", "Send X-Organization-Id header or organizationId parameter",
            "access_level", "ORG_CONTEXT"
        ));
    }
} 