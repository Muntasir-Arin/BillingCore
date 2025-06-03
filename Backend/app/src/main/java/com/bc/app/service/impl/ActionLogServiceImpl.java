package com.bc.app.service.impl;

import com.bc.app.dto.actionlog.ActionLogDTO;
import com.bc.app.model.ActionLog;
import com.bc.app.model.ActionType;
import com.bc.app.model.Branch;
import com.bc.app.model.User;
import com.bc.app.repository.ActionLogRepository;
import com.bc.app.repository.BranchRepository;
import com.bc.app.repository.UserRepository;
import com.bc.app.service.ActionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActionLogServiceImpl implements ActionLogService {
    
    private final ActionLogRepository actionLogRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    @Override
    public List<ActionLogDTO> getActionLogsByUser(Long userId) {
        List<ActionLog> actionLogs = actionLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return actionLogs.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActionLogDTO> getActionLogsByBranch(Long branchId) {
        List<ActionLog> actionLogs = actionLogRepository.findByBranchIdOrderByCreatedAtDesc(branchId);
        return actionLogs.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActionLogDTO> getActionLogsByActionType(String actionType) {
        try {
            ActionType type = ActionType.valueOf(actionType.toUpperCase());
            List<ActionLog> actionLogs = actionLogRepository.findByActionTypeOrderByCreatedAtDesc(type);
            return actionLogs.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return List.of(); // Return empty list for invalid action type
        }
    }

    @Override
    public List<ActionLogDTO> getActionLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<ActionLog> actionLogs = actionLogRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate);
        return actionLogs.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ActionLogDTO> getActionLogs(Pageable pageable) {
        Page<ActionLog> actionLogs = actionLogRepository.findAllByOrderByCreatedAtDesc(pageable);
        return actionLogs.map(this::mapToDTO);
    }

    @Override
    @Transactional
    public void logAction(String actionType, String description, Long userId, Long branchId) {
        try {
            ActionType type = ActionType.valueOf(actionType.toUpperCase());
            
            User user = userRepository.findById(userId).orElse(null);
            Branch branch = branchId != null ? branchRepository.findById(branchId).orElse(null) : null;
            
            ActionLog actionLog = ActionLog.builder()
                    .actionType(type)
                    .description(description)
                    .user(user)
                    .branch(branch)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            actionLogRepository.save(actionLog);
        } catch (IllegalArgumentException e) {
            // Log error or handle invalid action type
        }
    }

    private ActionLogDTO mapToDTO(ActionLog actionLog) {
        ActionLogDTO dto = new ActionLogDTO();
        dto.setId(actionLog.getId());
        dto.setActionType(actionLog.getActionType().name());
        dto.setDescription(actionLog.getDescription());
        dto.setUserId(actionLog.getUser() != null ? actionLog.getUser().getId() : null);
        dto.setUsername(actionLog.getUser() != null ? actionLog.getUser().getUsername() : null);
        dto.setBranchId(actionLog.getBranch() != null ? actionLog.getBranch().getId() : null);
        dto.setBranchName(actionLog.getBranch() != null ? actionLog.getBranch().getName() : null);
        dto.setCreatedAt(actionLog.getCreatedAt());
        return dto;
    }
} 