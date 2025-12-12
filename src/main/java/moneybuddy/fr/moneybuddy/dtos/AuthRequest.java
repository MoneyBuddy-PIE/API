/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {

  @NotBlank(message = "L'email est obligatoire")
  @Email(message = "Le format de l'email est invalide")
  private String email;

  @NotBlank(message = "Le mot de passe est obligatoire")
  private String password;
}
