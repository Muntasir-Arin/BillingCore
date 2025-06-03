package com.bc.app.repository;

import com.bc.app.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    List<UserGroup> findByActive(boolean active);
    boolean existsByName(String name);
} 