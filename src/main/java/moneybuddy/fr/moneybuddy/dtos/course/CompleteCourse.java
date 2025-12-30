/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.course;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompleteCourse {

  @NotBlank(message = "ChapterId is mandatory")
  private String chapterId;
}
