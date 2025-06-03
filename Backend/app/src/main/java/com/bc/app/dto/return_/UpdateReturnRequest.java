package com.bc.app.dto.return_;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class UpdateReturnRequest {
    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<ReturnItemRequest> items;

    private String reason;

    @Data
    public static class ReturnItemRequest {
        private Long productId;
        private Integer quantity;
        private String reason;
    }
} 