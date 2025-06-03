package com.bc.app.dto.return_;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ReturnItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String reason;
} 