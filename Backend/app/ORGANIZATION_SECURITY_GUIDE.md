# Organization-Level Security Guide

This guide explains how to secure endpoints using organization-level roles in addition to system-level roles.

## Overview

The application supports two types of permissions:
1. **System-Level Roles**: Direct user roles (`DEV`, `OWNER`, `MANAGER`, `EMPLOYEE`)
2. **Organization-Level Roles**: Roles within specific organizations (`OWNER`, `ADMIN`, `MANAGER`, `EMPLOYEE`, `VIEWER`)

## Organization Role Hierarchy

```
OWNER (highest)
  ↓
ADMIN
  ↓
MANAGER
  ↓
EMPLOYEE
  ↓
VIEWER (lowest)
```

## Security Components

### 1. OrganizationSecurityService (`@orgSecurity`)

Main service for organization-level security checks:

#### Basic Role Checks
```java
// Check if user has specific role(s) in organization
@PreAuthorize("@orgSecurity.hasOrganizationRole(#organizationId, 'OWNER')")
@PreAuthorize("@orgSecurity.hasOrganizationRole(#organizationId, 'OWNER', 'ADMIN')")

// Convenience methods
@PreAuthorize("@orgSecurity.isOrganizationOwnerOrAdmin(#organizationId)")
@PreAuthorize("@orgSecurity.isOrganizationManager(#organizationId)") // OWNER, ADMIN, MANAGER
@PreAuthorize("@orgSecurity.canViewOrganization(#organizationId)")   // All roles
```

#### Hierarchical Permission Checks
```java
// Check if user has minimum permission level
@PreAuthorize("@orgSecurity.hasOrganizationPermissionLevel(#organizationId, 'MANAGER')")
// This allows MANAGER, ADMIN, and OWNER
```

#### Membership Checks
```java
// Check if user belongs to organization
@PreAuthorize("@orgSecurity.belongsToOrganization(#organizationId)")

// Check specific capabilities
@PreAuthorize("@orgSecurity.canManageOrganizationUsers(#organizationId)")   // OWNER, ADMIN
@PreAuthorize("@orgSecurity.canEditOrganization(#organizationId)")          // OWNER only
```

### 2. OrganizationContextResolver

Extracts organization ID from requests:

```java
@Component
public class MyController {
    private final OrganizationContextResolver resolver;
    
    @GetMapping("/my-endpoint")
    @PreAuthorize("@orgSecurity.canViewOrganization(@organizationContextResolver.resolveOrganizationId().orElse(0L))")
    public ResponseEntity<?> myEndpoint() {
        Long orgId = resolver.resolveOrganizationId().orElse(null);
        // ... implementation
    }
}
```

## Security Patterns

### 1. Path Variable Pattern (Recommended)
```java
@GetMapping("/api/organizations/{organizationId}/products")
@PreAuthorize("@orgSecurity.canViewOrganization(#organizationId)")
public ResponseEntity<List<ProductDTO>> getProducts(@PathVariable Long organizationId) {
    // Implementation
}
```

### 2. Request Header Pattern
```java
@GetMapping("/api/products")
@PreAuthorize("@orgSecurity.canViewOrganization(@organizationContextResolver.resolveOrganizationId().orElse(0L))")
public ResponseEntity<List<ProductDTO>> getProducts() {
    // Client sends: X-Organization-Id: 123
}
```

### 3. Request Parameter Pattern
```java
@GetMapping("/api/products")
@PreAuthorize("@orgSecurity.canViewOrganization(@organizationContextResolver.resolveOrganizationId().orElse(0L))")
public ResponseEntity<List<ProductDTO>> getProducts() {
    // Client sends: GET /api/products?organizationId=123
}
```

### 4. Combined System + Organization Security
```java
@PreAuthorize("hasRole('DEV') or @orgSecurity.isOrganizationManager(#organizationId)")
@PreAuthorize("(hasRole('OWNER') and @orgSecurity.belongsToOrganization(#organizationId)) or @orgSecurity.hasOrganizationRole(#organizationId, 'OWNER')")
```

## Usage Examples

### Example 1: Product Management
```java
@RestController
@RequestMapping("/api/organizations/{organizationId}/products")
public class ProductController {

    // Create: Only org owners and admins
    @PostMapping
    @PreAuthorize("@orgSecurity.isOrganizationOwnerOrAdmin(#organizationId)")
    public ResponseEntity<ProductDTO> createProduct(@PathVariable Long organizationId, @RequestBody CreateProductRequest request) {
        // Implementation
    }

    // Update: Org managers and above
    @PutMapping("/{id}")
    @PreAuthorize("@orgSecurity.isOrganizationManager(#organizationId)")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long organizationId, @PathVariable Long id, @RequestBody UpdateProductRequest request) {
        // Implementation
    }

    // Delete: Only org owners
    @DeleteMapping("/{id}")
    @PreAuthorize("@orgSecurity.hasOrganizationRole(#organizationId, 'OWNER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long organizationId, @PathVariable Long id) {
        // Implementation
    }

    // View: Anyone in the organization
    @GetMapping("/{id}")
    @PreAuthorize("@orgSecurity.canViewOrganization(#organizationId)")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long organizationId, @PathVariable Long id) {
        // Implementation
    }
}
```

