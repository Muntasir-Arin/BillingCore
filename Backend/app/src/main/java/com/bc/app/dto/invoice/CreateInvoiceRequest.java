package com.bc.app.dto.invoice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class CreateInvoiceRequest {
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Branch ID is required")
    private Long branchId;

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<InvoiceItemRequest> items;

    @Data
    public static class InvoiceItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;
    }
} 