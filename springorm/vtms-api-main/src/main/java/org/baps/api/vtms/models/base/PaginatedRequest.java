package org.baps.api.vtms.models.base;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class PaginatedRequest {

    @NotNull
    @Min(value = 1)
    private Integer pageNo;

    @NotNull
    @Min(value = 1)
    private Integer pageSize;
}
