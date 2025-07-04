package moneybuddy.fr.moneybuddy.dtos;
import moneybuddy.fr.moneybuddy.model.SubAccountRole;
import moneybuddy.fr.moneybuddy.utils.CheckByRegex;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubAccountDto {
    private final CheckByRegex checkByRegex = new CheckByRegex();

    @NotBlank(message = "Name is mandatory")
    private String name;
    
    @NotNull(message = "Role is mandatory")
    private SubAccountRole role;

    private String pin;

    @AssertTrue(message = "PIN must be exactly 4 digits")
    private boolean isPinValid() {
        System.out.println("sfgdfgdfgd");
        System.out.println(role);
        if (SubAccountRole.PARENT.equals(role)){
            return pin != null &&  pin.length() == 4 && checkByRegex.validate(pin, "^\\d{4}$");
        }
        return true; 
    }
}