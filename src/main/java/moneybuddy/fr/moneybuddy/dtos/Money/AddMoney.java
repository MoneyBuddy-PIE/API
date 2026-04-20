/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.Money;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMoney {

  @NotBlank(message = "SubAccountId est obligatoire")
  private String subAccountId;

  // @NotBlank ne fonctionne que sur String — utiliser @NotNull + @Positive sur BigDecimal
  @NotNull(message = "Le montant est obligatoire")
  @Positive(message = "Le montant doit être supérieur à zéro")
  private BigDecimal amount;

  private String description;
  private String goalId;
}
