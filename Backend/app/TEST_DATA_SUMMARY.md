# Test Data Summary

## Overview
The application has been initialized with comprehensive test data including organizations, users with different roles, branches, customers, and products. This data is automatically created when the application starts.

## System Roles
- **DEV**: System Developer - Has full access to everything
- **Regular**: Regular User - Must use organization-level permissions

## Organization-Level Roles (Hierarchical)
1. **OWNER**: Full control over the organization
2. **ADMIN**: Can manage most aspects except ownership
3. **MANAGER**: Can manage employees and day-to-day operations
4. **EMPLOYEE**: Can perform basic operations
5. **VIEWER**: Read-only access

## Test Users Created

### System Admin
- **Username**: `dev`
- **Password**: `password123`
- **Role**: DEV (System Admin)
- **Access**: Full system access across all organizations

### Organization Users (TechCorp Solutions)
All users below belong to "TechCorp Solutions" organization:

- **Username**: `owner`
  - **Password**: `password123`
  - **System Role**: Regular
  - **Organization Role**: OWNER
  - **Branch**: Main Branch

- **Username**: `admin`
  - **Password**: `password123`
  - **System Role**: Regular
  - **Organization Role**: ADMIN
  - **Branch**: Main Branch

- **Username**: `manager`
  - **Password**: `password123`
  - **System Role**: Regular
  - **Organization Role**: MANAGER
  - **Branch**: Main Branch

- **Username**: `employee`
  - **Password**: `password123`
  - **System Role**: Regular
  - **Organization Role**: EMPLOYEE
  - **Branch**: Main Branch

- **Username**: `viewer`
  - **Password**: `password123`
  - **System Role**: Regular
  - **Organization Role**: VIEWER
  - **Branch**: Main Branch

### Regular User (No Organization)
- **Username**: `user`
- **Password**: `password123`
- **System Role**: Regular
- **Organization**: None

## Organization Data

### Organization: TechCorp Solutions
- **Name**: TechCorp Solutions
- **Description**: A comprehensive technology solutions company for testing purposes
- **Email**: contact@techcorp.com
- **Phone**: +1-555-0123
- **Address**: 123 Tech Street, Silicon Valley, CA 94000
- **Website**: https://techcorp.com

### Branches
1. **Main Branch**
   - **Address**: 123 Tech Street, Silicon Valley, CA 94000
   - **Phone**: +1-555-0124

2. **Sales Branch**
   - **Address**: 456 Sales Avenue, San Francisco, CA 94100
   - **Phone**: +1-555-0125

### Test Customers
1. **Acme Corporation**
   - **Email**: john.doe@acme.com
   - **Phone**: +1-555-1001
   - **Address**: 456 Business Plaza, New York, NY 10001

2. **Global Tech Ltd**
   - **Email**: contact@globaltech.com
   - **Phone**: +1-555-1002
   - **Address**: 789 Enterprise Way, Los Angeles, CA 90001

3. **Startup Inc**
   - **Email**: info@startup.com
   - **Phone**: +1-555-1003
   - **Address**: 321 Innovation Drive, Austin, TX 73301

4. **Enterprise Solutions**
   - **Email**: sales@enterprise.com
   - **Phone**: +1-555-1004
   - **Address**: 654 Corporate Center, Chicago, IL 60601

### Test Products
1. **Microsoft Office 365**
   - **SKU**: MS-OFFICE-365
   - **Price**: $99.99
   - **Purchase Price**: $79.99
   - **Stock**: 100 units
   - **Description**: Productivity suite with Word, Excel, PowerPoint

2. **Adobe Creative Suite**
   - **SKU**: ADOBE-CS-2024
   - **Price**: $599.99
   - **Purchase Price**: $499.99
   - **Stock**: 50 units
   - **Description**: Design and creativity software package

3. **Dell Laptop**
   - **SKU**: DELL-LAPTOP-001
   - **Price**: $1,299.99
   - **Purchase Price**: $999.99
   - **Stock**: 25 units
   - **Description**: High-performance business laptop

4. **iPhone 15**
   - **SKU**: IPHONE-15-001
   - **Price**: $999.99
   - **Purchase Price**: $799.99
   - **Stock**: 30 units
   - **Description**: Latest smartphone from Apple

5. **IT Consulting**
   - **SKU**: IT-CONSULT-HR
   - **Price**: $150.00/hour
   - **Purchase Price**: $100.00/hour
   - **Stock**: 999 units
   - **Description**: Professional IT consultation services

6. **Cloud Migration**
   - **SKU**: CLOUD-MIG-001
   - **Price**: $5,000.00
   - **Purchase Price**: $3,500.00
   - **Stock**: 10 units
   - **Description**: Cloud infrastructure migration service

## API Testing Examples

### Authentication
```bash
# Login as DEV user
POST /api/auth/signin
{
  "username": "dev",
  "password": "password123"
}

# Login as Organization Owner
POST /api/auth/signin
{
  "username": "owner",
  "password": "password123"
}
```

### Test Endpoints
The application includes comprehensive test endpoints at `/api/test/*` to verify security:

- `GET /api/test/dev-only` - DEV role only
- `GET /api/test/authenticated` - Any authenticated user
- `GET /api/test/organizations/{organizationId}/basic` - Organization members
- `GET /api/test/organizations/{organizationId}/manager` - Organization managers+
- `GET /api/test/organizations/{organizationId}/admin` - Organization admins+
- `GET /api/test/organizations/{organizationId}/owner` - Organization owners only

### Organization Context
You can specify organization context in three ways:
1. **Path Variable**: `/api/organizations/{organizationId}/resource`
2. **Header**: `X-Organization-Id: 1`
3. **Parameter**: `?organizationId=1`

## Security Patterns
All business endpoints use the pattern:
```java
@PreAuthorize("hasRole('DEV') or @orgSecurity.methodName(#organizationId)")
```

This allows:
- **DEV users**: Full access to everything
- **Regular users**: Access based on organization-level permissions

## Database Schema
The application uses PostgreSQL with Hibernate auto-DDL. All tables are created automatically on startup with proper foreign key relationships.

## Getting Started
1. Start the application: `./gradlew bootRun`
2. Use any of the test accounts above
3. Test API endpoints with proper authentication
4. Use organization ID `1` for TechCorp Solutions in API calls 