/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.quiz;

import java.util.Map;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuizRequest {
  @NotBlank(message = "SectionId is mandatory")
  private String sectionId;

  @NotBlank(message = "Question is mandatory")
  private String question;

  @NotBlank(message = "Response is mandatory")
  private String response;

  @NotNull(message = "At least two options are required")
  private Map<String, String> options;

  @Min(0)
  @NotNull(message = "CorrectAnswerIndex has to have at least one correct answer")
  private int correctAnswerIndex;
}
