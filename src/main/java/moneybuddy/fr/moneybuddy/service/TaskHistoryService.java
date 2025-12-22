/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.exception.TaskHistoryNotFounded;
import moneybuddy.fr.moneybuddy.model.Task;
import moneybuddy.fr.moneybuddy.model.TaskHistory;
import moneybuddy.fr.moneybuddy.repository.TaskHistoryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskHistoryService {

  private final TaskHistoryRepository taskHistoryRepository;

  public TaskHistory createTaskHistory(Task task) {
    TaskHistory taskHistory =
        TaskHistory.builder()
            .taskId(task.getId())
            .accountId(task.getAccountId())
            .subAccounttId(task.getSubaccountIdChild())
            .coinReward(task.getCoinReward())
            .moneyReward(task.getMoneyReward())
            .status(task.getStatus())
            .type(task.getType())
            .build();
    taskHistoryRepository.save(taskHistory);
    return taskHistory;
  }

  public List<TaskHistory> getTaskHistory(String taskId) {
    List<TaskHistory> taskHistory =
        taskHistoryRepository
            .findAllByTaskId(taskId)
            .orElseThrow(() -> new TaskHistoryNotFounded());

    return taskHistory;
  }
}
