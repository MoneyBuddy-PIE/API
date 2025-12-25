/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.Allowance;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import moneybuddy.fr.moneybuddy.model.enums.AllowanceFrequency;

@Data
@Builder
public class CreateAllowance {

  @NotBlank(message = "Veillez selectionner un enfant")
  private String subAccountIdChild;

  @NotNull(message = "Choisir une frequence")
  private AllowanceFrequency frequency;

  @Positive(message = "Amount doit etre > 0")
  private BigDecimal amount;

  private boolean active;

  @Builder.Default private DayOfWeek weeklyDay = DayOfWeek.SATURDAY;

  @Builder.Default private LocalDate startDate = LocalDate.now();

  @AssertTrue(message = "Si c'est WEEKLY alors weeklyDay doit un jour de la semaine coché")
  public boolean isWeeklyDay() {
    return !frequency.equals(AllowanceFrequency.WEEKLY) || (weeklyDay != null);
  }
}
