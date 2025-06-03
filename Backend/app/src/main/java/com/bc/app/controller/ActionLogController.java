package com.bc.app.controller;

import com.bc.app.dto.actionlog.ActionLogDTO;
import com.bc.app.service.ActionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/action-logs")
@RequiredArgsConstructor
public class ActionLogController {

    private final ActionLogService actionLogService;

    // Only DEV can access all action logs without organization context
    @GetMapping
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<Page<ActionLogDTO>> getAllActionLogs(Pageable pageable) {
        return ResponseEntity.ok(actionLogService.getActionLogs(pageable));
    }

    // Only DEV can access action logs by user without organization context
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<List<ActionLogDTO>> getActionLogsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(actionLogService.getActionLogsByUser(userId));
    }

    // Only DEV can access action logs by branch without organization context
    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<List<ActionLogDTO>> getActionLogsByBranch(@PathVariable Long branchId) {
        return ResponseEntity.ok(actionLogService.getActionLogsByBranch(branchId));
    }

    // Only DEV can access action logs by action type without organization context
    @GetMapping("/action-type/{actionType}")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<List<ActionLogDTO>> getActionLogsByActionType(@PathVariable String actionType) {
        return ResponseEntity.ok(actionLogService.getActionLogsByActionType(actionType));
    }

    // Only DEV can access action logs by date range without organization context
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<List<ActionLogDTO>> getActionLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(actionLogService.getActionLogsByDateRange(startDate, endDate));
    }

    // Organization-scoped endpoints

    /**
     * Get action logs for organization - organization managers and above can view
     */
    @GetMapping("/organizations/{organizationId}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.hasOrganizationPermissionLevel(#organizationId, 'MANAGER')")
    public ResponseEntity<Page<ActionLogDTO>> getActionLogsByOrganization(
            @PathVariable Long organizationId,
            Pageable pageable) {
        // This would need implementation in ActionLogService to filter by organization
        return ResponseEntity.ok(actionLogService.getActionLogs(pageable)); // Placeholder
    }

    /**
     * Get action logs by user within organization - managers and above can view
     */
    @GetMapping("/organizations/{organizationId}/users/{userId}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.hasOrganizationPermissionLevel(#organizationId, 'MANAGER')")
    public ResponseEntity<List<ActionLogDTO>> getActionLogsByUserInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(actionLogService.getActionLogsByUser(userId));
    }

    /**
     * Get action logs by branch within organization - organization members can view
     */
    @GetMapping("/organizations/{organizationId}/branches/{branchId}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.canViewOrganization(#organizationId)")
    public ResponseEntity<List<ActionLogDTO>> getActionLogsByBranchInOrganization(
            @PathVariable Long organizationId,
            @PathVariable Long branchId) {
        return ResponseEntity.ok(actionLogService.getActionLogsByBranch(branchId));
    }

    /**
     * Get action logs by action type within organization - organization members can view
     */
    @GetMapping("/organizations/{organizationId}/action-type/{actionType}")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.canViewOrganization(#organizationId)")
    public ResponseEntity<List<ActionLogDTO>> getActionLogsByActionTypeInOrganization(
            @PathVariable Long organizationId,
            @PathVariable String actionType) {
        return ResponseEntity.ok(actionLogService.getActionLogsByActionType(actionType));
    }

    /**
     * Get action logs by date range within organization - organization members can view
     */
    @GetMapping("/organizations/{organizationId}/date-range")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.canViewOrganization(#organizationId)")
    public ResponseEntity<List<ActionLogDTO>> getActionLogsByDateRangeInOrganization(
            @PathVariable Long organizationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        // This would need implementation to filter by organization
        return ResponseEntity.ok(actionLogService.getActionLogsByDateRange(startDate, endDate));
    }

    /**
     * Get current user's action logs within organization
     */
    @GetMapping("/organizations/{organizationId}/my-logs")
    @PreAuthorize("hasRole('DEV') or @orgSecurity.canViewOrganization(#organizationId)")
    public ResponseEntity<List<ActionLogDTO>> getMyActionLogsInOrganization(@PathVariable Long organizationId) {
        // This would need implementation to get current user's logs within organization
        return ResponseEntity.ok(List.of());
    }
} 