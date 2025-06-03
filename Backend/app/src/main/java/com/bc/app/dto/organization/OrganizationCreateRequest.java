package com.bc.app.dto.organization;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrganizationCreateRequest {
    
    @NotBlank(message = "Organization name is required")
    @Size(max = 255, message = "Organization name must not exceed 255 characters")
    private String name;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    private String phone;
    
    @Email(message = "Email should be valid")
    private String email;
    
    private String website;
    
    private String logoUrl;
    
    private String photoUrl;
    
    private String businessRegistrationNumber;
    
    private String taxIdentificationNumber;
} 