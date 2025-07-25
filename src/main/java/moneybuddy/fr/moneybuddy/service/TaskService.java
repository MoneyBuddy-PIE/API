package moneybuddy.fr.moneybuddy.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.TaskRequest;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.model.Task;
import moneybuddy.fr.moneybuddy.repository.TaskRepository;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final JwtService jwtService;

    public ResponseEntity<AuthResponse> response(String message, HttpStatus status) {
        return ResponseEntity
                .status(status)
                .body(AuthResponse.builder()
                    .error(message)
                    .build());
    }

    public ResponseEntity<AuthResponse> createTask (TaskRequest request, String token) {        
        SubAccountRole role = jwtService.extractSubAccountRole(token);
        String subAccountIdParent = jwtService.extractSubAccountId(token);
        String accountId = jwtService.extractSubAccountAccountId(token);

        if (SubAccountRole.CHILD.equals(role)) {
            return response("Vous n avez pas les droits", HttpStatus.FORBIDDEN);
        }

        Task task = Task.builder()
                    .description(request.getDescription())
                    .category(request.getCategory())
                    .subaccountIdParent(subAccountIdParent)
                    .subaccountIdChild(request.getSubAccountId())
                    .accountId(accountId)
                    .reward(request.getReward())
                    .dateLimit(request.getDateLimit())
                    .isDone(false)
                    .createdAt(LocalDateTime.now())
                    .build();
                    
        taskRepository.save(task);
        return response("Task created", HttpStatus.CREATED);
    }

    public ResponseEntity<List<Task>> getTasks (String token, SubAccountRole source) {
        List<Task> tasks = new ArrayList<>();
        SubAccountRole role = jwtService.extractSubAccountRole(token);
        String id = (source != null) ?
            jwtService.extractSubAccountId(token)
            : jwtService.extractSubAccountAccountId(token);
        
        if (SubAccountRole.PARENT.equals(source) && (SubAccountRole.PARENT.equals(role) || SubAccountRole.OWNER.equals(role))) {
            System.out.println("ici");
            tasks = taskRepository.findBySubaccountIdParent(id);
        } else if (SubAccountRole.CHILD.equals(source) && SubAccountRole.CHILD.equals(role)) {
            tasks = taskRepository.findBySubaccountIdChild(id);
        } else if (SubAccountRole.PARENT.equals(role) || SubAccountRole.OWNER.equals(role)) {
            tasks = taskRepository.findByAccountId(id);
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(tasks);
    }

    public ResponseEntity<Task> getTask (String id) {
        Optional<Task> task = taskRepository.findById(id);

        return task.isPresent() ? 
            ResponseEntity.status(HttpStatus.ACCEPTED).body(task.get())
            : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    public ResponseEntity<AuthResponse> deleteTask (String token, String taskId) {
        taskRepository.deleteById(taskId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    public ResponseEntity<AuthResponse> completeTask (String token, String taskId) {
        String subAccountId = jwtService.extractSubAccountId(token);
        Task task = taskRepository.findByIdAndSubaccountIdParent(taskId, subAccountId);
        
        task.setDone(true);
        task.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    public ResponseEntity<Task> modifyTask (TaskRequest request, String token, String taskId) {
        SubAccountRole role = jwtService.extractSubAccountRole(token);
        String subAccountIdParent = jwtService.extractSubAccountId(token);

        if (!SubAccountRole.PARENT.equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Task task = taskRepository.findByIdAndSubaccountIdParent(taskId, subAccountIdParent);

        task.setDescription(request.getDescription());
        task.setCategory(request.getCategory());
        task.setSubaccountIdChild(request.getSubAccountId());
        task.setReward(request.getReward());
        task.setDateLimit(request.getDateLimit());
        task.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }
}
