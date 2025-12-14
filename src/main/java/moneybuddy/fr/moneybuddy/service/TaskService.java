/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.Money.AddMoney;
import moneybuddy.fr.moneybuddy.dtos.TaskComplete;
import moneybuddy.fr.moneybuddy.dtos.TaskRequest;
import moneybuddy.fr.moneybuddy.dtos.coin.UpdateCoin;
import moneybuddy.fr.moneybuddy.exception.NoRight;
import moneybuddy.fr.moneybuddy.exception.TaskNotFoundException;
import moneybuddy.fr.moneybuddy.model.Task;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.model.enums.TaskStatus;
import moneybuddy.fr.moneybuddy.repository.TaskRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

  private final MongoTemplate mongoTemplate;
  private final TaskRepository taskRepository;
  private final MoneyService moneyService;
  private final CoinService coinService;
  private final JwtService jwtService;

  public ResponseEntity<AuthResponse> createTask(TaskRequest request, String token) {
    SubAccountRole role = jwtService.extractSubAccountRole(token);
    String subAccountIdParent = jwtService.extractSubAccountId(token);
    String accountId = jwtService.extractSubAccountAccountId(token);

    if (SubAccountRole.CHILD.equals(role)) throw new NoRight();

    boolean preValidate = request.isPrevalidation() ? request.isPrevalidation() : false;
    Task task =
        Task.builder()
            .description(request.getDescription())
            .type(request.getType())
            .subaccountIdParent(subAccountIdParent)
            .subaccountIdChild(request.getSubAccountId())
            .accountId(accountId)
            .preValidate(preValidate)
            .moneyReward(request.getMoneyReward())
            .coinReward(request.getCoinReward())
            .dateLimit(request.getDateLimit())
            .createdAt(LocalDateTime.now())
            .build();

    taskRepository.save(task);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(AuthResponse.builder().error("Tache crée avec succes").build());
  }

  public ResponseEntity<List<Task>> getTasks(String token, String childId, TaskStatus status) {
    SubAccountRole role = jwtService.extractSubAccountRole(token);
    role = SubAccountRole.OWNER.equals(role) ? SubAccountRole.PARENT : role;
    boolean isParent = SubAccountRole.PARENT.equals(role);

    String id =
        isParent
            ? jwtService.extractSubAccountAccountId(token)
            : jwtService.extractSubAccountId(token);
    Criteria criteria = new Criteria();

    if (isParent) {
      criteria.and("accountId").is(id);
    } else {
      criteria.and("subaccountIdChild").is(id);
    }

    if (isParent && childId != null && !childId.isEmpty())
      criteria.and("subaccountIdChild").is(childId);

    if (status != null) criteria.and("status").is(status);

    Query query = new Query(criteria);
    List<Task> tasks = mongoTemplate.find(query, Task.class);

    return ResponseEntity.status(HttpStatus.ACCEPTED).body(tasks);
  }

  public ResponseEntity<Task> getTask(String id) {
    Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));

    return ResponseEntity.status(HttpStatus.ACCEPTED).body(task);
  }

  public ResponseEntity<AuthResponse> deleteTask(String token, String taskId) {
    taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
    taskRepository.deleteById(taskId);
    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }

  public ResponseEntity<AuthResponse> completeTask(TaskComplete req, String token, String taskId) {
    Task task =
        taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));

    if (SubAccountRole.CHILD.equals(jwtService.extractSubAccountRole(token))
        && task.isPreValidate()) throw new NoRight();

    if (req.isDone()) {
      AddMoney addMoney =
          AddMoney.builder()
              .amount(task.getMoneyReward().toString())
              .subAccountId(task.getSubaccountIdChild())
              .description(task.getDescription())
              .build();
      UpdateCoin updateCoin =
          UpdateCoin.builder()
              .subAccountId(task.getSubaccountIdChild())
              .coin(task.getCoinReward())
              .description(task.getDescription())
              .build();
      moneyService.updateMoney(addMoney, token, true);
      coinService.updateCoin(updateCoin, token, true);
      task.setStatus(TaskStatus.COMPLETED);
    }

    if (!req.isDone()) task.setStatus(TaskStatus.REFUSED);

    task.setUpdatedAt(LocalDateTime.now());
    taskRepository.save(task);
    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }

  public ResponseEntity<AuthResponse> preValidateTask(String token, String taskId) {
    Task task =
        taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));

    task.setStatus(TaskStatus.PRE_VALIDATE);
    task.setUpdatedAt(LocalDateTime.now());
    taskRepository.save(task);

    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }

  public ResponseEntity<Task> modifyTask(TaskRequest request, String token, String taskId) {
    SubAccountRole role = jwtService.extractSubAccountRole(token);
    String subAccountIdParent = jwtService.extractSubAccountId(token);

    if (!SubAccountRole.PARENT.equals(role)) throw new NoRight();

    Task task =
        taskRepository
            .findByIdAndSubaccountIdParent(taskId, subAccountIdParent)
            .orElseThrow(() -> new TaskNotFoundException(taskId));

    task.setDescription(request.getDescription());
    task.setType(request.getType());
    task.setSubaccountIdChild(request.getSubAccountId());
    task.setMoneyReward(request.getMoneyReward());
    task.setDateLimit(request.getDateLimit());
    task.setUpdatedAt(LocalDateTime.now());

    Task updatedTask = taskRepository.save(task);
    return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
  }
}
