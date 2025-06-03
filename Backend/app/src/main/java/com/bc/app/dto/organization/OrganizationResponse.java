package com.bc.app.dto.organization;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Builder
public class OrganizationResponse {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String phone;
    private String email;
    private String website;
    private String logoUrl;
    private String photoUrl;
    private String businessRegistrationNumber;
    private String taxIdentificationNumber;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int totalBranches;
    private int totalUsers;
} 