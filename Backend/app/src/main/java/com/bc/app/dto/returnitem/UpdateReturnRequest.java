package com.bc.app.dto.returnitem;

import com.bc.app.model.ReturnStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class UpdateReturnRequest {
    @NotNull(message = "Status is required")
    private ReturnStatus status;

    @Valid
    private List<CreateReturnItemRequest> items;
} 