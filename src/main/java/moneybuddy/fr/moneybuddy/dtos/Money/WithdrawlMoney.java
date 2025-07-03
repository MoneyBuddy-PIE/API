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
public class WithdrawlMoney {

    @NotBlank(message = "SubAccountid is mandatory")
    private String subAccountId;

    @NotBlank(message = "Amount is mandatory")
    private Float amount;

    private String description;
}
