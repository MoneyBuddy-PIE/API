package moneybuddy.fr.moneybuddy.dtos;

import moneybuddy.fr.moneybuddy.model.SubAccountRole;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubAccountDto {

    @NotBlank
    private String name;
    
    @NotBlank
    private SubAccountRole role;

    private String pin;
}