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
  private String category;
  private String reward;
  private String dateLimit;

  @Builder.Default private TaskStatus status = TaskStatus.PENDING;

  @Builder.Default private boolean preValidate = false;

  private String subaccountIdParent;
  private String subaccountIdChild;
  private String accountId;

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;
}