### Example 2: User Management Within Organization
```java
@RestController
@RequestMapping("/api/organizations/{organizationId}/users")
public class OrganizationUserController {

    // Add user to organization: Only owners and admins
    @PostMapping
    @PreAuthorize("@orgSecurity.canManageOrganizationUsers(#organizationId)")
    public ResponseEntity<UserDTO> addUserToOrganization(@PathVariable Long organizationId, @RequestBody AddUserRequest request) {
        // Implementation
    }

    // Change user role: Only owners and admins
    @PutMapping("/{userId}/role")
    @PreAuthorize("@orgSecurity.canManageOrganizationUsers(#organizationId)")
    public ResponseEntity<Void> changeUserRole(@PathVariable Long organizationId, @PathVariable Long userId, @RequestBody ChangeRoleRequest request) {
        // Implementation
    }

    // List users: Managers and above
    @GetMapping
    @PreAuthorize("@orgSecurity.hasOrganizationPermissionLevel(#organizationId, 'MANAGER')")
    public ResponseEntity<List<UserDTO>> getOrganizationUsers(@PathVariable Long organizationId) {
        // Implementation
    }
}
```

### Example 3: Mixed Security (System + Organization)
```java
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    // System DEV can access all, or organization owners can access their org's reports
    @GetMapping("/organizations/{organizationId}/financial")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.hasOrganizationRole(#organizationId, 'OWNER')")
    public ResponseEntity<FinancialReportDTO> getFinancialReport(@PathVariable Long organizationId) {
        // Implementation
    }

    // System OWNER role + belongs to org, OR organization manager
    @GetMapping("/organizations/{organizationId}/operational")
    @PreAuthorize("(hasRole('OWNER') and @orgSecurity.belongsToOrganization(#organizationId)) or @orgSecurity.isOrganizationManager(#organizationId)")
    public ResponseEntity<OperationalReportDTO> getOperationalReport(@PathVariable Long organizationId) {
        // Implementation
    }
}
```

## Client Usage

### Using Path Variables (Recommended)
```javascript
// Clearly indicates which organization the request is for
GET /api/organizations/123/products
POST /api/organizations/123/branches
PUT /api/organizations/123/customers/456
```

### Using Headers
```javascript
fetch('/api/products', {
    headers: {
        'Authorization': 'Bearer ' + token,
        'X-Organization-Id': '123'  // Organization context
    }
});
```

### Using Parameters
```javascript
GET /api/customers?organizationId=123
POST /api/invoices?organizationId=123
```

## Best Practices

### 1. Use Path Variables for Organization Context
- Clearest intent
- Easy to extract
- RESTful design

### 2. Choose Appropriate Permission Level
```java
// Too restrictive - only owners can view
@PreAuthorize("@orgSecurity.hasOrganizationRole(#organizationId, 'OWNER')")

// Better - all organization members can view
@PreAuthorize("@orgSecurity.canViewOrganization(#organizationId)")

// Hierarchical - managers and above
@PreAuthorize("@orgSecurity.hasOrganizationPermissionLevel(#organizationId, 'MANAGER')")
```

### 3. Combine with System Roles When Needed
```java
// System DEV always has access, plus organization-specific permissions
@PreAuthorize("hasRole('DEV') or @orgSecurity.isOrganizationManager(#organizationId)")
```

### 4. Fail Gracefully
```java
@GetMapping("/org-context")
@PreAuthorize("@orgSecurity.canViewOrganization(@organizationContextResolver.resolveOrganizationId().orElse(0L))")
public ResponseEntity<List<DataDTO>> getData() {
    Long orgId = organizationContextResolver.resolveOrganizationId().orElse(null);
    if (orgId == null) {
        return ResponseEntity.badRequest().build(); // Clear error for missing org context
    }
    // ... implementation
}
```

## Security Notes

1. **DEV Role Override**: Users with system `DEV` role have access to all organizations
2. **Null Safety**: Always handle cases where organization ID cannot be resolved
3. **Performance**: Organization security checks query the database, consider caching for high-traffic endpoints
4. **Validation**: Always validate that resources actually belong to the specified organization in your service layer

## Testing Organization Security

```java
@Test
@WithMockUser(username = "user1", roles = {"EMPLOYEE"})
void testOrganizationAccess() {
    // Setup user with MANAGER role in organization 1
    setupUserInOrganization("user1", 1L, OrganizationRole.MANAGER);
    
    // Should allow access
    mvc.perform(get("/api/organizations/1/products"))
       .andExpect(status().isOk());
    
    // Should deny access to different organization
    mvc.perform(get("/api/organizations/2/products"))
       .andExpect(status().isForbidden());
}
```

This organization-level security system provides fine-grained access control while maintaining flexibility and performance. 