package com.bc.app.repository;

import com.bc.app.model.ActionLog;
import com.bc.app.model.ActionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {
    
    List<ActionLog> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<ActionLog> findByBranchIdOrderByCreatedAtDesc(Long branchId);
    
    List<ActionLog> findByActionTypeOrderByCreatedAtDesc(ActionType actionType);
    
    List<ActionLog> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);
    
    Page<ActionLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
} 