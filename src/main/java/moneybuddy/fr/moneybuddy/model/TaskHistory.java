/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moneybuddy.fr.moneybuddy.model.enums.TaskStatus;
import moneybuddy.fr.moneybuddy.model.enums.TaskType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tasksHistory")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskHistory {

  @Id private String id;
  private String taskId;
  private String subAccounttId;
  private String accountId;

  private TaskStatus status;
  private TaskType type;

  private int coinReward;
  private BigDecimal moneyReward;

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;
}
