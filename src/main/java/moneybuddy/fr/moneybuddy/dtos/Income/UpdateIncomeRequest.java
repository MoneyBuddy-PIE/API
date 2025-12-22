/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.Income;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import moneybuddy.fr.moneybuddy.model.enums.IncomeStatus;

@Data
@Builder
@AllArgsConstructor
public class UpdateIncomeRequest {

  @NotNull(message = "Le status doit etre envoyé !")
  private final IncomeStatus status;
}
