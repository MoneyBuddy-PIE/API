/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.dtos.section;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CreateSectionRequest {
  @NotBlank(message = "CourseId is mandatory")
  private String courseId;

  @NotBlank(message = "Title is mandatory")
  private String title;

  @NotBlank(message = "MarkdownContent is mandatory")
  private String markdownContent;

  @NotNull(message = "MinimumScoreToPass is mandatory")
  @PositiveOrZero(message = "Amount doit etre > 0")
  BigDecimal minimumScoreToPass;
}
