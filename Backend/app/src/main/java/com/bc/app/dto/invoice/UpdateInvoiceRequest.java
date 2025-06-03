package com.bc.app.dto.invoice;

import com.bc.app.model.PaymentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateInvoiceRequest {
    @NotNull(message = "Payment status is required")
    private PaymentStatus paymentStatus;

    @NotNull(message = "Paid amount is required")
    @DecimalMin(value = "0.0", message = "Paid amount must be greater than or equal to 0")
    private BigDecimal paidAmount;

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<InvoiceItemRequest> items;

    @Data
    public static class InvoiceItemRequest {
        private Long productId;
        private Integer quantity;
    }
} 