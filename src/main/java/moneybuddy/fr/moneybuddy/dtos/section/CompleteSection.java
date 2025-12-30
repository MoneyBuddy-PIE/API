/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.section;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CompleteSection {
  @NotNull(message = "Score is required")
  @PositiveOrZero(message = "Score doit etre > 0")
  private BigDecimal score;
}
