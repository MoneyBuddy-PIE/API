/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalRequest {

  @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
  private String name;

  @Positive(message = "Le montant doit être supérieur à zéro")
  private BigDecimal amount;

  @Size(max = 10, message = "L'emoji ne peut pas dépasser 10 caractères")
  private String emoji;
}
