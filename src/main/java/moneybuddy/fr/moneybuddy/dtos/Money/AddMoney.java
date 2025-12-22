/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.Money;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMoney {

  @NotBlank(message = "SubAccountid is mandatory")
  private String subAccountId;

  @NotBlank(message = "Amount is mandatory")
  private BigDecimal amount;

  private String description;
  private String goalId;
}
