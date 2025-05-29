package moneybuddy.fr.moneybuddy.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResetPassword {
    @NotBlank
    @Size(min = 8)
    private String password;
    
    @NotBlank
    @Size(min = 8)
    private String confirmPassword;
    
    @NotBlank
    @Size(min = 4, max = 4)
    private String pin;
}