/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.cron;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.model.Task;
import moneybuddy.fr.moneybuddy.model.enums.TaskStatus;
import moneybuddy.fr.moneybuddy.model.enums.TaskType;
import moneybuddy.fr.moneybuddy.repository.TaskRepository;
import moneybuddy.fr.moneybuddy.service.DiscordService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class SchedulingTasksApplication {

  private final TaskRepository taskRepository;
  private final DiscordService discordService;

  @Scheduled(cron = "0 0 2 * * ?")
  public void resetWeeklyOrMonthlyTask() {
    List<Task> tasks =
        taskRepository.findCompletedTasks(
            TaskStatus.COMPLETED,
            Arrays.asList(TaskType.WEEKLY, TaskType.MONTHLY),
            LocalDateTime.now());

    discordService.SendMessage("CRON JOB - Reset Tasks, tasks: " + tasks.size());

    for (Task task : tasks) {
      task.setStatus(TaskStatus.PENDING);
      task.setUpdatedAt(LocalDateTime.now());
      taskRepository.save(task);
    }
  }
}
