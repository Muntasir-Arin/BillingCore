package com.bc.app.dto.return_;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class CreateReturnRequest {
    @NotNull(message = "Invoice ID is required")
    private Long invoiceId;

    @NotNull(message = "Branch ID is required")
    private Long branchId;

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<ReturnItemRequest> items;

    private String reason;

    @Data
    public static class ReturnItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        private String reason;
    }
} 