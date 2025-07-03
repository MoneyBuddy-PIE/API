package moneybuddy.fr.moneybuddy.dtos.Money;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChildBalanceDto {
    private String name;
    private Float balance;
}
