package dev.oscar.tasttracker.task;

import dev.oscar.tasttracker.user.User;
import dev.oscar.tasttracker.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody TaskRequest taskRequest, Authentication authentication) {
        String userName = authentication.getName();
        Task task = taskService.createTask(userName, taskRequest);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(Authentication authentication) {
        String userName = authentication.getName();
        List<TaskResponse> tasks = taskService.getAllTaskFromUser(userName);

        return ResponseEntity.ok(tasks);
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Task> toggleTaskCompletion(@PathVariable Long id, Authentication authentication) {
        String currentUsername = authentication.getName();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!task.getUser().getUsername().equals(currentUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        task.setCompleted(!task.getCompleted());
        taskRepository.save(task);

        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, Authentication authentication) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!task.getUser().getUsername().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        taskRepository.delete(task);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @RequestBody TaskRequest updatedTask,
            Authentication authentication) {

        String username = authentication.getName();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!task.getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        // Update the task
        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setDueDate(updatedTask.getDueDate());

        taskRepository.save(task);

        return ResponseEntity.ok(task);
    }
}
