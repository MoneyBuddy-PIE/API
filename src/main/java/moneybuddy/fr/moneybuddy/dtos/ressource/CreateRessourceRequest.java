/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.ressource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import moneybuddy.fr.moneybuddy.model.enums.RessourceType;

@Data
public class CreateRessourceRequest {
  @NotBlank(message = "CourseId is mandatory")
  private String courseId;

  @NotBlank(message = "Title is mandatory")
  private String title;

  @NotBlank(message = "Url is mandatory")
  private String url;

  @NotNull(message = "Type is mandatory")
  private RessourceType type;
}
