/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.Allowance;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;
import moneybuddy.fr.moneybuddy.model.enums.AllowanceFrequency;

@Data
public class UpdateAllowance {

  private String subAccountIdChild;

  private AllowanceFrequency frequency;

  private BigDecimal amount;

  private DayOfWeek weeklyDay = DayOfWeek.SATURDAY;

  private LocalDate startDate = LocalDate.now();

  private boolean active;

  @AssertTrue(message = "Si c'est WEEKLY alors weeklyDay doit un jour de la semaine coché")
  public boolean isWeeklyDayValid() {
    return frequency != AllowanceFrequency.WEEKLY || weeklyDay != null;
  }
}
