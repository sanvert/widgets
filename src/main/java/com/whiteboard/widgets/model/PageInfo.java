package com.whiteboard.widgets.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.Data;

@Data
public class PageInfo {
    @Min(1)
    private Integer page;
    @Min(1)
    @Max(500)
    private Integer size;
}
