/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
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
public class TaskUpdate {
  @Size(min = 2, max = 150)
  private String description;

  private TaskType type;

  private String subAccountId;

  @PositiveOrZero(message = "coinReward doit etre positif ou égale a 0")
  @Max(message = "Doit pas exceder 50", value = 50)
  private int coinReward;

  @PositiveOrZero(message = "moneyReward doit etre positif ou égale a 0")
  private BigDecimal moneyReward;

  private LocalDateTime dateLimit;
  private List<DayOfWeek> weeklyDays;
  private int monthlyDay;
  private boolean preValidate;
  private boolean disable;

  @AssertTrue(message = "Au moins une récompense (coinReward ou moneyReward) doit être fournie")
  @Schema(hidden = true)
  public boolean isRewardValid() {
    return (coinReward == 0 && moneyReward == null) || coinReward != 0 || moneyReward != null;
  }

  @AssertTrue(
      message =
          "Si c'est hebdomadaire alors weeklyDays doit au moins avoir un jour de la semaine coché")
  public boolean isWeeklyDays() {
    return type == null
        || !TaskType.WEEKLY.equals(type)
        || (weeklyDays != null && !weeklyDays.isEmpty());
  }

  @AssertTrue(message = "Si c'est mensuel alors monthlyDay doit avoir une valeur")
  public boolean isMonthlyDay() {
    return type == null || !TaskType.MONTHLY.equals(type) || monthlyDay != 0;
  }
}
