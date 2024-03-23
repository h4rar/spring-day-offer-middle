package com.onedayoffer.taskdistribution.DTO;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
@Builder
public class TaskDTO {
    private Integer id;
    private String name;
    private TaskType taskType;
    private TaskStatus status;
    private Integer priority;
    private Integer leadTime;
}
