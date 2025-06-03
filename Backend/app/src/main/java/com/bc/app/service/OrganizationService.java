package com.bc.app.service;

import com.bc.app.dto.organization.*;
import com.bc.app.model.Organization;

import java.util.List;

public interface OrganizationService {
    
    OrganizationResponse createOrganization(OrganizationCreateRequest request);
    
    OrganizationResponse updateOrganization(Long id, OrganizationUpdateRequest request);
    
    OrganizationResponse getOrganizationById(Long id);
    
    List<OrganizationResponse> getAllOrganizations();
    
    List<OrganizationResponse> getActiveOrganizations();
    
    List<OrganizationResponse> getOrganizationsByUserId(Long userId);
    
    void deleteOrganization(Long id);
    
    void deactivateOrganization(Long id);
    
    void activateOrganization(Long id);
    
    UserOrganizationResponse addUserToOrganization(UserOrganizationRequest request);
    
    void removeUserFromOrganization(Long userId, Long organizationId);
    
    List<UserOrganizationResponse> getOrganizationUsers(Long organizationId);
    
    List<UserOrganizationResponse> getUserOrganizations(Long userId);
    
    boolean isUserInOrganization(Long userId, Long organizationId);
    
    Organization findOrganizationById(Long id);
} 