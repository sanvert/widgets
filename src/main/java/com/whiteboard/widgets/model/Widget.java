package com.whiteboard.widgets.model;

import java.time.LocalDateTime;
import javax.validation.constraints.Min;
import lombok.Data;
import lombok.NonNull;

@Data
public class Widget {
    private String id;
    @NonNull
    private Integer x;
    @NonNull
    private Integer y;
    private Integer z;
    @NonNull
    @Min(1)
    private Integer width;
    @NonNull
    @Min(1)
    private Integer height;
    private LocalDateTime lastModificationDate;
}
