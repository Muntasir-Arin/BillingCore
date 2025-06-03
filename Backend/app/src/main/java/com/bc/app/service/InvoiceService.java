package com.bc.app.service;

import com.bc.app.dto.invoice.InvoiceDTO;
import com.bc.app.dto.invoice.CreateInvoiceRequest;
import com.bc.app.dto.invoice.UpdateInvoiceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceService {
    InvoiceDTO createInvoice(CreateInvoiceRequest request);
    InvoiceDTO updateInvoice(Long id, UpdateInvoiceRequest request);
    void deleteInvoice(Long id);
    InvoiceDTO getInvoice(Long id);
    List<InvoiceDTO> getInvoicesByBranch(Long branchId);
    List<InvoiceDTO> getInvoicesByCustomer(Long customerId);
    List<InvoiceDTO> getInvoicesByDateRange(Long branchId, LocalDateTime startDate, LocalDateTime endDate);
    Page<InvoiceDTO> getInvoicesByBranch(Long branchId, Pageable pageable);
    void updatePaymentStatus(Long id, String status);
    void cancelInvoice(Long id);
} 