package com.bc.app.repository;

import com.bc.app.model.UserOrganization;
import com.bc.app.model.UserOrganization.OrgRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserOrganizationRepository extends JpaRepository<UserOrganization, Long> {
    
    @Query("SELECT uo FROM UserOrganization uo WHERE uo.user.id = :userId AND uo.active = true")
    List<UserOrganization> findByUserIdAndActiveTrue(@Param("userId") Long userId);
    
    @Query("SELECT uo FROM UserOrganization uo WHERE uo.organization.id = :organizationId AND uo.active = true")
    List<UserOrganization> findByOrganizationIdAndActiveTrue(@Param("organizationId") Long organizationId);
    
    @Query("SELECT uo FROM UserOrganization uo WHERE uo.user.id = :userId AND uo.organization.id = :organizationId AND uo.active = true")
    Optional<UserOrganization> findByUserIdAndOrganizationIdAndActiveTrue(@Param("userId") Long userId, @Param("organizationId") Long organizationId);
    
    @Query("SELECT uo FROM UserOrganization uo WHERE uo.organization.id = :organizationId AND uo.roleInOrg = :role AND uo.active = true")
    List<UserOrganization> findByOrganizationIdAndRoleAndActiveTrue(@Param("organizationId") Long organizationId, @Param("role") OrgRole role);
    
    boolean existsByUserIdAndOrganizationIdAndActiveTrue(Long userId, Long organizationId);
} 