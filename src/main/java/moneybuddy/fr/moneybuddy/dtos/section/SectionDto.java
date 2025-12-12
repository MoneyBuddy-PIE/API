/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.section;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import moneybuddy.fr.moneybuddy.dtos.quiz.QuizDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionDto {
  @NotBlank(message = "Section title is mandatory")
  private String title;

  @NotBlank(message = "Section content is mandatory")
  private String content;

  @Valid private QuizDto quiz;
}
