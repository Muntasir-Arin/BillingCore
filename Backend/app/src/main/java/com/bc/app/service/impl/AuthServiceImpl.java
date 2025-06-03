package com.bc.app.service.impl;

import com.bc.app.dto.auth.JwtResponse;
import com.bc.app.dto.auth.LoginRequest;
import com.bc.app.dto.auth.MessageResponse;
import com.bc.app.dto.auth.SignupRequest;
import com.bc.app.exception.BusinessException;
import com.bc.app.model.*;
import com.bc.app.repository.*;
import com.bc.app.security.JwtUtils;
import com.bc.app.security.UserDetailsImpl;
import com.bc.app.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final UserOrganizationRepository userOrganizationRepository;
    private final BranchRepository branchRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());

        return new JwtResponse(
            jwt,
            userPrincipal.getId(),
            userPrincipal.getUsername(),
            userPrincipal.getEmail(),
            roles,
            userPrincipal.getBranchId(),
            userPrincipal.getBranchName()
        );
    }

    @Override
    @Transactional
    public MessageResponse registerUser(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new BusinessException("Username is already taken!");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new BusinessException("Email is already in use!");
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        if (signupRequest.getRoles() == null || signupRequest.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByName(ERole.Regular)
                .orElseThrow(() -> new BusinessException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            signupRequest.getRoles().forEach(role -> {
                switch (role) {
                    case "dev":
                        Role devRole = roleRepository.findByName(ERole.DEV)
                            .orElseThrow(() -> new BusinessException("Error: Role is not found."));
                        roles.add(devRole);
                        break;
                    default:
                        Role regularRole = roleRepository.findByName(ERole.Regular)
                            .orElseThrow(() -> new BusinessException("Error: Role is not found."));
                        roles.add(regularRole);
                }
            });
        }

        user.setRole(roles.iterator().next());
        userRepository.save(user);

        return new MessageResponse("User registered successfully!");
    }

    @Override
    public MessageResponse validateToken(String token) {
        if (jwtUtils.validateJwtToken(token)) {
            return new MessageResponse("Token is valid");
        }
        throw new BusinessException("Invalid token");
    }

    @Override
    @Transactional
    public void createInitialAdmin() {
        try {
            log.info("🚀 Starting comprehensive data initialization...");

            // 1. Create system roles (DEV, Regular)
            createSystemRoles();
            
            // 2. Create test organization
            Organization testOrg = createTestOrganization();
            
            // 3. Create test branches
            List<Branch> branches = createTestBranches(testOrg);
            
            // 4. Create users with different system and organization roles
            createTestUsers(testOrg, branches);
            
            // 5. Create test customers
            createTestCustomers(testOrg, branches.get(0));
            
            // 6. Create test products
            createTestProducts(testOrg, branches.get(0));
            
            log.info("✅ Comprehensive data initialization completed successfully!");
            log.info("📋 Test Accounts Created:");
            log.info("   👑 DEV: dev/password123 (System Admin - Full Access)");
            log.info("   🏢 Org Owner: owner/password123 (Organization Owner)");
            log.info("   👔 Org Admin: admin/password123 (Organization Admin)");
            log.info("   📊 Org Manager: manager/password123 (Organization Manager)");
            log.info("   👤 Org Employee: employee/password123 (Organization Employee)");
            log.info("   👁 Org Viewer: viewer/password123 (Organization Viewer)");
            log.info("   📝 Regular User: user/password123 (No Organization)");
            log.info("🏢 Organization: TechCorp Solutions (ID: {})", testOrg.getId());
            
        } catch (Exception e) {
            log.error("❌ Error during data initialization", e);
            throw new RuntimeException("Failed to initialize data", e);
        }
    }

    private void createSystemRoles() {
        log.info("Creating system roles...");
        
        // Create DEV role
        if (!roleRepository.existsByName(ERole.DEV)) {
            Role devRole = new Role(ERole.DEV);
            roleRepository.save(devRole);
            log.info("✅ Created DEV role");
        }

        // Create Regular role
        if (!roleRepository.existsByName(ERole.Regular)) {
            Role regularRole = new Role(ERole.Regular);
            roleRepository.save(regularRole);
            log.info("✅ Created Regular role");
        }
    }

    private Organization createTestOrganization() {
        log.info("Creating test organization...");
        
        if (organizationRepository.findByName("TechCorp Solutions").isEmpty()) {
            Organization org = Organization.builder()
                    .name("TechCorp Solutions")
                    .description("A comprehensive technology solutions company for testing purposes")
                    .email("contact@techcorp.com")
                    .phone("+1-555-0123")
                    .address("123 Tech Street, Silicon Valley, CA 94000")
                    .website("https://techcorp.com")
                    .active(true)
                    .build();
            
            org = organizationRepository.save(org);
            log.info("✅ Created organization: {}", org.getName());
            return org;
        }
        
        return organizationRepository.findByName("TechCorp Solutions").get();
    }

    private List<Branch> createTestBranches(Organization org) {
        log.info("Creating test branches...");
        
        Branch mainBranch = null;
        Branch salesBranch = null;
        
        if (!branchRepository.existsByNameAndOrganizationId("Main Branch", org.getId())) {
            mainBranch = Branch.builder()
                    .name("Main Branch")
                    .address("123 Tech Street, Silicon Valley, CA 94000")
                    .phone("+1-555-0124")
                    .organization(org)
                    .active(true)
                    .build();
            branchRepository.save(mainBranch);
            log.info("✅ Created branch: Main Branch");
        } else {
            mainBranch = branchRepository.findByOrganizationId(org.getId()).stream()
                    .filter(b -> b.getName().equals("Main Branch"))
                    .findFirst().orElse(null);
        }

        if (!branchRepository.existsByNameAndOrganizationId("Sales Branch", org.getId())) {
            salesBranch = Branch.builder()
                    .name("Sales Branch")
                    .address("456 Sales Avenue, San Francisco, CA 94100")
                    .phone("+1-555-0125")
                    .organization(org)
                    .active(true)
                    .build();
            branchRepository.save(salesBranch);
            log.info("✅ Created branch: Sales Branch");
        } else {
            salesBranch = branchRepository.findByOrganizationId(org.getId()).stream()
                    .filter(b -> b.getName().equals("Sales Branch"))
                    .findFirst().orElse(null);
        }

        return List.of(mainBranch, salesBranch);
    }

    private void createTestUsers(Organization org, List<Branch> branches) {
        log.info("Creating test users...");
        
        Role devRole = roleRepository.findByName(ERole.DEV).orElseThrow();
        Role regularRole = roleRepository.findByName(ERole.Regular).orElseThrow();
        Branch mainBranch = branches.get(0);

        // 1. Create DEV user (system admin)
        createUserIfNotExists("dev", "dev@techcorp.com", 
                              devRole, null, null);

        // 2. Create organization users with different roles
        User owner = createUserIfNotExists("owner", "owner@techcorp.com", 
                                          regularRole, org, mainBranch);
        createOrgRole(owner, org, UserOrganization.OrgRole.OWNER);

        User admin = createUserIfNotExists("admin", "admin@techcorp.com", 
                                          regularRole, org, mainBranch);
        createOrgRole(admin, org, UserOrganization.OrgRole.ADMIN);

        User manager = createUserIfNotExists("manager", "manager@techcorp.com", 
                                            regularRole, org, mainBranch);
        createOrgRole(manager, org, UserOrganization.OrgRole.MANAGER);

        User employee = createUserIfNotExists("employee", "employee@techcorp.com", 
                                             regularRole, org, mainBranch);
        createOrgRole(employee, org, UserOrganization.OrgRole.EMPLOYEE);

        User viewer = createUserIfNotExists("viewer", "viewer@techcorp.com", 
                                           regularRole, org, mainBranch);
        createOrgRole(viewer, org, UserOrganization.OrgRole.VIEWER);

        // 3. Create a regular user not associated with any organization
        createUserIfNotExists("user", "user@example.com", 
                              regularRole, null, null);

        log.info("✅ Created all test users");
    }

    private User createUserIfNotExists(String username, String email,
                                      Role role, Organization org, Branch branch) {
        if (userRepository.findByUsername(username).isEmpty()) {
            User user = User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode("password123"))
                    .role(role)
                    .branch(branch)
                    .active(true)
                    .build();
            
            return userRepository.save(user);
        }
        return userRepository.findByUsername(username).get();
    }

    private void createOrgRole(User user, Organization org, UserOrganization.OrgRole role) {
        if (!userOrganizationRepository.existsByUserIdAndOrganizationIdAndActiveTrue(user.getId(), org.getId())) {
            UserOrganization userOrg = UserOrganization.builder()
                    .user(user)
                    .organization(org)
                    .roleInOrg(role)
                    .active(true)
                    .joinedAt(LocalDateTime.now())
                    .build();
            userOrganizationRepository.save(userOrg);
        }
    }

    private void createTestCustomers(Organization org, Branch branch) {
        log.info("Creating test customers...");

        createCustomerIfNotExists("Acme Corporation", "john.doe@acme.com", "+1-555-1001", 
                "456 Business Plaza, New York, NY 10001", org, branch);
        createCustomerIfNotExists("Global Tech Ltd", "contact@globaltech.com", "+1-555-1002", 
                "789 Enterprise Way, Los Angeles, CA 90001", org, branch);
        createCustomerIfNotExists("Startup Inc", "info@startup.com", "+1-555-1003", 
                "321 Innovation Drive, Austin, TX 73301", org, branch);
        createCustomerIfNotExists("Enterprise Solutions", "sales@enterprise.com", "+1-555-1004", 
                "654 Corporate Center, Chicago, IL 60601", org, branch);

        log.info("✅ Created test customers");
    }

    private void createCustomerIfNotExists(String name, String email, String phone, String address,
                                          Organization org, Branch branch) {
        if (!customerRepository.existsByEmailAndOrganizationId(email, org.getId())) {
            Customer customer = Customer.builder()
                    .name(name)
                    .email(email)
                    .phone(phone)
                    .address(address)
                    .organization(org)
                    .branch(branch)
                    .active(true)
                    .build();
            customerRepository.save(customer);
        }
    }

    private void createTestProducts(Organization org, Branch branch) {
        log.info("Creating test products...");

        createProductIfNotExists("Microsoft Office 365", "Productivity suite with Word, Excel, PowerPoint", 
                new BigDecimal("99.99"), new BigDecimal("79.99"), new BigDecimal("99.99"), 
                100, "MS-OFFICE-365", org, branch);
        createProductIfNotExists("Adobe Creative Suite", "Design and creativity software package", 
                new BigDecimal("599.99"), new BigDecimal("499.99"), new BigDecimal("599.99"), 
                50, "ADOBE-CS-2024", org, branch);
        createProductIfNotExists("Dell Laptop", "High-performance business laptop", 
                new BigDecimal("1299.99"), new BigDecimal("999.99"), new BigDecimal("1299.99"), 
                25, "DELL-LAPTOP-001", org, branch);
        createProductIfNotExists("iPhone 15", "Latest smartphone from Apple", 
                new BigDecimal("999.99"), new BigDecimal("799.99"), new BigDecimal("999.99"), 
                30, "IPHONE-15-001", org, branch);
        createProductIfNotExists("IT Consulting", "Professional IT consultation services", 
                new BigDecimal("150.00"), new BigDecimal("100.00"), new BigDecimal("150.00"), 
                999, "IT-CONSULT-HR", org, branch);
        createProductIfNotExists("Cloud Migration", "Cloud infrastructure migration service", 
                new BigDecimal("5000.00"), new BigDecimal("3500.00"), new BigDecimal("5000.00"), 
                10, "CLOUD-MIG-001", org, branch);

        log.info("✅ Created test products");
    }

    private void createProductIfNotExists(String name, String description, BigDecimal price,
                                         BigDecimal purchasePrice, BigDecimal sellingPrice, 
                                         Integer stockQuantity, String sku,
                                         Organization org, Branch branch) {
        // Check if product with this name exists in the organization
        List<Product> existingProducts = productRepository.findByOrganizationIdAndNameContaining(org.getId(), name);
        if (existingProducts.isEmpty()) {
            Product product = Product.builder()
                    .name(name)
                    .description(description)
                    .price(price)
                    .purchasePrice(purchasePrice)
                    .sellingPrice(sellingPrice)
                    .stockQuantity(stockQuantity)
                    .sku(sku)
                    .organization(org)
                    .branch(branch)
                    .active(true)
                    .build();
            productRepository.save(product);
        }
    }

    private void createRoleIfNotExists(ERole roleName) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
} 