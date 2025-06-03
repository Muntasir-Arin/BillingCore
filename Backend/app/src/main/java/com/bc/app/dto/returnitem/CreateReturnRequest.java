package com.bc.app.dto.returnitem;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class CreateReturnRequest {
    @NotNull(message = "Invoice ID is required")
    private Long invoiceId;

    @NotBlank(message = "Reason is required")
    private String reason;

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<CreateReturnItemRequest> items;
} 