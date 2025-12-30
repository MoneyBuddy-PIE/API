/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.section;

import java.math.BigDecimal;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class UpdateSectionRequest {
  private String title;

  private String markdownContent;

  @PositiveOrZero(message = "Amount doit etre > 0")
  BigDecimal minimumScoreToPass;
}
