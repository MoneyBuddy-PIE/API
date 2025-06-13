package moneybuddy.fr.moneybuddy.dtos;

import moneybuddy.fr.moneybuddy.model.SubAccountRole;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubAccountDto {

    @NotBlank(message = "Name is mandatory")
    private String name;
    
    @NotBlank(message = "Role is mandatory")
    private SubAccountRole role;

    @Pattern(regexp = "^\\d{4}$", message = "PIN must be exactly 4 digits")
    @Length(min = 4, max = 4, message = "PIN must be exactly 4 characters")
    private String pin;
}