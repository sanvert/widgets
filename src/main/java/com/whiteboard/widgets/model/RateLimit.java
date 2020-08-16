package com.whiteboard.widgets.model;

import com.whiteboard.widgets.util.Operation;
import javax.validation.constraints.Min;
import lombok.Data;
import lombok.NonNull;

@Data
public class RateLimit {
    @NonNull
    @Min(0)
    private Integer limit;
    @NonNull
    private Operation operation;
}
