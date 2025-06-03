package com.bc.app.dto.return_;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReturnDTO {
    private Long id;
    private String returnNumber;
    private Long invoiceId;
    private String invoiceNumber;
    private Long customerId;
    private String customerName;
    private Long branchId;
    private String branchName;
    private String status;
    private String reason;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private List<ReturnItemDTO> items;
} 