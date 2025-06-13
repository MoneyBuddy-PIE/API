package moneybuddy.fr.moneybuddy.dtos;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Wrong format for email")
    private String email;
    
    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password has to have length 8")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$", message = "Password has to be 1 Capital letter and 1 number at least")
    private String password;
    
    @NotBlank(message = "ConfirmPassword is mandatory")
    private String confirmPassword;
    
    @NotBlank(message = "Pin is mandatory")
    @Size(min = 4, max = 4, message = "Pin has to have length 4")
    @Pattern(regexp =  "^\\d{4}$", message = "Wrong format for Pin")
    private String pin;

    @NotBlank(message = "Name is mandatory")
    private String name;
}

