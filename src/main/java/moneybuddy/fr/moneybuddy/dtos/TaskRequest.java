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
public class TaskRequest {

    @NotBlank(message = "Description is mandatory")
    private String description;
    
    @NotBlank(message = "Category is mandatory")
    private String category;

    @NotBlank(message = "SubAccountId is mandatory")
    private String subAccountId;

    @NotBlank(message = "Reward is mandatory")
    private String reward;

    @NotBlank(message = "DateLimit is mandatory")
    private String dateLimit;

    private boolean prevalidation;
}