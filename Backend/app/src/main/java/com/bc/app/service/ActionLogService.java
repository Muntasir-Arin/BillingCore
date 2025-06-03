package com.bc.app.service;

import com.bc.app.dto.actionlog.ActionLogDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ActionLogService {
    List<ActionLogDTO> getActionLogsByUser(Long userId);
    List<ActionLogDTO> getActionLogsByBranch(Long branchId);
    List<ActionLogDTO> getActionLogsByActionType(String actionType);
    List<ActionLogDTO> getActionLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    Page<ActionLogDTO> getActionLogs(Pageable pageable);
    void logAction(String actionType, String description, Long userId, Long branchId);
} 