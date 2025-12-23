/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.dtos.AuthResponse;
import moneybuddy.fr.moneybuddy.dtos.TaskComplete;
import moneybuddy.fr.moneybuddy.dtos.TaskRequest;
import moneybuddy.fr.moneybuddy.dtos.TaskUpdate;
import moneybuddy.fr.moneybuddy.exception.NoRight;
import moneybuddy.fr.moneybuddy.exception.SubAccountNotFoundException;
import moneybuddy.fr.moneybuddy.exception.TaskNotFoundException;
import moneybuddy.fr.moneybuddy.model.SubAccount;
import moneybuddy.fr.moneybuddy.model.Task;
import moneybuddy.fr.moneybuddy.model.TaskHistory;
import moneybuddy.fr.moneybuddy.model.TaskWithHistory;
import moneybuddy.fr.moneybuddy.model.enums.SubAccountRole;
import moneybuddy.fr.moneybuddy.model.enums.TaskStatus;
import moneybuddy.fr.moneybuddy.model.enums.TaskType;
import moneybuddy.fr.moneybuddy.repository.SubAccountRepository;
import moneybuddy.fr.moneybuddy.repository.TaskRepository;
import moneybuddy.fr.moneybuddy.repository.TaskWithHistoryRepository;
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
  private final SubAccountRepository subAccountRepository;
  private final TaskWithHistoryRepository taskWithHistoryRepository;
  private final TaskHistoryService taskHistoryService;
  private final IncomeService incomeService;
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
            .weeklyDays(request.getWeeklyDays())
            .createdAt(LocalDateTime.now())
            .build();

    taskRepository.save(task);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(AuthResponse.builder().error("Tache crée avec succes").build());
  }

  public ResponseEntity<List<Task>> getTasks(
      String token, String childId, TaskStatus status, TaskType type) {
    SubAccountRole role = jwtService.extractSubAccountRole(token);
    role = SubAccountRole.OWNER.equals(role) ? SubAccountRole.PARENT : role;
    boolean isParent = SubAccountRole.PARENT.equals(role);
    System.out.println(role);
    System.out.println(isParent);
    String id =
        isParent
            ? jwtService.extractSubAccountAccountId(token)
            : jwtService.extractSubAccountId(token);
    System.out.println(id);
    Criteria criteria = new Criteria();

    Criteria dateLimitCriteria =
        new Criteria()
            .orOperator(
                Criteria.where("dateLimit").gte(LocalDateTime.now()),
                Criteria.where("dateLimit").isNull());

    Criteria weeklyCriteria =
        new Criteria()
            .orOperator(
                Criteria.where("type").ne("WEEKLY"),
                new Criteria()
                    .andOperator(
                        Criteria.where("type").is("WEEKLY"),
                        Criteria.where("weeklyDays").is(LocalDateTime.now().getDayOfWeek())));

    if (isParent) {
      criteria.and("accountId").is(id);
    } else {
      criteria.and("subaccountIdChild").is(id);
      criteria.and("disable").is(false);
      criteria.and("status").ne(TaskStatus.COMPLETED.name());
      criteria.andOperator(dateLimitCriteria, weeklyCriteria);
    }

    if (isParent && childId != null && !childId.isEmpty())
      criteria.and("subaccountIdChild").is(childId);

    if (status != null) criteria.and("status").is(status);
    if (type != null) criteria.and("type").is(type);

    Query query = new Query(criteria);
    List<Task> tasks = mongoTemplate.find(query, Task.class);

    return ResponseEntity.status(HttpStatus.ACCEPTED).body(tasks);
  }

  public ResponseEntity<TaskWithHistory> getTask(String id) {
    TaskWithHistory task =
        taskWithHistoryRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));

    return ResponseEntity.status(HttpStatus.ACCEPTED).body(task);
  }

  public ResponseEntity<AuthResponse> deleteTask(String token, String taskId) {
    taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
    taskRepository.deleteById(taskId);

    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }

  public ResponseEntity<AuthResponse> completeTask(TaskComplete req, String token, String taskId) {
    TaskWithHistory task =
        taskWithHistoryRepository
            .findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));

    if (SubAccountRole.CHILD.equals(jwtService.extractSubAccountRole(token))
        && task.isPreValidate()) throw new NoRight();

    SubAccount subAccount =
        subAccountRepository
            .findById(task.getSubaccountIdChild())
            .orElseThrow(() -> new SubAccountNotFoundException(task.getSubaccountIdChild()));

    if (req.isDone()) {
      incomeService.increaseSubAccountIncome(subAccount, task.getMoneyReward(), task);
      coinService.updateCoin(subAccount, task, true);
      task.setStatus(TaskStatus.COMPLETED);
    }

    if (!req.isDone()) task.setStatus(TaskStatus.REFUSED);

    TaskHistory taskHistory = taskHistoryService.createTaskHistory(task);

    List<TaskHistory> taskHistories = task.getTaskHistory();
    if (taskHistories == null) {
      taskHistories = new ArrayList<>();
    }

    taskHistories.add(taskHistory);
    task.setTaskHistory(taskHistories);
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

  public ResponseEntity<Task> modifyTask(TaskUpdate request, String token, String taskId) {
    SubAccountRole role = jwtService.extractSubAccountRole(token);
    if (SubAccountRole.CHILD.equals(role)) throw new NoRight();

    String subAccountIdParent = jwtService.extractSubAccountId(token);
    Task task =
        taskRepository
            .findByIdAndSubaccountIdParent(taskId, subAccountIdParent)
            .orElseThrow(() -> new TaskNotFoundException(taskId));
    TaskType type = request.getType() != null ? request.getType() : task.getType();

    task.setDescription(
        Optional.ofNullable(request.getDescription())
            .filter(s -> !s.isEmpty())
            .orElse(task.getDescription()));

    if (request.getType() != null) task.setType(request.getType());

    task.setSubaccountIdChild(
        Optional.ofNullable(request.getSubAccountId())
            .filter(s -> !s.isEmpty())
            .orElse(task.getSubaccountIdChild()));

    task.setMoneyReward(
        Optional.ofNullable(request.getMoneyReward()).orElse(task.getMoneyReward()));

    task.setCoinReward(Optional.ofNullable(request.getCoinReward()).orElse(task.getCoinReward()));

    task.setPreValidate(Optional.ofNullable(request.isPreValidate()).orElse(task.isPreValidate()));

    task.setDateLimit(Optional.ofNullable(request.getDateLimit()).orElse(task.getDateLimit()));

    task.setDisable(Optional.ofNullable(request.isDisable()).orElse(task.isDisable()));

    switch (type) {
      case TaskType.WEEKLY:
        task.setWeeklyDays(
            Optional.ofNullable(request.getWeeklyDays()).orElse(task.getWeeklyDays()));
        task.setMonthlyDay(0);
        break;
      case TaskType.MONTHLY:
        task.setMonthlyDay(
            Optional.ofNullable(request.getMonthlyDay()).orElse(task.getMonthlyDay()));
        task.setWeeklyDays(null);
        break;
      case TaskType.PONCTUAL:
        task.setWeeklyDays(null);
        task.setMonthlyDay(0);
        break;
      default:
        break;
    }

    task.setUpdatedAt(LocalDateTime.now());

    Task updatedTask = taskRepository.save(task);
    return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
  }
}
