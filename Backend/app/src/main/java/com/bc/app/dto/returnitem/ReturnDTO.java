package com.bc.app.dto.returnitem;

import com.bc.app.model.ReturnStatus;
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
    private Long employeeId;
    private String employeeName;
    private Long branchId;
    private String branchName;
    private BigDecimal totalAmount;
    private ReturnStatus status;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ReturnItemDTO> items;
} 