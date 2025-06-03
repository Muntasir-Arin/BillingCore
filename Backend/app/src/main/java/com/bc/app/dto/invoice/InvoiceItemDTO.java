package com.bc.app.dto.invoice;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class InvoiceItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
} 