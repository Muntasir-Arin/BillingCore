package com.bc.app.service.impl;

import com.bc.app.dto.organization.*;
import com.bc.app.exception.EntityNotFoundException;
import com.bc.app.exception.DuplicateEntityException;
import com.bc.app.model.Organization;
import com.bc.app.model.User;
import com.bc.app.model.UserOrganization;
import com.bc.app.repository.OrganizationRepository;
import com.bc.app.repository.UserOrganizationRepository;
import com.bc.app.repository.UserRepository;
import com.bc.app.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserOrganizationRepository userOrganizationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OrganizationResponse createOrganization(OrganizationCreateRequest request) {
        if (organizationRepository.existsByName(request.getName())) {
            throw new DuplicateEntityException("Organization with name '" + request.getName() + "' already exists");
        }

        if (request.getEmail() != null && organizationRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEntityException("Organization with email '" + request.getEmail() + "' already exists");
        }

        Organization organization = Organization.builder()
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .website(request.getWebsite())
                .logoUrl(request.getLogoUrl())
                .photoUrl(request.getPhotoUrl())
                .businessRegistrationNumber(request.getBusinessRegistrationNumber())
                .taxIdentificationNumber(request.getTaxIdentificationNumber())
                .active(true)
                .build();

        Organization savedOrganization = organizationRepository.save(organization);
        return mapToResponse(savedOrganization);
    }

    @Override
    @Transactional
    public OrganizationResponse updateOrganization(Long id, OrganizationUpdateRequest request) {
        Organization organization = findOrganizationById(id);

        if (request.getName() != null && !request.getName().equals(organization.getName())) {
            if (organizationRepository.existsByName(request.getName())) {
                throw new DuplicateEntityException("Organization with name '" + request.getName() + "' already exists");
            }
            organization.setName(request.getName());
        }

        if (request.getEmail() != null && !request.getEmail().equals(organization.getEmail())) {
            if (organizationRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateEntityException("Organization with email '" + request.getEmail() + "' already exists");
            }
            organization.setEmail(request.getEmail());
        }

        if (request.getDescription() != null) organization.setDescription(request.getDescription());
        if (request.getAddress() != null) organization.setAddress(request.getAddress());
        if (request.getPhone() != null) organization.setPhone(request.getPhone());
        if (request.getWebsite() != null) organization.setWebsite(request.getWebsite());
        if (request.getLogoUrl() != null) organization.setLogoUrl(request.getLogoUrl());
        if (request.getPhotoUrl() != null) organization.setPhotoUrl(request.getPhotoUrl());
        if (request.getBusinessRegistrationNumber() != null) organization.setBusinessRegistrationNumber(request.getBusinessRegistrationNumber());
        if (request.getTaxIdentificationNumber() != null) organization.setTaxIdentificationNumber(request.getTaxIdentificationNumber());
        if (request.getActive() != null) organization.setActive(request.getActive());

        Organization updatedOrganization = organizationRepository.save(organization);
        return mapToResponse(updatedOrganization);
    }

    @Override
    public OrganizationResponse getOrganizationById(Long id) {
        Organization organization = findOrganizationById(id);
        return mapToResponse(organization);
    }

    @Override
    public List<OrganizationResponse> getAllOrganizations() {
        List<Organization> organizations = organizationRepository.findAll();
        return organizations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrganizationResponse> getActiveOrganizations() {
        List<Organization> organizations = organizationRepository.findByActiveTrue();
        return organizations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrganizationResponse> getOrganizationsByUserId(Long userId) {
        List<Organization> organizations = organizationRepository.findByUserId(userId);
        return organizations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteOrganization(Long id) {
        Organization organization = findOrganizationById(id);
        organizationRepository.delete(organization);
    }

    @Override
    @Transactional
    public void deactivateOrganization(Long id) {
        Organization organization = findOrganizationById(id);
        organization.setActive(false);
        organizationRepository.save(organization);
    }

    @Override
    @Transactional
    public void activateOrganization(Long id) {
        Organization organization = findOrganizationById(id);
        organization.setActive(true);
        organizationRepository.save(organization);
    }

    @Override
    @Transactional
    public UserOrganizationResponse addUserToOrganization(UserOrganizationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        Organization organization = findOrganizationById(request.getOrganizationId());

        if (userOrganizationRepository.existsByUserIdAndOrganizationIdAndActiveTrue(
                request.getUserId(), request.getOrganizationId())) {
            throw new DuplicateEntityException("User is already a member of this organization");
        }

        UserOrganization userOrganization = UserOrganization.builder()
                .user(user)
                .organization(organization)
                .roleInOrg(request.getRoleInOrg())
                .active(true)
                .build();

        UserOrganization savedUserOrganization = userOrganizationRepository.save(userOrganization);
        return mapToUserOrganizationResponse(savedUserOrganization);
    }

    @Override
    @Transactional
    public void removeUserFromOrganization(Long userId, Long organizationId) {
        UserOrganization userOrganization = userOrganizationRepository
                .findByUserIdAndOrganizationIdAndActiveTrue(userId, organizationId)
                .orElseThrow(() -> new EntityNotFoundException("User is not a member of this organization"));

        userOrganization.setActive(false);
        userOrganizationRepository.save(userOrganization);
    }

    @Override
    public List<UserOrganizationResponse> getOrganizationUsers(Long organizationId) {
        List<UserOrganization> userOrganizations = userOrganizationRepository
                .findByOrganizationIdAndActiveTrue(organizationId);
        return userOrganizations.stream()
                .map(this::mapToUserOrganizationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserOrganizationResponse> getUserOrganizations(Long userId) {
        List<UserOrganization> userOrganizations = userOrganizationRepository
                .findByUserIdAndActiveTrue(userId);
        return userOrganizations.stream()
                .map(this::mapToUserOrganizationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isUserInOrganization(Long userId, Long organizationId) {
        return userOrganizationRepository.existsByUserIdAndOrganizationIdAndActiveTrue(userId, organizationId);
    }

    @Override
    public Organization findOrganizationById(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found with id: " + id));
    }

    private OrganizationResponse mapToResponse(Organization organization) {
        return OrganizationResponse.builder()
                .id(organization.getId())
                .name(organization.getName())
                .description(organization.getDescription())
                .address(organization.getAddress())
                .phone(organization.getPhone())
                .email(organization.getEmail())
                .website(organization.getWebsite())
                .logoUrl(organization.getLogoUrl())
                .photoUrl(organization.getPhotoUrl())
                .businessRegistrationNumber(organization.getBusinessRegistrationNumber())
                .taxIdentificationNumber(organization.getTaxIdentificationNumber())
                .active(organization.isActive())
                .createdAt(organization.getCreatedAt())
                .updatedAt(organization.getUpdatedAt())
                .totalBranches(organization.getBranches() != null ? organization.getBranches().size() : 0)
                .totalUsers(organization.getUserOrganizations() != null ? 
                    (int) organization.getUserOrganizations().stream().filter(UserOrganization::isActive).count() : 0)
                .build();
    }

    private UserOrganizationResponse mapToUserOrganizationResponse(UserOrganization userOrganization) {
        return UserOrganizationResponse.builder()
                .id(userOrganization.getId())
                .userId(userOrganization.getUser().getId())
                .username(userOrganization.getUser().getUsername())
                .userEmail(userOrganization.getUser().getEmail())
                .organizationId(userOrganization.getOrganization().getId())
                .organizationName(userOrganization.getOrganization().getName())
                .roleInOrg(userOrganization.getRoleInOrg())
                .active(userOrganization.isActive())
                .joinedAt(userOrganization.getJoinedAt())
                .build();
    }
} 