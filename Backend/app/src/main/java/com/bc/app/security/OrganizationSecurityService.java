package com.bc.app.security;

import com.bc.app.model.User;
import com.bc.app.model.UserOrganization;
import com.bc.app.repository.UserOrganizationRepository;
import com.bc.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service("orgSecurity")
@RequiredArgsConstructor
public class OrganizationSecurityService {

    private final UserRepository userRepository;
    private final UserOrganizationRepository userOrganizationRepository;

    /**
     * Check if current user has any of the specified roles in the given organization
     */
    public boolean hasOrganizationRole(Long organizationId, String... roles) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        // DEV role has access to everything across all organizations
        if (currentUser.getRole().getName().name().equals("DEV")) {
            return true;
        }

        Optional<UserOrganization> userOrg = userOrganizationRepository
                .findByUserIdAndOrganizationIdAndActiveTrue(currentUser.getId(), organizationId);

        if (userOrg.isEmpty()) {
            return false;
        }

        String userRole = userOrg.get().getRoleInOrg().name();
        return Arrays.asList(roles).contains(userRole);
    }

    /**
     * Check if current user has organization owner or admin role
     */
    public boolean isOrganizationOwnerOrAdmin(Long organizationId) {
        return hasOrganizationRole(organizationId, "OWNER", "ADMIN");
    }

    /**
     * Check if current user has organization management role (owner, admin, or manager)
     */
    public boolean isOrganizationManager(Long organizationId) {
        return hasOrganizationRole(organizationId, "OWNER", "ADMIN", "MANAGER");
    }

    /**
     * Check if current user can view organization data
     */
    public boolean canViewOrganization(Long organizationId) {
        return hasOrganizationRole(organizationId, "OWNER", "ADMIN", "MANAGER", "EMPLOYEE", "VIEWER");
    }

    /**
     * Check if current user belongs to the organization
     */
    public boolean belongsToOrganization(Long organizationId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        // DEV role has access to all organizations
        if (currentUser.getRole().getName().name().equals("DEV")) {
            return true;
        }

        return userOrganizationRepository.existsByUserIdAndOrganizationIdAndActiveTrue(
                currentUser.getId(), organizationId);
    }

    /**
     * Get current user's role in the specified organization
     */
    public Optional<String> getUserRoleInOrganization(Long organizationId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Optional.empty();
        }

        // DEV role is treated as having OWNER role in all organizations
        if (currentUser.getRole().getName().name().equals("DEV")) {
            return Optional.of("OWNER");
        }

        Optional<UserOrganization> userOrg = userOrganizationRepository
                .findByUserIdAndOrganizationIdAndActiveTrue(currentUser.getId(), organizationId);

        return userOrg.map(uo -> uo.getRoleInOrg().name());
    }

    /**
     * Check if current user can manage users in the organization
     */
    public boolean canManageOrganizationUsers(Long organizationId) {
        return hasOrganizationRole(organizationId, "OWNER", "ADMIN");
    }

    /**
     * Check if current user can edit organization settings
     */
    public boolean canEditOrganization(Long organizationId) {
        return hasOrganizationRole(organizationId, "OWNER");
    }

    /**
     * Get current authenticated user
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username).orElse(null);
    }

    /**
     * Check if user has hierarchical permission (e.g., OWNER > ADMIN > MANAGER > EMPLOYEE > VIEWER)
     */
    public boolean hasOrganizationPermissionLevel(Long organizationId, String requiredLevel) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        // DEV role has all permissions
        if (currentUser.getRole().getName().name().equals("DEV")) {
            return true;
        }

        Optional<UserOrganization> userOrg = userOrganizationRepository
                .findByUserIdAndOrganizationIdAndActiveTrue(currentUser.getId(), organizationId);

        if (userOrg.isEmpty()) {
            return false;
        }

        String userRole = userOrg.get().getRoleInOrg().name();
        return hasPermissionLevel(userRole, requiredLevel);
    }

    /**
     * Check if userRole has permission level equal or higher than requiredLevel
     */
    private boolean hasPermissionLevel(String userRole, String requiredLevel) {
        List<String> hierarchy = Arrays.asList("VIEWER", "EMPLOYEE", "MANAGER", "ADMIN", "OWNER");
        
        int userLevel = hierarchy.indexOf(userRole);
        int requiredLevelIndex = hierarchy.indexOf(requiredLevel);
        
        return userLevel >= requiredLevelIndex;
    }
} 