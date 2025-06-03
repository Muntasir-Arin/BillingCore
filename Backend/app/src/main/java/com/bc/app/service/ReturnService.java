package com.bc.app.service;

import com.bc.app.dto.return_.ReturnDTO;
import com.bc.app.dto.return_.CreateReturnRequest;
import com.bc.app.dto.return_.UpdateReturnRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ReturnService {
    ReturnDTO createReturn(CreateReturnRequest request);
    ReturnDTO updateReturn(Long id, UpdateReturnRequest request);
    void deleteReturn(Long id);
    ReturnDTO getReturn(Long id);
    List<ReturnDTO> getReturnsByBranch(Long branchId);
    List<ReturnDTO> getReturnsByCustomer(Long customerId);
    List<ReturnDTO> getReturnsByDateRange(Long branchId, LocalDateTime startDate, LocalDateTime endDate);
    Page<ReturnDTO> getReturnsByBranch(Long branchId, Pageable pageable);
    void updateReturnStatus(Long id, String status);
    void cancelReturn(Long id);
} 