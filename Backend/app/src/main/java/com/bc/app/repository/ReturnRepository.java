package com.bc.app.repository;

import com.bc.app.model.Return;
import com.bc.app.model.ReturnStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReturnRepository extends JpaRepository<Return, Long> {
    List<Return> findByBranchId(Long branchId);
    List<Return> findByCustomerId(Long customerId);
    List<Return> findByEmployeeId(Long employeeId);
    List<Return> findByStatus(ReturnStatus status);
    List<Return> findByBranchIdAndStatus(Long branchId, ReturnStatus status);
    List<Return> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Return> findByBranchIdAndCreatedAtBetween(Long branchId, LocalDateTime start, LocalDateTime end);
    Page<Return> findByBranchId(Long branchId, Pageable pageable);
    boolean existsByReturnNumber(String returnNumber);
} 