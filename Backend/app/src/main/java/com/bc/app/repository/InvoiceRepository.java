package com.bc.app.repository;

import com.bc.app.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByBranchId(Long branchId);
    List<Invoice> findByCustomerId(Long customerId);
    List<Invoice> findByBranchIdAndCreatedAtBetween(Long branchId, LocalDateTime startDate, LocalDateTime endDate);
    Page<Invoice> findByBranchId(Long branchId, Pageable pageable);
    boolean existsByInvoiceNumber(String invoiceNumber);
} 