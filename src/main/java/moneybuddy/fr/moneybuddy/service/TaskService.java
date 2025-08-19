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
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.Task;
import moneybuddy.fr.moneybuddy.repository.SubAccountRepository;
import moneybuddy.fr.moneybuddy.repository.TaskRepository;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final MongoTemplate mongoTemplate;
    private final TaskRepository taskRepository;
    private final SubAccountRepository subAccountRepository;
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

        boolean prevalidation = request.isPrevalidation();
        Task task = Task.builder()
                    .description(request.getDescription())
                    .category(request.getCategory())
                    .subaccountIdParent(subAccountIdParent)
                    .subaccountIdChild(request.getSubAccountId())
                    .accountId(accountId)
                    .reward(request.getReward())
                    .dateLimit(request.getDateLimit())
                    .isDone(false)
                    .prevalidation(prevalidation)
                    .createdAt(LocalDateTime.now())
                    .build();
                    
        taskRepository.save(task);
        return response("Task created", HttpStatus.CREATED);
    }

    public ResponseEntity<List<Task>> getTasks (
        String token, 
        String childId,
        Boolean prevalidation,
        Boolean refused,
        Boolean isDone 
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

        if (prevalidation != null) {
            criteria.and("prevalidation").is(prevalidation);
        }
        
        if (refused != null) {
            criteria.and("refused").is(refused);
        }
        
        if (isDone != null) {
            criteria.and("isDone").is(isDone);
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
        if (SubAccountRole.CHILD.equals(jwtService.extractSubAccountRole(token))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Task task = taskRepository.findById(taskId).orElseThrow();
        SubAccount subAccount = subAccountRepository.findById(task.getSubaccountIdChild()).orElseThrow();

        task.setDone(req.isDone());

        if (req.isDone()) {
            String newMoney = String.valueOf(Double.parseDouble(subAccount.getMoney()) + Double.parseDouble(task.getReward()));
            subAccount.setMoney(newMoney);
            subAccount.setUpdatedAt(LocalDateTime.now());
            subAccountRepository.save(subAccount);
        }

        if (!req.isDone()) {
            task.setRefused(true);
        }

        task.setUpdatedAt(LocalDateTime.now());
        taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    public ResponseEntity<AuthResponse> preValidationTask (String token, String taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        
        task.setPrevalidation(true);
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
