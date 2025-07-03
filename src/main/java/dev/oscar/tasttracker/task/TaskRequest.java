package dev.oscar.tasttracker.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private boolean completed;
}
