/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moneybuddy.fr.moneybuddy.model.enums.TaskType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

  @NotBlank(message = "Description is mandatory")
  @Size(min = 2, max = 150)
  private String description;

  @NotNull(message = "Type is mandatory")
  private TaskType type;

  @NotBlank(message = "SubAccountId is mandatory")
  private String subAccountId;

  @PositiveOrZero(message = "coinReward doit etre positif ou égale a 0")
  @Max(message = "Doit pas exceder 50", value = 50)
  private int coinReward;

  @PositiveOrZero(message = "moneyReward doit etre positif")
  private Float moneyReward;

  @NotNull(message = "DateLimit is mandatory")
  private LocalDateTime dateLimit;

  private boolean prevalidation;

  @AssertTrue(message = "Au moins une récompense (coinReward ou moneyReward) doit être fournie")
  public boolean isRewardValid() {
    return coinReward != 0 || moneyReward != null;
  }
}
