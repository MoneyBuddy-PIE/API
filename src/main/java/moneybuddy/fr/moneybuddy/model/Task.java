/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.model;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moneybuddy.fr.moneybuddy.model.enums.TaskStatus;
import moneybuddy.fr.moneybuddy.model.enums.TaskType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
  @Id private String id;
  private String subaccountIdParent;
  private String subaccountIdChild;
  private String accountId;

  private String description;

  @Builder.Default private TaskType type = TaskType.PONCTUAL;
  @Builder.Default private TaskStatus status = TaskStatus.PENDING;
  @Builder.Default private boolean preValidate = false;
  @Builder.Default private boolean disable = false;

  @DBRef private Income income;

  private List<DayOfWeek> weeklyDays;
  private int monthlyDay;

  @Builder.Default private BigDecimal moneyReward = BigDecimal.ZERO.setScale(2);
  @Builder.Default private int coinReward = 0;

  private LocalDateTime dateLimit;
  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;
}
