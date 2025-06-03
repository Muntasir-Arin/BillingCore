package com.bc.app.dto.returnitem;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReturnItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal total;
    private String reason;
} 