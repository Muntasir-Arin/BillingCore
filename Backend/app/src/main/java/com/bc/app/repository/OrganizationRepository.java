package com.bc.app.repository;

import com.bc.app.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    
    Optional<Organization> findByName(String name);
    
    List<Organization> findByActiveTrue();
    
    @Query("SELECT o FROM Organization o WHERE o.active = true AND o.name LIKE %:name%")
    List<Organization> findByNameContainingIgnoreCaseAndActive(@Param("name") String name);
    
    boolean existsByName(String name);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT o FROM Organization o JOIN o.userOrganizations uo WHERE uo.user.id = :userId AND uo.active = true")
    List<Organization> findByUserId(@Param("userId") Long userId);
} 