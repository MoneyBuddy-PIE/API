package moneybuddy.fr.moneybuddy.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalRequest {

    private String name;

    private Float amount;

    private String emoji;
}