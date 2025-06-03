package com.bc.app.repository;

import com.bc.app.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    List<Branch> findByActive(boolean active);
    boolean existsByName(String name);
    
    List<Branch> findByOrganizationId(Long organizationId);
    List<Branch> findByOrganizationIdAndActive(Long organizationId, boolean active);
    
    @Query("SELECT b FROM Branch b WHERE b.organization.id = :organizationId AND b.name LIKE %:name%")
    List<Branch> findByOrganizationIdAndNameContaining(@Param("organizationId") Long organizationId, @Param("name") String name);
    
    boolean existsByNameAndOrganizationId(String name, Long organizationId);
    boolean existsByNameAndOrganizationIdAndIdNot(String name, Long organizationId, Long id);
    
    Optional<Branch> findByIdAndOrganizationId(Long id, Long organizationId);
    
    long countByOrganizationId(Long organizationId);
    long countByOrganizationIdAndActive(Long organizationId, boolean active);
} 