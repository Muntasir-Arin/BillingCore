package com.bc.app.dto.actionlog;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActionLogDTO {
    private Long id;
    private String actionType;
    private String description;
    private Long userId;
    private String username;
    private Long branchId;
    private String branchName;
    private LocalDateTime createdAt;
} 