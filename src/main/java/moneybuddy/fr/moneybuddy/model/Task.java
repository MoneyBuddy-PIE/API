/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moneybuddy.fr.moneybuddy.model.enums.TaskStatus;
import moneybuddy.fr.moneybuddy.model.enums.TaskType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
  @Id private String id;

  private String description;

  @Builder.Default private TaskType type = TaskType.PONCTUAL;
  @Builder.Default private TaskStatus status = TaskStatus.PENDING;
  @Builder.Default private boolean preValidate = false;

  @Builder.Default private Float moneyReward = new Float(0);
  @Builder.Default private int coinReward = 0;

  private String subaccountIdParent;
  private String subaccountIdChild;
  private String accountId;

  private LocalDateTime dateLimit;
  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;
}
