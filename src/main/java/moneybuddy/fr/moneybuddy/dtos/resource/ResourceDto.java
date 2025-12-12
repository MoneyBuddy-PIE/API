/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.resource;

import jakarta.validation.constraints.*;
import lombok.*;
import moneybuddy.fr.moneybuddy.model.enums.RessourceType;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDto {
  @NotBlank private String title;

  @NotBlank @URL private String url;

  @NotNull(message = "Type is mandatory")
  private RessourceType type;
}
