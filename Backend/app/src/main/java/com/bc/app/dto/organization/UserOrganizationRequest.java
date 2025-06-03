package com.bc.app.dto.organization;

import com.bc.app.model.UserOrganization.OrgRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserOrganizationRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Organization ID is required")
    private Long organizationId;
    
    @NotNull(message = "Organization role is required")
    private OrgRole roleInOrg;
} 