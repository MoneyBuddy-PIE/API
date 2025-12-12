/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moneybuddy.fr.moneybuddy.model.enums.GoalStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "goals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Goal {
  @Id private String id;

  private String name;
  private Float amount;
  private String emoji;

  @Builder.Default private Float depositStatement = new Float(0);

  @Builder.Default private GoalStatus goalStatus = GoalStatus.ACTIVATED;

  @Builder.Default private Number progression = 0;

  private String subaccountIdChild;
  private String accountId;

  @Builder.Default private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime updatedAt;
}
