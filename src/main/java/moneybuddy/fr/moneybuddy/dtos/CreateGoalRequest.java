package moneybuddy.fr.moneybuddy.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGoalRequest {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Amount is mandatory")
    private Number amount;

    @NotBlank(message = "SubAccountId is mandatory")
    private String subAccountId;

    private String emoji;

    private Number transferMoney;
}