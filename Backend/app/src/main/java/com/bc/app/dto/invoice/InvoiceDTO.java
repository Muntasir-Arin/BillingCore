package com.bc.app.dto.invoice;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoiceDTO {
    private Long id;
    private String invoiceNumber;
    private Long customerId;
    private String customerName;
    private Long branchId;
    private String branchName;
    private String paymentStatus;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private List<InvoiceItemDTO> items;
} 