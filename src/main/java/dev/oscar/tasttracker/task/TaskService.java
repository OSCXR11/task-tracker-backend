package dev.oscar.tasttracker.task;

import dev.oscar.tasttracker.user.User;
import dev.oscar.tasttracker.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.grammars.hql.HqlParser;
import org.hibernate.type.descriptor.java.LocalDateTimeJavaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public List<TaskResponse> getAllTaskFromUser(String userName){
        User user = userRepository.findByUserName(userName).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Task> tasks = taskRepository.findByUser(user);

        return tasks.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private TaskResponse mapToDto(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.getCompleted())
                .dueDate(task.getDueDate())
                .build();
    }

    public Task createTask(String userName, TaskRequest taskRequest){

        User user = userRepository.findByUserName(userName).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Task task = Task.builder()
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .completed(taskRequest.isCompleted())
                .dueDate(taskRequest.getDueDate())
                .user(user)
                .build();
        return taskRepository.save(task);
    }
}
