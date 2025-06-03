package com.bc.app.repository;

import com.bc.app.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByPhone(String phone);
    List<Customer> findByUserGroupId(Long userGroupId);
    List<Customer> findByActive(boolean active);
    Page<Customer> findByActive(boolean active, Pageable pageable);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    List<Customer> findByBranchId(Long branchId);
    List<Customer> findByBranchIdAndActive(Long branchId, boolean active);
    Page<Customer> findByBranchId(Long branchId, Pageable pageable);
    boolean existsByPhoneAndBranchId(String phone, Long branchId);
    List<Customer> findByOrganizationId(Long organizationId);
    List<Customer> findByOrganizationIdAndActive(Long organizationId, boolean active);
    Page<Customer> findByOrganizationId(Long organizationId, Pageable pageable);
    Page<Customer> findByOrganizationIdAndActive(Long organizationId, boolean active, Pageable pageable);
    List<Customer> findByOrganizationIdAndBranchId(Long organizationId, Long branchId);
    List<Customer> findByOrganizationIdAndBranchIdAndActive(Long organizationId, Long branchId, boolean active);
    @Query("SELECT c FROM Customer c WHERE c.organization.id = :organizationId AND c.name LIKE %:name%")
    List<Customer> findByOrganizationIdAndNameContaining(@Param("organizationId") Long organizationId, @Param("name") String name);
    @Query("SELECT c FROM Customer c WHERE c.organization.id = :organizationId AND c.phone LIKE %:phone%")
    List<Customer> findByOrganizationIdAndPhoneContaining(@Param("organizationId") Long organizationId, @Param("phone") String phone);
    boolean existsByPhoneAndOrganizationId(String phone, Long organizationId);
    boolean existsByEmailAndOrganizationId(String email, Long organizationId);
    boolean existsByPhoneAndOrganizationIdAndIdNot(String phone, Long organizationId, Long id);
    boolean existsByEmailAndOrganizationIdAndIdNot(String email, Long organizationId, Long id);
    Optional<Customer> findByIdAndOrganizationId(Long id, Long organizationId);
    Optional<Customer> findByPhoneAndOrganizationId(String phone, Long organizationId);
} 