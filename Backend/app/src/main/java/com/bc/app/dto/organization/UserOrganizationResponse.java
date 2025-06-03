package com.bc.app.dto.organization;

import com.bc.app.model.UserOrganization.OrgRole;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Builder
public class UserOrganizationResponse {
    private Long id;
    private Long userId;
    private String username;
    private String userEmail;
    private Long organizationId;
    private String organizationName;
    private OrgRole roleInOrg;
    private boolean active;
    private LocalDateTime joinedAt;
} 