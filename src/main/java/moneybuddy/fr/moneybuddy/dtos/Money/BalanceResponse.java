package moneybuddy.fr.moneybuddy.dtos.Money;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceResponse {

    @NotBlank(message = "SubAccountid is mandatory")
    private String subAccountId;
}
