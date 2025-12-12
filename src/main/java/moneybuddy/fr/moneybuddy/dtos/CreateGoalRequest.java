package moneybuddy.fr.moneybuddy.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGoalRequest {

    @NotNull(message = "Name is mandatory")
    private String name;

    @NotNull(message = "Amount is mandatory")
    private Float amount;

    private String emoji;
}