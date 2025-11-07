package moneybuddy.fr.moneybuddy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.TaskComplete;
import moneybuddy.fr.moneybuddy.dtos.TaskRequest;
import moneybuddy.fr.moneybuddy.dtos.Money.AddMoney;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.model.enums.TaskStatus;
import moneybuddy.fr.moneybuddy.model.Task;
import moneybuddy.fr.moneybuddy.repository.TaskRepository;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final MongoTemplate mongoTemplate;
    private final TaskRepository taskRepository;
    private final MoneyService moneyService;
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

        boolean preValidate = request.isPrevalidation() ? request.isPrevalidation() : false;
        Task task = Task.builder()
                    .description(request.getDescription())
                    .category(request.getCategory())
                    .subaccountIdParent(subAccountIdParent)
                    .subaccountIdChild(request.getSubAccountId())
                    .accountId(accountId)
                    .preValidate(preValidate)
                    .reward(request.getReward())
                    .dateLimit(request.getDateLimit())
                    .createdAt(LocalDateTime.now())
                    .build();
                    
        taskRepository.save(task);
        return response("Task created", HttpStatus.CREATED);
    }

    public ResponseEntity<List<Task>> getTasks (
        String token, 
        String childId,
        TaskStatus status
        ) {
        SubAccountRole role = jwtService.extractSubAccountRole(token);
        role = SubAccountRole.OWNER.equals(role) ? SubAccountRole.PARENT : role;
        boolean isParent = SubAccountRole.PARENT.equals(role);

        String id = isParent ? jwtService.extractSubAccountAccountId(token) : jwtService.extractSubAccountId(token);
        Criteria criteria = new Criteria();
        
        if (isParent) {
            criteria.and("accountId").is(id);
        } else {
            criteria.and("subaccountIdChild").is(id);
        }

        if (isParent && childId != null && !childId.isEmpty()) {
            criteria.and("subaccountIdChild").is(childId);
        }

        if (status != null) {
            criteria.and("status").is(status);
        }

        Query query = new Query(criteria);
        List<Task> tasks = mongoTemplate.find(query, Task.class);

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

    public ResponseEntity<AuthResponse> completeTask (TaskComplete req, String token, String taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        if (SubAccountRole.CHILD.equals(jwtService.extractSubAccountRole(token)) && task.isPreValidate()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (req.isDone()) {
            AddMoney addMoney = AddMoney.builder()
                .amount(task.getReward())
                .subAccountId(task.getSubaccountIdChild())
                .description("Reward")
                .build();
            moneyService.updateMoney(addMoney, token, true);
            task.setStatus(TaskStatus.COMPLETED);
        }

        if (!req.isDone()) {
            task.setStatus(TaskStatus.REFUSED);
        }

        task.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    public ResponseEntity<AuthResponse> preValidateTask (String token, String taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        
        task.setStatus(TaskStatus.PRE_VALIDATE);
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
