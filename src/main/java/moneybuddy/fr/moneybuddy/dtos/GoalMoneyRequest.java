package moneybuddy.fr.moneybuddy.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalMoneyRequest {

    @NotNull(message = "Le montant à transférer est obligatoire")
    @Positive(message = "Le montant doit être supérieur à zéro")
    private Float transferMoney;

}